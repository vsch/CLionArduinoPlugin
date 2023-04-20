package com.vladsch.clionarduinoplugin.actions

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.actionSystem.LangDataKeys
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.fileEditor.FileEditorManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.Messages
import com.intellij.openapi.vfs.VirtualFile
import com.vladsch.clionarduinoplugin.ArduinoSupportBundle
import com.vladsch.clionarduinoplugin.generators.ArduinoProjectGenerator
import com.vladsch.clionarduinoplugin.resources.TemplateResolver
import com.vladsch.clionarduinoplugin.settings.ArduinoApplicationSettings
import com.vladsch.clionarduinoplugin.util.FileCreator
import com.vladsch.plugin.util.StudiedWord
import icons.PluginIcons
import java.io.IOException

class NewSketchFile : AnAction() {
    override fun actionPerformed(e: AnActionEvent) {
        val project = e.getData(CommonDataKeys.PROJECT)
        val view = e.getData(LangDataKeys.IDE_VIEW)

        if (project != null && view != null) {
            val directory = view.orChooseDirectory ?: return
            val directoryVirtualFile = directory.virtualFile

            mainLoop@
            while (true) {
                val filename: String? = getDesiredFilename(project) ?: return

                if (filename!!.isEmpty()) { //no name entered
                    return
                    //                    showEmptyFilenameError(project)
                    //                    continue
                }

                val projectName = project.name
                val word = StudiedWord(projectName, StudiedWord.DOT or StudiedWord.DASH or StudiedWord.UNDER)
                val snakeName = word.makeSnakeCase()
                val camelName = word.makeProperCamelCase()
                val pascalName = word.makePascalCase()
                val templateVars = mutableMapOf(
                        "PROJECT_NAME" to snakeName.toUpperCase(),
                        "project_name" to snakeName.toLowerCase(),
                        "ProjectName" to pascalName,
                        "projectName" to camelName,
                        "sketch_name" to filename
                )

                val templateDir = ArduinoApplicationSettings.getInstance().templateDir
                val templates = TemplateResolver.getTemplates("sketch", templateDir)
                val resolvedTemplates = TemplateResolver.resolveTemplates(templates, templateVars)
                val toDelete = ArrayList<String>()

                for ((key, _) in resolvedTemplates) {
                    val existingFile = directoryVirtualFile.findChild(key)
                    if (existingFile != null) {
                        val overwriteChoice = getOverwriteChoice(project, key) //ask to overwrite file
                        when (overwriteChoice) {
                            Messages.YES -> {
                                toDelete.add(key)
                            }
                            Messages.NO -> {
                                continue@mainLoop
                            }
                            Messages.CANCEL -> return
                        }
                    }
                }

                // now delete all at the same time
                for (key in toDelete) {
                    val existingFile = directoryVirtualFile.findChild(key)
                    if (existingFile != null) {
                        deleteVirtualFile(existingFile)
                    }
                }

                val sketches = FileCreator.createFiles(project, directoryVirtualFile, resolvedTemplates)
                val editorManager = FileEditorManager.getInstance(project)
                var i = 0
                var createdFiles = false
                for ((_, sketch) in sketches) {
                    i++
                    if (sketch == null) continue
                    createdFiles = true

                    //ArduinoSketchFileCreator.addFileToCMakeLists(project, sketch); //not sure if i need to do this or not
                    editorManager.openFile(sketch, true, i == sketches.size); //open in editor
                }

                if (createdFiles) {
                    // reload CMakeLists.txt
                    // vsch: Need to reload the CMakeList.txt to generate build files, first time generation is incorrect
                    ArduinoProjectGenerator.reloadCMakeLists(project)
                }
                break
            }
        }
    }

    private fun showEmptyFilenameError(project: Project) {
        Messages.showErrorDialog(project, ArduinoSupportBundle.message("action.new-sketch.empty-name.message"), ArduinoSupportBundle.message("action.new-sketch.empty-name.title"))
    }

    private fun getOverwriteChoice(project: Project, filename: String): Int {
        return Messages
                .showYesNoCancelDialog(project, ArduinoSupportBundle.message("action.new-sketch.overwrite.message", filename), ArduinoSupportBundle.message("action.new-sketch.overwrite.title"), PluginIcons.arduino_logo)
    }

    private fun getDesiredFilename(project: Project): String? {
        return Messages
                .showInputDialog(project, ArduinoSupportBundle.message("action.new-sketch.enter-filename.message"), ArduinoSupportBundle.message("action.new-sketch.enter-filename.message.title"), PluginIcons.arduino_logo)
    }

    private fun deleteVirtualFile(virtualFile: VirtualFile?) {
        if (virtualFile == null) {
            return
        }
        ApplicationManager.getApplication().runWriteAction(object : Runnable {
            override fun run() {
                try {
                    virtualFile.delete(this)
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
        })
    }
}
