package com.vladsch.clionarduinoplugin.serial;

public interface SerialPortStatusListener {
    default void onStatusChanged() { }
}
