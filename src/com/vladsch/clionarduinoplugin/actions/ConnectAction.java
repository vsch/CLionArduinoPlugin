package com.vladsch.clionarduinoplugin.actions;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.Presentation;
import com.intellij.openapi.project.DumbAwareAction;
import com.intellij.openapi.project.Project;
import com.vladsch.clionarduinoplugin.Bundle;
import com.vladsch.clionarduinoplugin.serial.SerialProjectComponent;
import com.vladsch.clionarduinoplugin.settings.ArduinoProjectSettings;
import icons.PluginIcons;
import org.jetbrains.annotations.NotNull;

public class ConnectAction extends DumbAwareAction {
    
    public ConnectAction() {
        super(Bundle.message("action.connect.label"), Bundle.message("actions.connect.description"), PluginIcons.serial_port_disconnected);
    }
    
    @Override
    public void update(@NotNull final AnActionEvent e) {
        Project project = e.getProject();
        boolean enabled = false;
        Presentation presentation = e.getPresentation();
        if (project != null) {
            SerialProjectComponent serialProjectComponent = SerialProjectComponent.getInstance(project);
            if (serialProjectComponent.isPortConnected()) {
                presentation.setIcon(PluginIcons.serial_port);

                presentation.setText(Bundle.message("actions.connect.connected.2.description", serialProjectComponent.getConnectedPort(), "" + serialProjectComponent.getConnectedBaudRate()));
                //presentation.setDescription("Disconnect " + serialProjectComponent.getConnectedPort() + " @ " + serialProjectComponent.getConnectedBaudRate());
            } else {
                ArduinoProjectSettings projectSettings = ArduinoProjectSettings.getInstance(project);
                presentation.setIcon(PluginIcons.serial_port_disconnected);
                presentation.setText(Bundle.message("actions.connect.disconnected.2.description", projectSettings.getPort(), "" + projectSettings.getBaudRate()));
            }

            enabled = serialProjectComponent.canConnectPort();
        }

        presentation.setEnabled(enabled);
    }

    @Override
    public void actionPerformed(AnActionEvent e) {
        Project project = e.getProject();
        if (project != null) {
            SerialProjectComponent serialProjectComponent = SerialProjectComponent.getInstance(project);
            if (serialProjectComponent.isPortConnected()) {
                serialProjectComponent.disconnectPort();
            } else if (serialProjectComponent.canConnectPort()) {
                serialProjectComponent.connectPort();
            }
        }
    }
}
