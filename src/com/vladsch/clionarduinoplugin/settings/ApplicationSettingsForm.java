// Copyright (c) 2015-2023 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package com.vladsch.clionarduinoplugin.settings;

import com.intellij.openapi.Disposable;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.fileChooser.FileChooserDescriptor;
import com.intellij.openapi.fileTypes.FileTypeManager;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.ui.TextComponentAccessor;
import com.intellij.openapi.ui.TextFieldWithBrowseButton;
import com.intellij.ui.DocumentAdapter;
import com.intellij.ui.components.JBCheckBox;
import com.vladsch.clionarduinoplugin.Bundle;
import com.vladsch.clionarduinoplugin.resources.ArduinoConfig;
import com.vladsch.clionarduinoplugin.resources.ArduinoToolchainFiles;
import com.vladsch.clionarduinoplugin.resources.ResourceUtils;
import com.vladsch.clionarduinoplugin.resources.TemplateResolver;
import com.vladsch.clionarduinoplugin.util.ApplicationSettingsListener;
import com.vladsch.clionarduinoplugin.util.DiffActionListener;
import com.vladsch.plugin.util.ui.Settable;
import com.vladsch.plugin.util.ui.SettingsComponents;
import org.jetbrains.annotations.NotNull;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.DocumentEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

public class ApplicationSettingsForm implements Disposable, ApplicationSettingsListener {
    static final Logger LOG = Logger.getInstance("com.vladsch.clionarduinoplugin.settings");

    JPanel myMainPanel;
    JTextField myAuthorName;
    JTextField myAuthorEMail;

    JBCheckBox myBundledBoardsTxt;
    TextFieldWithBrowseButton myBoardsTxtPath;
    private JLabel myBoardsTxtLabel;
    private JButton myBoardsTxtDiff;

    JBCheckBox myBundledProgrammersTxt;
    TextFieldWithBrowseButton myProgrammersTxtPath;
    private JLabel myProgrammersTxtLabel;
    private JButton myProgrammersTxtDiff;

    JBCheckBox myBundledTemplates;
    TextFieldWithBrowseButton myTemplatesPath;
    private JLabel myTemplatesPathLabel;
    private JButton myTemplatesDiff;
    private JButton myTemplatesCreate;
    JSpinner myDisconnectRestTime;
    private TextFieldWithBrowseButton myToolchainTxtPath;
    private JBCheckBox myBundledToolchainTxt;
    private JButton myToolchainTxtDiff;
    private JButton myToolchainTxtDiff2;
    private JButton myToolchainTxtCreate;
    private JLabel myToolchainTxtLabel;

    @NotNull ArduinoConfig myArduinoConfig;

    private boolean myInUpdate = false;

    public JComponent getComponent() {
        return myMainPanel;
    }

    private final SettingsComponents<ArduinoApplicationSettings> components;
    final ArduinoApplicationSettings mySettings;

