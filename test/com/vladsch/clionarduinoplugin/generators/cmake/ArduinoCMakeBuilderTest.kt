package com.vladsch.clionarduinoplugin.generators.cmake

import com.intellij.openapi.util.io.FileUtil
import com.vladsch.clionarduinoplugin.generators.cmake.commands.CMakeCommand
import com.vladsch.clionarduinoplugin.resources.Strings
import com.vladsch.clionarduinoplugin.resources.TemplateResolver
import com.vladsch.clionarduinoplugin.settings.ArduinoApplicationSettings
import com.vladsch.clionarduinoplugin.settings.ArduinoApplicationSettingsProxy
import com.vladsch.clionarduinoplugin.settings.ArduinoProjectFileSettings
import com.vladsch.plugin.util.StudiedWord
import com.vladsch.plugin.util.getFileContent
import com.vladsch.plugin.util.plus
import org.junit.Assert.assertEquals
import org.junit.Test
import java.io.File
import java.util.*
import kotlin.collections.HashMap

class ArduinoCMakeBuilderTest {
    val testProjects: File = File(ArduinoCMakeBuilderTest::class.java.getResource("/projects/dummy.txt").toURI()).parentFile
    val createdProjects: File = File(ArduinoCMakeBuilderTest::class.java.getResource("/created/dummy.txt").toURI()).parentFile
    val applicationSettings: ArduinoApplicationSettings = ArduinoApplicationSettings()

    private val source = """
# set(lib1_RECURSE true)
set(lib2_RECURSE true)
set(lib3_RECURSE true)
# set(\$\{LIB_NAME}_RECURSE true)
set(lib4_RECURSE false)
"""

    private fun createFiles(projectName: String, rootDir: File, mySettings: ArduinoApplicationSettingsProxy, templateType: String): Map<File, String> {
        val createdFiles = HashMap<File, String>()

        val name = FileUtil.sanitizeFileName(projectName)
        val word = StudiedWord(name, StudiedWord.DOT or StudiedWord.DASH or StudiedWord.UNDER)
        val snakeName = word.makeSnakeCase()
        val camelName = word.makeProperCamelCase()
        val pascalName = word.makePascalCase()
        val templateVars = mutableMapOf(
                "PROJECT_NAME" to snakeName.toUpperCase(),
                "project_name" to snakeName.toLowerCase(),
                "ProjectName" to pascalName,
                "projectName" to camelName
        )

        if (mySettings.isLibrary) {
            templateVars.putAll(
                    mapOf(
                            "LIBRARY_NAME" to snakeName.toUpperCase(),
                            "library_name" to snakeName.toLowerCase(),
                            "LibraryName" to pascalName,
                            "libraryName" to camelName,
                            "LIBRARY_CATEGORY" to mySettings.libraryCategory,
                            "LIBRARY_DISPLAY_NAME" to mySettings.libraryDisplayName,
                            "USER_NAME" to mySettings.authorName,
                            "E_MAIL" to mySettings.authorEMail
                    )
            )
        }

        val templateDir: File? = null
        val templates = TemplateResolver.getTemplates(templateType, templateDir)
        val resolvedTemplates = TemplateResolver.resolveTemplates(templates, templateVars)

        val nonSourceFiles = ArrayList<File>()
        val sourceFiles = resolvedTemplates
                .map { (name, content) ->
                    if (name != Strings.CMAKE_LISTS_FILENAME) createProjectFileWithContent(createdFiles, rootDir, name, content) else null
                }.filterNotNull()
                //  if non source files are left in the source array then CLion 2018.1 throws an exception on project create
                .filterNot {
                    if (it.name == Strings.LIBRARY_PROPERTIES_FILENAME || it.name == "keywords.txt") {
                        nonSourceFiles.add(it)
                        true
                    } else false
                }.toTypedArray()

        val cMakeFileTemplate = resolvedTemplates[Strings.CMAKE_LISTS_FILENAME] ?: ""

        val projectDir = File(rootDir.path)
        val fileList = sourceFiles.map { File(it.path).relativeTo(projectDir).path }
        val cMakeFileContent = ArduinoCMakeListsTxtBuilder.getCMakeFileContent(cMakeFileTemplate, name, mySettings, fileList, true)
        val cMakeFile = createProjectFileWithContent(createdFiles, rootDir, Strings.CMAKE_LISTS_FILENAME, cMakeFileContent)

        //        val extraFiles = ArduinoToolchainFiles.copyToDirectory(VfsUtil.findFileByIoFile(VfsUtilCore.virtualToIoFile(rootDir), false))
        if (mySettings.isAddLibraryDirectory) {
            val libDir = File(rootDir.path) + (mySettings.libraryDirectories.firstOrNull() ?: "")
            if (!libDir.exists() && libDir.canonicalPath != rootDir.canonicalPath) {
                libDir.mkdirs()
            }
        }
        return createdFiles
    }

