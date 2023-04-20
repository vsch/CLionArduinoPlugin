// Copyright (c) 2015-2023 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package com.vladsch.clionarduinoplugin.settings;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.ui.ValidationInfo;
import com.intellij.ui.DocumentAdapter;
import com.intellij.ui.ToolbarDecorator;
import com.intellij.ui.components.JBCheckBox;
import com.intellij.ui.table.TableView;
import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import com.intellij.util.ui.ColumnInfo;
import com.intellij.util.ui.ElementProducer;
import com.intellij.util.ui.JBUI;
import com.intellij.util.ui.ListTableModel;
import com.intellij.util.ui.UIUtil;
import com.vladsch.clionarduinoplugin.ArduinoSupportBundle;
import com.vladsch.flexmark.util.misc.DelimitedBuilder;
import com.vladsch.flexmark.util.html.ui.BackgroundColor;
import com.vladsch.flexmark.util.html.ui.HtmlBuilder;
import com.vladsch.flexmark.util.html.ui.HtmlHelpers;
import com.vladsch.plugin.util.ui.Helpers;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.Action;
import javax.swing.DefaultCellEditor;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.DocumentEvent;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.EventObject;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegExTestDialog extends DialogWrapper {
    JPanel myMainPanel;
    JTextField myPattern;
    JTextField mySampleText;
    private JPanel myViewPanel;
    private JTextPane myTextPane;
    private JPanel myTablesPanel;
    private ListTableModel<RegExSampleSet> myTextModel;
    TableView<RegExSampleSet> myTextTable;
    private boolean myIsBadRegEx;

    @SuppressWarnings("FieldCanBeLocal") private @NotNull String myOriginalSampleText;
    @SuppressWarnings("FieldCanBeLocal") private @NotNull String myOriginalPatternText;
    private int myTableRowPadding = 0;
    private boolean myIsBadRegex;

    static class RegExSampleSet {
        boolean shouldMatch;
        @NotNull String sample;
        @NotNull String resultHtml;
        @Nullable String toolTipText;

        public RegExSampleSet() {
            shouldMatch = true;
            sample = "";
            resultHtml = "";
            toolTipText = null;
        }

        public RegExSampleSet(@NotNull String sample) {
            if (!sample.isEmpty() && sample.charAt(0) == '!') {
                shouldMatch = false;
                sample = sample.substring(1);
            } else {
                shouldMatch = true;
            }

            this.sample = sample;
            resultHtml = "";
            toolTipText = null;
        }
    }

    private final RegExSettingsHolder mySettingsHolder;

    private BackgroundColor getInvalidTextFieldBackground() {
        return BackgroundColor.of(Helpers.errorColor(UIUtil.getTextFieldBackground()));
    }

    private BackgroundColor getWarningTextFieldBackground() {
        return BackgroundColor.of(Helpers.warningColor(UIUtil.getTextFieldBackground()));
    }

    private BackgroundColor getValidTextFieldBackground() {
        return BackgroundColor.of(UIUtil.getTextFieldBackground());
    }

    private BackgroundColor getSelectedTextFieldBackground() {
        return BackgroundColor.of(mySampleText.getSelectionColor());
    }

    private BackgroundColor getInvalidTableBackground(boolean isSelected) {
        return BackgroundColor.of(Helpers.errorColor(UIUtil.getTableBackground()));
    }

    BackgroundColor getTableBackground(boolean isSelected) {
        return BackgroundColor.of(UIUtil.getTableBackground());
    }

    public boolean saveSettings(boolean onlySamples) {
        // save settings return false if regex is not valid
        mySettingsHolder.setSampleText(mySampleText.getText());

        if (!onlySamples) {
            mySettingsHolder.setPatternText(myPattern.getText().trim());
        }

        return onlySamples || updateResults().isEmpty();
    }

    public RegExTestDialog(JComponent parent, @NotNull RegExSettingsHolder settingsHolder) {
        super(parent, false);

        mySettingsHolder = settingsHolder;

        myOriginalSampleText = settingsHolder.getSampleText();
        myOriginalPatternText = settingsHolder.getPatternText();

        mySampleText.setText(myOriginalSampleText);
        myPattern.setText(myOriginalPatternText);

        mySampleText.setVisible(false);

        final DocumentAdapter listener = new DocumentAdapter() {
            @Override
            protected void textChanged(DocumentEvent e) {
                validateResults(false);
            }
        };

        myPattern.getDocument().addDocumentListener(listener);

        myTextModel.addTableModelListener(e -> {
            updateResults();
        });

        validateResults(true);

        init();
    }

    private void validateResults(boolean init) {
        List<RegExSampleSet> sampleSets;
        if (init) {
            String[] samples = mySampleText.getText().split(ArduinoProjectSettings.TEXT_SPLIT_REGEX);
            sampleSets = new ArrayList<>();
            for (String sample : samples) {
                if (!sample.isEmpty()) {
                    RegExSampleSet sampleSet = new RegExSampleSet(sample);
                    sampleSets.add(sampleSet);
                }
            }
            myTextModel.setItems(sampleSets);
        } else {
            sampleSets = new ArrayList<>(myTextModel.getItems());
        }

        for (RegExSampleSet sampleSet : sampleSets) {
            checkRegEx(myPattern, sampleSet);
            if (myIsBadRegEx) break;
        }

        myTextTable.repaint();
    }

    private String updateResults() {
        DelimitedBuilder error = new DelimitedBuilder("\n");
        DelimitedBuilder samples = new DelimitedBuilder(ArduinoProjectSettings.TEXT_DELIMITER);
        for (RegExSampleSet sampleSet : myTextModel.getItems()) {
            if (!sampleSet.sample.isEmpty()) {
                if (!sampleSet.shouldMatch) samples.append("!");
                samples.append(sampleSet.sample).mark();
                String err = checkRegEx(myPattern, sampleSet);
                if (!err.isEmpty()) {
                    error.append(err).mark();
                }
                if (myIsBadRegEx) break;
            }
        }
        mySampleText.setText(samples.toString());
        return error.toString();
    }

    @Nullable
    @Override
    protected JComponent createCenterPanel() {
        return myMainPanel;
    }

    @NotNull
    @Override
    protected Action[] createActions() {
        super.createDefaultActions();
        return new Action[] { getOKAction(), getCancelAction() };
    }

    public static boolean showDialog(JComponent parent, @NotNull RegExSettingsHolder settingsHolder) {
        RegExTestDialog dialog = new RegExTestDialog(parent, settingsHolder);
        boolean save = dialog.showAndGet();
        return dialog.saveSettings(!save);
    }

    @Nullable
    @Override
    protected ValidationInfo doValidate() {
        String error = updateResults();

        if (!error.isEmpty()) {
            return new ValidationInfo(error, myPattern);
        }
        return super.doValidate();
    }

    @Nullable
    @Override
    protected String getDimensionServiceKey() {
        return "ArduinoSupport.RegExTestDialog";
    }

    @Nullable
    @Override
    public JComponent getPreferredFocusedComponent() {
        return myPattern;
    }

    String checkRegEx(final JTextField pattern, RegExSampleSet sampleSet) {
        final String patternText = pattern.getText().trim();
        Color validBackground = getValidTextFieldBackground();
        Color selectedBackground = getSelectedTextFieldBackground();
        Color invalidBackground = getInvalidTextFieldBackground();
        Color warningBackground = getWarningTextFieldBackground();
        String error = "";
        String warning = "";

        Pattern regexPattern;
        Matcher matcher = null;
        final String text = sampleSet.sample.trim();
        myIsBadRegEx = false;
        boolean hadMatch = false;

        if (!myPattern.getText().trim().isEmpty()) {
            try {
                regexPattern = Pattern.compile(patternText);
                matcher = regexPattern.matcher(text);
                hadMatch = matcher.find();

                if (hadMatch == sampleSet.shouldMatch) {
                    // these are ok
                    //if (matcher.start() != 0) {
                    //    error = Bundle.message("regex.matched-middle.description");
                    //} else {
                    //    // see if it will match in the middle
                    //    Matcher matcher2 = regexPattern.matcher(text + text);
                    //    if (matcher2.find() && matcher2.find()) {
                    //        warning = Bundle.message("regex.also-matches-middle.description");
                    //    }
                    //}
                } else {
                    error = sampleSet.shouldMatch ? "not matched" : "matched";
                }
            } catch (IllegalArgumentException | IndexOutOfBoundsException e) {
                error = e.getMessage();
                myIsBadRegEx = true;
            }
        } else {
            error = "empty pattern";
        }

        if (error.isEmpty() && matcher != null) {
            // have match
            HtmlBuilder html = new HtmlBuilder();
            html.tag("html").style("margin:2px").attr(getValidTextFieldBackground(), mySampleText.getFont()).tag("body");
            String matchedText = hadMatch ? text.substring(0, matcher.end()) : text;
            String trailingText = hadMatch ? text.substring(matcher.end()) : "";
            html.attr(warning.isEmpty() ? selectedBackground : warningBackground).span(matchedText);
            html.span(trailingText);
            html.closeTag("body");
            html.closeTag("html");

            myViewPanel.setVisible(true);
            myTextPane.setVisible(false);
            sampleSet.resultHtml = html.toFinalizedString();
            sampleSet.toolTipText = warning;
        } else if (!myIsBadRegEx) {
            HtmlBuilder html = new HtmlBuilder();
            html.tag("html").style("margin:2px;vertical-align:middle;").attr(getValidTextFieldBackground(), mySampleText.getFont()).tag("body");
            if (matcher == null || !hadMatch) {
                html.attr(error.equals("not matched") ? invalidBackground : warningBackground).span(text);
            } else {
                html.span(text.substring(0, matcher.start()));
                html.attr(invalidBackground).span(text.substring(matcher.start(), matcher.end()));
                html.span(text.substring(matcher.end()));
            }
            html.closeTag("body");
            html.closeTag("html");

            myViewPanel.setVisible(true);
            myTextPane.setVisible(false);
            sampleSet.resultHtml = html.toFinalizedString();
            sampleSet.toolTipText = error.isEmpty() ? null : error;
        } else {
            myViewPanel.setVisible(false);
            HtmlHelpers.setRegExError(error, myTextPane, mySampleText.getFont(), getValidTextFieldBackground(), getWarningTextFieldBackground());
        }
        return error;
    }

    private void createUIComponents() {
        ElementProducer<RegExSampleSet> producer = new ElementProducer<RegExSampleSet>() {
            @Override
            public RegExSampleSet createElement() {
                return new RegExSampleSet();
            }

            @Override
            public boolean canCreateElement() {
                return true;
            }
        };

        GridConstraints constraints = new GridConstraints(0, 0, 1, 1
                , GridConstraints.ANCHOR_CENTER
                , GridConstraints.FILL_BOTH
                , GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW | GridConstraints.SIZEPOLICY_WANT_GROW
                , GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW | GridConstraints.SIZEPOLICY_WANT_GROW
                , null, null, null);

        ColumnInfo[] sampleTextColumns = { new MatchColumn(), new SampleColumn(), new ResultColumn() };
        myTextModel = new ListTableModel<>(sampleTextColumns, new ArrayList<>(), 0);
        myTextTable = new TableView<RegExSampleSet>(myTextModel) {
            @Override
            public void editingCanceled(ChangeEvent e) {
                super.editingCanceled(e);
                ApplicationManager.getApplication().invokeLater(() -> validateResults(false));
            }
        };

        myTextTable.setPreferredScrollableViewportSize(JBUI.size(-1, 500));
        myTextTable.setRowSelectionAllowed(true);

        //int height = (int) myTextTable.getTableHeader().getPreferredSize().getHeight() + 10;
        //myTextTable.setRowHeight(height);
        int height = myTextTable.getRowHeight();
        myTableRowPadding = 8;
        myTextTable.setRowHeight(height + myTableRowPadding);

        ToolbarDecorator decorator = ToolbarDecorator.createDecorator(myTextTable, producer);
        myTablesPanel = new JPanel(new GridLayoutManager(1, 1));
        myTablesPanel.add(decorator.createPanel(), constraints);

        myViewPanel = new JPanel(new BorderLayout());
        myViewPanel.add(myTablesPanel, BorderLayout.CENTER);
    }

    static abstract class CheckBoxCellRenderer extends JBCheckBox implements TableCellRenderer {
        private final JTable myTable;

        public CheckBoxCellRenderer(final JTable table) {
            myTable = table;
        }

        @Override
        public Component getTableCellRendererComponent(final JTable table, final Object value, final boolean isSelected, final boolean hasFocus, final int row, final int column) {
            if (!isSelected) {
                setBackground(myTable.getBackground());
            } else {
                setBackground(myTable.getSelectionBackground());
            }

            //Foreground could be changed using another Hashtable...
            setForeground(myTable.getForeground());
            setHorizontalAlignment(SwingConstants.CENTER);

            return this;
        }
    }

    class MatchColumn extends MyColumnInfo<Boolean> {
        MatchColumn() {
            super(ArduinoSupportBundle.message("settings.regex.match.label"));
        }

        @Override
        public TableCellRenderer getRenderer(final RegExSampleSet item) {
            return new CheckBoxCellRenderer(myTextTable) {
                public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                    final Component rendererComponent = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                    setSelected(item.shouldMatch);
                    return rendererComponent;
                }
            };
        }

        @Override
        public boolean isCellEditable(RegExSampleSet set) {
            return true;
        }

        @Override
        public TableCellEditor getEditor(final RegExSampleSet item) {
            JBCheckBox checkBox = new JBCheckBox();

            checkBox.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(final ActionEvent e) {
                    item.shouldMatch = checkBox.isSelected();
                    checkRegEx(myPattern, item);
                    myTextTable.repaint();
                }
            });

            return new DefaultCellEditor(checkBox) {
                @Override
                public boolean isCellEditable(EventObject anEvent) {
                    return true;
                }
            };
        }

        @Override
        public Boolean valueOf(final RegExSampleSet object) {
            return object.shouldMatch;
        }

        @Override
        public void setValue(final RegExSampleSet item, final Boolean value) {
            if (item != null) {
                item.shouldMatch = value;
                checkRegEx(myPattern, item);
                myTextTable.repaint();
            }
        }
    }

    class SampleColumn extends MyColumnInfo<String> {

        SampleColumn() {
            super(ArduinoSupportBundle.message("settings.regex.sample.label"));
        }

        @Override
        public TableCellRenderer getRenderer(final RegExSampleSet item) {
            return new DefaultTableCellRenderer() {
                public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                    final Component rendererComponent = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                    setText(item.sample);
                    return rendererComponent;
                }
            };
        }

        @Override
        public boolean isCellEditable(RegExSampleSet set) {
            return true;
        }

        @Override
        public TableCellEditor getEditor(final RegExSampleSet item) {
            JTextField textField = new JTextField();

            textField.getDocument().addDocumentListener(new DocumentAdapter() {
                @Override
                protected void textChanged(DocumentEvent e) {
                    String sample = item.sample;
                    item.sample = textField.getText();
                    checkRegEx(myPattern, item);
                    item.sample = sample;
                    myTextTable.repaint();
                }
            });

            return new DefaultCellEditor(textField) {
                @Override
                public boolean isCellEditable(EventObject anEvent) {
                    return true;
                }
            };
        }

        @Override
        public String valueOf(final RegExSampleSet object) {
            return object.sample;
        }

        @Override
        public void setValue(final RegExSampleSet item, final String value) {
            if (item != null) {
                item.sample = value;
                checkRegEx(myPattern, item);
                myTextTable.repaint();
            }
        }
    }

    class ResultColumn extends MyColumnInfo<String> {
        private String tooltipText;

        ResultColumn() {
            super(ArduinoSupportBundle.message("settings.regex.results.label"));
            tooltipText = null;
        }

        @Override
        public TableCellRenderer getRenderer(final RegExSampleSet item) {
            TextPaneTableCellRenderer cellRenderer = new TextPaneTableCellRenderer("text/html") {
                private Color myInvalidBackground = getInvalidTableBackground(false);
                private Color myInvalidSelectedBackground = getInvalidTableBackground(true);

                public TextPaneTableCellRenderer getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                    final TextPaneTableCellRenderer rendererComponent = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

                    tooltipText = item.toolTipText;
                    String resultHtml = item.resultHtml;
                    if (myTableRowPadding > 0) {
                        resultHtml = resultHtml.replace("margin:2px", "margin:" + String.valueOf(myTableRowPadding / 2) + "px 2px 0 2px");
                    }
                    rendererComponent.setText(resultHtml);
                    return rendererComponent;
                }

                @Override
                public String getToolTipText() {
                    return tooltipText != null ? tooltipText : super.getToolTipText();
                }
            };

            JTextPane textPane = cellRenderer.getTextComponent();
            textPane.setFont(mySampleText.getFont());
            textPane.setBackground(mySampleText.getBackground());
            textPane.setOpaque(true);
            return cellRenderer;
        }

        @Override
        public TableCellEditor getEditor(final RegExSampleSet item) {
            return null;
        }

        @Override
        public String valueOf(final RegExSampleSet object) {
            return object.resultHtml;
        }

        @Override
        public void setValue(final RegExSampleSet item, final String value) {
            if (item != null) {
                item.resultHtml = value;
            }
        }
    }

    private abstract class MyColumnInfo<T> extends ColumnInfo<RegExSampleSet, T> {
        MyColumnInfo(final String name) {
            super(name);
        }
    }
}
