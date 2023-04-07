/*
 * Copyright (c) 2015-2018 Vladimir Schneider <vladimir.schneider@gmail.com>, all rights reserved.
 *
 * This code is private property of the copyright holder and cannot be used without
 * having obtained a license or prior written permission of the of the copyright holder.
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 *
 */
package com.vladsch.clionarduinoplugin.settings;

import com.intellij.diff.DiffContentFactoryEx;
import com.intellij.diff.DiffManager;
import com.intellij.diff.contents.DiffContent;
import com.intellij.diff.requests.SimpleDiffRequest;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.application.ModalityState;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.fileTypes.PlainTextFileType;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.ui.ValidationInfo;
import com.intellij.openapi.util.Disposer;
import com.intellij.openapi.util.io.FileUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.jetbrains.cidr.cpp.cmake.workspace.CMakeWorkspace;
import com.vladsch.clionarduinoplugin.Bundle;
import com.vladsch.clionarduinoplugin.generators.ArduinoProjectGenerator;
import com.vladsch.clionarduinoplugin.generators.cmake.ArduinoCMakeListsTxtBuilder;
import com.vladsch.flexmark.util.html.ui.BackgroundColor;
import com.vladsch.flexmark.util.html.ui.HtmlBuilder;
import com.vladsch.plugin.util.ui.Helpers;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.Action;
import javax.swing.JComponent;
import javax.swing.JEditorPane;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.nio.file.Path;

public class ProjectBuildSettingsDialog extends DialogWrapper {
    //private static final Logger logger = com.intellij.openapi.diagnostic.Logger.getInstance("com.vladsch.idea.multimarkdown.settings.license.fetch-dialog");

    private JPanel myMainPanel;
    private JPanel myFormPanel;
    private JEditorPane myNotificationEditorPane;
    private JLabel myNotificationIcon;
    private JPanel myNotificationsPanel;
    NewProjectSettingsForm myNewProjectSettingsForm;

    final Project myProject;
    final VirtualFile myFile;
    final ArduinoApplicationSettingsProxy mySettings;
    final ArduinoApplicationSettingsProxy myResetSettings;
    //final Alarm myAlarm;

    private ProjectBuildSettingsDialog(Project project, ArduinoApplicationSettingsProxy settings, final VirtualFile file) {
        super(project, false);
        myProject = project;
        mySettings = settings;
        myResetSettings = ArduinoApplicationSettingsProxy.copyOf(settings);
        myFile = file;
        //myAlarm = new Alarm();

        init();
        setTitle(Bundle.message("settings.project-build-settings.title"));
        setModal(true);

        myNewProjectSettingsForm.setRunnable(this::updateOptions);
        myNewProjectSettingsForm.reset(myResetSettings.getApplicationSettings());
        updateOptions();

        String[] notifications = settings.getNotifications();
        if (notifications.length > 0) {
            HtmlBuilder builder = new HtmlBuilder();

            JTextField label = new JTextField();
            Font font = label.getFont();
            Color textColor = label.getForeground();
            Color textBackColor = BackgroundColor.of(myMainPanel.getBackground());
            Color errorColor = Helpers.errorColor(textColor);
            Color highlightColor = label.getSelectedTextColor();
            Color highlightBackColor = BackgroundColor.of(label.getSelectionColor());

            builder.tag("html").attr(font).tag("body");

            // add warning icon from GitHub

            for (String notification : notifications) {
                String[] parts = notification.split("\r");
                String text = Bundle.message(parts[0]);
                if (parts.length > 1) {
                    String[] convertedParts = new String[parts.length - 1];

                    for (int i = 1; i < parts.length; i++) {
                        HtmlBuilder snippet = new HtmlBuilder();
                        snippet.attr(i == 1 ? errorColor : i == 2 ? highlightColor : textColor).attr(i > 2 ? textBackColor : highlightBackColor).span(parts[i]);
                        convertedParts[i - 1] = snippet.toFinalizedString();
                    }

                    builder.raw(Bundle.message(parts[0], (Object[]) convertedParts));
                } else {
                    builder.attr(errorColor).tag("p").span().raw(text);
                }
            }

            String html = builder.toFinalizedString();
            myNotificationEditorPane.setText(html);
            myNotificationEditorPane.setVisible(true);
            myNotificationsPanel.setVisible(true);
            //myNotificationEditorPane.setOpaque(true);
        } else {
            myNotificationsPanel.setVisible(false);
        }

        //descriptionEditorPane.setText("" +
        //        "<html>\n" +
        //        "  <head>\n" +
        //        "  <style>\n" +
        //        //"     body.multimarkdown-preview, body.multimarkdown-preview p, body.multimarkdown-preview p.error { background: transparent !important;}\n" +
        //        "     body.multimarkdown-preview, body.multimarkdown-preview div { margin: 0 !important; padding: 0 !important; font-family: sans-serif; font-size: " + JBUI.scale(11) + "px; }\n" +
        //        "     body.multimarkdown-preview p { margin: " + JBUI.scale(10) + "px !important; padding: 0 !important; }\n" +
        //        "     p.error { color: #ec2e5c; }\n" +
        //        "  </style>\n" +
        //        "  </head>\n" +
        //        "  <body class='multimarkdown-preview'>\n" +
        //        "    <p>\n" +
        //        "      " + MultiMarkdownBundle.message("settings.license-fetch.description") + "\n" +
        //        "    </p>\n" +
        //        "  </body>\n" +
        //        "</html>\n" +
        //        "");
    }

