/*
 * Copyright (c) 2016-2023 Vladimir Schneider <vladimir.schneider@gmail.com>
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

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
import com.vladsch.clionarduinoplugin.Bundle;
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
        Content content = contentFactory.createContent(mainPanel, Bundle.message("toolwindow.serial-monitor.title"), false);
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
