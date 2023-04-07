package com.vladsch.clionarduinoplugin.serial;

import com.intellij.execution.actions.ClearConsoleAction;
import com.intellij.execution.filters.TextConsoleBuilderImpl;
import com.intellij.execution.impl.ConsoleViewImpl;
import com.intellij.execution.ui.ConsoleView;
import com.intellij.execution.ui.ConsoleViewContentType;
import com.intellij.openapi.Disposable;
import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.actionSystem.ActionToolbar;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.DefaultActionGroup;
import com.intellij.openapi.editor.actions.ScrollToTheEndToolbarAction;
import com.intellij.openapi.editor.actions.ToggleUseSoftWrapsToolbarAction;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Disposer;
import com.intellij.ui.components.JBTextField;
import com.vladsch.clionarduinoplugin.actions.ConnectAction;
import com.vladsch.clionarduinoplugin.actions.EditSettingsAction;
import com.vladsch.clionarduinoplugin.actions.ShowSendOptionsAction;
import com.vladsch.clionarduinoplugin.settings.ArduinoProjectSettings;
import com.vladsch.clionarduinoplugin.settings.SendSettingsForm;
import com.vladsch.clionarduinoplugin.util.ProjectSettingsListener;
import jssc.SerialPortEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

public class SerialMonitorPanel implements Disposable, SerialPortListener, ProjectSettingsListener {
    final static public ConsoleViewContentType LOG_ERROR_OUTPUT = new ConsoleViewContentType("LOG_ERROR_OUTPUT", ConsoleViewContentType.LOG_ERROR_OUTPUT_KEY);
    final static public ConsoleViewContentType CONNECTION_STATUS = ConsoleViewContentType.SYSTEM_OUTPUT;
    static public ConsoleViewContentType ALT_CONNECTION_STATUS = null;

    private JPanel myPanel;
    private JButton mySendButton;
    private JPanel myConsoleHolder;
    private JPanel myToolbarPanel;
    JBTextField mySendText;
    SendSettingsForm mySendSettingsForm;
    private @Nullable ConsoleView myConsoleView;

    @NotNull final Project myProject;
    final ArduinoProjectSettings myProjectSettings;

