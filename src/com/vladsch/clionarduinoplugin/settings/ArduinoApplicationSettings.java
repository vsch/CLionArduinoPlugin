package com.vladsch.clionarduinoplugin.settings;

import com.intellij.openapi.application.Application;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.openapi.util.SystemInfo;
import com.intellij.ui.mac.foundation.Foundation;
import com.intellij.util.containers.ContainerUtil;
import com.intellij.util.xmlb.XmlSerializerUtil;
import com.intellij.util.xmlb.annotations.Transient;
import com.intellij.util.xmlb.annotations.XCollection;
import com.intellij.util.xmlb.annotations.XMap;
import com.jetbrains.cidr.cpp.cmake.projectWizard.generators.settings.CMakeProjectSettings;
import com.vladsch.clionarduinoplugin.generators.CppLanguageVersions;
import com.vladsch.clionarduinoplugin.resources.ArduinoConfig;
import com.vladsch.clionarduinoplugin.resources.Board;
import com.vladsch.clionarduinoplugin.resources.Programmer;
import com.vladsch.clionarduinoplugin.resources.ResourceUtils;
import com.vladsch.clionarduinoplugin.resources.TemplateResolver;
import com.vladsch.clionarduinoplugin.util.ApplicationSettingsListener;
import com.vladsch.flexmark.util.sequence.SequenceUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;

@State(name = "Arduino Support Settings",
        storages = @Storage("arduino-plugin-settings.xml")
)
public class ArduinoApplicationSettings extends CMakeProjectSettings implements PersistentStateComponent<ArduinoApplicationSettings> {
    private static final String TEXT_DELIMITER = "|";
    private static final String TEXT_SPLIT_REGEX = "\\s*\\" + TEXT_DELIMITER + "\\s*";

    public static final String[] EMPTY = new String[0];
    public static final @NotNull String[] LANGUAGE_VERSIONS = ContainerUtil.map2Array(CppLanguageVersions.values(), String.class, CppLanguageVersions::getDisplayString);

    private int myGroupCount = 0;

    private @NotNull String languageVersionName = LANGUAGE_VERSIONS[0];
    private @NotNull String libraryType = ArduinoProjectFileSettings.ARDUINO_LIB_TYPE;
    private boolean addLibraryDirectory = false;
    private @NotNull String[] libraryDirectories = EMPTY;
    private @NotNull String[] nestedLibraries = EMPTY;
    private @NotNull String boardId = "uno";
    private @NotNull String cpuId = "";
    private @NotNull String programmerId = "";
    private @NotNull String port = "";
    private int baudRate = 0;
    private @NotNull String libraryCategory = "";
    private @NotNull String libraryDisplayName = "";
    private @NotNull String authorName = "";
    private @NotNull String authorEMail = "";
    private boolean verbose = false;
    private boolean asmSources = false;
    private @NotNull HashMap<String, String> boardCpuMap = new HashMap<>();
    private @NotNull LinkedHashSet<String> portHistory = new LinkedHashSet<>();
    private @NotNull String boardsTxtPath = "";
    private @NotNull String programmersTxtPath = "";
    private @NotNull String templatesPath = "";
    private @NotNull String toolchainTxtPath = "";
    private boolean bundledBoardsTxt = true;
    private boolean bundledProgrammersTxt = true;
    private boolean bundledTemplates = true;
    private boolean bundledToolchainTxt = true;
    private boolean commentUnusedSettings = true;
    private int myDisconnectRestTime = 50;  // ms to delay after disconnecting on build

    private ArduinoConfig myArduinoConfig = null;

    @NotNull
    @Override
    public String getEnabledProjectLanguages() {
        return "";
    }

    @NotNull
    @Override
    public String getLanguageVersionLineForCMake() {
        return CppLanguageVersions.fromDisplayString(languageVersionName).trim();
    }

    @NotNull
    public String getLanguageVersionName() {
        return languageVersionName;
    }

    @NotNull
    public String getLanguageVersionId() {
        return CppLanguageVersions.fromDisplayString(languageVersionName).trim();
    }

    public void setLanguageVersion(@NotNull String languageVersionName) {
        this.languageVersionName = languageVersionName;
    }

    public void setLanguageVersionName(@NotNull String languageVersionName) {
        this.languageVersionName = languageVersionName;
    }

