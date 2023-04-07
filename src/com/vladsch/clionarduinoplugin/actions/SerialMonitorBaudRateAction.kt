package com.vladsch.clionarduinoplugin.actions

import com.intellij.openapi.actionSystem.*
import com.vladsch.clionarduinoplugin.serial.SerialProjectComponent
import com.vladsch.clionarduinoplugin.settings.ArduinoProjectSettings
import jssc.SerialPort

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
            // TODO: add to string properties
            val group = object : ActionGroup("Baud rate", true) {
                override fun getChildren(e: AnActionEvent?): Array<AnAction> {
                    return arrayOf(
                            SerialMonitorBaudRateAction(SerialPort.BAUDRATE_110),
                            SerialMonitorBaudRateAction(SerialPort.BAUDRATE_300),
                            SerialMonitorBaudRateAction(SerialPort.BAUDRATE_600),
                            SerialMonitorBaudRateAction(SerialPort.BAUDRATE_1200),
                            SerialMonitorBaudRateAction(SerialPort.BAUDRATE_4800),
                            SerialMonitorBaudRateAction(SerialPort.BAUDRATE_9600),
                            SerialMonitorBaudRateAction(SerialPort.BAUDRATE_14400),
                            SerialMonitorBaudRateAction(SerialPort.BAUDRATE_19200),
                            SerialMonitorBaudRateAction(SerialPort.BAUDRATE_38400),
                            SerialMonitorBaudRateAction(SerialPort.BAUDRATE_57600),
                            SerialMonitorBaudRateAction(SerialPort.BAUDRATE_115200),
                            SerialMonitorBaudRateAction(SerialPort.BAUDRATE_128000),
                            SerialMonitorBaudRateAction(SerialPort.BAUDRATE_256000)
                    )
                }
            }
            return group
        }
    }
}
