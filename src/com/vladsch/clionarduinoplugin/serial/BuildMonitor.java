package com.vladsch.clionarduinoplugin.serial;

import com.intellij.openapi.Disposable;
import com.intellij.openapi.project.Project;
import com.intellij.util.messages.MessageBusConnection;
import com.jetbrains.cidr.cpp.cmake.model.CMakeConfiguration;
import com.jetbrains.cidr.execution.CidrBuildConfiguration;
import com.jetbrains.cidr.execution.build.CidrBuildEvent;
import com.jetbrains.cidr.execution.build.CidrBuildListener;
import com.jetbrains.cidr.execution.build.CidrBuildResult;
import org.jetbrains.annotations.NotNull;

public class BuildMonitor implements CidrBuildListener {
    final @NotNull Project myProject;
    final @NotNull BuildListener myListener;
    MessageBusConnection myConnection;

    public BuildMonitor(@NotNull final Project project, @NotNull final BuildListener listener) {
        myProject = project;
        myListener = listener;
    }

    public void projectOpened(Disposable parent) {
        myConnection = myProject.getMessageBus().connect(parent);
        myConnection.subscribe(CidrBuildListener.TOPIC, this);
    }

    public void projectClosed() {
        myConnection.disconnect();
        myConnection.dispose();
    }

    @Override
    public void beforeStarted(@NotNull final CidrBuildEvent buildEvent) {
        CidrBuildConfiguration configuration = buildEvent.getBuildConfiguration();
        String targetName = configuration.getName();
        if (configuration instanceof CMakeConfiguration) {
            CMakeConfiguration makeConfiguration = (CMakeConfiguration) configuration;
            targetName = makeConfiguration.getTarget().getName();
        }
        myListener.beforeBuildStarted(targetName);
    }

    @Override
    public void afterFinished(@NotNull final CidrBuildEvent buildEvent, @NotNull final CidrBuildResult result) {
        myListener.afterBuildFinished(result.getSucceeded());
    }
}
