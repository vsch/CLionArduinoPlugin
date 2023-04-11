/*
        based on CLion CPP Executable/Library Project Generators
        and
        CLionArduinoPlugin new project wizard
 */

package com.vladsch.clionarduinoplugin.generators

import com.intellij.facet.ui.ValidationResult
import com.intellij.ide.util.PsiNavigationSupport
import com.intellij.openapi.Disposable
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.module.Module
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.Messages
import com.intellij.openapi.ui.TextFieldWithBrowseButton
import com.intellij.openapi.ui.ValidationInfo
import com.intellij.openapi.util.ThrowableComputable
import com.intellij.openapi.util.io.FileUtil
import com.intellij.openapi.util.text.StringUtil
import com.intellij.openapi.vfs.VfsUtil
import com.intellij.openapi.vfs.VfsUtilCore
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.platform.GeneratorPeerImpl
import com.intellij.platform.ProjectGeneratorPeer
import com.intellij.uiDesigner.core.GridConstraints
import com.intellij.uiDesigner.core.GridLayoutManager
import com.intellij.util.messages.MessageBusConnection
import com.jetbrains.cidr.cpp.CPPLog
import com.jetbrains.cidr.cpp.cmake.projectWizard.generators.CMakeProjectGenerator
import com.jetbrains.cidr.cpp.cmake.projectWizard.generators.settings.CMakeProjectSettings
import com.jetbrains.cidr.cpp.cmake.projectWizard.generators.settings.ui.CMakeSettingsPanel
import com.jetbrains.cidr.cpp.cmake.workspace.CMakeWorkspace
import com.jetbrains.cidr.cpp.cmake.workspace.CMakeWorkspaceListener
import com.vladsch.clionarduinoplugin.Bundle
import com.vladsch.clionarduinoplugin.generators.cmake.ArduinoCMakeListsTxtBuilder
import com.vladsch.clionarduinoplugin.resources.ArduinoToolchainFiles
import com.vladsch.clionarduinoplugin.resources.Strings
import com.vladsch.clionarduinoplugin.resources.TemplateResolver
import com.vladsch.clionarduinoplugin.settings.ArduinoApplicationSettings
import com.vladsch.clionarduinoplugin.settings.ArduinoApplicationSettingsProxy
import com.vladsch.clionarduinoplugin.settings.NewProjectSettingsForm
import com.vladsch.clionarduinoplugin.util.ApplicationSettingsListener
import com.vladsch.plugin.util.StudiedWord
import com.vladsch.plugin.util.plus
import icons.PluginIcons
import org.jetbrains.annotations.Nls
import java.awt.BorderLayout
import java.io.File
import java.io.IOException
import java.lang.ref.WeakReference
import java.util.*
import javax.swing.Icon
import javax.swing.JComponent
import javax.swing.JPanel
import javax.swing.JTextField

abstract class ArduinoProjectGenerator(isLibrary: Boolean) : CMakeProjectGenerator(), Disposable {

    protected val mySettings: ArduinoApplicationSettingsProxy =
        ArduinoApplicationSettingsProxy.wrap(ArduinoApplicationSettings.getInstance().state, isLibrary)
    private val myListeners = HashSet<WeakReference<GeneratorFailedValidationListener>>()

    override fun dispose() {
    }

    override fun getGroupName(): String {
        return Bundle.message("new-project.group.name")
    }

    @Nls
    abstract override fun getName(): String

    override fun getLogo(): Icon? {
        return PluginIcons.arduino_logo
    }

    override fun getLanguageVersion(): String {
        return mySettings.applicationSettings.languageVersionName
    }

    override fun getLibraryType(): String? {
        return mySettings.libraryType
    }

    override fun setLanguageVersion(languageVersion: String) {
        mySettings.applicationSettings.languageVersionName = languageVersion
    }

    override fun setLibraryType(libraryType: String?) {
        mySettings.libraryType = libraryType ?: ""
    }

    override fun getLanguageVersions(): Array<String> {
        return ArduinoApplicationSettings.LANGUAGE_VERSIONS
    }

    // not used
    override fun getCMakeFileContent(projectName: String): String {
        return ""
    }

    // not used
    override fun createSourceFiles(p0: String, p1: VirtualFile): Array<VirtualFile> {
        return arrayOf()
    }