    public void setLanguageVersionId(@NotNull String languageVersionId) {
        CppLanguageVersions version = CppLanguageVersions.valueOrNull(languageVersionId);
        languageVersionName = version == null ? "" : version.getDisplayString();
    }

    public void setLibraryType(@Nullable String libraryType) {
        this.libraryType = libraryType == null ? ArduinoProjectFileSettings.ARDUINO_LIB_TYPE : libraryType;
    }

    public String getTemplatesPath() {
        return templatesPath.trim();
    }

    public void setTemplatesPath(final String templatesPath) {
        this.templatesPath = templatesPath.trim();
    }

    public boolean isBundledTemplates() {
        return bundledTemplates;
    }

    public int getDisconnectRestTime() {
        return myDisconnectRestTime;
    }

    public void setDisconnectRestTime(final int disconnectRestTime) {
        myDisconnectRestTime = disconnectRestTime;
    }

    public void setBundledTemplates(final boolean bundledTemplates) {
        this.bundledTemplates = bundledTemplates;
    }

    @NotNull
    public String getLibraryType() {
        return libraryType;
    }

    public boolean isStaticLibraryType() {
        return ArduinoProjectFileSettings.STATIC_LIB_TYPE.equals(getLibraryType());
    }

    public boolean isArduinoLibraryType() {
        return ArduinoProjectFileSettings.ARDUINO_LIB_TYPE.equals(getLibraryType());
    }

    @NotNull
    public String getLibraryDisplayName() {
        return libraryDisplayName;
    }

    public void setLibraryDisplayName(@NotNull final String libraryDisplayName) {
        this.libraryDisplayName = libraryDisplayName;
    }

    @NotNull
    public String[] getLibraryDirectories() {
        return libraryDirectories;
    }

    @Transient
    @NotNull
    public String getLibraryDirectory() {
        StringBuilder sb = new StringBuilder();
        String sep = "";

        for (String dir : libraryDirectories) {
            sb.append(sep);
            sep = ";";
            sb.append(dir);
        }
        return sb.toString();
    }

    @Transient
    public void setLibraryDirectory(final @NotNull String libraryDirectory) {
        libraryDirectories = libraryDirectory.split(";");
    }

    public String[] getNestedLibraries() {
        return nestedLibraries;
    }

    public void setNestedLibraries(@NotNull final String[] nestedLibraries) {
        this.nestedLibraries = nestedLibraries;
    }

    public boolean isAddLibraryDirectory() {
        return addLibraryDirectory;
    }

    public void setAddLibraryDirectory(final boolean addLibraryDirectory) {
        this.addLibraryDirectory = addLibraryDirectory;
    }

    public void setLibraryDirectories(@NotNull String[] libraryDirectories) {
        this.libraryDirectories = libraryDirectories;
    }

    @NotNull
    public String getBoardId() {
        return boardId;
    }

    public void setBoardId(@NotNull final String boardId) {
        this.boardId = boardId;
        cpuId = getBoardCpuId();
        validateCpuId();
    }

    @NotNull
    public String getBoardName() {
        return getArduinoConfig().getBoardById(boardId).getName();
    }

    public void setBoardName(@NotNull final String board) {
        boardId = getBoardByName(board).getId();
        cpuId = getBoardCpuId();
        validateCpuId();
    }

    @NotNull
    private String getBoardCpuId() {
        String bCpu = boardCpuMap.get(boardId);
        return bCpu == null ? "" : bCpu;
    }

    private void validateCpuId() {
        cpuId = getArduinoConfig().getBoardCpuId(boardId, cpuId);
        setBoardCpu(boardId, cpuId);
    }

    @NotNull
    public String getCpuId() {
        validateCpuId();
        return cpuId;
    }

    public void setCpuId(@NotNull final String cpuId) {
        this.cpuId = cpuId;
        validateCpuId();
    }

    @NotNull
    public String getCpuName() {
        validateCpuId();
        return getArduinoConfig().getBoardCpuNameById(boardId, cpuId);
    }

    public void setCpuName(@NotNull final String cpuName) {
        cpuId = getArduinoConfig().getBoardCpuIdByName(boardId, cpuName);
        validateCpuId();
    }

    @NotNull
    public String getProgrammerId() {
        return programmerId;
    }

    @NotNull
    public String getProgrammerName() {
        return getArduinoConfig().getProgrammerById(programmerId).getName();
    }