    public SerialMonitorPanel(@NotNull Project project) {
        myProject = project;
        createConsole();

        myProjectSettings = ArduinoProjectSettings.getInstance(myProject);
        mySendButton.addActionListener(e -> {
            // send the text
            handleEnter();
        });

        mySendText.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(final KeyEvent e) {
                if (e.getKeyChar() == '\n') {
                    handleEnter();
                } else {
                    super.keyTyped(e);
                    if (myProjectSettings.isImmediateSend()) {
                        SerialProjectComponent.getInstance(myProject).send(String.valueOf(e.getKeyChar()).getBytes());
                    }
                }
            }
        });

        mySendText.setEnabled(false);
        mySendButton.setEnabled(false);

        //noinspection ThisEscapedInObjectConstruction
        project.getMessageBus().connect(this).subscribe(ProjectSettingsListener.TOPIC, this);

        mySendSettingsForm.getComponent().setVisible(myProjectSettings.isShowSendOptions());
        mySendSettingsForm.setChangeMonitor(() -> mySendSettingsForm.apply(myProjectSettings));

        mySendSettingsForm.reset(myProjectSettings);
    }

    void handleEnter() {
        String eol = myProjectSettings.getSerialEndOfLineType().eol;
        if (myProjectSettings.isImmediateSend()) {
            SerialProjectComponent.getInstance(myProject).send(eol.getBytes());
        } else {
            SerialProjectComponent.getInstance(myProject).send((mySendText.getText() + eol).getBytes());
        }
        mySendText.setText("");
    }

    @Nullable
    public ConsoleView getConsoleView() {
        return myConsoleView;
    }

    @Override
    public void onSettingsChanged() {
        mySendSettingsForm.reset(myProjectSettings);
        mySendSettingsForm.getComponent().setVisible(myProjectSettings.isShowSendOptions());

        //mySendButton.setEnabled(settings.isImmediateSend());
    }

    private void createConsole() {
        TextConsoleBuilderImpl consoleBuilder = new TextConsoleBuilderImpl(myProject);
        consoleBuilder.setViewer(true);

        myConsoleView = consoleBuilder.getConsole();
        JComponent consoleComponent = myConsoleView.getComponent();
        myConsoleHolder.add(consoleComponent, BorderLayout.CENTER);

        DefaultActionGroup actionGroup = new DefaultActionGroup();
        actionGroup.add(new ConnectAction());
        actionGroup.add(new EditSettingsAction());
        actionGroup.add(new ShowSendOptionsAction());
        actionGroup.addAll(filterActions(myConsoleView.createConsoleActions()));

        ActionToolbar toolbar = ActionManager.getInstance().createActionToolbar("com.vladsch.clionarduinoplugin.SerialMonitor", actionGroup, false);
        toolbar.setTargetComponent(consoleComponent);
        myToolbarPanel.add(toolbar.getComponent(), BorderLayout.WEST);

        SerialProjectComponent.getInstance(myProject).addPortListener(this);
    }

    @Override
    public void onReceive(final byte[] buf) {
        if (myConsoleView != null) {
            String s = new String(buf).replaceAll("\r", "");
            myConsoleView.print(s, ConsoleViewContentType.NORMAL_OUTPUT);
        }
    }

    @Override
    public void onSent(final byte[] buf) {
        if (myConsoleView != null && myProjectSettings.isLogSentText()) {
            String s = new String(buf).replaceAll("\r", "");
            myConsoleView.print(s, ConsoleViewContentType.USER_INPUT);
        }
    }

    @Override
    public void onConnect(final String portName, final int baudRate) {
        mySendText.setEnabled(true);
        mySendButton.setEnabled(true);

        if (myConsoleView != null && myProjectSettings.isLogConnectDisconnect()) {
            myConsoleView.print("--------------- Connected " + portName + " @ " + baudRate + " ----------------\n", ALT_CONNECTION_STATUS == null ? CONNECTION_STATUS : ALT_CONNECTION_STATUS);
        }
    }

    @Override
    public void onDisconnect(final String portName, final int baudRate) {
        mySendText.setEnabled(false);
        mySendButton.setEnabled(false);

        if (myConsoleView != null && myProjectSettings.isLogConnectDisconnect()) {
            myConsoleView.print("-------------- Disconnected " + portName + " @ " + baudRate + " --------------\n", ALT_CONNECTION_STATUS == null ? CONNECTION_STATUS : ALT_CONNECTION_STATUS);
        }
    }

    @Override
    public void onEvent(final SerialPortEvent event) {

    }

    @Override
    public void onError(final boolean isError, final String message, final Throwable e) {
        if (myConsoleView != null && myProjectSettings.isLogExceptions()) {
            ConsoleViewContentType viewContentType = isError ? LOG_ERROR_OUTPUT : ConsoleViewContentType.LOG_WARNING_OUTPUT;
            myConsoleView.print(message, viewContentType);
            myConsoleView.print("\n", viewContentType);
            if (e != null) {
                StringWriter out = new StringWriter();
                PrintWriter s = new PrintWriter(out);
                e.printStackTrace(s);
                s.close();
                out.flush();
                try { out.close(); } catch (IOException ignored) { }
                myConsoleView.print(out.toString(), viewContentType);
                myConsoleView.print("\n", viewContentType);
            }
        }
    }

    private static List<AnAction> filterActions(AnAction[] actions) {
        ArrayList<AnAction> filteredActions = new ArrayList<>();
        addIfFoundAction(filteredActions, actions, ToggleUseSoftWrapsToolbarAction.class);
        addIfFoundAction(filteredActions, actions, ScrollToTheEndToolbarAction.class);
        addIfFoundAction(filteredActions, actions, ActionManager.getInstance().getAction("Print").getClass());

        // try to load the new version, if not found try the old
        if (!addIfFoundAction(filteredActions, actions, ClearConsoleAction.class)) {
            //noinspection removal,UnnecessarilyQualifiedInnerClassAccess
            addIfFoundAction(filteredActions, actions, ConsoleViewImpl.ClearAllAction.class);
        }

        return filteredActions;
    }

    private static <T extends AnAction> boolean addIfFoundAction(ArrayList<AnAction> list, AnAction[] actions, Class<T> actionClass) {
        for (AnAction action : actions) {
            if (actionClass.isInstance(action)) {
                list.add(action);
                return true;
            }
        }
        return false;
    }

    public JComponent getComponent() {
        return myPanel;
    }

    @Override
    public void dispose() {
        SerialProjectComponent.getInstance(myProject).removePortListener(this);

        if (myConsoleView != null) {
            Disposer.dispose(myConsoleView);
            myConsoleView = null;
        }
    }
}
