package com.vladsch.clionarduinoplugin.settings;

import org.jetbrains.annotations.NotNull;

import java.util.Collection;

public class ArduinoApplicationSettingsProxy implements ArduinoProjectFileSettings {
    public static final String[] EMPTY_STRINGS = new String[0];

    private final ArduinoApplicationSettings mySettings;
    private boolean myIsLibrary;
    private String[] myHeaders = EMPTY_STRINGS;
    private String[] mySources = EMPTY_STRINGS;
    private String mySketch = "";
    private String myProjectName = "";
    private String[] myNotifications = EMPTY_STRINGS;

    private ArduinoApplicationSettingsProxy(final @NotNull ArduinoApplicationSettings settings, final boolean isLibrary) {
        myIsLibrary = isLibrary;
        mySettings = settings;
    }

    private ArduinoApplicationSettingsProxy() {
        this(new ArduinoApplicationSettings(), false);
    }

    private ArduinoApplicationSettingsProxy(final boolean isLibrary) {
        this(new ArduinoApplicationSettings(), isLibrary);
    }

    public static ArduinoApplicationSettingsProxy of() {
        return new ArduinoApplicationSettingsProxy();
    }

    public static ArduinoApplicationSettingsProxy of(boolean isLibrary) {
        return new ArduinoApplicationSettingsProxy(isLibrary);
    }

    public static ArduinoApplicationSettingsProxy wrap(@NotNull ArduinoApplicationSettings settings, boolean isLibrary) {
        return new ArduinoApplicationSettingsProxy(settings, isLibrary);
    }

    public static ArduinoApplicationSettingsProxy copyOf(@NotNull ArduinoProjectFileSettings settings) {
        ArduinoApplicationSettingsProxy proxy = new ArduinoApplicationSettingsProxy();
        proxy.copyFrom(settings);
        if (settings instanceof ArduinoApplicationSettingsProxy) {
            proxy.myNotifications = ((ArduinoApplicationSettingsProxy) settings).myNotifications;
        }
        return proxy;
    }

    public void copyFrom(ArduinoProjectFileSettings other) {
        myIsLibrary = other.isLibrary();
        setLanguageVersionId(other.getLanguageVersionId());
        setLibraryType(other.getLibraryType());
        setLibraryDisplayName(other.getLibraryDisplayName());
        setLibraryDirectories(other.getLibraryDirectories());
        setNestedLibraries(other.getNestedLibraries());
        setAddLibraryDirectory(other.isAddLibraryDirectory());
        setBoardId(other.getBoardId());
        setCpuId(other.getCpuId());
        setProgrammerId(other.getProgrammerId());
        setPort(other.getPort());
        setBaudRate(other.getBaudRate());
        setVerbose(other.isVerbose());
        setAsmSources(other.isAsmSources());
        setLibraryCategory(other.getLibraryCategory());
        setAuthorName(other.getAuthorName());
        setAuthorEMail(other.getAuthorEMail());
        myHeaders = other.getHeaders();
        mySources = other.getSources();
        mySketch = other.getSketch();
        myProjectName = other.getProjectName();
        myNotifications = EMPTY_STRINGS;
        setCommentUnusedSettings(other.isCommentUnusedSettings());
    }

    public String[] getNotifications() {
        return myNotifications;
    }

    public void setNotifications(final String[] notifications) {
        myNotifications = notifications;
    }

    public void setNotifications(final Collection<String> notifications) {
        myNotifications = notifications.toArray(EMPTY_STRINGS);
    }

    public boolean isLibrary() {
        return myIsLibrary;
    }

    public void setLibrary(final boolean library) {
        myIsLibrary = library;
    }

    public boolean isCommentUnusedSettings() {
        return mySettings.isCommentUnusedSettings();
    }

    public void setCommentUnusedSettings(final boolean commentUnusedSettings) {
        mySettings.setCommentUnusedSettings(commentUnusedSettings);
    }

    @NotNull
    @Override
    public String[] getHeaders() {
        return myHeaders;
    }

    @NotNull
    @Override
    public String[] getSources() {
        return mySources;
    }

    @Override
    public @NotNull String getSketch() {
        return mySketch;
    }

    @Override
    public @NotNull String getProjectName() {
        return myProjectName;
    }

    public ArduinoApplicationSettings getApplicationSettings() {
        return mySettings;
    }

    @Override
    public void setHeaders(@NotNull final String[] headers) {
        myHeaders = headers;
    }

    @Override
    public void setSources(@NotNull final String[] sources) {
        mySources = sources;
    }

    @Override
    public void setSketch(@NotNull final String sketch) {
        mySketch = sketch;
    }

    @Override
    public void setProjectName(@NotNull final String projectName) {
        myProjectName = projectName;
    }

    @Override
    public @NotNull String getLanguageVersionId() {
        return mySettings.getLanguageVersionId();
    }

    @Override
    public @NotNull String getLibraryType() {
        return mySettings.getLibraryType();
    }

    @Override
    public String getLibraryDisplayName() {
        return mySettings.getLibraryDisplayName();
    }

    @NotNull
    @Override
    public String[] getLibraryDirectories() {
        return mySettings.getLibraryDirectories();
    }

    @Override
    public String[] getNestedLibraries() {
        return mySettings.getNestedLibraries();
    }

    @Override
    public boolean isAddLibraryDirectory() {
        return mySettings.isAddLibraryDirectory();
    }