    @SuppressWarnings("StatementWithEmptyBody")
    public ApplicationSettingsForm(ArduinoApplicationSettings settings) {
        mySettings = settings;

        final String boardsTxt = ArduinoConfig.Companion.getBoardsTxtString();
        final String programmersTxt = ArduinoConfig.Companion.getProgrammersTxtString();
        final String toolchainTxt = ArduinoConfig.Companion.getToolchainTxtString();
        final String toolchainTxt2 = ArduinoConfig.Companion.getToolchainTxtString2();
        myArduinoConfig = new ArduinoConfig(boardsTxt, programmersTxt);
        //noinspection unchecked,rawtypes
        components = new SettingsComponents<>(mySettings) {
            @Override
            protected Settable[] createComponents(@NotNull ArduinoApplicationSettings i) {
                return new Settable[] {
                        component(myAuthorName, i::getAuthorName, i::setAuthorName),
                        component(myAuthorEMail, i::getAuthorEMail, i::setAuthorEMail),
                        component(myBundledBoardsTxt, i::isBundledBoardsTxt, i::setBundledBoardsTxt),
                        component(myBundledProgrammersTxt, i::isBundledProgrammersTxt, i::setBundledProgrammersTxt),
                        component(myBundledTemplates, i::isBundledTemplates, i::setBundledTemplates),
                        component(myBundledToolchainTxt, i::isBundledToolchainTxt, i::setBundledToolchainTxt),
                        component(myBoardsTxtPath.getTextField(), i::getBoardsTxtPath, i::setBoardsTxtPath),
                        component(myProgrammersTxtPath.getTextField(), i::getProgrammersTxtPath, i::setProgrammersTxtPath),
                        component(myTemplatesPath.getTextField(), i::getTemplatesPath, i::setTemplatesPath),
                        component(myToolchainTxtPath.getTextField(), i::getToolchainTxtPath, i::setToolchainTxtPath),
                        component(myDisconnectRestTime, i::getDisconnectRestTime, i::setDisconnectRestTime),
                        //component(myNewSketchForm, i),
                        //component(myNewLibraryForm, i),
                };
            }
        };

        ApplicationManager.getApplication().getMessageBus().connect(this).subscribe(ApplicationSettingsListener.TOPIC, this);

        ActionListener listener = e -> updateOptions(false);

        DocumentAdapter change = new DocumentAdapter() {
            @Override
            protected void textChanged(@NotNull final DocumentEvent e) {
                updateOptions(false);
            }
        };

        myBundledBoardsTxt.addActionListener(listener);
        myBundledProgrammersTxt.addActionListener(listener);
        myBundledTemplates.addActionListener(listener);
        myBundledToolchainTxt.addActionListener(listener);

        //noinspection DialogTitleCapitalization
        myBoardsTxtPath.addBrowseFolderListener(Bundle.message("settings.boards-file.is-directory.title"), null, null,
                new FileChooserDescriptor(true, false, false, false, false, false),
                TextComponentAccessor.TEXT_FIELD_WHOLE_TEXT
        );

        //noinspection DialogTitleCapitalization
        myProgrammersTxtPath.addBrowseFolderListener(Bundle.message("settings.programmers-file-directory.title"), null, null,
                new FileChooserDescriptor(true, false, false, false, false, false),
                TextComponentAccessor.TEXT_FIELD_WHOLE_TEXT
        );

        myTemplatesPath.addBrowseFolderListener(Bundle.message("settings.templates.file-directory.title"), null, null,
                new FileChooserDescriptor(false, true, false, false, false, false),
                TextComponentAccessor.TEXT_FIELD_WHOLE_TEXT
        );

        myToolchainTxtPath.addBrowseFolderListener(Bundle.message("settings.toolchain-txt.file-directory.title"), null, null,
                new FileChooserDescriptor(true, true, false, false, false, false),
                TextComponentAccessor.TEXT_FIELD_WHOLE_TEXT
        );

        myBoardsTxtPath.getTextField().getDocument().addDocumentListener(change);
        myProgrammersTxtPath.getTextField().getDocument().addDocumentListener(change);
        myTemplatesPath.getTextField().getDocument().addDocumentListener(change);
        myToolchainTxtPath.getTextField().getDocument().addDocumentListener(change);

        myBoardsTxtDiff.addActionListener(new DiffActionListener(
                null
                , Bundle.message("settings.boards-txt.diff.title")
                , boardsTxt
                , null
                , Bundle.message("settings.bundled.0.title", "boards.txt")
                , () -> myBoardsTxtPath.getText()
        ));

        myProgrammersTxtDiff.addActionListener(new DiffActionListener(
                null
                , Bundle.message("settings.programmers-txt-diff.title")
                , programmersTxt
                , null
                , Bundle.message("settings.bundled.0.title", "programmers.txt")
                , () -> myProgrammersTxtPath.getText()
        ));

        myToolchainTxtDiff.addActionListener(new DiffActionListener(
                null
                , Bundle.message("settings.toolchain-txt.diff.title")
                , toolchainTxt
                , FileTypeManager.getInstance().getFileTypeByExtension("cmake")
                , Bundle.message("settings.bundled.0.title", "ArduinoToolchain.cmake")
                , () -> myToolchainTxtPath.getText()
        ));
        
        myToolchainTxtDiff2.addActionListener(new DiffActionListener(
                null
                , Bundle.message("settings.toolchain-txt.diff2.title")
                , toolchainTxt2
                , FileTypeManager.getInstance().getFileTypeByExtension("cmake")
                , Bundle.message("settings.bundled.0.title", "Platform/Arduino.cmake")
                , () -> new File(new File(myToolchainTxtPath.getText()).getParentFile(), "Platform/Arduino.cmake").getPath()
        ));
        
        myTemplatesCreate.addActionListener(e -> {
            // copy templates to location
            String templatesPathText = myTemplatesPath.getText();
            if (!myBundledTemplates.isSelected() && !templatesPathText.trim().isEmpty()) {
                File file = new File(templatesPathText);
                if (!file.exists()) {
                    // TODO: move this out to utils as two functions: parentPath() and canCreatePath()
                    String parentPath = file.getPath();
                    boolean canCreate = false;

                    while (!parentPath.isEmpty()) {
                        File parent = new File(parentPath);
                        if (parent.exists()) {
                            canCreate = parent.isDirectory() && parent.canRead() && parent.canWrite();
                            break;
                        }

                        int pos = parentPath.lastIndexOf(File.separator);
                        if (pos <= 0) break;

                        parentPath = parentPath.substring(0, pos);
                    }

                    if (canCreate) {
                        try {
                            TemplateResolver.INSTANCE.copyTemplatesDirectoryTo(file);
                            //FileUtil.copyDir(bundledTemplateDir, file);
                            updateOptions(false);
                            Messages.showMessageDialog(
                                    myMainPanel,
                                    Bundle.message("settings.templates.create-success.label", file.getPath()),
                                    Bundle.message("settings.templates.create-success.title"),
                                    Messages.getInformationIcon());
                        } catch (IOException e1) {
                            LOG.info("Create template dir failed", e1);

                            Messages.showMessageDialog(
                                    myMainPanel,
                                    Bundle.message("settings.templates.create-failed.label"),
                                    Bundle.message("settings.templates.create-failed.title"),
                                    Messages.getErrorIcon());
                        }
                    }
                }
            }
        });

        myToolchainTxtCreate.addActionListener(e -> {
            // copy templates to location
            String toolchainTxtPathText = myToolchainTxtPath.getText();
            if (!myBundledToolchainTxt.isSelected() && !toolchainTxtPathText.trim().isEmpty()) {
                File file = new File(toolchainTxtPathText);
                if (!file.exists()) {
                    // TODO: move this out to utils as two functions: parentPath() and canCreatePath()
                    String parentPath = file.getPath();
                    boolean canCreate = false;

                    while (!parentPath.isEmpty()) {
                        File parent = new File(parentPath);
                        if (parent.exists()) {
                            canCreate = parent.isDirectory() && parent.canRead() && parent.canWrite();
                            break;
                        }

                        int pos = parentPath.lastIndexOf(File.separator);
                        if (pos <= 0) break;

                        parentPath = parentPath.substring(0, pos);
                    }

                    if (canCreate) {
                        File[] files = ArduinoToolchainFiles.copyToDirectory(file.getParentFile());

                        if (files.length != 0) {
                            updateOptions(false);
                            Messages.showMessageDialog(
                                    myMainPanel,
                                    Bundle.message("settings.toolchain-txt.create-success.label", file.getParentFile().getPath()),
                                    Bundle.message("settings.toolchain-txt.create-success.title"),
                                    Messages.getInformationIcon());
                        } else {
                            Messages.showMessageDialog(
                                    myMainPanel,
                                    Bundle.message("settings.toolchain-txt.create-failed.label"),
                                    Bundle.message("settings.toolchain-txt.create-failed.title"),
                                    Messages.getErrorIcon());
                        }
                    }
                }
            }
        });

        updateOptions(true);
    }

