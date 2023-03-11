package com.vladsch.clionarduinoplugin.resources

import com.intellij.openapi.diagnostic.Logger
import com.vladsch.flexmark.util.misc.TemplateUtil
import com.vladsch.flexmark.util.sequence.BasedSequence
import com.vladsch.plugin.util.*
import java.io.File
import java.io.IOException
import java.net.URL
import java.net.URLDecoder
import java.util.jar.JarFile
import java.util.regex.Pattern

@Suppress("MemberVisibilityCanBePrivate")
object TemplateResolver {

    private val LOG = Logger.getInstance("com.vladsch.clionarduinoplugin")

    private const val BUNDLED_TEMPLATES = "/com/vladsch/clionarduinoplugin/templates"
    val templateTypes = setOf(
        "sketch",
        "library",
        "project${File.separator}library_arduino",
        "project${File.separator}library_static",
        "project${File.separator}sketch"
    )
    val templateTypesTest = setOf(
        "sketch",
        "library",
        "project${File.separator}library_arduino",
        "project${File.separator}library_static",
        "project${File.separator}sketch",
        "project${File.separator}sketchsubdir"
    )
    val templateLevel1Dirs = setOf("sketch", "library", "project")
    val templateTypesPrefixes = templateTypes.map { it + File.separator }.toSet()
    val templateTypesTestPrefixes = templateTypesTest.map { it + File.separator }.toSet()

    var inTest = false

    fun haveAllTemplates(templateDir: File?): Boolean {
        if (templateDir == null || !isValidTemplateDir(templateDir)) return false

        for (templateType in templateTypes()) {
            val templates = TemplateResolver.getTemplates(templateType, templateDir)
            val bundledTemplates = TemplateResolver.getTemplates(templateType, null)
            if (!templates.keys.containsAll(bundledTemplates.keys)) {
                return false
            }
        }
        return true
    }

    @Throws(IOException::class)
    fun copyTemplatesDirectoryTo(templateDir: File) {
        val bundledTemplates = TemplateResolver.getTemplates("", null)
        for ((path, content) in bundledTemplates) {
            if (!path.startsWith("project${File.separator}sketchsubdir/")) {
                val file = templateDir + path
                val parentDir = file.parentFile
                if (!parentDir.exists()) {
                    parentDir.mkdirs()
                }
                file.writeText(content, Charsets.UTF_8)
            }
        }
    }

    private fun getTemplatesURL(): URL {
        val resource = TemplateResolver.javaClass.getResource(BUNDLED_TEMPLATES)
        LOG.info("Bundled Templates $resource")
        return resource;
    }

    fun isValidTemplateDir(templatesDir: File): Boolean {
        if (!templatesDir.exists() || !templatesDir.isDirectory) return false;

        var count = 0;
        templatesDir.list { _, name ->
            if (templateLevel1Dirs.contains(name)) {
                count++
            }
            false
        }

        return count == templateLevel1Dirs.size
    }

    /**
     * return map of file path relative to templateType dir -> template content
     */
    fun getTemplates(templateType: String, templatesDir: File?): Map<String, String> {
        val result = HashMap<String, String>()
        if (templatesDir != null && !isValidTemplateDir(templatesDir)) return result;

        val resource =
            getTemplatesURL() //URL("jar:file:/Users/vlad/Library/Application%20Support/CLion2018.1/CLionArduinoPlugin/lib/CLionArduinoPlugin.jar!/com/vladsch/clionarduinoplugin/templates") //getTemplatesURL();
        val baseDir: File
        val templateDir: File
        val templateFiles = HashMap<File, String>()

        /**
         * Adapted from code example by Greg Briggs - http://www.uofr.net/~greg/java/get-resource-listing.html
         */
        if (resource.protocol == "jar" && templatesDir == null) {
            val jarPath = resource.path.substring(5, resource.path.indexOf("!")).replace("/", File.separator)
            val jar = JarFile(URLDecoder.decode(jarPath, "UTF-8"))
            baseDir = File(resource.path.substring(5 + 1 + jarPath.length).replace("/", File.separator))
            templateDir = baseDir + templateType

            val entries = jar.entries() //gives ALL entries in jar
            val prefix = baseDir.path.substring(1).replace("/", File.separator) + File.separator
//            System.out.println(prefix)

            while (entries.hasMoreElements()) {
                val jarEntry = entries.nextElement()
                if (!jarEntry.isDirectory) {
                    val name = jarEntry.name.replace("/", File.separator)
//                    System.out.println(name)
                    if (name.startsWith(prefix)) { //filter according to the path
                        val entry = name.substring(prefix.length)
                        if (entry.isNotEmpty()) {
                            val stream = jar.getInputStream(jarEntry);
                            val sb = StringBuilder()
                            sb.streamAppend(stream)
                            val entryFile = baseDir + entry
                            templateFiles[entryFile] = sb.toString()
                        }
//                    } else {
//                        System.out.println("Excluding $name")
                    }
                }
            }
        } else if (resource.protocol == "file" || templatesDir != null) {
            baseDir = templatesDir ?: File(resource.toURI())
            templateDir = baseDir + templateType

            getTemplateFiles(baseDir, baseDir, templateFiles, 1, false)
        } else {
            throw IllegalStateException("Bundled templates not accessible")
        }

        // now resolve based on content inheritance but only for directory requested
        for (file in templateFiles.keys) {
            if (file.isChildOf(templateDir)) {
                var content = templateFiles[file] ?: ""

                result[file.relativeTo(templateDir).path] = content

                if (content.isEmpty()) {
                    // try to resolve it using back path
                    var parentDir = file.parentFile
                    while (parentDir != null) {
                        parentDir = parentDir.parentFile

                        var starMatch: File? = null
                        var nameMatch: File? = null

                        for (parentTemplate in templateFiles.keys) {
                            if (parentTemplate.parentFile == parentDir) {
                                if (parentTemplate.extension == file.extension) {
                                    if (parentTemplate.name == "@@" + if (file.extension.isEmpty()) "" else "." + file.extension) {
                                        starMatch = parentTemplate
                                    } else if (parentTemplate.name == file.name) {
                                        nameMatch = parentTemplate
                                    }

                                    if (starMatch != null && nameMatch != null) break;
                                }
                            }
                        }

                        if (nameMatch == null) nameMatch = starMatch
                        if (nameMatch != null) {

                            content = templateFiles[nameMatch] ?: ""
                            if (!content.isEmpty()) {
                                templateFiles[file] =
                                    content   // just in case there is a sub directory with an empty template that would match
                                result[file.relativeTo(templateDir).path] = content
                                break
                            }
                        }
                    }
                }
            }
        }

        return result
    }