    @Override
    public @NotNull String getBoardId() {
        return mySettings.getBoardId();
    }

    @Override
    public @NotNull String getCpuId() {
        return mySettings.getCpuId();
    }

    @Override
    public @NotNull String getProgrammerId() {
        return mySettings.getProgrammerId();
    }

    @Override
    public @NotNull String getPort() {
        return mySettings.getPort();
    }

    @Override
    public int getBaudRate() {
        return mySettings.getBaudRate();
    }

    @Override
    public boolean isVerbose() {
        return mySettings.isVerbose();
    }

    @Override
    public boolean isAsmSources() {
        return mySettings.isAsmSources();
    }

    @Override
    public @NotNull String getLibraryCategory() {
        return mySettings.getLibraryCategory();
    }

    @Override
    public String getAuthorName() {
        return mySettings.getAuthorName();
    }

    @Override
    public @NotNull String getAuthorEMail() {
        return mySettings.getAuthorEMail();
    }

    @Override
    public void setLanguageVersionId(@NotNull final String languageVersionId) {
        mySettings.setLanguageVersionId(languageVersionId);
    }

    @Override
    public void setLibraryType(@NotNull final String libraryType) {
        mySettings.setLibraryType(libraryType);
    }

    @Override
    public void setLibraryDisplayName(final String libraryDisplayName) {
        mySettings.setLibraryDisplayName(libraryDisplayName);
    }

    @Override
    public void setLibraryDirectories(final @NotNull String[] libraryDirectories) {
        mySettings.setLibraryDirectories(libraryDirectories);
    }

    @Override
    public void setNestedLibraries(final String[] nestedLibraries) {
        mySettings.setNestedLibraries(nestedLibraries);
    }

    @Override
    public void setAddLibraryDirectory(final boolean isAddLibraryDirectory) {
        mySettings.setAddLibraryDirectory(isAddLibraryDirectory);
    }

    @Override
    public void setBoardId(@NotNull final String boardId) {
        mySettings.setBoardId(boardId);
    }

    @Override
    public void setCpuId(@NotNull final String cpuId) {
        mySettings.setCpuId(cpuId);
    }

    @Override
    public void setProgrammerId(@NotNull final String programmerId) {
        mySettings.setProgrammerId(programmerId);
    }

    @Override
    public void setPort(@NotNull final String port) {
        mySettings.setPort(port);
    }

    @Override
    public void setBaudRate(final int baudRate) {
        mySettings.setBaudRate(baudRate);
    }

    @Override
    public void setVerbose(final boolean isVerbose) {
        mySettings.setVerbose(isVerbose);
    }

    @Override
    public void setAsmSources(final boolean isAsmSources) {
        mySettings.setAsmSources(isAsmSources);
    }

    @Override
    public void setLibraryCategory(@NotNull final String libraryCategory) {
        mySettings.setLibraryCategory(libraryCategory);
    }

    @Override
    public void setAuthorName(final String authorName) {
        mySettings.setAuthorName(authorName);
    }

    @Override
    public void setAuthorEMail(@NotNull final String authorEMail) {
        mySettings.setAuthorEMail(authorEMail);
    }

    public String getTemplatesPath() {
        return mySettings.getTemplatesPath();
    }

    public void setTemplatesPath(final String templatesPath) {
        mySettings.setTemplatesPath(templatesPath);
    }

    public boolean isBundledTemplates() {
        return mySettings.isBundledTemplates();
    }

    public int getDisconnectRestTime() {
        return mySettings.getDisconnectRestTime();
    }

    public void setDisconnectRestTime(final int disconnectRestTime) {
        mySettings.setDisconnectRestTime(disconnectRestTime);
    }

    public void setBundledTemplates(final boolean bundledTemplates) {
        mySettings.setBundledTemplates(bundledTemplates);
    }

    @NotNull
    public String getBoardsTxtPath() {
        return mySettings.getBoardsTxtPath();
    }

    public void setBoardsTxtPath(@NotNull final String boardsTxtPath) {
        mySettings.setBoardsTxtPath(boardsTxtPath);
    }

    public boolean isBundledBoardsTxt() {
        return mySettings.isBundledBoardsTxt();
    }

    public void setBundledBoardsTxt(final boolean bundledBoardsTxt) {
        mySettings.setBundledBoardsTxt(bundledBoardsTxt);
    }

    public boolean isBundledProgrammersTxt() {
        return mySettings.isBundledProgrammersTxt();
    }

    public void setBundledProgrammersTxt(final boolean bundledProgrammersTxt) {
        mySettings.setBundledProgrammersTxt(bundledProgrammersTxt);
    }

    @NotNull
    public String getProgrammersTxtPath() {
        return mySettings.getProgrammersTxtPath();
    }

    public void setProgrammersTxtPath(@NotNull final String programmersTxtPath) {
        mySettings.setProgrammersTxtPath(programmersTxtPath);
    }

    public boolean isBundledToolchainTxt() {
        return mySettings.isBundledToolchainTxt();
    }

    public void setBundledToolchainTxt(final boolean bundledToolchainTxt) {
        mySettings.setBundledToolchainTxt(bundledToolchainTxt);
    }

    @NotNull
    public String getToolchainTxtPath() {
        return mySettings.getToolchainTxtPath();
    }

    public void setToolchainTxtPath(@NotNull final String toolchainTxtPath) {
        mySettings.setToolchainTxtPath(toolchainTxtPath);
    }
}
