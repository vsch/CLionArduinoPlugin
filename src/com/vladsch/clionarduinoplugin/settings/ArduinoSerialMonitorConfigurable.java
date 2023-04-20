// Copyright (c) 2015-2023 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package com.vladsch.clionarduinoplugin.settings;

import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.options.SearchableConfigurable;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Disposer;
import com.vladsch.clionarduinoplugin.ArduinoSupportBundle;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.JComponent;

public class ArduinoSerialMonitorConfigurable implements SearchableConfigurable {
    @NotNull final protected Project myProject;
    @Nullable private SerialMonitorSettingsForm myForm = null;
    @Nullable final private ArduinoProjectSettings mySettings;

    private ArduinoSerialMonitorConfigurable(@NotNull Project project) {
        this.myProject = project;
        this.mySettings = ArduinoProjectSettings.getInstance(project);
    }

    @NotNull
    @Override
    public String getId() {
        return "com.vladsch.clionarduinoplugin.components.ProjectConfigurable";
    }

    @Nullable
    @Override
    public Runnable enableSearch(String option) {
        //final ProjectSettingsForm form = getForm();
        //final Runnable runnable = form.enableSearch(option);
        //if (runnable != null) {
        //    return new Runnable() {
        //        @Override
        //        public void run() {
        //            form.getComponent().show();
        //            runnable.run();
        //        }
        //    };
        //}
        return null;
    }

    @Nls
    @Override
    public String getDisplayName() {
        return ArduinoSupportBundle.message("plugin.project-configurable.name");
    }

    @Nullable
    @Override
    public String getHelpTopic() {
        return null; //"com.vladsch.markdown.navigator.settings.stylesheet";
    }

    @NotNull
    @Override
    public JComponent createComponent() {
        return getForm().getComponent();
    }

    @NotNull
    public SerialMonitorSettingsForm getForm() {
        if (myForm == null) {
            myForm = new SerialMonitorSettingsForm(mySettings, true);
        }
        return myForm;
    }

    @Override
    public boolean isModified() {
        return getForm().isModified(mySettings);
    }

    @Override
    public void apply() throws ConfigurationException {
        getForm().apply(mySettings);
    }

    @Override
    public void reset() {
        getForm().reset(mySettings);
    }

    @Override
    public void disposeUIResources() {
        if (myForm != null) {
            Disposer.dispose(myForm);
            myForm = null;
        }
    }
}