    private fun String.startsWith(prefixes: Set<String>): Boolean {
        val suffixed = this + File.separator
        return prefixes.any { suffixed.startsWith(it) }
    }

    fun getTemplateFiles(baseDir: File, templateDir: File, files: HashMap<File, String>, level: Int, isBundled: Boolean) {
        if (level >= 5 || files.size > 200) {
            return
        }

        for (resource in templateDir.list()) {
            val resourceFile = templateDir + resource
            if (!isBundled && resourceFile.isDirectory) {
                if (level == 1 && !templateLevel1Dirs.contains(resource)) {
                    continue
                }
                if (level >= 2 && !resourceFile.relativeTo(baseDir).path.startsWith(templateTypePrefixes())) {
                    continue
                }
            }

            if (resourceFile.isDirectory) {
                getTemplateFiles(baseDir, resourceFile, files, level + 1, isBundled)
            } else {
                files[resourceFile] = getContent(resourceFile)
            }
        }
    }

    private fun templateTypes(): Set<String> {
        return if (inTest) templateTypesTest else templateTypes
    }

    private fun templateTypePrefixes(): Set<String> {
        return if (inTest) templateTypesTestPrefixes else templateTypesPrefixes
    }

    fun getContent(file: File): String {
        return if (file.exists()) getFileContent(file) else getResourceAsString(this.javaClass, file.path)
    }

    val COMMAND_REF: Pattern = Pattern.compile("<@([a-zA-Z_$][a-zA-Z_0-9$]+)(?:\\[([^]]*)])?@>")
    val FILE_NAME_REF: Pattern = Pattern.compile("@([a-zA-Z_$][a-zA-Z_0-9$]+)(?:\\[([^]]*)])?@")

    fun resolveTemplates(templates: Map<String, String>, values: Map<String, String>): Map<String, String> {
        val result = HashMap<String, String>();
        val simpleResolver = simpleRefResolver(values)

        for ((templatePath, content) in templates) {
            val file = File(templatePath)
            val fileNameOnly = file.nameOnly
            val resolvedFile = fileNameOnly.resolveRefs(FILE_NAME_REF, simpleResolver)
            if (resolvedFile.isNotEmpty()) {
                val lines = BasedSequence.of(content).split("\n", Integer.MAX_VALUE - 2, BasedSequence.SPLIT_INCLUDE_DELIMS)
                val template = StringBuilder()

                for (line in lines) {
                    var append = true
                    val resolved = line.resolveRefs(COMMAND_REF) { name, index ->
                        if (name == "DELETE_IF_BLANK") {
                            append = index.resolveRefs(COMMAND_REF, simpleResolver).isNotEmpty()
                            ""
                        } else {
                            if (index != null) null
                            else values[name]
                        }
                    }
                    if (append) template.append(resolved)
                }
                result[file.pathSlash + resolvedFile + file.dotExtension] = template.toString()
            }
        }
        return result
    }
}

fun CharSequence?.resolveRefs(pattern: Pattern, resolver: (name: String, index: String?) -> String?): String {
    return TemplateUtil.resolveRefs(this, pattern) { groups ->
        val name = groups[1]
        val index: String? = if (groups.size > 2) groups[2] else null

        resolver.invoke(name, index)
    }
}

fun simpleRefResolver(values: Map<String, String>): (String, String?) -> String? {
    return { name1, index1 ->
        if (index1 != null) null
        else values[name1]
    }
}