    @Nullable
    @Override
    protected String getDimensionServiceKey() {
        return "ArduinoSupport.ProjectBuildSettingsDialog";
    }

    private void createUIComponents() {
        // TODO: place custom component creation code here
    }

    //void updateOptions() {
    //    if (myAlarm.isDisposed()) return;
    //
    //    myAlarm.cancelAllRequests();
    //    myAlarm.addRequest(this::updateOptionsRaw, 500);
    //}

    void updateOptions() {
        ApplicationManager.getApplication().invokeLater(() -> {
            boolean enabled = myNewProjectSettingsForm.isModified(mySettings.getApplicationSettings());

            if (myShowDiffAction != null && myResetSettings != null && myOkAction != null) {
                myResetAction.setEnabled(enabled);

                if (!enabled) {
                    String content = getCMakeFileContent();
                    enabled = !content.equals(getModifiedContent(content));
                }

                myOkAction.setEnabled(enabled);
                myShowDiffAction.setEnabled(enabled);
            }
        }, ModalityState.any());
    }

    @Nullable
    @Override
    protected JComponent createCenterPanel() {
        myNewProjectSettingsForm = new NewProjectSettingsForm(mySettings, false, true);
        myFormPanel.add(myNewProjectSettingsForm.getComponent(), BorderLayout.CENTER);
        return myMainPanel;
    }

    protected class MyOkAction extends OkAction {
        protected MyOkAction() {
            super();
            putValue(Action.NAME, Bundle.message("settings.project-build-settings.ok.label"));
        }

        @Override
        protected void doAction(ActionEvent e) {
            if (doValidate(true) == null) {
                getOKAction().setEnabled(true);
            }
            super.doAction(e);
        }
    }

    protected class MyAction extends OkAction {
        final private Runnable runnable;

        protected MyAction(String name, Runnable runnable) {
            super();
            putValue(Action.NAME, name);
            this.runnable = runnable;
        }

        @Override
        protected void doAction(ActionEvent e) {
            runnable.run();
        }
    }

    String getCMakeFileContent() {
        Document document = FileDocumentManager.getInstance().getDocument(myFile);
        if (document != null) {
            return document.getText();
        } else {
            try {
                return new String(myFile.contentsToByteArray());
            } catch (IOException e) {
                return "";
            }
        }
    }

