package com.vladsch.clionarduinoplugin.components

import com.intellij.openapi.Disposable
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFileListener
import com.intellij.util.messages.MessageBusConnection
import com.jetbrains.cidr.cpp.cmake.workspace.CMakeWorkspaceListener
import java.util.concurrent.atomic.AtomicBoolean

class ArduinoProjectComponent(project: Project) :
    VirtualFileListener, CMakeWorkspaceListener, Disposable {

    companion object {

        fun getInstance(project: Project): ArduinoProjectComponent {
            return project.getService(ArduinoProjectComponent::class.java)
        }
    }

    private val myBusConnection: MessageBusConnection = project.messageBus.connect(this)

    init {
        myBusConnection.subscribe(CMakeWorkspaceListener.TOPIC, this)
    }

    @Suppress("MemberVisibilityCanBePrivate")
    var inReload: AtomicBoolean = AtomicBoolean(false)
        private set

    @Suppress("MemberVisibilityCanBePrivate")
    var isArduinoProject: Boolean? = null
        private set

    override fun dispose() {
        myBusConnection.disconnect()
        myBusConnection.dispose()
    }

    override fun beforeApplying() {
        inReload.set(true)
        isArduinoProject = null
    }

    override fun generationStarted() {
        inReload.set(true)
        isArduinoProject = null
    }

    override fun filesRefreshedAfterGeneration() {
    }

    override fun generationFinished() {
    }

    override fun reloadingStarted() {
        inReload.set(true)
        isArduinoProject = null
        //        alarm.cancelAllRequests()
    }

    override fun reloadingFinished(canceled: Boolean) {
        inReload.set(false)
        isArduinoProject = null
        //        alarm.cancelAllRequests()
    }

    override fun reloadingRescheduled() {
        inReload.set(true)
        isArduinoProject = null
        //        alarm.cancelAllRequests()
    }
}
