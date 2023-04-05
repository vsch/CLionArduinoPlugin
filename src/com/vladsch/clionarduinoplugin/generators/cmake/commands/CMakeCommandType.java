package com.vladsch.clionarduinoplugin.generators.cmake.commands;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;

public class CMakeCommandType {
    final public static String[] EMPTY = new String[0];
    final public static CMakeCommandType NULL = new CMakeCommandType("", "", EMPTY, 0, 0);
    final public static String WILDCARD_ARG_MARKER = "<@@>";
    final public static int INF_MAX_ARGS = 1000;

    final public static int POST_INDENT = 0x01;    // post indent after command
    final public static int PRE_UNINDENT = 0x02;   // unindent before outputting command, can have both pre and post
    final protected static CMakeCommandType UNKNOWN = new CMakeCommandType("", "", EMPTY, 0, INF_MAX_ARGS);

    final protected @NotNull String myName;
    final protected @NotNull String myCommand;
    final protected @NotNull String[] myFixedArgs;
    final protected @NotNull String[] myDefaultArgs;
    final protected boolean myNoDupeArgs;
    final protected int myMinArgs;
    final protected int myMaxArgs;
    final protected boolean myIsMultiple;     // whether can have multiple commands
    final protected boolean myIsKeepLast;   // if cannot have multiple then which one drives Last, First
    final protected int myWildcardFixedArgs;
    final protected int myWildcardCount;

    final protected int myIndentFlags;           // amount by which this command post indents or pre-unindents

    public CMakeCommandType(@NotNull final String name, @NotNull final String command, final @NotNull String[] fixedArgs, final int minArgs, final int maxArgs) {
        this(name, command, fixedArgs, minArgs, maxArgs, false, true, true, null, 0);
    }

    public CMakeCommandType(@NotNull final String name, @NotNull final String command, final @NotNull String[] fixedArgs, final int minArgs, final int maxArgs, final boolean noDupeArgs) {
        this(name, command, fixedArgs, minArgs, maxArgs, noDupeArgs, true, true, null, 0);
    }

    public CMakeCommandType(@NotNull final String name, @NotNull final String command, final @NotNull String[] fixedArgs, final int minArgs, final int maxArgs, final boolean noDupeArgs, final boolean isMultiple, final boolean isKeepLast) {
        this(name, command, fixedArgs, minArgs, maxArgs, noDupeArgs, isMultiple, isKeepLast, null, 0);
    }

    public CMakeCommandType(@NotNull final String name, @NotNull final String command, final @NotNull String[] fixedArgs, final int minArgs, final int maxArgs, final boolean noDupeArgs, final boolean isMultiple, final boolean isKeepLast, @Nullable String[] defaults, int indentFlags) {
        myName = name;
        myCommand = command;
        myFixedArgs = fixedArgs;
        myMinArgs = minArgs;
        myMaxArgs = maxArgs;
        myNoDupeArgs = noDupeArgs;
        myIsMultiple = isMultiple;
        myIsKeepLast = isKeepLast;
        myDefaultArgs = defaults != null ? defaults : EMPTY;
        myWildcardFixedArgs = getWildcardFixedArgs(fixedArgs);
        myWildcardCount = getWildcardCount(fixedArgs);
        myIndentFlags = indentFlags;
    }

    public boolean isPreUnIndenting() {
        return (myIndentFlags & PRE_UNINDENT) != 0;
    }

    public boolean isPostIndenting() {
        return (myIndentFlags & POST_INDENT) != 0;
    }

    public static int getWildcardFixedArgs(final @NotNull String[] fixedArgs) {
        int wildcardArgs = 0;

        for (String arg : fixedArgs) {
            if (arg.contains(WILDCARD_ARG_MARKER)) wildcardArgs++;
        }
        return wildcardArgs;
    }

    public static int getWildcardCount(final @NotNull String[] fixedArgs) {
        int wildcardArgs = 0;

        for (String arg : fixedArgs) {
            int pos = 0;
            while (pos < arg.length()) {
                pos = arg.indexOf(WILDCARD_ARG_MARKER, pos);
                if (pos == -1) break;
                wildcardArgs++;
                pos += WILDCARD_ARG_MARKER.length();
            }
        }
        return wildcardArgs;
    }

    public int getAncestors() {
        return 0;
    }

    public boolean isOfType(CMakeCommandType commandType) {
        return this == commandType;
    }

    public boolean isSubTypeOf(CMakeCommandType commandType) {
        return false;
    }

    @NotNull
    public String getName() {
        return myName;
    }

    @NotNull
    public String getCommand() {
        return myCommand;
    }

    @NotNull
    public String[] getFixedArgs() {
        return myFixedArgs;
    }

    public int getWildcardFixedArgs() {
        return myWildcardFixedArgs;
    }

    public int getWildcardCount() {
        return myWildcardCount;
    }

    public int getMinArgs() {
        return myMinArgs;
    }

    public int getMaxArgs() {
        return myMaxArgs;
    }

    public String[] getDefaultArgs() {
        return myDefaultArgs;
    }

    public boolean isNoDupeArgs() {
        return myNoDupeArgs;
    }

    public boolean isMultiple() {
        return myIsMultiple;
    }

    public boolean isKeepLast() {
        return myIsKeepLast;
    }

    @Override
    public String toString() {
        return "CMakeCommandType{" +
                "'" + myName + '\'' +
                ", '" + myCommand + '\'' +
                ", " + Arrays.toString(myFixedArgs) +
                ", [" + myMinArgs +
                ", " + myMaxArgs +
                "], NoDupes " + myNoDupeArgs +
                ", IsMulti " + myIsMultiple +
                ", indent " + myIndentFlags +
                '}';
    }
}