    private fun createProjectFileWithContent(createdFiles: HashMap<File, String>, rootDir: File, name: String, content: String): File {
        val file = rootDir + name
        createdFiles[file] = content;
        return file
    }

    fun compareCommand(expected: Array<Array<String>>, actual: List<CMakeCommand>) {
        assertEquals("Result size differs", expected.size, actual.size)

        for (i in 0..expected.size - 1) {
            val exp = expected[i]
            val act = actual[i]

            assertEquals("Argument count differs", exp.size, act.argCount)

            for (j in 0..exp.size - 1) {
                assertEquals("Item[$i+1] argument ${j + 1} differs", exp[j], actual[i].getArg(j))
            }
        }
    }

    @Test
    fun test_superType() {
        val builder = ArduinoCMakeListsTxtBuilder(source)
        val commands = builder.getCommands(ArduinoCMakeListsTxtBuilder.SET_LIB_NAMES_RECURSE)

        compareCommand(arrayOf(
                arrayOf("lib2", "true"),
                arrayOf("lib3", "true"),
                arrayOf("lib4", "false")
        ), commands)
    }

    @Test
    fun test_subTypeSuperType() {
        val builder = ArduinoCMakeListsTxtBuilder(source, null, mapOf<String, Any>("LIB_NAME" to "lib3"))
        val commands = builder.getCommands(ArduinoCMakeListsTxtBuilder.SET_LIB_NAMES_RECURSE)

        compareCommand(arrayOf(
                arrayOf("lib2", "true"),
                arrayOf("true"),
                arrayOf("lib4", "false")
        ), commands)
    }

    fun compareFiles(rootDir: File, expected: Map<File, String>, actual: Map<File, String>) {
        val sortedKeys = expected.keys.sortedBy { it.path }
        assertEquals(sortedKeys, actual.keys.sortedBy { it.path })

        for (key in sortedKeys) {
            assertEquals(getFileUriFromProjectResource(key, rootDir), expected[key], actual[key])
        }
    }

    private fun getFileUriFromProjectResource(file: File, rootDir: File) =
            "File: file:///Users/vlad/src/projects/CLionArduinoPlugin/test-resources/created/${file.relativeTo(rootDir.parentFile).path}"

    fun compareFiles(rootDir: File, files: Map<File, String>) {
        val expected = HashMap<File, String>()
        rootDir.list().forEach { name ->
            val file = rootDir + name
            // TODO: handle directories
            if (file.exists() && file.isFile) {
                expected[file] = getFileContent(file)
            }
        }

        compareFiles(rootDir, expected, files)
    }

    @Test
    fun test_non_arduino() {
        val projectDir = testProjects + "non_arduino"
        val settings = ArduinoCMakeListsTxtBuilder.loadProjectConfiguration(projectDir, null, applicationSettings)
        val expected = ArduinoApplicationSettingsProxy.of() as ArduinoProjectFileSettings

        assertEquals(null, settings?.asString("expected"))
    }

    @Test
    fun test_tft_life() {
        val projectDir = testProjects + "tft_life"
        val settings = ArduinoCMakeListsTxtBuilder.loadProjectConfiguration(projectDir, null, applicationSettings)
        val expected = ArduinoApplicationSettingsProxy.of() as ArduinoProjectFileSettings

        expected.projectName = projectDir.name
        expected.headers = arrayOf()
        expected.sources = arrayOf("tft_life.cpp")
        expected.boardId = "pro"
        expected.cpuId = "8MHzatmega328"
        expected.programmerId = "avrispmkii"
        expected.port = "/dev/cu.usbserial-00000000"
        expected.isAddLibraryDirectory = true
        expected.libraryDirectories = arrayOf("..")
        expected.isVerbose = true

        assertEquals(expected.asString("expected"), settings?.asString("expected"))

        val rootDir = createdProjects + expected.projectName
        val cmakelists = getFileContent(projectDir + Strings.CMAKE_LISTS_FILENAME)
        val modified = ArduinoCMakeListsTxtBuilder.getCMakeFileContent(cmakelists, expected.projectName, expected as ArduinoApplicationSettingsProxy, true)
        val expectedContent = getFileContent(rootDir + Strings.CMAKE_LISTS_FILENAME)

        assertEquals("Expected ${getFileUriFromProjectResource(rootDir + Strings.CMAKE_LISTS_FILENAME, rootDir)}", expectedContent, modified);
    }

