package com.vladsch.clionarduinoplugin.actions

import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.fileEditor.FileDocumentManager
import com.intellij.openapi.project.DumbAwareAction
import com.intellij.openapi.ui.Messages
import com.intellij.openapi.vfs.VirtualFileManager
import com.jetbrains.cidr.cpp.cmake.workspace.CMakeWorkspace
import com.vladsch.clionarduinoplugin.Bundle
import com.vladsch.clionarduinoplugin.generators.cmake.ArduinoCMakeListsTxtBuilder
import com.vladsch.clionarduinoplugin.resources.Strings
import com.vladsch.clionarduinoplugin.settings.ProjectBuildSettingsDialog
import com.vladsch.plugin.util.plus

class ChangeBuildSettingsAction : DumbAwareAction(Bundle.message("action.change-build-settings.label"), Bundle.message("actions.change-build-settings.description"), null) {

    override fun update(e: AnActionEvent) {
        val project = e.project
        var enabled = false
        if (project != null) {
            enabled = true
            //            val projectComponent = ArduinoProjectComponent.getInstance(project)
            //            if (projectComponent.isArduinoProject == true) {
            //                enabled = true
            //            } else if (!projectComponent.inReload.get()) {
            //                val workspace = CMakeWorkspace.getInstance(project)
            //                val projectDir = workspace.projectDir
            //                val settings = ArduinoCMakeListsTxtBuilder.loadProjectConfiguration(projectDir)
            //                projectComponent.isArduinoProject = settings != null
            //            }
        }
        e.presentation.isEnabled = enabled
    }

    override fun actionPerformed(e: AnActionEvent) {
        val project = e.project
        if (project != null) {
            val workspace = CMakeWorkspace.getInstance(project)
            val projectDir = workspace.projectDir

            val cMakeListsTxt = projectDir + Strings.CMAKE_LISTS_FILENAME
            val virtualFile = VirtualFileManager.getInstance().findFileByUrl("file://" + cMakeListsTxt.absolutePath)
            val document = if (virtualFile == null) null else FileDocumentManager.getInstance().getDocument(virtualFile)
            
            val settings = if (virtualFile == null) null else ArduinoCMakeListsTxtBuilder.loadProjectConfiguration(
                projectDir,
                document?.text
            ) 

            if (settings == null || virtualFile == null) {
                Messages.showErrorDialog(project, Bundle.message("action.change-build-settings.not-arduino-project.message"), Bundle.message("action.change-build-settings.not-arduino-project.title"))
                return
            }

            if (ProjectBuildSettingsDialog.showDialog(project, settings, virtualFile)) {

            }
        }
    }
}
