package com.vladsch.clionarduinoplugin.generators.cmake.commands;

import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.util.Map;

public interface CMakeElement {
    String getText(@Nullable Map<String, Object> valueSet, final @Nullable String projectNameMacro, final boolean suppressCommented);
    void appendTo(StringBuilder out, final @Nullable String projectNameMacro, @Nullable Map<String, Object> valueSet, final boolean suppressCommented) throws IOException;

    void setAddEOL(boolean addEOL);
    boolean isAddEOL();
}
