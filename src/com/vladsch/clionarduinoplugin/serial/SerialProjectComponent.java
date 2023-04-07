package com.vladsch.clionarduinoplugin.serial;

import com.intellij.execution.ui.ConsoleViewContentType;
import com.intellij.openapi.Disposable;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.vladsch.clionarduinoplugin.components.ArduinoApplicationComponent;
import com.vladsch.clionarduinoplugin.settings.ArduinoApplicationSettings;
import com.vladsch.clionarduinoplugin.settings.ArduinoProjectSettings;
import com.vladsch.clionarduinoplugin.util.ProjectSettingsListener;
import jssc.SerialPort;
import jssc.SerialPortEvent;
import jssc.SerialPortEventListener;
import jssc.SerialPortException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import static com.vladsch.clionarduinoplugin.serial.SerialMonitorPanel.ALT_CONNECTION_STATUS;

public class SerialProjectComponent implements BuildListener, Disposable, ProjectSettingsListener {
    private static final Logger LOG = Logger.getInstance("com.vladsch.clionarduinoplugin.serial");
    public static final String NEED_CLION_2018_3_OR_LATER = "Build monitoring not available. Need CLion 2018.3 or later";

    final Project myProject;
    private ArduinoApplicationComponent myApplicationComponent;
    @Nullable StatusWidget myStatusWidget;
    final @NotNull ArduinoProjectSettings myProjectSettings;
    boolean myIsBuilding = false;
    boolean myWasConnected;
    String myConnectedPort;
    int myConnectedBaudRate;
    private SerialPortManager myPortManager;
    ToolWindow myToolWindow;
    SerialMonitorPanel mySerialMonitorPanel;
    BuildMonitor myBuildMonitor;

    SerialPort mySerialPort;

    final Set<SerialPortListener> myListeners = Collections.synchronizedSet(new HashSet<>());

    public SerialProjectComponent(final Project project) {
        myProject = project;
        myApplicationComponent = ArduinoApplicationComponent.getInstance();
        myProjectSettings = ArduinoProjectSettings.getInstance(myProject);

        try {
            //noinspection ThisEscapedInObjectConstruction
            myBuildMonitor = new BuildMonitor(myProject, this);
            myBuildMonitor.projectOpened();
            try {
                if (ALT_CONNECTION_STATUS == null) {
                    //noinspection AssignmentToStaticFieldFromInstanceMethod
                    ALT_CONNECTION_STATUS = ConsoleViewContentType.LOG_VERBOSE_OUTPUT;
                }
            } catch (Throwable ignored) {

            }
        } catch (NoClassDefFoundError e) {
            LOG.debug(NEED_CLION_2018_3_OR_LATER, e);
            myBuildMonitor = null;
        }

        myPortManager = SerialPortManager.getInstance();

        project.getMessageBus().connect(this).subscribe(ProjectSettingsListener.TOPIC, this);
    }

    @Override
    public void onSettingsChanged() {
        if (isPortConnected() && (myConnectedBaudRate != myProjectSettings.getBaudRate() || !myConnectedPort.equals(myProjectSettings.getPort()))) {
            if (myPortManager.disconnectPort(myProjectSettings.getPort())) {
                // need to pause for a very short delay to allow port to reset
                int restTime = ArduinoApplicationSettings.getInstance().getDisconnectRestTime();
                if (restTime > 0) {
                    try {
                        Thread.sleep(restTime);
                    } catch (InterruptedException ignored) {
                    }
                }
            }

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

            disconnectPort();
            myPortManager = null;

            myListeners.clear();

            if (myBuildMonitor != null) {
                myBuildMonitor.projectClosed();
                myBuildMonitor = null;
            }

            myStatusWidget = null;

            SerialPortManager portManager = SerialPortManager.getInstance();
            portManager.removeProjectComponent(this);
        }
    }

