package com.vladsch.clionarduinoplugin.serial;

import com.intellij.ide.DataManager;
import com.intellij.openapi.actionSystem.ActionGroup;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.application.ModalityState;
import com.intellij.openapi.ui.popup.JBPopupFactory;
import com.intellij.openapi.ui.popup.ListPopup;
import com.intellij.openapi.util.Disposer;
import com.intellij.openapi.wm.CustomStatusBarWidget;
import com.intellij.openapi.wm.StatusBar;
import com.intellij.openapi.wm.StatusBarWidget;
import com.intellij.openapi.wm.impl.status.TextPanel;
import com.intellij.ui.awt.RelativePoint;
import com.intellij.util.Alarm;
import com.intellij.util.Consumer;
import com.intellij.util.ui.UIUtil;
import com.vladsch.clionarduinoplugin.Bundle;
import com.vladsch.clionarduinoplugin.actions.SerialMonitorBaudRateAction;
import com.vladsch.clionarduinoplugin.actions.SerialMonitorPortAction;
import com.vladsch.clionarduinoplugin.settings.ArduinoProjectSettings;
import com.vladsch.clionarduinoplugin.util.ProjectSettingsListener;
import icons.PluginIcons;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.Icon;
import javax.swing.JComponent;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class StatusWidget implements CustomStatusBarWidget, StatusBarWidget.IconPresentation, StatusBarWidget.MultipleTextValuesPresentation, ProjectSettingsListener {
    public static final String SERIAL_MONITOR_ID = "ArduinoSupport.SerialMonitor";
    SerialProjectComponent mySerialProjectComponent;
    private StatusBar myStatusBar;
    private Alarm myUpdate;
    private final TextPanel.WithIconAndArrows myComponent;
    boolean myActionEnabled;
    private boolean myIsDisposed;
    private String myText;
    private String myTooltip;
    boolean myIsConnected;
    boolean myCanConnect;

    public StatusWidget(@NotNull SerialProjectComponent serialProjectComponent) {
        mySerialProjectComponent = serialProjectComponent;
        //noinspection ThisEscapedInObjectConstruction
        myUpdate = new Alarm(this);
        myActionEnabled = true;

        myComponent = new TextPanel.WithIconAndArrows();

        myComponent.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(final MouseEvent e) {
                int button = e.getButton();
                int clickCount = e.getClickCount();
                if (button == 1) {
                    if (myActionEnabled) {
                        if (myIsConnected) {
                            mySerialProjectComponent.disconnectPort();
                        } else {
                            if (myCanConnect) {
                                mySerialProjectComponent.connectPort();
                            } else {
                                update(null);
                                showPopup(e);
                            }
                        }
                    }
                    e.consume();
                } else if (button == 3) {
                    // show popup
                    update(null);
                    showPopup(e);
                    e.consume();
                }
            }
        });

        serialProjectComponent.getProject().getMessageBus().connect(this).subscribe(ProjectSettingsListener.TOPIC, this);
        //public static final Border ICON = JBUI.CurrentTheme.StatusBar.Widget.iconBorder();
        //public static final Border INSTANCE = JBUI.CurrentTheme.StatusBar.Widget.border();
        //public static final Border WIDE = ;

        // DEPRECATED: replacement available only since 221.5080.210 
        // myComponent.setBorder(JBUI.CurrentTheme.StatusBar.Widget.border());
        myComponent.setBorder(WidgetBorder.WIDE);
    }

    public boolean isEnabled() {
        return myActionEnabled && myIsConnected;
    }

    @Override
    public void onSettingsChanged() {
        update(null);
    }

    @Override
    public JComponent getComponent() {
        return myComponent;
    }

    @Override
    public @NotNull String ID() {
        return SERIAL_MONITOR_ID;
    }

    @Override
    public @NotNull Icon getIcon() {
        return myIsConnected ? PluginIcons.serial_port : PluginIcons.serial_port_disconnected;
    }

    @Override
    @Nullable("null means the widget is unable to show the popup")
    public ListPopup getPopupStep() {
        //final ActionGroup group = SerialMonitorPortActionBase.Companion.createSerialPortsActionGroup();
        final ActionGroup group = new ActionGroup() {
            @NotNull
            @Override
            public AnAction[] getChildren(@Nullable final AnActionEvent e) {
                return new AnAction[] {
                        SerialMonitorPortAction.Companion.createSerialPortsActionGroup(),
                        SerialMonitorBaudRateAction.Companion.createBaudRateActionGroup(),
                };
            }
        };

        DataContext context;
        context = DataManager.getInstance().getDataContext(myComponent);

        return JBPopupFactory.getInstance().createActionGroupPopup(
                "Serial Monitor",
                group,
                context,
                JBPopupFactory.ActionSelectionAid.SPEEDSEARCH,
                false
        );
    }

    @Override
    public @Nullable String getSelectedValue() {
        return Integer.toString(ArduinoProjectSettings.getInstance(mySerialProjectComponent.getProject()).getBaudRate());
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
    public @Nullable WidgetPresentation getPresentation() {
        return CustomStatusBarWidget.super.getPresentation();
    }

    @Override
    public void install(@NotNull final StatusBar statusBar) {
        myStatusBar = statusBar;
    }

    @Override
    public void dispose() {
        myIsDisposed = true;
        myUpdate = null;
        myStatusBar = null;
        mySerialProjectComponent = null;
    }

    protected final boolean isDisposed() {
        return myIsDisposed;
    }

    public void update(@Nullable Runnable finishUpdate) {
        if (myIsDisposed || myUpdate.isDisposed()) return;

        myUpdate.cancelAllRequests();
        myUpdate.addRequest(() -> {
            if (myIsDisposed || myUpdate.isDisposed()) return;

            myComponent.setVisible(true);
            myActionEnabled = true;
            myCanConnect = mySerialProjectComponent.canConnectPort();

            if (myActionEnabled) {
                myComponent.setForeground(UIUtil.getActiveTextColor());
                myComponent.setTextAlignment(Component.LEFT_ALIGNMENT);
            } else {
                myComponent.setForeground(UIUtil.getInactiveTextColor());
                myComponent.setTextAlignment(Component.CENTER_ALIGNMENT);
            }

            myComponent.setIcon(getIcon());
            myComponent.setToolTipText(getTooltipText());
            myComponent.setText(myText);
            myComponent.invalidate();

            if (myStatusBar != null) {
                myStatusBar.updateWidget(ID());
            }

            if (finishUpdate != null) {
                finishUpdate.run();
            }
        }, 200, ModalityState.any());
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
        update(null);
    }

    void showPopup(@NotNull MouseEvent e) {
        if (!myActionEnabled) {
            return;
        }

        ListPopup popup = getPopupStep();

        if (popup != null) {
            Dimension dimension = popup.getContent().getPreferredSize();
            Point at = new Point(0, -dimension.height);
            popup.show(new RelativePoint(e.getComponent(), at));
            Disposer.register(this, popup); // destroy popup on unexpected project close
        }
    }
}