    public void setProgrammerId(@NotNull final String programmerId) {
        this.programmerId = getArduinoConfig().getProgrammerById(programmerId).getId();
    }

    public void setProgrammerName(@NotNull final String programmerName) {
        programmerId = getArduinoConfig().getProgrammerByName(programmerName).getId();
    }

    @NotNull
    public String getPort() {
        return port;
    }

    public void setPort(@NotNull final String port) {
        this.port = port;
        addPortHistory(port);
    }

    public int getBaudRate() {
        return baudRate;
    }

    public void setBaudRate(final int baudRate) {
        this.baudRate = baudRate;
    }

    @Transient
    @NotNull
    public String getBaudRateText() {
        return baudRate == 0 ? "" : Integer.toString(baudRate);
    }

    @Transient
    public void setBaudRateText(final @NotNull String baudRateText) {
        Integer baud = SequenceUtils.parseIntOrNull(baudRateText);
        baudRate = baud == null ? 0 : baud;
    }

    public boolean isVerbose() {
        return verbose;
    }

    public void setVerbose(final boolean verbose) {
        this.verbose = verbose;
    }

    public boolean isAsmSources() {
        return asmSources;
    }

    public void setAsmSources(final boolean asmSources) {
        this.asmSources = asmSources;
    }

    @XMap
    public Map<String, String> getBoardCpuMap() {
        return boardCpuMap;
    }

    public boolean isCommentUnusedSettings() {
        return commentUnusedSettings;
    }

    public void setCommentUnusedSettings(final boolean commentUnusedSettings) {
        this.commentUnusedSettings = commentUnusedSettings;
    }

    @XMap
    public void setBoardCpuMap(final Map<String, String> boardCpuMap) {
        this.boardCpuMap = new HashMap<>(boardCpuMap);
    }

    @XCollection
    public Collection<String> getPortHistory() {
        return portHistory;
    }

    @XCollection
    public void setPortHistory(final Collection<String> portHistory) {
        this.portHistory = new LinkedHashSet<>();
        for (String port : portHistory) {
            if (port != null && !port.isEmpty()) {
                this.portHistory.add(port);
            }
        }
    }

    public void addPortHistory(@Nullable final String port) {
        if (port != null && !port.isEmpty()) {
            LinkedHashSet<String> portHistory = new LinkedHashSet<>();
            portHistory.add(port);
            portHistory.addAll(this.portHistory);
            this.portHistory = portHistory;
        }
    }

    public void setBoardCpu(String board, String cpu) {
        boardCpuMap.put(board, cpu);
    }

    @NotNull
    public String getLibraryCategory() {
        return libraryCategory;
    }

    public void setLibraryCategory(@NotNull final String category) {
        libraryCategory = category;
    }

    public String getAuthorName() {
        return getAuthorName(this);
    }

    public void setAuthorName(@NotNull final String authorName) {
        this.authorName = authorName;
    }

    @NotNull
    public String getAuthorEMail() {
        return authorEMail;
    }

    public void setAuthorEMail(@NotNull final String authorEMail) {
        this.authorEMail = authorEMail;
    }

    @NotNull
    public String getBoardsTxtPath() {
        return boardsTxtPath;
    }

    public void setBoardsTxtPath(@NotNull final String boardsTxtPath) {
        if (!this.boardsTxtPath.equals(boardsTxtPath)) {
            invalidateArduinoConfig();
            this.boardsTxtPath = boardsTxtPath;
        }
    }

    public boolean isBundledBoardsTxt() {
        return bundledBoardsTxt;
    }

    public void setBundledBoardsTxt(final boolean bundledBoardsTxt) {
        if (this.bundledBoardsTxt != bundledBoardsTxt) {
            invalidateArduinoConfig();
            this.bundledBoardsTxt = bundledBoardsTxt;
        }
    }

    public boolean isBundledProgrammersTxt() {
        return bundledProgrammersTxt;
    }

    public void setBundledProgrammersTxt(final boolean bundledProgrammersTxt) {
        if (this.bundledProgrammersTxt != bundledProgrammersTxt) {
            invalidateArduinoConfig();
            this.bundledProgrammersTxt = bundledProgrammersTxt;
        }
    }

    @NotNull
    public String getProgrammersTxtPath() {
        return programmersTxtPath;
    }

