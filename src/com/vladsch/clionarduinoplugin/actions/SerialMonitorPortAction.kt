package com.vladsch.clionarduinoplugin.actions

import com.intellij.openapi.actionSystem.ActionGroup
import com.intellij.openapi.actionSystem.ActionPlaces
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.Toggleable
import com.vladsch.clionarduinoplugin.serial.SerialPortManager
import com.vladsch.clionarduinoplugin.settings.ArduinoProjectSettings

class SerialMonitorPortAction(private val myPort: String) : AnAction(myPort), Toggleable {

    override fun actionPerformed(e: AnActionEvent) {
        val project = e.project
        if (project != null) {
            val projectSettings = ArduinoProjectSettings.getInstance(project)
            projectSettings.groupChanges {
                projectSettings.port = myPort
            }
        }
    }

    override fun update(e: AnActionEvent) {
        val project = e.project
        if (project != null) {
            val port = ArduinoProjectSettings.getInstance(project).port
            val presentation = e.presentation
            
            Toggleable.setSelected(presentation, port == myPort)
            
            if (ActionPlaces.isPopupPlace(e.place)) {
                //force to show check marks instead of toggled icons in context menu
                presentation.icon = null
            }
        }
    }

    companion object {
        fun createSerialPortsActionGroup(): ActionGroup {
            val serialPorts = SerialPortManager.getSerialPorts(true)
            val portActions = serialPorts.map { SerialMonitorPortAction(it) }

            val group = object : ActionGroup("Port", true) {
                override fun getChildren(e: AnActionEvent?): Array<AnAction> {
                    return portActions.toTypedArray()
                }
            }
            return group
        }
    }
}
