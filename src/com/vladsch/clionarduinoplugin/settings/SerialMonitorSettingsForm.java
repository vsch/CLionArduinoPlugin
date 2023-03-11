/*
 * Copyright (c) 2015-2016 Vladimir Schneider <vladimir.schneider@gmail.com>, all rights reserved.
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

import com.intellij.openapi.Disposable;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import com.intellij.ui.TextFieldWithHistory;
import com.intellij.ui.components.JBCheckBox;
import com.intellij.ui.components.JBTextField;
import com.vladsch.clionarduinoplugin.serial.SerialProjectComponent;
import com.vladsch.flexmark.util.sequence.SequenceUtils;
import com.vladsch.plugin.util.ui.FormParams;
import com.vladsch.plugin.util.ui.Settable;
import com.vladsch.plugin.util.ui.SettingsComponents;
import org.jetbrains.annotations.NotNull;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class SerialMonitorSettingsForm extends FormParams<Boolean> implements Disposable, RegExSettingsHolder {
    private static final Logger logger = Logger.getInstance("com.vladsch.clionarduinoplugin.settings");

    JPanel myMainPanel;
    JComboBox<String> myPort;
    JComboBox<String> myBaudRate;
    JBCheckBox myDisconnectOnBuild;
    JBCheckBox myReconnectAfterBuild;
    JBTextField myBuildConfigurationNames;
    JBTextField myCustomBaudRate;
    JComboBox<String> myBuildConfigurationPattern;
    private JButton myEditRegExButton;
    JBCheckBox myAfterSuccessfulBuild;
    JBCheckBox myLogConnectDisconnect;
    JBCheckBox myActivateOnConnect;
    private JPanel myBuildControlPanel;
    private JLabel myNoBuildMonitorLabel;
    private SendSettingsForm mySendSettings;
    JBCheckBox myReloadOnFileChange;

    private @NotNull String myRegexSampleText;

    public JComponent getComponent() {
        return myMainPanel;
    }

    private final SettingsComponents<ArduinoProjectSettings> components;

    public SerialMonitorSettingsForm(ArduinoProjectSettings settings, boolean allowPortEdit) {
        super(allowPortEdit);

        //noinspection unchecked
        components = new SettingsComponents<ArduinoProjectSettings>(settings) {
            @Override
            protected Settable[] createComponents(ArduinoProjectSettings i) {
                return new Settable[] {
                        componentString(SerialPortNames.ADAPTER, myPort, i::getPort, i::setPort),
                        //component(SerialBaudRates.ADAPTER, myBaudRate, i::getBaudRate, i::setBaudRate),
                        component(() -> {
                            SerialBaudRates baudRates = SerialBaudRates.ADAPTER.get(myBaudRate);
                            if (baudRates.isCustom()) {
                                Integer rate = SequenceUtils.parseIntOrNull(myCustomBaudRate.getText());
                                if (rate != null) {
                                    return rate;
                                } else {
                                    return SerialBaudRates.BAUDRATE_57600.intValue;
                                }
                            } else {
                                return baudRates.intValue;
                            }
                        }, rate -> {
                            SerialBaudRates baudRates = SerialBaudRates.ADAPTER.findEnumNameOrNull(Integer.toString(rate));
                            if (baudRates == null) {
                                myCustomBaudRate.setText(Integer.toString(rate));
                                SerialBaudRates.ADAPTER.set(myBaudRate, SerialBaudRates.CUSTOM.intValue);
                            } else {
                                myCustomBaudRate.setText(Integer.toString(rate));
                                SerialBaudRates.ADAPTER.set(myBaudRate, rate);
                            }
                        }, i::getBaudRate, i::setBaudRate),
                        //component(myReloadOnFileChange, i::isReloadOnFileChange, i::setReloadOnFileChange),
                        component(myLogConnectDisconnect, i::isLogConnectDisconnect, i::setLogConnectDisconnect),
                        component(myDisconnectOnBuild, i::isDisconnectOnBuild, i::setDisconnectOnBuild),
                        component(myReconnectAfterBuild, i::isReconnectAfterBuild, i::setReconnectAfterBuild),
                        component(myActivateOnConnect, i::isActivateOnConnect, i::setActivateOnConnect),
                        component(myAfterSuccessfulBuild, i::isAfterSuccessfulBuild, i::setAfterSuccessfulBuild),
                        component(BuildConfigurationPatternType.ADAPTER, myBuildConfigurationPattern, i::getBuildConfigurationPatternType, i::setBuildConfigurationPatternType),
                        component(myBuildConfigurationNames, i::getBuildConfigurationNames, i::setBuildConfigurationNames),
                };
            }
        };

        myRegexSampleText = settings.getRegexSampleText();

        final ActionListener actionListener = e -> updateOptions(false);

        myReconnectAfterBuild.addActionListener(actionListener);
        myBaudRate.addActionListener(actionListener);

        myEditRegExButton.addActionListener(e -> {
            boolean valid = RegExTestDialog.showDialog(myMainPanel, this);
            //myRemovePrefixOnPaste.setSelected(valid);
            //myAddPrefixOnPaste.setSelected(valid);
        });

        Project project = settings.getProject();
        if (project != null && !project.isDefault() && !SerialProjectComponent.getInstance(project).isBuildMonitored()) {
            disableChildren(myBuildControlPanel);
        } else {
            myNoBuildMonitorLabel.setVisible(false);
        }
    }

    private void disableChildren(JComponent parent) {
        int iMax = parent.getComponentCount();

        for (int i = 0; i < iMax; i++) {
            Component component = parent.getComponent(i);
            component.setEnabled(false);
            if (component instanceof JComponent) {
                disableChildren((JComponent) component);
            }
        }
    }

    void updateOptions(boolean onInit) {
        BuildConfigurationPatternType selectedType = BuildConfigurationPatternType.ADAPTER.get(myBuildConfigurationPattern);
        final boolean regexPrefixes = selectedType == BuildConfigurationPatternType.REGEX;
        final boolean all = selectedType == BuildConfigurationPatternType.ALL;
        boolean enablePrefixes = !all && !regexPrefixes;

        myBuildConfigurationNames.setEnabled(enablePrefixes);
        myEditRegExButton.setVisible(regexPrefixes);
        myCustomBaudRate.setEnabled(SerialBaudRates.ADAPTER.get(myBaudRate).isCustom());
        
        SerialBaudRates baudRates = SerialBaudRates.ADAPTER.get(myBaudRate);
        if (!baudRates.isCustom()) {
            myCustomBaudRate.setText(baudRates.displayName);
        }

        myAfterSuccessfulBuild.setEnabled(myReconnectAfterBuild.isSelected());
    }

    // RegExSettingsHolder
    // @formatter:off
    @NotNull @Override public String getPatternText() { return myBuildConfigurationNames.getText().trim(); }
    @Override public void setPatternText(final String patternText) { myBuildConfigurationNames.setText(patternText); }

    @NotNull @Override public String getSampleText() { return myRegexSampleText; }
    @Override public void setSampleText(final String sampleText) {
        myRegexSampleText = sampleText;
    }
    @Override public boolean isCaseSensitive() { return true; }
    @Override public boolean isBackwards() { return false; }
    @Override public void setCaseSensitive(final boolean isCaseSensitive) { }
    @Override public void setBackwards(final boolean isBackwards) { }
    @Override public boolean isCaretToGroupEnd() { return false; }
    @Override public void setCaretToGroupEnd(final boolean isCaretToGroupEnd) { }
    // @formatter:on

    private void createUIComponents() {
        final ActionListener actionListener = new ActionListener() {
            @Override
            public void actionPerformed(final ActionEvent e) {updateOptions(false);}
        };

        SerialPortNames.updateValues(true, null);

        if (mySettings) {
            // allow edit
            myPort = new TextFieldWithHistory();
        } else {
            myPort = SerialPortNames.ADAPTER.createComboBox();
        }

        myBaudRate = SerialBaudRates.ADAPTER.createComboBox(SerialBaudRates.DEFAULT);

        myBuildConfigurationPattern = BuildConfigurationPatternType.ADAPTER.createComboBox();
        myBuildConfigurationPattern.addActionListener(actionListener);
    }

    public boolean isModified(@NotNull ArduinoProjectSettings settings) {
        return components.isModified(settings) || !myRegexSampleText.equals(settings.getRegexSampleText()) || mySendSettings.isModified(settings);
    }

    public void apply(@NotNull ArduinoProjectSettings settings) {
        settings.groupChanges(() -> {
            components.apply(settings);
            mySendSettings.apply(settings);
            settings.setRegexSampleText(myRegexSampleText);
        });
    }

    public void reset(@NotNull ArduinoProjectSettings settings) {
        components.reset(settings);

        if (myPort instanceof TextFieldWithHistory) {
            ((TextFieldWithHistory) myPort).setHistorySize(-1);
            ((TextFieldWithHistory) myPort).setHistory(SerialPortNames.getDisplayNames());
            ((TextFieldWithHistory) myPort).setText(/*settings.getPort().isEmpty() ? ArduinoApplicationSettings.getInstance().getPort() : */settings.getPort());
        }

        mySendSettings.reset(settings);
        myRegexSampleText = settings.getRegexSampleText();
    }

    @Override
    public void dispose() {

    }
}
