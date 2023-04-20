package com.vladsch.clionarduinoplugin.actions

import com.intellij.openapi.actionSystem.*
import com.vladsch.clionarduinoplugin.settings.ArduinoProjectSettings
import com.vladsch.plugins.simpleserialconnectorservice.SerialPortProfile

class SerialMonitorBaudRateAction(internal val myBaudRate: Int) : AnAction(Integer.toString(myBaudRate)), Toggleable {

    override fun actionPerformed(e: AnActionEvent) {
        val project = e.project
        if (project != null) {
            val projectSettings = ArduinoProjectSettings.getInstance(project)
            projectSettings.groupChanges {
                projectSettings.baudRate = myBaudRate
            }
        }
    }

    override fun update(e: AnActionEvent) {
        val project = e.project
        if (project != null) {
            val baudRate = ArduinoProjectSettings.getInstance(project).baudRate
            val presentation = e.presentation

            Toggleable.setSelected(presentation, baudRate == myBaudRate)
            if (ActionPlaces.isPopupPlace(e.place)) {
                //force to show check marks instead of toggled icons in context menu
                presentation.icon = null
            }
        }
    }

    companion object {

        fun createBaudRateActionGroup(): ActionGroup {
            val group = object : ActionGroup("Baud Rate", true) {
                override fun getChildren(e: AnActionEvent?): Array<AnAction> {
                    return arrayOf(
                        SerialMonitorBaudRateAction(SerialPortProfile.BaudRate.BAUDRATE_110.intValue),
                        SerialMonitorBaudRateAction(SerialPortProfile.BaudRate.BAUDRATE_300.intValue),
                        SerialMonitorBaudRateAction(SerialPortProfile.BaudRate.BAUDRATE_600.intValue),
                        SerialMonitorBaudRateAction(SerialPortProfile.BaudRate.BAUDRATE_1200.intValue),
                        SerialMonitorBaudRateAction(SerialPortProfile.BaudRate.BAUDRATE_2400.intValue),
                        SerialMonitorBaudRateAction(SerialPortProfile.BaudRate.BAUDRATE_4800.intValue),
                        SerialMonitorBaudRateAction(SerialPortProfile.BaudRate.BAUDRATE_9600.intValue),
                        SerialMonitorBaudRateAction(SerialPortProfile.BaudRate.BAUDRATE_14400.intValue),
                        SerialMonitorBaudRateAction(SerialPortProfile.BaudRate.BAUDRATE_19200.intValue),
                        SerialMonitorBaudRateAction(SerialPortProfile.BaudRate.BAUDRATE_38400.intValue),
                        SerialMonitorBaudRateAction(SerialPortProfile.BaudRate.BAUDRATE_57600.intValue),
                        SerialMonitorBaudRateAction(SerialPortProfile.BaudRate.BAUDRATE_115200.intValue),
                        SerialMonitorBaudRateAction(SerialPortProfile.BaudRate.BAUDRATE_128000.intValue),
                        SerialMonitorBaudRateAction(SerialPortProfile.BaudRate.BAUDRATE_230400.intValue),
                        SerialMonitorBaudRateAction(SerialPortProfile.BaudRate.BAUDRATE_256000.intValue),
                        SerialMonitorBaudRateAction(SerialPortProfile.BaudRate.BAUDRATE_460800.intValue),
                        SerialMonitorBaudRateAction(SerialPortProfile.BaudRate.BAUDRATE_512000.intValue),
                    )
                }
            }
            return group
        }
    }
}
