package com.vladsch.clionarduinoplugin.settings;

import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.openapi.project.Project;
import com.intellij.util.xmlb.XmlSerializerUtil;
import com.vladsch.clionarduinoplugin.util.ProjectSettingsListener;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.regex.Pattern;

@State(name = "Arduino Project Settings", storages = @Storage(value = "arduino-project-settings.xml"))
public class ArduinoProjectSettings implements PersistentStateComponent<ArduinoProjectSettings> {
    public static final String TEXT_DELIMITER = "|";
    public static final String TEXT_SPLIT_REGEX = "\\s*\\" + TEXT_DELIMITER + "\\s*";

    final private Project myProject;
    private int myGroupCount = 0;

    private String port = "";
    private int baudRate = 9600;
    private boolean disconnectOnBuild = true;
    private boolean reconnectAfterBuild = true;
    private boolean clearOnReconnect = true;
    private boolean afterSuccessfulBuild = true;
    private int buildConfigurationPatternType = BuildConfigurationPatternType.REGEX.intValue;
    private @NotNull String regexSampleText = "upload|test-upload|!-upload";
    private @NotNull String buildConfigurationNames = "(^|.+-)upload$";
    private boolean activateOnConnect = true;
    private boolean logConnectDisconnect = true;
    private boolean myShowSendOptions = true;
    private int mySerialEndOfLine = SerialEndOfLineTypes.LF.intValue;
    private boolean myLogSentText = true;
    private boolean myIsImmediateSend = true;
    private boolean myLogExceptions = true;

    // cached values
    private @Nullable Pattern myBuildConfigurationNamesPattern = null;
    private @Nullable String[] myBuildConfigurationNamesList = null;
    private @Nullable String[] myRegexSampleList = null;

    public Project getProject() {
        return myProject;
    }

    public SerialEndOfLineTypes getSerialEndOfLineType() {
        return SerialEndOfLineTypes.ADAPTER.get(mySerialEndOfLine);
    }

    public int getSerialEndOfLine() {
        return mySerialEndOfLine;
    }

    public void setSerialEndOfLine(final int serialEndOfLine) {
        mySerialEndOfLine = serialEndOfLine;
    }

    public boolean isLogSentText() {
        return myLogSentText;
    }

    public void setLogSentText(final boolean logSentText) {
        myLogSentText = logSentText;
    }

    public boolean isImmediateSend() {
        return myIsImmediateSend;
    }

    public void setImmediateSend(final boolean immediateSend) {
        myIsImmediateSend = immediateSend;
    }

    public boolean isLogExceptions() {
        return myLogExceptions;
    }

    public void setLogExceptions(final boolean logExceptions) {
        myLogExceptions = logExceptions;
    }

    public boolean isShowSendOptions() {
        return myShowSendOptions;
    }

    public void setShowSendOptions(final boolean showSendOptions) {
        this.myShowSendOptions = showSendOptions;
    }

    public String getPort() {
        return port;
    }

    public void setPort(final String port) {
        this.port = port;
    }

    public int getBaudRate() {
        return baudRate;
    }

    public void setBaudRate(final int baudRate) {
        this.baudRate = baudRate;
    }

    public boolean isDisconnectOnBuild() {
        return disconnectOnBuild;
    }

    public void setDisconnectOnBuild(final boolean disconnectOnBuild) {
        this.disconnectOnBuild = disconnectOnBuild;
    }

    public boolean isReconnectAfterBuild() {
        return reconnectAfterBuild;
    }

    public void setReconnectAfterBuild(final boolean reconnectAfterBuild) {
        this.reconnectAfterBuild = reconnectAfterBuild;
    }

    public boolean isClearOnReconnect() {
        return clearOnReconnect;
    }

    public void setClearOnReconnect(final boolean clearOnReconnect) {
        this.clearOnReconnect = clearOnReconnect;
    }

    public int getBuildConfigurationPatternType() {
        return buildConfigurationPatternType;
    }

    public void setBuildConfigurationPatternType(final int buildConfigurationPatternType) {
        this.buildConfigurationPatternType = buildConfigurationPatternType;
    }

    public String getRegexSampleText() {
        return regexSampleText;
    }

    public void setRegexSampleText(final String regexSampleText) {
        this.regexSampleText = regexSampleText;
    }

    public String getBuildConfigurationNames() {
        return buildConfigurationNames;
    }

    public void setBuildConfigurationNames(final String buildConfigurationNames) {
        this.buildConfigurationNames = buildConfigurationNames;
    }

    public boolean isAfterSuccessfulBuild() {
        return afterSuccessfulBuild;
    }

