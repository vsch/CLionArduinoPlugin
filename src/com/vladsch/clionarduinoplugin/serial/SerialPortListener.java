package com.vladsch.clionarduinoplugin.serial;

import jssc.SerialPortEvent;
import org.jetbrains.annotations.Nullable;

public interface SerialPortListener {
    default void onConnect(final String portName, final int baudRate) { onStatusChanged(); }
    default void onDisconnect(final String portName, final int baudRate) { onStatusChanged(); }
    default void onReceive(final byte[] buf) {}
    default void onSent(final byte[] buf) {}
    default void onEvent(SerialPortEvent event) {}
    default void onError(final boolean isError, final String message, @Nullable Throwable exception) {}
    default void onStatusChanged() {}
}