    @Test
    fun test_tft_life2() {
        val projectDir = testProjects + "tft_life2"
        val settings = ArduinoCMakeListsTxtBuilder.loadProjectConfiguration(projectDir, null, applicationSettings)
        val expected = ArduinoApplicationSettingsProxy.of() as ArduinoProjectFileSettings

        // NOTE: the file sets CMAKE_PROJECT_NAME but uses ${PROJECT_NAME}_SRCS and ${PROJECT_NAME}_HDRS
        expected.projectName = projectDir.name
        expected.headers = arrayOf()
        expected.sources = arrayOf("tft_life2.cpp")
        expected.boardId = "pro"
        expected.cpuId = "8MHzatmega328"
        expected.programmerId = "avrispmkii"
        expected.port = "/dev/cu.usbserial-00000000"
        expected.isAddLibraryDirectory = true
        expected.libraryDirectories = arrayOf("..")
        expected.isVerbose = true

        assertEquals(expected.asString("expected"), settings?.asString("expected"))

        val rootDir = createdProjects + expected.projectName
        val cmakelists = getFileContent(projectDir + Strings.CMAKE_LISTS_FILENAME)
        val modified = ArduinoCMakeListsTxtBuilder.getCMakeFileContent(cmakelists, expected.projectName, expected as ArduinoApplicationSettingsProxy, true)
        val expectedContent = getFileContent(rootDir + Strings.CMAKE_LISTS_FILENAME)

        assertEquals("Expected ${getFileUriFromProjectResource(rootDir + Strings.CMAKE_LISTS_FILENAME, rootDir)}", expectedContent, modified);
    }

    @Test
    fun test_sketch1() {
        val projectDir = testProjects + "sketch_1"
        val settings = ArduinoCMakeListsTxtBuilder.loadProjectConfiguration(projectDir, null, applicationSettings)
        val expected = ArduinoApplicationSettingsProxy.of() as ArduinoProjectFileSettings

        expected.projectName = projectDir.name
        expected.headers = arrayOf("User_Setup.h")
        expected.sketch = "sketch_1.ino"
        expected.boardId = "pro"
        expected.cpuId = "8MHzatmega328"
        expected.programmerId = "avrispmkii"
        expected.port = "/dev/cu.usbserial-00000000"
        expected.isVerbose = true

        assertEquals(expected.asString("expected"), settings?.asString("expected"))

        val rootDir = createdProjects + expected.projectName
        val files = createFiles(expected.projectName, rootDir, expected as ArduinoApplicationSettingsProxy, "project/sketch")
        compareFiles(rootDir, files);
    }

    @Test
    fun test_sketch2() {
        val projectDir = testProjects + "sketch_2"
        val settings = ArduinoCMakeListsTxtBuilder.loadProjectConfiguration(projectDir, null, applicationSettings)
        val expected = ArduinoApplicationSettingsProxy.of() as ArduinoProjectFileSettings

        expected.projectName = projectDir.name
        expected.headers = arrayOf("User_Setup.h")
        expected.sketch = "sketch_2.ino"
        expected.boardId = "uno"
        expected.cpuId = ""
        expected.programmerId = "usbtinyisp"
        expected.port = "/dev/cu.Bluetooth-Incoming-Port"
        expected.isAddLibraryDirectory = true
        expected.libraryDirectories = arrayOf("libs")
        expected.isVerbose = false

        assertEquals(expected.asString("expected"), settings?.asString("expected"))

        val rootDir = createdProjects + expected.projectName
        val files = createFiles(expected.projectName, rootDir, expected as ArduinoApplicationSettingsProxy, "project/sketch")
        compareFiles(rootDir, files);
    }

    @Test
    fun test_tft_test() {
        val projectDir = testProjects + "tft_test"
        val settings = ArduinoCMakeListsTxtBuilder.loadProjectConfiguration(projectDir, null, applicationSettings)
        val expected = ArduinoApplicationSettingsProxy.of() as ArduinoProjectFileSettings

        expected.projectName = projectDir.name
        expected.headers = arrayOf()
        expected.sketch = "tft_test.ino"
        expected.languageVersionId = ""
        expected.boardId = "pro"
        expected.cpuId = "8MHzatmega328"
        expected.programmerId = "avrispmkii"
        expected.port = "/dev/cu.usbserial-00000000"
        expected.isAddLibraryDirectory = true
        expected.libraryDirectories = arrayOf("libs")
        expected.isVerbose = true

        assertEquals(expected.asString("expected"), settings?.asString("expected"))

        val rootDir = createdProjects + expected.projectName
        val files = createFiles(expected.projectName, rootDir, expected as ArduinoApplicationSettingsProxy, "project/sketch")
        compareFiles(rootDir, files);
    }

