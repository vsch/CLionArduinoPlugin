package com.vladsch.clionarduinoplugin.serial;

import org.jetbrains.annotations.Nullable;

public interface SerialPortListener {
    default void onConnect(final String portName, final int baudRate) { }

    default void onDisconnect(final String portName, final int baudRate) { }

    default void onReceive(final byte[] buf) { }

    default void onSend(final byte[] buf) { }

    default void onError(final boolean isError, final String message, @Nullable Throwable exception) { }
}