    public void setProgrammersTxtPath(@NotNull final String programmersTxtPath) {
        if (!this.programmersTxtPath.equals(programmersTxtPath)) {
            invalidateArduinoConfig();
            this.programmersTxtPath = programmersTxtPath;
        }
    }

    public boolean isBundledToolchainTxt() {
        return bundledToolchainTxt;
    }

    public void setBundledToolchainTxt(final boolean bundledToolchainTxt) {
        if (this.bundledToolchainTxt != bundledToolchainTxt) {
            invalidateArduinoConfig();
            this.bundledToolchainTxt = bundledToolchainTxt;
        }
    }

    @NotNull
    public String getToolchainTxtPath() {
        return toolchainTxtPath;
    }

    public void setToolchainTxtPath(@NotNull final String toolchainTxtPath) {
        if (!this.toolchainTxtPath.equals(toolchainTxtPath)) {
            invalidateArduinoConfig();
            this.toolchainTxtPath = toolchainTxtPath;
        }
    }

    private static String getFileContent(String path) {
        if (!path.isEmpty()) {
            File file = new File(path);
            if (!file.exists()) {
            } else if (file.isDirectory()) {
            } else {
                return ResourceUtils.getFileContent(file);
            }
        }
        return null;
    }

    @NotNull
    public String[] getBoardNames() {
        ArduinoConfig arduinoConfig = getArduinoConfig();
        return ContainerUtil.map2Array(arduinoConfig.getBoardIdMap().values(), String.class, Board::getName);
    }

    @NotNull
    public String[] getProgrammerNames() {
        ArduinoConfig arduinoConfig = getArduinoConfig();
        return ContainerUtil.map2Array(arduinoConfig.getProgrammerIdMap().values(), String.class, Programmer::getName);
    }

    @NotNull
    public String[] getBoardCpuNames(final @NotNull String boardName) {
        Board board = getBoardByName(boardName);
        HashMap<String, String> list = board.getCpuNameMap();
        return list.values().toArray(new String[0]);
    }

    @NotNull
    public String getCpuLabel() {
        return getArduinoConfig().getCpuMenu();
    }

    public @NotNull Board getBoardByName(final @Nullable String boardName) {
        return getArduinoConfig().getBoardByName(boardName);
    }

    public @NotNull Programmer getProgrammerFromName(final @Nullable String programmerName) {
        return getArduinoConfig().getProgrammerByName(programmerName);
    }

    public void invalidateArduinoConfig() {
        myArduinoConfig = null;
    }

    @NotNull
    public ArduinoConfig getArduinoConfig() {
        if (myArduinoConfig == null) {
            String boardsTxt = ArduinoConfig.Companion.getBoardsTxtString();
            String programmersTxt = ArduinoConfig.Companion.getProgrammersTxtString();

            if (!bundledBoardsTxt) {
                String otherBoardsTxt = getFileContent(boardsTxtPath);
                ArduinoConfig arduinoConfig = new ArduinoConfig(otherBoardsTxt, "");
                if (!arduinoConfig.getBoardIdMap().isEmpty()) {
                    boardsTxt = otherBoardsTxt;
                }
            }

            if (!bundledProgrammersTxt) {
                String otherProgrammerTxt = getFileContent(programmersTxtPath);
                ArduinoConfig arduinoConfig = new ArduinoConfig(otherProgrammerTxt, "");
                if (!arduinoConfig.getProgrammerIdMap().isEmpty()) {
                    programmersTxt = otherProgrammerTxt;
                }
            }

            myArduinoConfig = new ArduinoConfig(boardsTxt, programmersTxt);
        }
        return myArduinoConfig;
    }

    @SuppressWarnings("NonFinalFieldReferenceInEquals")
    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ArduinoApplicationSettings settings = (ArduinoApplicationSettings) o;

