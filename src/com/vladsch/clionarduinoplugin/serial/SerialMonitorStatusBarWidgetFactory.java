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

import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.StatusBar;
import com.intellij.openapi.wm.StatusBarWidget;
import com.intellij.openapi.wm.StatusBarWidgetFactory;
import com.vladsch.clionarduinoplugin.Bundle;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

class SerialMonitorStatusBarWidgetFactory implements StatusBarWidgetFactory {
    public SerialMonitorStatusBarWidgetFactory() {
    }

    @Override
    public @NonNls
    @NotNull String getId() {
        return StatusWidget.SERIAL_MONITOR_ID;
    }

    @Override
    public @Nls
    @NotNull String getDisplayName() {
        return Bundle.getString("toolwindow.serial-monitor.title");
    }

    @Override
    public boolean isAvailable(@NotNull Project project) {
        // RELEASE: see if arduino project, only then it is available
        return true;
    }

    @Override
    public @NotNull StatusBarWidget createWidget(@NotNull Project project) {
        SerialProjectComponent serialProjectComponent = SerialProjectComponent.getInstance(project);
        return new StatusWidget(serialProjectComponent);
    }

    @Override
    public void disposeWidget(@NotNull StatusBarWidget widget) {
        
    }

    @Override
    public boolean canBeEnabledOn(@NotNull StatusBar bar) {
        return true;
    }
}
