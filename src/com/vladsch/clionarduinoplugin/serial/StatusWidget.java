package com.vladsch.clionarduinoplugin.serial;

import com.intellij.ide.DataManager;
import com.intellij.openapi.actionSystem.ActionGroup;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.ui.popup.JBPopupFactory;
import com.intellij.openapi.ui.popup.JBPopupFactory.ActionSelectionAid;
import com.intellij.openapi.ui.popup.ListPopup;
import com.intellij.openapi.wm.StatusBar;
import com.intellij.openapi.wm.StatusBarWidget;
import com.intellij.util.Consumer;
import com.vladsch.clionarduinoplugin.Bundle;
import com.vladsch.clionarduinoplugin.actions.ConnectAction;
import com.vladsch.clionarduinoplugin.actions.SerialMonitorBaudRateAction;
import com.vladsch.clionarduinoplugin.actions.SerialMonitorPortAction;
import com.vladsch.clionarduinoplugin.settings.ArduinoProjectSettings;
import com.vladsch.clionarduinoplugin.util.ProjectSettingsListener;
import icons.PluginIcons;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.Icon;
import java.awt.event.MouseEvent;

public class StatusWidget implements StatusBarWidget, /*CustomStatusBarWidget,*/ /*StatusBarWidget.IconPresentation,*/ StatusBarWidget.MultipleTextValuesPresentation, ProjectSettingsListener {
    public static final String SERIAL_MONITOR_ID = "ArduinoSupport.SerialMonitor";
    SerialProjectComponent mySerialProjectComponent;
    private StatusBar myStatusBar;
    boolean myActionEnabled;
    private String myText;
    private String myTooltip;
    boolean myIsConnected;
    boolean myCanConnect;
    private SerialPortListener mySerialPortListener;
    private Icon myIcon = null;

    public StatusWidget(@NotNull SerialProjectComponent serialProjectComponent) {
        mySerialProjectComponent = serialProjectComponent;
        myActionEnabled = true;

        //noinspection ThisEscapedInObjectConstruction
        serialProjectComponent.getProject().getMessageBus().connect(this).subscribe(ProjectSettingsListener.TOPIC, this);

        mySerialPortListener = new SerialPortListener() {
            @Override
            public void onStatusChanged() {
                setStatus(!serialProjectComponent.isBuilding(), serialProjectComponent.isPortConnected(), serialProjectComponent.canConnectPort());
            }
        };

        serialProjectComponent.addPortListener(mySerialPortListener);

        setStatus(!serialProjectComponent.isBuilding(), serialProjectComponent.isPortConnected(), serialProjectComponent.canConnectPort());
    }

    @Override
    public void onSettingsChanged() {
        update();
    }

    @Override
    public @NotNull String ID() {
        return SERIAL_MONITOR_ID;
    }

    @Override
    public @NotNull Icon getIcon() {
        return myIcon;
    }

    @Override
    @Nullable("null means the widget is unable to show the popup")
    public ListPopup getPopupStep() {
        if (myStatusBar != null) {
            final ActionGroup group = new MyActionGroup();

            DataContext context = DataManager.getInstance().getDataContext(myStatusBar.getComponent());

            return JBPopupFactory.getInstance().createActionGroupPopup(
                    "Serial Monitor",
                    group,
                    context,
                    ActionSelectionAid.SPEEDSEARCH,
                    true
            );
        }
        return null;
    }

    private static class MyActionGroup extends ActionGroup {
        @Override
        public AnAction @NotNull [] getChildren(@Nullable final AnActionEvent e) {
            ConnectAction connectAction = new ConnectAction();

            return new AnAction[] {
                    connectAction,
                    SerialMonitorPortAction.Companion.createSerialPortsActionGroup(),
                    SerialMonitorBaudRateAction.Companion.createBaudRateActionGroup(),
            };
        }
    }

    @Override
    public @Nullable String getSelectedValue() {
        return myText;
    }

    @Override
    public @Nullable String getTooltipText() {
        ArduinoProjectSettings settings = ArduinoProjectSettings.getInstance(mySerialProjectComponent.getProject());
        return myActionEnabled ? (myIsConnected ? Bundle.message("widget.connected.2.label", settings.getPort(), Integer.toString(settings.getBaudRate()))
                : Bundle.message("widget.disconnected.2.label", settings.getPort(), Integer.toString(settings.getBaudRate())))
                : myTooltip;
    }

    @Override
    public @Nullable Consumer<MouseEvent> getClickConsumer() {
        return null;
    }

    @Override
    public void install(@NotNull final StatusBar statusBar) {
        myStatusBar = statusBar;
    }

    public boolean isDisposed() {
        return mySerialProjectComponent == null;
    }

    @Override
    public void dispose() {
        if (!isDisposed()) {
            myStatusBar = null;
            mySerialProjectComponent.removePortListener(mySerialPortListener);
            mySerialPortListener = null;
            mySerialProjectComponent = null;
            myIcon = null;
            myText = null;
            myTooltip = null;
        }
    }

    public void update() {
        myText = null;
        myIcon = null;
        myTooltip = null;

        if (isDisposed()) return;

        myActionEnabled = true;
        myCanConnect = mySerialProjectComponent.canConnectPort();
        myIcon = myIsConnected ? PluginIcons.serial_port : PluginIcons.serial_port_disconnected;
        int baudRate = ArduinoProjectSettings.getInstance(mySerialProjectComponent.getProject()).getBaudRate();
        myText = Integer.toString(baudRate);

        if (myStatusBar != null) {
            myStatusBar.updateWidget(ID());
        }
    }

    public void setStatus(final boolean isEnabled, final boolean isConnected, final boolean canConnect) {
        setStatus(isEnabled, isConnected, canConnect, "");
    }

    public void setStatus(final boolean isEnabled, final boolean isConnected, final boolean canConnect, final String tooltip) {
        myActionEnabled = isEnabled;
        myCanConnect = canConnect;
        myText = "";
        myTooltip = tooltip;
        myIsConnected = isConnected;
        update();
    }

    @Override
    public @Nullable WidgetPresentation getPresentation() {
        return this;
    }
}