    @Test
    fun test_arduino_lib1() {
        val projectDir = testProjects + "arduino_lib1"
        val settings = ArduinoCMakeListsTxtBuilder.loadProjectConfiguration(projectDir, null, applicationSettings)
        val expected = ArduinoApplicationSettingsProxy.of() as ArduinoProjectFileSettings

        expected.isLibrary = true
        expected.projectName = projectDir.name
        expected.headers = arrayOf("User_Setup.h", "arduino_lib1.h")
        expected.sources = arrayOf("arduino_lib1_test.cpp", "arduino_lib1.cpp")
        expected.boardId = "pro"
        expected.cpuId = "8MHzatmega328"
        expected.programmerId = "avrispmkii"
        expected.port = "/dev/cu.usbserial-00000000"
        expected.isAddLibraryDirectory = true
        expected.libraryDirectories = arrayOf("test")
        expected.isVerbose = true
        // TODO: implement library.properties reading
        //        expected.libraryDisplayName = "Arduino Lib1"

        assertEquals(expected.asString("expected"), settings?.asString("expected"))

        // since we don't extract library display name
        expected.libraryDisplayName = "Lib1"

        val rootDir = createdProjects + expected.projectName
        val files = createFiles(expected.projectName, rootDir, expected as ArduinoApplicationSettingsProxy, "project/library_arduino")
        compareFiles(rootDir, files);
    }

    @Test
    fun test_arduino_lib2() {
        val projectDir = testProjects + "arduino_lib2"
        val settings = ArduinoCMakeListsTxtBuilder.loadProjectConfiguration(projectDir, null, applicationSettings)
        val expected = ArduinoApplicationSettingsProxy.of() as ArduinoProjectFileSettings

        expected.isLibrary = true
        expected.projectName = projectDir.name
        expected.headers = arrayOf("User_Setup.h", "arduino_lib2.h")
        expected.sources = arrayOf("arduino_lib2.cpp", "arduino_lib2_test.cpp")
        expected.boardId = "uno"
        expected.cpuId = ""
        expected.programmerId = "usbtinyisp"
        expected.port = "/dev/cu.ARPT"
        expected.isAddLibraryDirectory = true
        expected.libraryDirectories = arrayOf("lib", "util")
        expected.isVerbose = true
        // TODO: implement library.properties reading
        //expected.libraryDisplayName = "Arduino Lib2"

        assertEquals(expected.asString("expected"), settings?.asString("expected"))
        
        // since we don't extract library display name
        expected.libraryDisplayName = "Lib2"

        val rootDir = createdProjects + expected.projectName
        val files = createFiles(expected.projectName, rootDir, expected as ArduinoApplicationSettingsProxy, "project/library_arduino")
        compareFiles(rootDir, files);
    }

    @Test
    fun test_static_lib1() {
        val projectDir = testProjects + "static_lib1"
        val settings = ArduinoCMakeListsTxtBuilder.loadProjectConfiguration(projectDir, null, applicationSettings)
        val expected = ArduinoApplicationSettingsProxy.of() as ArduinoProjectFileSettings

        expected.isLibrary = true
        expected.libraryType = "static"
        expected.projectName = projectDir.name
        expected.headers = arrayOf("static_lib1.h")
        expected.sources = arrayOf("static_lib1.cpp")
        expected.boardId = "pro"
        expected.cpuId = "16MHzatmega328"
        expected.programmerId = "usbasp"
        expected.port = "/dev/cu.Bluetooth-Incoming-Port"
        expected.isVerbose = true

        assertEquals(expected.asString("expected"), settings?.asString("expected"))

        val rootDir = createdProjects + expected.projectName
        val files = createFiles(expected.projectName, rootDir, expected as ArduinoApplicationSettingsProxy, "project/library_static")
        compareFiles(rootDir, files);
    }

    @Test
    fun test_static_lib2() {
        val projectDir = testProjects + "static_lib2"
        val settings = ArduinoCMakeListsTxtBuilder.loadProjectConfiguration(projectDir, null, applicationSettings)
        val expected = ArduinoApplicationSettingsProxy.of() as ArduinoProjectFileSettings

        expected.isLibrary = true
        expected.libraryType = "static"
        expected.projectName = projectDir.name
        expected.headers = arrayOf("static_lib2.h")
        expected.sources = arrayOf("static_lib2.cpp")
        expected.boardId = "robotControl"
        expected.cpuId = ""
        expected.programmerId = "buspirate"
        expected.port = "/dev/cu.usbserial-00000000"
        expected.isAddLibraryDirectory = true
        expected.libraryDirectories = arrayOf("")
        expected.isVerbose = false

        assertEquals(expected.asString("expected"), settings?.asString("expected"))

        val rootDir = createdProjects + expected.projectName
        val files = createFiles(expected.projectName, rootDir, expected as ArduinoApplicationSettingsProxy, "project/library_static")
        compareFiles(rootDir, files);
    }
}
