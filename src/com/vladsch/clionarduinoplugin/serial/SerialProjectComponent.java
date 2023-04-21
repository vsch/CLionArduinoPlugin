package com.vladsch.clionarduinoplugin.serial;

import com.intellij.execution.ui.ConsoleViewContentType;
import com.intellij.openapi.Disposable;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowManager;
import com.intellij.openapi.wm.impl.ToolWindowManagerImpl;
import com.vladsch.clionarduinoplugin.components.ArduinoApplicationComponent;
import com.vladsch.clionarduinoplugin.settings.ArduinoApplicationSettings;
import com.vladsch.clionarduinoplugin.settings.ArduinoProjectSettings;
import com.vladsch.clionarduinoplugin.util.ProjectSettingsListener;
import com.vladsch.plugins.simpleserialconnectorservice.JsscSerialService;
import com.vladsch.plugins.simpleserialconnectorservice.SerialMonitorException;
import com.vladsch.plugins.simpleserialconnectorservice.SerialPortOwnerAccess;
import com.vladsch.plugins.simpleserialconnectorservice.SerialPortProfile;
import com.vladsch.plugins.simpleserialconnectorservice.SerialPortProfile.NewLine;
import com.vladsch.plugins.simpleserialconnectorservice.SerialPortProfile.Parity;
import com.vladsch.plugins.simpleserialconnectorservice.SerialPortProfile.StopBits;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import static com.vladsch.clionarduinoplugin.serial.SerialMonitorPanel.ALT_CONNECTION_STATUS;
import static com.vladsch.plugins.simpleserialconnectorservice.SerialConnectionListener.PortStatus.DISCONNECTED;

public class SerialProjectComponent implements BuildListener, Disposable, ProjectSettingsListener {
    final Project myProject;
    @SuppressWarnings("unused")
    private ArduinoApplicationComponent myApplicationComponent;
    final @NotNull ArduinoProjectSettings myProjectSettings;
    boolean myIsBuilding = false;
    boolean myWasConnected = false;
    @Nullable SerialPortOwnerAccess myPortOwnerAccess = null;
    int myConnectedBaudRate;
    private JsscSerialService mySerialPortService;
    ToolWindow myToolWindow;
    SerialMonitorPanel mySerialMonitorPanel;
    BuildMonitor myBuildMonitor;

    final Set<SerialPortListener> myListeners = Collections.synchronizedSet(new HashSet<>());
    final Set<SerialPortStatusListener> myStatusListeners = Collections.synchronizedSet(new HashSet<>());

    public SerialProjectComponent(final Project project) {
        myProject = project;
        myApplicationComponent = ArduinoApplicationComponent.getInstance();
        myProjectSettings = ArduinoProjectSettings.getInstance(myProject);

        //noinspection ThisEscapedInObjectConstruction
        myBuildMonitor = new BuildMonitor(myProject, this);
        //noinspection ThisEscapedInObjectConstruction
        myBuildMonitor.projectOpened(this);
        try {
            if (ALT_CONNECTION_STATUS == null) {
                //noinspection AssignmentToStaticFieldFromInstanceMethod
                ALT_CONNECTION_STATUS = ConsoleViewContentType.LOG_VERBOSE_OUTPUT;
            }
        } catch (Throwable ignored) {

        }

        //noinspection ThisEscapedInObjectConstruction
        project.getMessageBus().connect(this).subscribe(ProjectSettingsListener.TOPIC, this);

        mySerialPortService = JsscSerialService.getInstance();
    }

    @Override
    public void onSettingsChanged() {
        if (isPortConnected() && (myConnectedBaudRate != myProjectSettings.getBaudRate()
                || myPortOwnerAccess != null && !myPortOwnerAccess.getPortName().equals(myProjectSettings.getPort()))
        ) {
            disconnectAndRest();
            connectPort(myProjectSettings.getPort(), myProjectSettings.getBaudRate());
        }
    }