    @Override
    public void beforeBuildStarted(@NotNull final String targetName) {
        if (myProjectSettings.isDisconnectOnBuild() && myProjectSettings.isBuildConfigurationMatched(targetName)) {
            boolean wasConnected = isPortConnected();
            myIsBuilding = true;
            if (myPortManager.disconnectPort(myProjectSettings.getPort())) {
                // need to pause for a very short delay to allow port to reset
                int restTime = ArduinoApplicationSettings.getInstance().getDisconnectRestTime();
                if (restTime > 0) {
                    try {
                        Thread.sleep(restTime);
                    } catch (InterruptedException ignored) {
                    }
                }
            }
            myWasConnected = wasConnected;
        } else {
            myWasConnected = false;
        }
        if (myStatusWidget != null) {
            myStatusWidget.setStatus(false, myConnectedPort != null, myConnectedPort != null, "In Build: " + targetName);
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

        if (myStatusWidget != null) {
            myStatusWidget.setStatus(true, myConnectedPort != null, canConnectPort());
        }
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
        return myConnectedPort != null;
    }

    public boolean connectPort(@NotNull String port, int baudRate) {
        if (!isPortConnected() && !myIsBuilding) {
            myPortManager.disconnectPort(port);

            connect(port, baudRate);

            //noinspection VariableNotUsedInsideIf
            if (mySerialPort != null) {
                // once connected
                myConnectedPort = port;
                myConnectedBaudRate = baudRate;
                myPortManager.setPortOwner(this, myConnectedPort);

                if (myProjectSettings.isActivateOnConnect() && myToolWindow != null) {
                    // active tool window
                    myToolWindow.show(null);
                }
            }
        }
        return true;
    }

    @SuppressWarnings("UnusedReturnValue")
    public boolean connectPort() {
        return connectPort(myProjectSettings.getPort(), myProjectSettings.getBaudRate());
    }

    public boolean isBuilding() {
        return myIsBuilding;
    }

    public String getConnectedPort() {
        return myConnectedPort;
    }

    public int getConnectedBaudRate() {
        return myConnectedBaudRate;
    }

    public void disconnectPort() {
        if (myConnectedPort != null) {
            if (mySerialPort != null) {
                try {
                    if (mySerialPort.isOpened()) {
                        mySerialPort.removeEventListener();
                        mySerialPort.closePort();
                    }
                } catch (SerialPortException e) {
                    LOG.error("Error closing port", e);
                }
            }

            final String port = myConnectedPort;
            final int baud = myConnectedBaudRate;
            mySerialPort = null;
            myWasConnected = false;
            myPortManager.removeProjectComponent(this);

            for (SerialPortListener listener : myListeners) {
                listener.onDisconnect(port, baud);
            }

            myConnectedPort = null;
        }
    }

    public void disconnectPort(@NotNull final String port) {
        if (port.equals(myConnectedPort)) {
            disconnectPort();
        }
    }

    public boolean canConnectPort() {
        return !myIsBuilding && SerialPortManager.getSerialPorts(true).contains(myProjectSettings.getPort());
    }

    public Project getProject() {
        return myProject;
    }

    private void connect(String portName, int baudRate) {
        int dataBits = SerialPort.DATABITS_8;
        int stopBits = SerialPort.STOPBITS_1;
        int parity = SerialPort.PARITY_NONE;
        boolean rts = true;
        boolean dtr = true;

        try {
            mySerialPort = new SerialPort(portName);
            mySerialPort.openPort();

            String[] stopBitNames = new String[] {
                    "1",
                    "2",
                    "1.5",
            };
            String[] parityNames = new String[] {
                    "none",
                    "odd",
                    "even",
                    "mark",
                    "space",
            };

            //noinspection ConstantValue
            String message = "Failed to set port parameters { baud: " + baudRate +
                    ", dataBits: " + dataBits +
                    ", stopBits: " + stopBitNames[stopBits] +
                    ", parity: " + parityNames[parity] +
                    ", rts: " + (rts ? "yes" : "no") +
                    ", dtr: " + (dtr ? "yes" : "no") +
                    "";
            //notifySerialError(true, message, null);

            if (!mySerialPort.setParams(baudRate, dataBits, stopBits, parity, rts, dtr)) {
                // Failed to set parameters
                mySerialPort = null;
                notifySerialError(true, message, null);
            } else {
                mySerialPort.addEventListener(new SerialPortEventListener() {
                    @Override
                    public synchronized void serialEvent(SerialPortEvent serialEvent) {
                        if (serialEvent.isRXCHAR()) {
                            if (myListeners.isEmpty()) {
                                return;
                            }
                            try {
                                byte[] buf = mySerialPort.readBytes(serialEvent.getEventValue());
                                if (buf.length > 0) {
                                    for (SerialPortListener listener : myListeners) {
                                        listener.onReceive(buf);
                                    }
                                }
                            } catch (SerialPortException e) {
                                LOG.error(e);
                                notifySerialError(true, "Serial port exception", e);
                            }
                        } else {
                            for (SerialPortListener listener : myListeners) {
                                listener.onEvent(serialEvent);
                            }
                        }
                    }
                });

                for (SerialPortListener listener : myListeners) {
                    listener.onConnect(portName, baudRate);
                }
            }
        } catch (SerialPortException e) {
            mySerialPort = null;
            String message = "Error opening port";
            LOG.error(message, e);
            notifySerialError(true, message, e);
        }
    }

    public void send(byte[] bytes) {
        if (isPortConnected()) {
            try {
                mySerialPort.writeBytes(bytes);
                for (SerialPortListener listener : myListeners) {
                    listener.onSent(bytes);
                }
                return;
            } catch (SerialPortException e) {
                String message = "Send serial port error";
                LOG.error(message, e);
                notifySerialError(true, message, e);
            }
        }

        throw new IllegalStateException("Serial Port is not connected");
    }

    public void notifySerialError(final boolean isError, final String message, final SerialPortException e) {
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
}
