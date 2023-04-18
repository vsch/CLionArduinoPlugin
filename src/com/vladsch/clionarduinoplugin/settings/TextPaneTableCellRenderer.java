// Copyright (c) 2015-2023 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package com.vladsch.clionarduinoplugin.settings;

import com.intellij.openapi.diagnostic.Logger;
import com.intellij.util.ui.JBUI;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.JTextPane;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;
import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

public class TextPaneTableCellRenderer extends JPanel implements TableCellRenderer {
    public static final TableCellRenderer INSTANCE = new TextPaneTableCellRenderer();

    /**
     * DefaultTableCellRenderer, that displays JTextPane on selected value.
     */
    public static final TableCellRenderer TEXT_PANE_RENDERER = new DefaultTableCellRenderer() {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            return super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
        }
    };

    private static final Logger LOG = Logger.getInstance("#com.intellij.util.ui.ComboBoxTableCellRenderer");

    private final JTextPane myPane = new JTextPane();

    public TextPaneTableCellRenderer() {
        this(null);
    }

    public TextPaneTableCellRenderer(@Nullable String contentType) {
        super(new GridBagLayout());
        if (contentType != null) {
            myPane.setContentType(contentType);
        }

        add(myPane,
                new GridBagConstraints(0, 0, 1, 1, 1, 1, GridBagConstraints.CENTER, GridBagConstraints.BOTH, JBUI.emptyInsets(), 0, 0));
    }

    public void setText(@Nullable String text) {
        myPane.setText(text);
    }

    public @NotNull
    String getText() {
        return myPane.getText();
    }

    @Override
    public TextPaneTableCellRenderer getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        myPane.setText((String) value);
        return this;
    }

    public JTextPane getTextComponent() {
        return myPane;
    }
}
