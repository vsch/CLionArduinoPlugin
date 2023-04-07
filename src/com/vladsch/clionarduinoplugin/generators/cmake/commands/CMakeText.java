package com.vladsch.clionarduinoplugin.generators.cmake.commands;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.util.Map;

public class CMakeText implements CMakeElement {
    final protected @NotNull String myText;
    protected boolean myAddEOL;

    public CMakeText(@NotNull final String text) {
        this(text, false);
    }

    public CMakeText(@NotNull final String text, boolean addEOL) {
        myText = text;
        myAddEOL = addEOL;
    }

    @Override
    public String getText(@Nullable Map<String, Object> valueSet, final @Nullable String projectNameMacro, final boolean suppressCommented) {
        return myText;
    }

    public String getText() {
        return myText;
    }

    public boolean isAddEOL() {
        return myAddEOL;
    }

    public void setAddEOL(final boolean addEOL) {
        myAddEOL = addEOL;
    }

    @Override
    public void appendTo(StringBuilder out, final @Nullable String projectNameMacro, @Nullable Map<String, Object> valueSet, final boolean suppressCommented) throws IOException {
        out.append(myText);
        if (myAddEOL) out.append("\n");
    }

    @Override
    public String toString() {
        return "CMakeText{" +
                '"' + myText + '"' +
                " " + myAddEOL +
                '}';
    }
}