    String getModifiedContent(String content) {
        ArduinoApplicationSettingsProxy saved = ArduinoApplicationSettingsProxy.copyOf(mySettings);
        myNewProjectSettingsForm.apply(mySettings.getApplicationSettings());

        CMakeWorkspace workspace = CMakeWorkspace.getInstance(myProject);
        Path projectDir = workspace.getProjectPath().getFileName();
        String name = FileUtil.sanitizeFileName(projectDir.toString());

        String modifiedContent = ArduinoCMakeListsTxtBuilder.Companion.getCMakeFileContent(content, name, mySettings, true);
        mySettings.copyFrom(saved);
        return modifiedContent;
    }

    static DiffContent getContent(String content) {
        return DiffContentFactoryEx.getInstanceEx().create(content, PlainTextFileType.INSTANCE, true);
    }

    MyAction myResetAction = null;
    MyAction myShowDiffAction = null;

    @NotNull
    protected Action[] createLeftSideActions() {
        myResetAction = new MyAction(Bundle.message("settings.project-build-settings.reset.label"), new Runnable() {
            @Override
            public void run() {
                mySettings.copyFrom(myResetSettings);
                myNewProjectSettingsForm.reset(mySettings.getApplicationSettings());
                updateOptions();
            }
        });

        myShowDiffAction = new MyAction(Bundle.message("settings.project-build-settings.show-diff.label"), new Runnable() {
            @Override
            public void run() {
                String original = getCMakeFileContent();
                String modified = getModifiedContent(original);
                SimpleDiffRequest request = new SimpleDiffRequest(
                        Bundle.message("settings.project-build-settings.show-diff.title"),
                        getContent(original),
                        getContent(modified),
                        myFile.getName(),
                        Bundle.message("settings.project-build-settings.modified.title")
                );
                DiffManager.getInstance().showDiff(null, request);
            }
        });

        return new Action[] {
                myResetAction,
                myShowDiffAction,
        };
    }

    MyOkAction myOkAction = null;

    @NotNull
    @Override
    protected Action[] createActions() {
        super.createDefaultActions();
        myOkAction = new MyOkAction();
        return new Action[] { myOkAction, getCancelAction() };
    }

    public static boolean showDialog(Project project, ArduinoApplicationSettingsProxy settings, final VirtualFile file) {
        ProjectBuildSettingsDialog dialog = new ProjectBuildSettingsDialog(project, settings, file);
        if (dialog.showAndGet()) {
            final String content = dialog.getCMakeFileContent();
            final String modifiedContent = dialog.getModifiedContent(content);
            if (!modifiedContent.equals(content)) {
                ApplicationManager.getApplication().runWriteAction(new Runnable() {
                    @Override
                    public void run() {
                        Document document = FileDocumentManager.getInstance().getDocument(dialog.myFile);
                        if (document != null) {
                            document.setText(modifiedContent);
                        } else {
                            try {
                                dialog.myFile.setBinaryContent(modifiedContent.getBytes());
                            } catch (IOException e) {
                                Messages.showErrorDialog(project, Bundle.message("settings.project-build-settings.save-failed.message", e.getMessage()), Bundle.message("settings.project-build-settings.save-failed.title"));
                            }
                        }

                        ArduinoProjectGenerator.Companion.reloadCMakeLists(dialog.myProject);
                        Disposer.dispose(dialog.myNewProjectSettingsForm);
                    }
                });
            } else {
                Disposer.dispose(dialog.myNewProjectSettingsForm);
            }
            return true;
        }
        return false;
    }

    protected ValidationInfo doValidate(boolean loadLicense) {
        ArduinoApplicationSettings settings = new ArduinoApplicationSettings();
        myNewProjectSettingsForm.apply(settings);

        ValidationInfo result = ArduinoProjectGenerator.Companion.validateOptionsInfo(settings, myNewProjectSettingsForm);
        if (result != null) {
            return result;
        }

        return super.doValidate();
    }

    @Nullable
    @Override
    protected ValidationInfo doValidate() {
        return doValidate(false);
    }

    @Nullable
    @Override
    public JComponent getPreferredFocusedComponent() {
        return myNewProjectSettingsForm.getPreferredFocusedComponent();
    }
}
