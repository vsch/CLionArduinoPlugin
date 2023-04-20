// Copyright (c) 2015-2023 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package com.vladsch.clionarduinoplugin.serial;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.StatusBar;
import com.intellij.openapi.wm.StatusBarWidget;
import com.intellij.openapi.wm.StatusBarWidgetFactory;
import com.vladsch.clionarduinoplugin.ArduinoSupportBundle;
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
        return ArduinoSupportBundle.getString("toolwindow.serial-monitor.title");
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
