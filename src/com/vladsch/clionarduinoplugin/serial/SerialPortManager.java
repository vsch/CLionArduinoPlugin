package com.vladsch.clionarduinoplugin.serial;

import com.intellij.openapi.Disposable;
import com.intellij.openapi.application.ApplicationManager;
import com.vladsch.clionarduinoplugin.generators.SerialPortList;
import jssc.SerialNativeInterface;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class SerialPortManager implements Disposable {
    private final HashMap<String, SerialProjectComponent> myConnectedSerialPorts;

    public SerialPortManager() {
        myConnectedSerialPorts = new HashMap<>();
    }

    @Override
    public void dispose() {
        
    }

    public static ArrayList<String> getSerialPorts(Boolean filtered) {
        ArrayList<String> ports = new ArrayList<>();

        if (filtered && SerialNativeInterface.getOsType() == SerialNativeInterface.OS_MAC_OS_X) {
            for (String port : SerialPortList.getPortNames()) {
                if (!port.matches("/dev/(:?tty)\\..*")) {
                    ports.add(port);
                }
            }
        } else {
            Collections.addAll(ports, SerialPortList.getPortNames());
        }

        return ports;
    }

    public void removeProjectComponent(SerialProjectComponent projectComponent) {
        ArrayList<String> removePorts = new ArrayList<>();
        for (Map.Entry<String, SerialProjectComponent> entry : myConnectedSerialPorts.entrySet()) {
            if (projectComponent == entry.getValue()) {
                // TODO: log error, should not happen
                removePorts.add(entry.getKey());
            }
        }

        for (String port : removePorts) {
            myConnectedSerialPorts.remove(port);
        }
    }

    public void setPortOwner(@NotNull SerialProjectComponent newPortOwner, @NotNull final String port) {
        SerialProjectComponent portOwner = getPortOwner(port);

        if (portOwner != null && portOwner != newPortOwner) {
            portOwner.disconnectPort(port);
        }

        myConnectedSerialPorts.put(port, newPortOwner);
    }

    @Nullable
    public SerialProjectComponent getPortOwner(@NotNull final String port) {
        return myConnectedSerialPorts.get(port);
    }

    public boolean disconnectPort(@NotNull final String port) {
        SerialProjectComponent portOwner = getPortOwner(port);

        if (portOwner != null) {
            portOwner.disconnectPort(port);
            return true;
        }
        return false;
    }

    public static SerialPortManager getInstance() {
        return ApplicationManager.getApplication().getService(SerialPortManager.class);
    }
}
