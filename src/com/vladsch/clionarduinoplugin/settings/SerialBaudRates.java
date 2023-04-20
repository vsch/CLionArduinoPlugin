package com.vladsch.clionarduinoplugin.settings;

import com.vladsch.clionarduinoplugin.ArduinoSupportBundle;
import com.vladsch.plugin.util.ui.ComboBoxAdaptable;
import com.vladsch.plugin.util.ui.ComboBoxAdapter;
import com.vladsch.plugin.util.ui.ComboBoxAdapterImpl;
import com.vladsch.plugins.simpleserialconnectorservice.SerialPortProfile.BaudRate;
import org.jetbrains.annotations.NotNull;

public enum SerialBaudRates implements ComboBoxAdaptable<SerialBaudRates> {
    DEFAULT(0, ArduinoSupportBundle.message("new-project.baud-rate.default")),
    BAUDRATE_110(BaudRate.BAUDRATE_110.intValue, Integer.toString(BaudRate.BAUDRATE_110.intValue)),
    BAUDRATE_300(BaudRate.BAUDRATE_300.intValue, Integer.toString(BaudRate.BAUDRATE_300.intValue)),
    BAUDRATE_600(BaudRate.BAUDRATE_600.intValue, Integer.toString(BaudRate.BAUDRATE_600.intValue)),
    BAUDRATE_1200(BaudRate.BAUDRATE_1200.intValue, Integer.toString(BaudRate.BAUDRATE_1200.intValue)),
    BAUDRATE_2400(BaudRate.BAUDRATE_2400.intValue, Integer.toString(BaudRate.BAUDRATE_2400.intValue)),
    BAUDRATE_4800(BaudRate.BAUDRATE_4800.intValue, Integer.toString(BaudRate.BAUDRATE_4800.intValue)),
    BAUDRATE_9600(BaudRate.BAUDRATE_9600.intValue, Integer.toString(BaudRate.BAUDRATE_9600.intValue)),
    BAUDRATE_14400(BaudRate.BAUDRATE_14400.intValue, Integer.toString(BaudRate.BAUDRATE_14400.intValue)),
    BAUDRATE_19200(BaudRate.BAUDRATE_19200.intValue, Integer.toString(BaudRate.BAUDRATE_19200.intValue)),
    BAUDRATE_38400(BaudRate.BAUDRATE_38400.intValue, Integer.toString(BaudRate.BAUDRATE_38400.intValue)),
    BAUDRATE_57600(BaudRate.BAUDRATE_57600.intValue, Integer.toString(BaudRate.BAUDRATE_57600.intValue)),
    BAUDRATE_115200(BaudRate.BAUDRATE_115200.intValue, Integer.toString(BaudRate.BAUDRATE_115200.intValue)),
    BAUDRATE_128000(BaudRate.BAUDRATE_128000.intValue, Integer.toString(BaudRate.BAUDRATE_128000.intValue)),
    BAUDRATE_230400(BaudRate.BAUDRATE_230400.intValue, Integer.toString(BaudRate.BAUDRATE_230400.intValue)),
    BAUDRATE_256000(BaudRate.BAUDRATE_256000.intValue, Integer.toString(BaudRate.BAUDRATE_256000.intValue)),
    BAUDRATE_460800(BaudRate.BAUDRATE_460800.intValue, Integer.toString(BaudRate.BAUDRATE_460800.intValue)),
    BAUDRATE_512000(BaudRate.BAUDRATE_512000.intValue, Integer.toString(BaudRate.BAUDRATE_512000.intValue)),
    CUSTOM(-1, ArduinoSupportBundle.message("new-project.baud-rate.custom")),
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