    abstract val templateType: String
    abstract fun templateVars(name: String, pascalName: String, camelName: String, snakeName: String): Map<String, String>

    @Throws(IOException::class)
    private fun createFiles(projectName: String, rootDir: VirtualFile): CreatedFilesHolder {
        val name = FileUtil.sanitizeFileName(projectName)
        val word = StudiedWord(name, StudiedWord.DOT or StudiedWord.DASH or StudiedWord.UNDER)
        val snakeName = word.makeSnakeCase()
        val camelName = word.makeProperCamelCase()
        val pascalName = word.makePascalCase()
        val templateVars = mutableMapOf(
            "PROJECT_NAME" to snakeName.toUpperCase(),
            "project_name" to snakeName.toLowerCase(),
            "ProjectName" to pascalName,
            //                "COMMENT_UNUSED" to if (mySettings.applicationSettings.isCommentUnusedSettings) "true" else "false",
            "projectName" to camelName
        )

        templateVars.putAll(
            templateVars(name, pascalName, camelName, snakeName).toMutableMap()
        )

        val templateDir = mySettings.applicationSettings.templateDir
        val templates = TemplateResolver.getTemplates(templateType, templateDir)
        val resolvedTemplates = TemplateResolver.resolveTemplates(templates, templateVars)

        val nonSourceFiles = ArrayList<VirtualFile>()
        val sourceFiles = resolvedTemplates
            .map { (name, content) ->
                if (name != Strings.CMAKE_LISTS_FILENAME) createProjectFileWithContent(rootDir, name, content) else null
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
        val cMakeFileContent = ArduinoCMakeListsTxtBuilder.getCMakeFileContent(cMakeFileTemplate, name, mySettings, fileList, false)
        val cMakeFile = createProjectFileWithContent(rootDir, Strings.CMAKE_LISTS_FILENAME, cMakeFileContent)

        val extraFiles: Array<VirtualFile> =
            if (mySettings.applicationSettings.isBundledToolchainTxt)
                ArduinoToolchainFiles.copyToDirectory(VfsUtil.findFileByIoFile(VfsUtilCore.virtualToIoFile(rootDir), false))
            else emptyArray()

        if (mySettings.isAddLibraryDirectory) {
            val libDir = File(rootDir.path) + mySettings.applicationSettings.libraryDirectory
            if (!libDir.exists() && libDir.canonicalPath != rootDir.canonicalPath) {
                libDir.mkdirs()
            }
        }
        return CreatedFilesHolder(cMakeFile, sourceFiles, nonSourceFiles.toTypedArray(), extraFiles)
    }

    override fun generateProject(project: Project, baseDir: VirtualFile, settings: CMakeProjectSettings, module: Module) {
        val createdFilesHolder: CreatedFilesHolder
        try {
            createdFilesHolder = ApplicationManager.getApplication().runWriteAction(ThrowableComputable<CreatedFilesHolder, IOException> {
                createFiles(project.name, baseDir)
            })
        } catch (e: IOException) {
            handleErrorDuringGeneration(project, e)
            return
        }

        // vsch: in 2020.1 reformat with as not CPP causes the sketch #include to split off onto the next line
        // vsch: in 2021.1 API has changed. So for compatibility with later IDEs, reformat will have to be done manually
        // CLionProjectWizardUtils.reformatProjectFiles(project, createdFilesHolder.cMakeFile, formatSourceFilesAsCpp(), *createdFilesHolder.sourceFiles)
        CMakeWorkspace.getInstance(project).selectProjectDir(VfsUtilCore.virtualToIoFile(baseDir))

        if (!ApplicationManager.getApplication().isHeadlessEnvironment) {
            PsiNavigationSupport.getInstance().createNavigatable(project, createdFilesHolder.cMakeFile, -1).navigate(false)
            Arrays.asList(*createdFilesHolder.nonSourceFiles)
                .forEach { file -> PsiNavigationSupport.getInstance().createNavigatable(project, file, -1).navigate(true) }
            Arrays.asList(*createdFilesHolder.sourceFiles)
                .forEach { file -> PsiNavigationSupport.getInstance().createNavigatable(project, file, -1).navigate(true) }
        }

        // vsch: Need to reload the CMakeList.txt to generate build files, first time generation is incorrect
        val workspace = CMakeWorkspace.getInstance(project)
        val busConnection = project.messageBus.connect()
        busConnection.subscribe(CMakeWorkspaceListener.TOPIC, MyCMakeWorkspaceListener(busConnection, workspace))
    }

    override fun formatSourceFilesAsCpp(): Boolean {
        return true
    }

    override fun validate(baseDirPath: String): ValidationResult {
        if (StringUtil.isEmptyOrSpaces(baseDirPath)) {
            return ValidationResult("Enter project location")
        }
        val baseDir = File(baseDirPath)
        if (!baseDir.isAbsolute) {
            return ValidationResult("Project location path should be absolute")
        } else if (baseDir.exists() && !baseDir.canWrite()) {
            return ValidationResult(String.format("Directory '%s' is not writable.\nPlease choose another directory.", baseDirPath))
        } else {
            // validate other fields, but only if the location text field was found so we can trigger another validation
            val settings = mySettings.applicationSettings
            val result = validateOptions(settings)
            if (result != null) return filterFailure(result)
        }
        return super.validate(baseDirPath)
        //return ValidationResult.OK;
    }

    private fun filterFailure(result: ValidationResult): ValidationResult {
        val canFail = fireValidationFailed()
        return if (canFail) result else ValidationResult.OK
        // here we have a problem, we'll use defaults and hope for the best
    }

    private class MyCMakeWorkspaceListener(private val myBusConnection: MessageBusConnection, private val myWorkspace: CMakeWorkspace) :
        CMakeWorkspaceListener {

        override fun reloadingFinished(canceled: Boolean) {
            myBusConnection.disconnect()

            if (!canceled) {
                ApplicationManager.getApplication().invokeLater {
                    // force reload after the first generation cycle is complete
                    myWorkspace.selectProjectDir(myWorkspace.projectDir)
                }
            }
        }
    }

    override fun handleErrorDuringGeneration(project: Project, e: Exception) {
        Messages.showErrorDialog(project, "Cannot create a new project: " + e.message, "New Project")
        CPPLog.LOG.info(e)
    }

    override fun createPeer(): ProjectGeneratorPeer<CMakeProjectSettings> {
        return GeneratorPeerImpl(mySettings.applicationSettings, settingsPanel)
    }

    override fun getSettingsPanel(): JComponent {
        return ArduinoNewProjectSettingsPanel(mySettings, this)
    }

    internal interface GeneratorFailedValidationListener {

        fun onValidationFailed(): Boolean
    }

    private fun addValidationListener(listener: GeneratorFailedValidationListener) {
        myListeners.add(WeakReference(listener))
    }

    private fun fireValidationFailed(): Boolean {
        var canFail = false
        val invalidListeners = ArrayList<WeakReference<GeneratorFailedValidationListener>>(myListeners.size)
        for (ref in myListeners) {
            val listener = ref.get()
            if (listener != null) {
                if (listener.onValidationFailed()) {
                    canFail = true
                } else {
                    invalidListeners.add(ref)
                }
            } else {
                invalidListeners.add(ref)
            }
        }

        myListeners.removeAll(invalidListeners)
        return canFail
    }

    private class ArduinoNewProjectSettingsPanel(val settings: ArduinoApplicationSettingsProxy, projectGenerator: ArduinoProjectGenerator) :
        CMakeSettingsPanel(projectGenerator), ApplicationSettingsListener, GeneratorFailedValidationListener, Disposable {

        private val myPanel: JPanel
        private var myHaveFailed = false
        private val myNewProjectSettingsForm = NewProjectSettingsForm(settings, true, false)

        private //.getParent();
        val locationTextField: JTextField?
            get() {
                return try {
                    val basePanel = myPanel.parent.parent.parent as JPanel
                    findLocationTextField(basePanel)
                } catch (ignored: Throwable) {
                    null
                }
            }

        init {

            layout = BorderLayout()

            val layoutManager = GridLayoutManager(1, 1)
            myPanel = JPanel(layoutManager)

            val constraints = GridConstraints()
            constraints.row = 0
            constraints.column = 0
            constraints.anchor = 8
            constraints.fill = GridConstraints.FILL_HORIZONTAL
            myPanel.add(myNewProjectSettingsForm.component, constraints)

            add(myPanel, "Center")

            ApplicationManager.getApplication().messageBus.connect(this).subscribe(ApplicationSettingsListener.TOPIC, this)
            projectGenerator.addValidationListener(this)
        }

        override fun onValidationFailed(): Boolean {
            myHaveFailed = locationTextField != null
            return myHaveFailed
        }

        override fun onSettingsChanged(settings: ArduinoApplicationSettings) {
            if (settings === myNewProjectSettingsForm.settings) {
                if (myHaveFailed) {
                    myHaveFailed = false
                    val textField = locationTextField
                    if (textField != null) {
                        textField.text = textField.text
                    }
                }
            }
        }

        // vsch: kludge: since this class is not a step in the wizard process it cannot trigger re-validation,
        // or at least I wasn't able to figure out how to do it. Workaround: find the location text field
        // and set it to itself to trigger validation to clear the error
        private fun findLocationTextField(jcomp: JComponent): JTextField? {
            var field: JTextField?
            val iMax = jcomp.componentCount

            for (i in 0 until iMax) {
                val comp = jcomp.getComponent(i)
                if (comp is JComponent) {
                    if (jcomp is TextFieldWithBrowseButton) {
                        field = jcomp.textField
                        return field
                    }
                    field = findLocationTextField(comp)
                    if (field != null) return field
                }
            }
            return null
        }

        //// show JComponent hierarchy
        //protected void showChildTree(JComponent jcomp, String lvl) {
        //    int iMax = jcomp.getComponentCount();
        //    for (int i = 0; i < iMax; i++) {
        //        Component comp = jcomp.getComponent(i);
        //        if (comp instanceof JComponent) {
        //            System.out.println(String.format("Component at %s.%d - %s[%d] listeners %d", lvl, i, jcomp.getClass().getSimpleName(), jcomp.getComponentCount(), jcomp.getListeners(ActionListener.class).length));
        //            if (jcomp.getClass().getSimpleName().equals("TextFieldWithBrowseButton")) {
        //                boolean isTF = jcomp instanceof TextFieldWithBrowseButton;
        //                JTextField field = ((TextFieldWithBrowseButton) jcomp).getTextField();
        //                String text = field.getText();
        //                field.setText(text);
        //            }
        //            showChildTree((JComponent) comp, lvl + "." + i);
        //        }
        //    }
        //}

        override fun dispose() {
        }
    }

    protected class CreatedFilesHolder constructor(
        val cMakeFile: VirtualFile,
        val sourceFiles: Array<VirtualFile>,
        val nonSourceFiles: Array<VirtualFile>,
        val extraFiles: Array<VirtualFile>
    )

    companion object {

        fun validateOptions(settings: ArduinoApplicationSettings): ValidationResult? {
            val result = validateOptionsInfo(settings, null)
            if (result != null) {
                return ValidationResult(result.message)
            }
            return null
        }

        fun validateOptionsInfo(settings: ArduinoApplicationSettings, form: NewProjectSettingsForm?): ValidationInfo? {
            if (settings.boardId.isEmpty()) {
                return ValidationInfo(
                    Bundle.message("new-project.no-board"),
                    form?.getErrorComponent(NewProjectSettingsForm.ErrorComp.BOARD)
                )
            }

            if (settings.getBoardCpuNames(settings.boardName).isNotEmpty() && settings.cpuId.isEmpty()) {
                return ValidationInfo(
                    Bundle.message("new-project.1.no-cpu", settings.cpuLabel, settings.boardName),
                    form?.getErrorComponent(NewProjectSettingsForm.ErrorComp.CPU)
                )
            }

            if (settings.isAddLibraryDirectory) {
                for (dir in settings.libraryDirectories) {
                    if (File(dir).isAbsolute) {
                        return ValidationInfo(
                            Bundle.message("new-project.0.absolute-lib-dir", dir),
                            form?.getErrorComponent(NewProjectSettingsForm.ErrorComp.LIB_DIR)
                        )
                    }
                }
            }
            return null
        }

        fun reloadCMakeLists(project: Project) {
            val workspace = CMakeWorkspace.getInstance(project)
            ApplicationManager.getApplication().invokeLater {
                // force reload
                workspace.selectProjectDir(workspace.projectDir)
            }
        }
    }
}
