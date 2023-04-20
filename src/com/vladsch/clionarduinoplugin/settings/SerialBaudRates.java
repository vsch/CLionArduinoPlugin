package com.vladsch.clionarduinoplugin.settings;

import com.vladsch.clionarduinoplugin.ArduinoSupportBundle;
import com.vladsch.plugin.util.ui.ComboBoxAdaptable;
import com.vladsch.plugin.util.ui.ComboBoxAdapter;
import com.vladsch.plugin.util.ui.ComboBoxAdapterImpl;
import com.vladsch.plugins.simpleserialconnectorservice.SerialPortProfile;
import org.jetbrains.annotations.NotNull;

public enum SerialBaudRates implements ComboBoxAdaptable<SerialBaudRates> {
    DEFAULT(0, ArduinoSupportBundle.message("new-project.baud-rate.default")),
    BAUDRATE_110(SerialPortProfile.BaudRate.BAUDRATE_110.intValue, Integer.toString(SerialPortProfile.BaudRate.BAUDRATE_110.intValue)),
    BAUDRATE_300(SerialPortProfile.BaudRate.BAUDRATE_300.intValue, Integer.toString(SerialPortProfile.BaudRate.BAUDRATE_300.intValue)),
    BAUDRATE_600(SerialPortProfile.BaudRate.BAUDRATE_600.intValue, Integer.toString(SerialPortProfile.BaudRate.BAUDRATE_600.intValue)),
    BAUDRATE_1200(SerialPortProfile.BaudRate.BAUDRATE_1200.intValue, Integer.toString(SerialPortProfile.BaudRate.BAUDRATE_1200.intValue)),
    BAUDRATE_2400(SerialPortProfile.BaudRate.BAUDRATE_2400.intValue, Integer.toString(SerialPortProfile.BaudRate.BAUDRATE_2400.intValue)),
    BAUDRATE_4800(SerialPortProfile.BaudRate.BAUDRATE_4800.intValue, Integer.toString(SerialPortProfile.BaudRate.BAUDRATE_4800.intValue)),
    BAUDRATE_9600(SerialPortProfile.BaudRate.BAUDRATE_9600.intValue, Integer.toString(SerialPortProfile.BaudRate.BAUDRATE_9600.intValue)),
    BAUDRATE_14400(SerialPortProfile.BaudRate.BAUDRATE_14400.intValue, Integer.toString(SerialPortProfile.BaudRate.BAUDRATE_14400.intValue)),
    BAUDRATE_19200(SerialPortProfile.BaudRate.BAUDRATE_19200.intValue, Integer.toString(SerialPortProfile.BaudRate.BAUDRATE_19200.intValue)),
    BAUDRATE_38400(SerialPortProfile.BaudRate.BAUDRATE_38400.intValue, Integer.toString(SerialPortProfile.BaudRate.BAUDRATE_38400.intValue)),
    BAUDRATE_57600(SerialPortProfile.BaudRate.BAUDRATE_57600.intValue, Integer.toString(SerialPortProfile.BaudRate.BAUDRATE_57600.intValue)),
    BAUDRATE_115200(SerialPortProfile.BaudRate.BAUDRATE_115200.intValue, Integer.toString(SerialPortProfile.BaudRate.BAUDRATE_115200.intValue)),
    BAUDRATE_128000(SerialPortProfile.BaudRate.BAUDRATE_128000.intValue, Integer.toString(SerialPortProfile.BaudRate.BAUDRATE_128000.intValue)),
    BAUDRATE_230400(SerialPortProfile.BaudRate.BAUDRATE_230400.intValue, Integer.toString(SerialPortProfile.BaudRate.BAUDRATE_230400.intValue)),
    BAUDRATE_256000(SerialPortProfile.BaudRate.BAUDRATE_256000.intValue, Integer.toString(SerialPortProfile.BaudRate.BAUDRATE_256000.intValue)),
    BAUDRATE_460800(SerialPortProfile.BaudRate.BAUDRATE_460800.intValue, Integer.toString(SerialPortProfile.BaudRate.BAUDRATE_460800.intValue)),
    BAUDRATE_512000(SerialPortProfile.BaudRate.BAUDRATE_512000.intValue, Integer.toString(SerialPortProfile.BaudRate.BAUDRATE_512000.intValue)),
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