    @SuppressWarnings({ "ConstantValue", "ReassignedVariable" })
    void updateOptions(boolean onSet) {
        if (myInUpdate) return;
        try {
            myInUpdate = true;
            myBoardsTxtPath.setEnabled(!myBundledBoardsTxt.isSelected());
            myProgrammersTxtPath.setEnabled(!myBundledProgrammersTxt.isSelected());
            myTemplatesPath.setEnabled(!myBundledTemplates.isSelected());
            myToolchainTxtPath.setEnabled(!myBundledToolchainTxt.isSelected());

            if (myBundledBoardsTxt.isSelected()) {
                myBoardsTxtDiff.setVisible(false);
                myBoardsTxtLabel.setVisible(true);
            }

            if (myBundledProgrammersTxt.isSelected()) {
                myProgrammersTxtDiff.setVisible(false);
                myProgrammersTxtLabel.setVisible(true);
            }

            if (myBundledTemplates.isSelected()) {
                myTemplatesDiff.setVisible(false);
                myTemplatesCreate.setVisible(false);
                myTemplatesPathLabel.setVisible(true);
            }

            if (myBundledToolchainTxt.isSelected()) {
                myToolchainTxtDiff.setVisible(false);
                myToolchainTxtDiff2.setVisible(false);
                myToolchainTxtCreate.setVisible(false);
                myToolchainTxtLabel.setVisible(true);
            }

            if (myBoardsTxtPath.isEnabled()) {
                String boardsTxtPath = myBoardsTxtPath.getText();
                String error = "";
                boolean canShowDiff = false;
                if (boardsTxtPath.isEmpty()) {
                    // using bundled boards txt
                    error = Bundle.message("settings.bundled-boards-txt.message");
                } else {
                    // try to open and read the file
                    File file = new File(boardsTxtPath);
                    if (!file.exists()) {
                        // should not happen
                        error = "<html><body><span style='color: red'>" + Bundle.message("settings.boards-txt.not-exists.message") + "</span></body></html>";
                    } else if (file.isDirectory()) {
                        error = "<html><body><span style='color: red'>" + Bundle.message("settings.boards-txt.is-directory.message") + "</span></body></html>";
                    } else {
                        canShowDiff = true;
                        String fileTxt = ResourceUtils.getFileContent(file);
                        ArduinoConfig arduinoConfig = new ArduinoConfig(fileTxt, "");
                        if (arduinoConfig.getBoardIdMap().size() == 0) {
                            // invalid
                            error = "<html><body><span style='color: red'>" + Bundle.message("settings.invalid-boards-txt.message") + "</span></body></html>";
                        }
                    }
                }

                if (!error.isEmpty()) {
                    myBoardsTxtLabel.setText(error);
                    myBoardsTxtLabel.setVisible(true);
                    myBoardsTxtDiff.setVisible(canShowDiff);

                    if (onSet) {
                        myBundledBoardsTxt.setSelected(true);
                    }
                } else {
                    myBoardsTxtLabel.setVisible(false);
                    myBoardsTxtDiff.setVisible(true);
                }
            } else {
                String error = Bundle.message("settings.bundled-boards-txt.message");
                myBoardsTxtLabel.setText(error);
            }

            if (myProgrammersTxtPath.isEnabled()) {
                String programmersTxtPath = myProgrammersTxtPath.getText();
                String error = "";
                boolean canShowDiff = false;
                if (programmersTxtPath.isEmpty()) {
                    // using bundled programmers txt
                    error = Bundle.message("settings.bundled-programmers-txt.message");
                } else {
                    // try to open and read the file
                    File file = new File(programmersTxtPath);
                    if (!file.exists()) {
                        // should not happen
                        error = "<html><body><span style='color: red'>" + Bundle.message("settings.programmers-txt.not-exists.message") + "</span></body></html>";
                    } else if (file.isDirectory()) {
                        error = "<html><body><span style='color: red'>" + Bundle.message("settings.programmers-txt.directory.message") + "</span></body></html>";
                    } else {
                        canShowDiff = true;
                        String fileTxt = ResourceUtils.getFileContent(file);
                        ArduinoConfig arduinoConfig = new ArduinoConfig("", fileTxt);
                        if (arduinoConfig.getProgrammerIdMap().size() == 0) {
                            // invalid
                            error = "<html><body><span style='color: red'>" + Bundle.message("settings.invalid-programmers-txt.message") + "</span></body></html>";
                        }
                    }
                }

                if (!error.isEmpty()) {
                    myProgrammersTxtLabel.setText(error);
                    myProgrammersTxtLabel.setVisible(true);
                    myProgrammersTxtDiff.setVisible(canShowDiff);

                    if (onSet) {
                        myBundledProgrammersTxt.setSelected(true);
                    }
                } else {
                    myProgrammersTxtLabel.setVisible(false);
                    myProgrammersTxtDiff.setVisible(true);
                }
            } else {
                String error = Bundle.message("settings.bundled-programmers-txt.message");
                myProgrammersTxtLabel.setText(error);
            }

            if (myTemplatesPath.isEnabled()) {
                String templatesPath = myTemplatesPath.getText();
                String error = "";
                boolean canCreate = false;
                boolean canShowDiff = false;
                if (templatesPath.isEmpty()) {
                    // using bundled programmers txt
                    error = Bundle.message("settings.bundled-templates.message");
                } else {
                    // try to open and read the file
                    File file = new File(templatesPath);
                    if (!file.exists()) {
                        // should not happen
                        String parentPath = file.getPath();

                        while (!parentPath.isEmpty()) {
                            File parent = new File(parentPath);
                            if (parent.exists()) {
                                canCreate = parent.isDirectory() && parent.canRead() && parent.canWrite();
                                break;
                            }
                            int pos = parentPath.lastIndexOf(File.separator);
                            if (pos <= 0) break;

                            parentPath = parentPath.substring(0, pos);
                        }

                        if (!canCreate) {
                            error = "<html><body><span style='color: red'>" + Bundle.message("settings.templates.not-exists.rd-only.message") + "</span></body></html>";
                        } else {
                            error = "<html><body><span style='color: red'>" + Bundle.message("settings.templates.not-exists.message") + "</span></body></html>";
                        }
                    } else if (file.isFile()) {
                        error = "<html><body><span style='color: red'>" + Bundle.message("settings.file-templates.message") + "</span></body></html>";
                    } else {
                        //noinspection DataFlowIssue
                        canShowDiff = false; // does not work for non-project directories

                        // should have all files in respective template types
                        if (!TemplateResolver.INSTANCE.haveAllTemplates(file)) {
                            // invalid
                            error = "<html><body><span style='color: red'>" + Bundle.message("settings.invalid-templates.message") + "</span></body></html>";
                        }
                    }
                }

                if (!error.isEmpty()) {
                    myTemplatesPathLabel.setText(error);
                    myTemplatesPathLabel.setVisible(true);
                    myTemplatesDiff.setVisible(canShowDiff);
                    myTemplatesCreate.setVisible(canCreate);

                    if (onSet) {
                        myBundledTemplates.setSelected(true);
                    }
                } else {
                    myTemplatesPathLabel.setVisible(false);
                    myTemplatesDiff.setVisible(canShowDiff);
                    myTemplatesCreate.setVisible(false);
                }
            } else {
                String error = Bundle.message("settings.bundled-templates.message");
                myTemplatesPathLabel.setText(error);
            }

            if (myToolchainTxtPath.isEnabled()) {
                String toolchainTxtPathText = myToolchainTxtPath.getText();
                String error = "";
                boolean canCreate = false;
                boolean canShowDiff = false;
                if (toolchainTxtPathText.isEmpty()) {
                    error = Bundle.message("settings.bundled-toolchain.message");
                } else {
                    // try to open and read the file
                    File file = new File(toolchainTxtPathText);
                    if (!file.exists() || file.isDirectory()) {
                        if (file.isDirectory()) {
                            // add file name automatically 
                            Path toolchainPath = new File(toolchainTxtPathText, "ArduinoToolchain.cmake").toPath();
                            toolchainTxtPathText = toolchainPath.toString();
                            file = toolchainPath.toFile();
                            String finalToolchainTxtPathText = toolchainTxtPathText;
                            ApplicationManager.getApplication().invokeLater(() -> myToolchainTxtPath.setText(finalToolchainTxtPathText));
                        } else {
                            if (!file.getName().equals("ArduinoToolchain.cmake")) {
                                error = "<html><body><span style='color: red'>" + Bundle.message("settings.toolchain-txt.not-exists.not-arduino-cmake.message") + "</span></body></html>";
                            }
                        }

                        if (error.isEmpty()) {
                            String parentPath = file.getParent();
                            String missingParentPath = parentPath;

                            while (!parentPath.isEmpty()) {
                                File parent = new File(parentPath);
                                if (parent.exists()) {
                                    canCreate = parent.isDirectory() && parent.canRead() && parent.canWrite();
                                    break;
                                }
                                int pos = parentPath.lastIndexOf(File.separator);
                                if (pos <= 0) break;

                                missingParentPath = parentPath;
                                parentPath = parentPath.substring(0, pos);
                            }

                            if (!canCreate) {
                                error = "<html><body><span style='color: red'>" + Bundle.message("settings.toolchain-txt.not-exists-rd-only.0.message", missingParentPath) + "</span></body></html>";
                            } else {
                                if (!file.getParentFile().exists()) {
                                    error = "<html><body><span style='color: red'>" + Bundle.message("settings.toolchain-txt.not-exists-directory.0.message", missingParentPath) + "</span></body></html>";
                                } else {
                                    error = "<html><body><span style='color: red'>" + Bundle.message("settings.toolchain-txt.not-exists.message") + "</span></body></html>";
                                }
                            }
                        }
                    } else if (file.isDirectory()) {
                        error = "<html><body><span style='color: red'>" + Bundle.message("settings.toolchain-txt.is-directory.message") + "</span></body></html>";
                    } else {
                        // not necessarily our bundled toolchain
                        if (file.getName().equals("ArduinoToolchain.cmake")) {
                            canShowDiff = true;
                        } else {
                            error = "<html><body><span style='color: red'>" + Bundle.message("settings.toolchain-txt.not-arduino-cmake.message") + "</span></body></html>";
                        }
                    }
                }

                if (!error.isEmpty()) {
                    myToolchainTxtLabel.setText(error);
                    myToolchainTxtLabel.setVisible(true);
                    myToolchainTxtDiff.setVisible(canShowDiff);
                    myToolchainTxtDiff2.setVisible(canShowDiff);
                    myToolchainTxtCreate.setVisible(canCreate);

                    if (onSet) {
                        myBundledToolchainTxt.setSelected(true);
                    }
                } else {
                    myToolchainTxtLabel.setVisible(false);
                    myToolchainTxtDiff.setVisible(canShowDiff);
                    myToolchainTxtDiff2.setVisible(canShowDiff);
                    myToolchainTxtCreate.setVisible(false);
                }
            } else {
                String error = Bundle.message("settings.bundled-toolchain.message");
                myToolchainTxtLabel.setText(error);
            }
        } finally {
            myInUpdate = false;
        }
    }

    @Override
    public void onSettingsChanged(ArduinoApplicationSettings settings) {
        if (settings == mySettings) {
            reset(mySettings);
        }
    }

    public boolean isModified(@NotNull ArduinoApplicationSettings settings) {
        return components.isModified(settings);
    }

    public void apply(@NotNull ArduinoApplicationSettings settings) {
        if (isModified(settings)) {
            components.apply(settings);
        }
    }

    public void reset(@NotNull ArduinoApplicationSettings settings) {
        if (isModified(settings)) {
            components.reset(settings);
            updateOptions(true);
        }
    }

    @Override
    public void dispose() {

    }

    private void createUIComponents() {
        final SpinnerNumberModel model = new SpinnerNumberModel(20, 0, 100, 5);
        myDisconnectRestTime = new JSpinner(model);
    }
}
