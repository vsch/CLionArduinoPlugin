// Copyright (c) 2015-2023 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package com.vladsch.clionarduinoplugin.serial;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.project.DumbAware;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.SimpleToolWindowPanel;
import com.intellij.openapi.util.Disposer;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentFactory;
import com.vladsch.clionarduinoplugin.ArduinoSupportBundle;
import org.jetbrains.annotations.NotNull;

class SerialMonitorToolWindowFactory implements ToolWindowFactory, DumbAware {
    public SerialMonitorToolWindowFactory() {

    }

    @Override
    public void createToolWindowContent(@NotNull Project project, @NotNull ToolWindow window) {
        SerialProjectComponent serialProjectComponent = SerialProjectComponent.getInstance(project);

        serialProjectComponent.mySerialMonitorPanel = new SerialMonitorPanel(project);
        Disposer.register(serialProjectComponent, serialProjectComponent.mySerialMonitorPanel);

        SimpleToolWindowPanel mainPanel = new SimpleToolWindowPanel(false, true);

        mainPanel.add(serialProjectComponent.mySerialMonitorPanel.getComponent());

        ContentFactory contentFactory = ApplicationManager.getApplication().getService(ContentFactory.class);
        Content content = contentFactory.createContent(mainPanel, ArduinoSupportBundle.message("toolwindow.serial-monitor.title"), false);
        serialProjectComponent.myToolWindow = window;
        window.getContentManager().addContent(content);
    }

    @Override
    public boolean isApplicable(@NotNull Project project) {
        return true;
    }

    @Override
    public void init(@NotNull ToolWindow toolWindow) {

    }

    @Override
    public boolean shouldBeAvailable(@NotNull Project project) {
        return true;
    }
}