    public static SerialProjectComponent getInstance(@NotNull Project project) {
        return project.getService(SerialProjectComponent.class);
    }

    @Override
    public void dispose() {
        if (mySerialMonitorPanel != null) {
            mySerialMonitorPanel.dispose();
            mySerialMonitorPanel = null;
            myApplicationComponent = null;

            myPortOwnerAccess = null;
            mySerialPortService = null;

            myListeners.clear();
            myStatusListeners.clear();

            if (myBuildMonitor != null) {
                myBuildMonitor.projectClosed();
                myBuildMonitor = null;
            }
        }
    }

    @Override
    public void beforeBuildStarted(@NotNull final String targetName) {
        if (myProjectSettings.isDisconnectOnBuild() && myProjectSettings.isBuildConfigurationMatched(targetName)) {
            boolean wasConnected = isPortConnected();
            myIsBuilding = true;
            disconnectAndRest();
            myWasConnected = wasConnected;
        } else {
            myWasConnected = false;
        }

        statusChanged();
    }

    private void disconnectAndRest() {
        if (myPortOwnerAccess != null) {
            try {
                mySerialPortService.close(myPortOwnerAccess.getPortName());
                myPortOwnerAccess = null;

                // need to pause for a very short delay to allow port to reset
                int restTime = ArduinoApplicationSettings.getInstance().getDisconnectRestTime();
                if (restTime > 0) {
                    Thread.sleep(restTime);
                }
            } catch (SerialMonitorException e) {
                notifySerialError(true, e.getMessage(), e);
            } catch (InterruptedException ignored) {
            }
        }
    }

    @Override
    public void afterBuildFinished(final boolean success) {
        myIsBuilding = false;
        if (myWasConnected && myProjectSettings.isReconnectAfterBuild()) {
            if (!myProjectSettings.isAfterSuccessfulBuild() || success) {
                clearSerialMonitorConsole();
                connectPort(myProjectSettings.getPort(), myProjectSettings.getBaudRate());
            }
        }
        statusChanged();
    }

    public void clearSerialMonitorConsole() {
        if (mySerialMonitorPanel != null && mySerialMonitorPanel.getConsoleView() != null) {
            mySerialMonitorPanel.getConsoleView().clear();
        }
    }

    public boolean isBuildMonitored() {
        return myBuildMonitor != null;
    }

    public boolean isPortConnected() {
        return myPortOwnerAccess != null;
    }

    public boolean connectPort(@NotNull String port, int baudRate) {
        if (!isPortConnected() && !myIsBuilding) {
            if (mySerialPortService.isConnected(port)) {
                try {
                    mySerialPortService.close(port);
                } catch (SerialMonitorException e) {
                    notifySerialError(true, e.getMessage(), e);
                }
            }

            connect(port, baudRate);
            portConnected();
        }
        return true;
    }

    private void portConnected() {
        if (myPortOwnerAccess != null) {
            // once connected
            if (myProjectSettings.isActivateOnConnect() && myToolWindow != null) {
                // active tool window
                myToolWindow.show(null);
            }

            ensureToolWindowCreated();

            for (SerialPortListener listener : myListeners) {
                listener.onConnect(myPortOwnerAccess.getPortName(), myConnectedBaudRate);
            }

            for (SerialPortStatusListener listener : myStatusListeners) {
                listener.onStatusChanged();
            }
        }
    }

    @SuppressWarnings("UnusedReturnValue")
    public boolean connectPort() {
        return connectPort(myProjectSettings.getPort(), myProjectSettings.getBaudRate());
    }

    public boolean isNotBuilding() {
        return !myIsBuilding;
    }

    public @Nullable String getConnectedPort() {
        return myPortOwnerAccess == null ? null : myPortOwnerAccess.getPortName();
    }

    public int getConnectedBaudRate() {
        return myConnectedBaudRate;
    }

