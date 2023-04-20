package com.vladsch.clionarduinoplugin.settings;

import com.vladsch.plugin.util.ui.ComboBoxAdapter;
import com.vladsch.plugin.util.ui.ComboBoxAdapterImpl;
import com.vladsch.plugin.util.ui.DynamicListAdaptable;
import com.vladsch.plugins.simpleserialconnectorservice.JsscSerialService;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.JComboBox;
import java.util.List;

public class SerialPortNames extends DynamicListAdaptable<SerialPortNames> {
    public SerialPortNames(final int intValue, @NotNull final String displayName) {
        super(intValue, displayName);
    }

    final public static SerialPortNames EMPTY = new SerialPortNames(0, "");
    @SuppressWarnings("rawtypes") 
    public static DynamicListAdaptable[] values = new DynamicListAdaptable[0];
    final public static Static<DynamicListAdaptable<SerialPortNames>> ADAPTER = new Static<>(new ComboBoxAdapterImpl<>(EMPTY));

    public static void updateValues(final boolean filtered, @Nullable JComboBox<String> comboBox, SerialPortNames... exclude) {
        values = DynamicListAdaptable.updateValues(EMPTY, JsscSerialService.getInstance().getPortNames(filtered), true, SerialPortNames::new);
        //noinspection unchecked
        ADAPTER.setDefaultValue(values[0]);

        if (comboBox != null) {
            ADAPTER.fillComboBox(comboBox, exclude);
        }
    }

    public static List<String> getDisplayNames() {
        return getDisplayNames(values);
    }

    @Override
    @NotNull
    public String getDisplayName() {
        return displayName;
    }

    @NotNull
    @Override
    public String name() {
        return displayName;
    }

    @Override
    public int getIntValue() {
        return intValue;
    }

    @NotNull
    @Override
    public ComboBoxAdapter<DynamicListAdaptable<SerialPortNames>> getAdapter() {
        return ADAPTER;
    }

    @NotNull
    @Override
    public DynamicListAdaptable<SerialPortNames>[] getValues() {
        //noinspection unchecked
        return values;
    }
}
