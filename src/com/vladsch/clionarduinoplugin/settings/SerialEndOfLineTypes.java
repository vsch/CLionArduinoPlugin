package com.vladsch.clionarduinoplugin.settings;

import com.vladsch.clionarduinoplugin.ArduinoSupportBundle;
import com.vladsch.plugin.util.ui.ComboBoxAdaptable;
import com.vladsch.plugin.util.ui.ComboBoxAdapter;
import com.vladsch.plugin.util.ui.ComboBoxAdapterImpl;
import org.jetbrains.annotations.NotNull;

public enum SerialEndOfLineTypes implements ComboBoxAdaptable<SerialEndOfLineTypes> {
    NONE(0, "", ArduinoSupportBundle.message("settings.eol.none")),
    LF(1, "\n", ArduinoSupportBundle.message("settings.eol.lf")),
    CR(2, "\r", ArduinoSupportBundle.message("settings.eol.cr")),
    CR_LF(3, "\r\n", ArduinoSupportBundle.message("settings.eol.crlf")),
    ;

    public final int intValue;
    public final @NotNull String displayName;
    public final @NotNull String eol;

    SerialEndOfLineTypes(int intValue, @NotNull String eol, @NotNull String displayName) {
        this.intValue = intValue;
        this.displayName = displayName;
        this.eol = eol;
    }

    public static Static<SerialEndOfLineTypes> ADAPTER = new Static<>(new ComboBoxAdapterImpl<>(LF));

    @NotNull
    @Override
    public ComboBoxAdapter<SerialEndOfLineTypes> getAdapter() {
        return ADAPTER;
    }

    @Override
    public int getIntValue() { return intValue; }

    @NotNull
    public String getDisplayName() { return displayName; }

    @NotNull
    public SerialEndOfLineTypes[] getValues() { return values(); }
}