    public void disconnectPort() {
        if (myPortOwnerAccess != null) {
            if (myPortOwnerAccess.isConnected()) {
                try {
                    // this will generate a DISCONNECTED status, which will trigger portDisconnected()
                    mySerialPortService.close(myPortOwnerAccess.getPortName());
                } catch (SerialMonitorException e) {
                    notifySerialError(true, e.getMessage(), e);
                    portDisconnected();
                }
            } else {
                portDisconnected();
            }
        }
    }

    private void portDisconnected() {
        final String portName = myPortOwnerAccess == null ? "" : myPortOwnerAccess.getPortName();
        final int baud = myConnectedBaudRate;
        myWasConnected = false;
        myPortOwnerAccess = null;

        ensureToolWindowCreated();

        for (SerialPortListener listener : myListeners) {
            listener.onDisconnect(portName, baud);
        }

        for (SerialPortStatusListener listener : myStatusListeners) {
            listener.onStatusChanged();
        }
    }

    private void ensureToolWindowCreated() {
        if (myToolWindow == null) {
            // get it up and running
            ToolWindowManager toolWindowManager = ToolWindowManager.getInstance(myProject);
            if (toolWindowManager instanceof ToolWindowManagerImpl) {
                ((ToolWindowManagerImpl) toolWindowManager).showToolWindow("Arduino Support");
            }
        }
    }

    public boolean canConnectPort() {
        return !myIsBuilding && mySerialPortService.isPortValid(myProjectSettings.getPort(), true);
    }

    public Project getProject() {
        return myProject;
    }

    private void connect(String portName, int baudRate) {
        try {
            SerialPortProfile settings = new SerialPortProfile();
            settings.bits = 8;
            settings.baudRate = baudRate;
            settings.stopBits = StopBits.BITS_1;
            settings.parity = Parity.NONE;
            switch (myProjectSettings.getSerialEndOfLineType()) {
                case LF:
                    settings.newLine = NewLine.LF;
                    break;
                case CR_LF:
                    settings.newLine = NewLine.CRLF;
                    break;
                default:
                case NONE:
                case CR:
                    settings.newLine = NewLine.CR;
                    break;
            }
            settings.portName = portName;

            myPortOwnerAccess = mySerialPortService.connect(settings, bytes -> {
                if (myListeners.isEmpty()) return;

                if (bytes.length > 0) {
                    for (SerialPortListener listener : myListeners) {
                        listener.onReceive(bytes);
                    }
                }
            }, status -> {
                // we handle direct errors, this is just a catch all
                if (status == DISCONNECTED) {
                    // this could be coming from someone else or ourselves
                    portDisconnected();
                }
            });

            myConnectedBaudRate = baudRate;
        } catch (SerialMonitorException e) {
            myPortOwnerAccess = null;
            notifySerialError(true, e.getMessage(), e);
        }
    }

    public void send(byte[] bytes) {
        if (myPortOwnerAccess != null) {
            try {
                myPortOwnerAccess.write(bytes);
                for (SerialPortListener listener : myListeners) {
                    listener.onSend(bytes);
                }
                return;
            } catch (SerialMonitorException e) {
                String message = "Send serial port error: " + e.getMessage();
                notifySerialError(true, message, e);
            }
        }

        throw new IllegalStateException("Serial Port is not connected");
    }

    public void notifySerialError(final boolean isError, final String message, final SerialMonitorException e) {
        for (SerialPortListener listener : myListeners) {
            listener.onError(isError, message, e);
        }
    }

    public void addPortListener(@NotNull SerialPortListener listener) {
        myListeners.add(listener);
    }

    public void removePortListener(SerialPortListener listener) {
        myListeners.remove(listener);
    }

    public void addPortListener(@NotNull SerialPortStatusListener listener) {
        myStatusListeners.add(listener);
    }

    public void removePortListener(SerialPortStatusListener listener) {
        myStatusListeners.remove(listener);
    }

    public void statusChanged() {
        for (SerialPortStatusListener listener : myStatusListeners) {
            listener.onStatusChanged();
        }
    }
}