        if (addLibraryDirectory != settings.addLibraryDirectory) return false;
        if (baudRate != settings.baudRate) return false;
        if (verbose != settings.verbose) return false;
        if (asmSources != settings.asmSources) return false;
        if (bundledBoardsTxt != settings.bundledBoardsTxt) return false;
        if (bundledProgrammersTxt != settings.bundledProgrammersTxt) return false;
        if (bundledTemplates != settings.bundledTemplates) return false;
        if (bundledToolchainTxt != settings.bundledToolchainTxt) return false;
        if (!languageVersionName.equals(settings.languageVersionName)) return false;
        if (!libraryType.equals(settings.libraryType)) return false;
        if (!Arrays.equals(libraryDirectories, settings.libraryDirectories)) return false;
        if (!Arrays.equals(nestedLibraries, settings.nestedLibraries)) return false;
        if (!boardId.equals(settings.boardId)) return false;
        if (!cpuId.equals(settings.cpuId)) return false;
        if (!programmerId.equals(settings.programmerId)) return false;
        if (!port.equals(settings.port)) return false;
        if (!libraryCategory.equals(settings.libraryCategory)) return false;
        if (!authorName.equals(settings.authorName)) return false;
        if (!authorEMail.equals(settings.authorEMail)) return false;
        if (!boardCpuMap.equals(settings.boardCpuMap)) return false;
        if (!portHistory.equals(settings.portHistory)) return false;
        if (!boardsTxtPath.equals(settings.boardsTxtPath)) return false;
        if (!programmersTxtPath.equals(settings.programmersTxtPath)) return false;
        if (!templatesPath.equals(settings.templatesPath)) return false;
        if (!toolchainTxtPath.equals(settings.toolchainTxtPath)) return false;
        return true;
    }

    @SuppressWarnings("NonFinalFieldReferencedInHashCode")
    @Override
    public int hashCode() {
        int result = languageVersionName.hashCode();
        result = 31 * result + libraryType.hashCode();
        result = 31 * result + (addLibraryDirectory ? 1 : 0);
        result = 31 * result + Arrays.hashCode(libraryDirectories);
        result = 31 * result + Arrays.hashCode(nestedLibraries);
        result = 31 * result + boardId.hashCode();
        result = 31 * result + cpuId.hashCode();
        result = 31 * result + programmerId.hashCode();
        result = 31 * result + port.hashCode();
        result = 31 * result + baudRate;
        result = 31 * result + libraryCategory.hashCode();
        result = 31 * result + authorName.hashCode();
        result = 31 * result + authorEMail.hashCode();
        result = 31 * result + (verbose ? 1 : 0);
        result = 31 * result + (asmSources ? 1 : 0);
        result = 31 * result + boardCpuMap.hashCode();
        result = 31 * result + portHistory.hashCode();
        result = 31 * result + boardsTxtPath.hashCode();
        result = 31 * result + programmersTxtPath.hashCode();
        result = 31 * result + templatesPath.hashCode();
        result = 31 * result + toolchainTxtPath.hashCode();
        result = 31 * result + (bundledBoardsTxt ? 1 : 0);
        result = 31 * result + (bundledProgrammersTxt ? 1 : 0);
        result = 31 * result + (bundledTemplates ? 1 : 0);
        result = 31 * result + (bundledToolchainTxt ? 1 : 0);
        return result;
    }

    @NotNull
    public static String getAuthorName(final ArduinoApplicationSettings settings) {
        if (settings.authorName.isEmpty()) {
            String userName = System.getProperty("user.name");

            if (SystemInfo.isMac) {
                String fullUserName = Foundation.fullUserName();
                if (fullUserName != null && !fullUserName.isEmpty()) {
                    userName = fullUserName;
                }
            }

            if (userName == null) {
                userName = "";
            }

            settings.authorName = userName;
        }

        return settings.authorName;
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

    @Nullable
    public File getTemplateDir() {
        File templateDir = null;
        if (!bundledTemplates && !templatesPath.isEmpty()) {
            File file = new File(templatesPath);
            if (TemplateResolver.INSTANCE.haveAllTemplates(file)) {
                templateDir = file;
            }
        }
        return templateDir;
    }

    private void fireSettingsChanged() {
        if (myGroupCount == 0) {
            ApplicationManager.getApplication().getMessageBus().syncPublisher(ApplicationSettingsListener.TOPIC).onSettingsChanged(this);
        }
    }

    boolean isServiceInstance() {
        return this == getInstance();
    }

    static @Nullable ArduinoApplicationSettings testSettings = null;

    @NotNull
    public static ArduinoApplicationSettings getInstance() {
        return ApplicationManager.getApplication().getService(ArduinoApplicationSettings.class);
    }

    @NotNull
    @Override
    public ArduinoApplicationSettings getState() {
        return this;
    }

    @Override
    public void loadState(@NotNull ArduinoApplicationSettings state) {
        XmlSerializerUtil.copyBean(state, this);
    }
}
