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
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.ui.content.ContentManager;
import com.vladsch.clionarduinoplugin.Bundle;
import org.jetbrains.annotations.Nullable;

public class SerialMonitorToolWindow implements Disposable {
    Project myProject;
    ToolWindow myToolWindow;

    static final String TOOL_WINDOW_ID = Bundle.message("toolwindow.serial-monitor.id");

    SerialMonitorPanel mySerialMonitorPanel;

    public SerialMonitorToolWindow(Project project) {
        myProject = project;
    }

    @Nullable
    public SerialMonitorPanel getSerialMonitorPanel() {
        return mySerialMonitorPanel;
    }

    @Override
    public void dispose() {

    }

    public void activate() {
        if (myToolWindow != null) {
            myToolWindow.show(null);
        }
    }

    public ContentManager getContentManager() {
        return myToolWindow.getContentManager();
    }
}
