package com.vladsch.clionarduinoplugin.settings;

import com.vladsch.clionarduinoplugin.Bundle;
import com.vladsch.plugin.util.ui.ComboBoxAdaptable;
import com.vladsch.plugin.util.ui.ComboBoxAdapter;
import com.vladsch.plugin.util.ui.ComboBoxAdapterImpl;
import jssc.SerialPort;
import org.jetbrains.annotations.NotNull;

public enum SerialBaudRates implements ComboBoxAdaptable<SerialBaudRates> {
    DEFAULT(0, Bundle.message("new-project.baud-rate.default")),
    BAUDRATE_110(SerialPort.BAUDRATE_110, Integer.toString(SerialPort.BAUDRATE_110)),
    BAUDRATE_300(SerialPort.BAUDRATE_300, Integer.toString(SerialPort.BAUDRATE_300)),
    BAUDRATE_600(SerialPort.BAUDRATE_600, Integer.toString(SerialPort.BAUDRATE_600)),
    BAUDRATE_1200(SerialPort.BAUDRATE_1200, Integer.toString(SerialPort.BAUDRATE_1200)),
    BAUDRATE_4800(SerialPort.BAUDRATE_4800, Integer.toString(SerialPort.BAUDRATE_4800)),
    BAUDRATE_9600(SerialPort.BAUDRATE_9600, Integer.toString(SerialPort.BAUDRATE_9600)),
    BAUDRATE_14400(SerialPort.BAUDRATE_14400, Integer.toString(SerialPort.BAUDRATE_14400)),
    BAUDRATE_19200(SerialPort.BAUDRATE_19200, Integer.toString(SerialPort.BAUDRATE_19200)),
    BAUDRATE_38400(SerialPort.BAUDRATE_38400, Integer.toString(SerialPort.BAUDRATE_38400)),
    BAUDRATE_57600(SerialPort.BAUDRATE_57600, Integer.toString(SerialPort.BAUDRATE_57600)),
    BAUDRATE_115200(SerialPort.BAUDRATE_115200, Integer.toString(SerialPort.BAUDRATE_115200)),
    BAUDRATE_128000(SerialPort.BAUDRATE_128000, Integer.toString(SerialPort.BAUDRATE_128000)),
    BAUDRATE_256000(SerialPort.BAUDRATE_256000, Integer.toString(SerialPort.BAUDRATE_256000)),
    BAUDRATE_512000(512000, Integer.toString(512000)),
    CUSTOM(-1, Bundle.message("new-project.baud-rate.custom")),
    ;

    public final int intValue;
    public final @NotNull String displayName;

    SerialBaudRates(int intValue, @NotNull String displayName) {
        this.intValue = intValue;
        this.displayName = displayName;
    }

    public static Static<SerialBaudRates> ADAPTER = new Static<>(new ComboBoxAdapterImpl<>(DEFAULT));

    @NotNull
    @Override
    public ComboBoxAdapter<SerialBaudRates> getAdapter() {
        return ADAPTER;
    }

    public boolean isCustom() {
        return this == CUSTOM;
    }

    @Override
    public int getIntValue() { return intValue; }

    @NotNull
    public String getDisplayName() { return displayName; }

    @NotNull
    public SerialBaudRates[] getValues() { return values(); }
}
