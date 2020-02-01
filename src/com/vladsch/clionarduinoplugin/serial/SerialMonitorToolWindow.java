/*
 * Copyright (c) 2016-2018 Vladimir Schneider <vladimir.schneider@gmail.com>
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

import com.intellij.openapi.Disposable;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.SimpleToolWindowPanel;
import com.intellij.openapi.util.Disposer;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowAnchor;
import com.intellij.openapi.wm.ToolWindowManager;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentFactory;
import com.intellij.ui.content.ContentManager;
import com.vladsch.clionarduinoplugin.Bundle;
import icons.PluginIcons;

import java.util.HashMap;

public class SerialMonitorToolWindow implements Disposable {
    private final ToolWindow toolWindow;

    private static final String TOOL_WINDOW_ID = Bundle.message("toolwindow.serial-monitor.id");
    private static final String SERIAL_MONITOR_ID = "SERIAL_MONITOR_ID";

    private final Project project;
    private final SerialMonitorPanel mySerialMonitorPanel;
    private final HashMap<String, Content> myToolWindowContentMap = new HashMap<>();

    public SerialMonitorToolWindow(Project project) {
        this.project = project;
        toolWindow = ToolWindowManager.getInstance(project).registerToolWindow(TOOL_WINDOW_ID, false, ToolWindowAnchor.BOTTOM, this, true);
        toolWindow.setIcon(PluginIcons.serial_port_tool);

        mySerialMonitorPanel = new SerialMonitorPanel(project);
        Disposer.register(this, mySerialMonitorPanel);
        Disposer.register(project, this);

        //JPanel mainPanel = new JPanel(new BorderLayout());
        //mainPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
        SimpleToolWindowPanel mainPanel = new SimpleToolWindowPanel(false, true);

        mainPanel.add(mySerialMonitorPanel.getComponent());

        ContentFactory contentFactory = ContentFactory.SERVICE.getInstance();
        Content content = contentFactory.createContent(mainPanel, Bundle.message("toolwindow.serial-monitor.title"), false);
        myToolWindowContentMap.put(SERIAL_MONITOR_ID, content);
        toolWindow.getContentManager().addContent(content);
    }

    public void unregisterToolWindow() {
        ToolWindowManager toolWindowManager = ToolWindowManager.getInstance(project);
        toolWindowManager.unregisterToolWindow(TOOL_WINDOW_ID);
    }

    public SerialMonitorPanel getSerialMonitorPanel() {
        return mySerialMonitorPanel;
    }

    @Override
    public void dispose() {

    }

    public void activate() {
        if (toolWindow != null) {
            toolWindow.show(null);
        }
    }

    public ContentManager getContentManager() {
        return toolWindow.getContentManager();
    }
}
