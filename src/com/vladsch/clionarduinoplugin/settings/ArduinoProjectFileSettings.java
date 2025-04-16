package com.vladsch.clionarduinoplugin.settings;

import com.jetbrains.cidr.cpp.cmake.projectWizard.generators.CMakeProjectGenerator;
import org.jetbrains.annotations.NotNull;

public interface ArduinoProjectFileSettings {
    String ARDUINO_LIB_TYPE = "arduino";
    String[] LIBRARY_TYPES = { ARDUINO_LIB_TYPE, CMakeProjectGenerator.STATIC_LIB_TYPE };
    String STATIC_LIB_TYPE = "static";
    String[] LIBRARY_CATEGORIES = new String[] {
            "Communications",
            "Data Processing",
            "Data Storage",
            "Device Control",
            "Display",
            "Other",
            "Sensors",
            "Signal Input/Output",
            "Timing",
    };

    boolean isLibrary();
    @NotNull String getLanguageVersionId();
    @NotNull String getLibraryType();
    String getLibraryDisplayName();
    @NotNull String[] getLibraryDirectories();
    String[] getNestedLibraries();
    boolean isAddLibraryDirectory();
    @NotNull String getBoardId();
    @NotNull String getCpuId();
    @NotNull String getProgrammerId();
    @NotNull String getPort();
    int getBaudRate();
    boolean isVerbose();
    boolean isAsmSources();
    boolean isCommentUnusedSettings();
    void setCommentUnusedSettings(boolean commentUnusedSettings);
    @NotNull String getLibraryCategory();
    String getAuthorName();
    @NotNull String getAuthorEMail();

    @NotNull String[] getHeaders();
    @NotNull String[] getSources();
    @NotNull String getSketch();
    @NotNull String getProjectName();

    void setLibrary(boolean isLibrary);
    void setLanguageVersionId(@NotNull String languageVersion);
    void setLibraryType(@NotNull String libraryType);
    void setLibraryDisplayName(String libraryDisplayName);
    void setLibraryDirectories(@NotNull String[] libraryDirectories);
    void setNestedLibraries(String[] nestedLibraries);
    void setAddLibraryDirectory(boolean isAddLibraryDirectory);
    void setBoardId(@NotNull String boardId);
    void setCpuId(@NotNull String cpuId);
    void setProgrammerId(@NotNull String programmerId);
    void setPort(@NotNull String port);
    void setBaudRate(int baudRate);
    void setVerbose(boolean isVerbose);
    void setAsmSources(boolean isAsmSources);
    void setLibraryCategory(@NotNull String libraryCategory);
    void setAuthorName(String authorName);
    void setAuthorEMail(@NotNull String authorEMail);

    void setHeaders(@NotNull String[] headers);
    void setSources(@NotNull String[] sources);
    void setSketch(@NotNull String sketch);
    void setProjectName(@NotNull String projectName);

    default String asString() {
        return asString(null);
    }

    static String arrayText(String[] array) {
        StringBuilder sb = new StringBuilder();
        String sep = "";
        sb.append("arrayOf(");
        for (String item : array) {
            sb.append(sep);
            sep = ", ";
            sb.append('"').append(item.replace("\"", "\\\"").replace("$", "\\$")).append('"');
        }
        sb.append(")");
        return sb.toString();
    }

    default String asString(String thizz) {
        if (thizz == null || thizz.isEmpty()) thizz = "";
        else thizz += ".";

        return "{\n" +
                "  " + thizz + "projectName = \"" + getProjectName() + "\"\n" +
                "  " + thizz + "headers = " + arrayText(getHeaders()) + "\n" +
                "  " + thizz + "sources = " + arrayText(getSources()) + "\n" +
                "  " + thizz + "sketch = \"" + getSketch() + "\"\n" +
                "  " + thizz + "isLibrary = " + isLibrary() + "\n" +
                "  " + thizz + "languageVersionId = \"" + getLanguageVersionId() + "\"\n" +
                "  " + thizz + "libraryType = \"" + getLibraryType() + "\"\n" +
                "  " + thizz + "isAddLibraryDirectory = " + isAddLibraryDirectory() + "\n" +
                "  " + thizz + "libraryDirectories = " + arrayText(getLibraryDirectories()) + "\n" +
                "  " + thizz + "nestedLibraries = " + arrayText(getNestedLibraries()) + "\n" +
                "  " + thizz + "boardId = \"" + getBoardId() + "\"\n" +
                "  " + thizz + "cpuId = \"" + getCpuId() + "\"\n" +
                "  " + thizz + "programmerId = \"" + getProgrammerId() + "\"\n" +
                "  " + thizz + "port = \"" + getPort() + "\"\n" +
                "  " + thizz + "baudRate = " + getBaudRate() + "\n" +
                "  " + thizz + "libraryCategory = \"" + getLibraryCategory() + "\"\n" +
                "  " + thizz + "libraryDisplayName = \"" + getLibraryDisplayName() + "\"\n" +
                "  " + thizz + "authorName = \"" + getAuthorName() + "\"\n" +
                "  " + thizz + "authorEMail = \"" + getAuthorEMail() + "\"\n" +
                "  " + thizz + "isVerbose = " + isVerbose() + "\n" +
                "  " + thizz + "isAsmSources = " + isAsmSources() + "\n" +
                "}\n";
    }

    default boolean isStaticLibraryType() {
        return STATIC_LIB_TYPE.equals(getLibraryType());
    }

    default boolean isArduinoLibraryType() {
        return ARDUINO_LIB_TYPE.equals(getLibraryType());
    }
}