    public void setAfterSuccessfulBuild(final boolean afterSuccessfulBuild) {
        this.afterSuccessfulBuild = afterSuccessfulBuild;
    }

    public boolean isActivateOnConnect() {
        return activateOnConnect;
    }

    public void setActivateOnConnect(final boolean activateOnConnect) {
        this.activateOnConnect = activateOnConnect;
    }

    public boolean isLogConnectDisconnect() {
        return logConnectDisconnect;
    }

    public void setLogConnectDisconnect(final boolean logConnectDisconnect) {
        this.logConnectDisconnect = logConnectDisconnect;
    }

    public ArduinoProjectSettings() {
        this(null);
    }

    public ArduinoProjectSettings(@Nullable Project project) {
        myProject = project;
    }

    public void groupChanges(Runnable grouped) {
        myGroupCount++;
        try {
            grouped.run();
        } finally {
            myGroupCount--;
            if (myGroupCount == 0) {
                fireSettingsChanged();
            }
        }
    }

    private void fireSettingsChanged() {
        if (myGroupCount == 0 && myProject != null) {
            myProject.getMessageBus().syncPublisher(ProjectSettingsListener.TOPIC).onSettingsChanged();
        }
    }

    @NotNull
    public static ArduinoProjectSettings getInstance(@NotNull Project project) {
        return ServiceManager.getService(project, ArduinoProjectSettings.class);
    }

    @NotNull
    @Override
    public ArduinoProjectSettings getState() {
        return this;
    }

    @Override
    public void loadState(@NotNull ArduinoProjectSettings state) {
        XmlSerializerUtil.copyBean(state, this);
    }

    @Nullable
    public String[] getBuildConfigurationNamesList() {
        if (myBuildConfigurationNamesList == null && !buildConfigurationNames.isEmpty()) {
            if (buildConfigurationPatternType != BuildConfigurationPatternType.REGEX.getIntValue()) {
                myBuildConfigurationNamesList = buildConfigurationNames.split(TEXT_SPLIT_REGEX);
            } else {
                myBuildConfigurationNamesList = new String[] { buildConfigurationNames };
            }
        }
        return myBuildConfigurationNamesList;
    }

    @Nullable
    public String[] getRegexSampleList() {
        if (myRegexSampleList == null && !regexSampleText.isEmpty()) {
            myRegexSampleList = regexSampleText.split(TEXT_SPLIT_REGEX);
        }
        return myBuildConfigurationNamesList;
    }

    @Nullable
    public Pattern getBuildConfigurationNamesPattern() {
        if (myBuildConfigurationNamesPattern == null && !buildConfigurationNames.isEmpty()) {
            if (buildConfigurationPatternType == BuildConfigurationPatternType.REGEX.getIntValue()) {
                try {
                    myBuildConfigurationNamesPattern = Pattern.compile(buildConfigurationNames);
                } catch (Throwable error) {
                    buildConfigurationPatternType = BuildConfigurationPatternType.ANYWHERE.getIntValue();
                    throw error;
                }
            } else {
                // Can build it from the list, but not now
                StringBuilder sb = new StringBuilder();

                if (buildConfigurationPatternType == BuildConfigurationPatternType.PREFIX.getIntValue()) {
                    sb.append("^");
                }

                sb.append("(?:\\Q");
                String sep = "";
                final String[] prefixList = getBuildConfigurationNamesList();
                assert prefixList != null;
                for (String prefix : prefixList) {
                    if (!prefix.isEmpty()) {
                        sb.append(sep);
                        sep = "\\E|\\Q";
                        sb.append(prefix);
                    }
                }
                sb.append("\\E)");

                if (buildConfigurationPatternType == BuildConfigurationPatternType.SUFFIX.getIntValue()) {
                    sb.append("$");
                }

                try {
                    myBuildConfigurationNamesPattern = Pattern.compile(sb.toString());
                } catch (Throwable error) {
                    buildConfigurationPatternType = BuildConfigurationPatternType.ANYWHERE.getIntValue();
                    throw error;
                }
            }
        }
        return myBuildConfigurationNamesPattern;
    }

    public boolean isBuildConfigurationMatched(final String buildConfiguration) {
        if (buildConfigurationPatternType == BuildConfigurationPatternType.ALL.intValue) {
            return true;
        } else {
            @Nullable String[] namesList = getBuildConfigurationNamesList();
            BuildConfigurationPatternType patternType = BuildConfigurationPatternType.ADAPTER.get(buildConfigurationPatternType);
            return !"".equals(patternType.getMatched(buildConfiguration, namesList));
        }
    }
}
