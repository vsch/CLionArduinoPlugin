package com.vladsch.clionarduinoplugin.generators.cmake.commands;

import com.vladsch.clionarduinoplugin.generators.cmake.CMakeListsTxtBuilder;
import com.vladsch.clionarduinoplugin.generators.cmake.CMakeParser;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

public abstract class CMakeCommandBase implements CMakeElement {
    final protected @NotNull CMakeCommandType myCommandType;
    final protected @NotNull ArrayList<String> myArgs;
    protected boolean myAddEOL;
    protected boolean myCommented;
    protected boolean mySuppressibleCommented;

    public CMakeCommandBase(@NotNull final CMakeCommandType commandType, @NotNull final List<String> args, boolean isAddEOL, boolean commented, boolean suppressibleCommented) {
        myCommandType = commandType;
        myAddEOL = isAddEOL;
        myCommented = commented;
        mySuppressibleCommented = suppressibleCommented;

        myArgs = new ArrayList<>(args);
        String[] defaults = commandType.getDefaultArgs();
        int iMax = defaults.length;

        // set defaults for missing or null values
        for (int i = 0; i < iMax; i++) {
            if (i >= myArgs.size() || myArgs.get(i) == null) {
                addArg(defaults[i]);
            }
        }
    }

    public boolean isSuppressibleCommented() {
        return mySuppressibleCommented;
    }

    public void setSuppressibleCommented(final boolean suppressibleCommented) {
        mySuppressibleCommented = suppressibleCommented;
    }

    public void setSuppressible(final boolean suppressibleCommented) {
        if (suppressibleCommented) myCommented = true;
        mySuppressibleCommented = suppressibleCommented;
    }

    public boolean isCommented() {
        return myCommented;
    }

    public void commentOut(final boolean commentedOut) {
        myCommented = commentedOut;
    }

    public void setCommentOut(final boolean commentedOut) {
        myCommented = commentedOut;
    }

    @NotNull
    public String getCommandName() {
        return myCommandType.getCommand();
    }

    @Override
    public String getText(@Nullable Map<String, Object> valueSet, final @Nullable String projectNameMacro, final boolean suppressCommented) {
        StringBuilder sb = new StringBuilder();
        try {
            appendTo(sb, projectNameMacro, valueSet, suppressCommented);
        } catch (IOException ignored) {

        }
        return sb.toString();
    }

    @Override
    public boolean isAddEOL() {
        return myAddEOL;
    }

    @Override
    public void setAddEOL(final boolean addEOL) {
        myAddEOL = addEOL;
    }

    @Override
    public void appendTo(StringBuilder out, final @Nullable String projectNameMacro, @Nullable Map<String, Object> valueSet, final boolean suppressCommented) throws IOException {
        if (myCommented) {
            if (mySuppressibleCommented && suppressCommented) return;
            out.append("# ");
        }

        out.append(getCommandName());
        out.append("(");
        String sep = "";

        HashSet<String> argValues = new HashSet<>();

        int wildcardArg = 0;
        for (String arg : myCommandType.getFixedArgs()) {
            int pos = 0;

            while (pos < arg.length()) {
                pos = arg.indexOf(CMakeCommandType.WILDCARD_ARG_MARKER, pos);
                if (pos == -1) break;
                if (wildcardArg >= myArgs.size()) break;

                String replacement = myArgs.get(wildcardArg);
                if (wildcardArg < myCommandType.myDefaultArgs.length && myCommandType.myDefaultArgs[wildcardArg].equals(replacement)) {
                    // its a default, we replace values in it
                    replacement = CMakeListsTxtBuilder.Companion.replacedCommandParams(replacement, valueSet);
                }

                wildcardArg++;
                arg = arg.substring(0, pos) + replacement + arg.substring(pos + CMakeCommandType.WILDCARD_ARG_MARKER.length());
                pos += replacement.length();
            }

            if (arg.isEmpty()) continue;

            arg = CMakeListsTxtBuilder.Companion.replacedCommandParams(arg, valueSet);

            if (projectNameMacro != null && !projectNameMacro.isEmpty()) {
                // replace project name macro with one provided
                arg = arg.replace(CMakeListsTxtBuilder.PROJECT_NAME, projectNameMacro);
            }

            if (!myCommandType.isNoDupeArgs() || !argValues.contains(arg)) {
                argValues.add(arg);

                out.append(sep);
                sep = " ";

                out.append(CMakeParser.getArgText(CMakeListsTxtBuilder.Companion.replacedCommandParams(arg, valueSet)));
            }
        }

        int iMax = myArgs.size();
        String useSep = " ";
        if (iMax - wildcardArg > 4) {
            useSep = "\n        ";
            if (!sep.isEmpty()) {
                sep = useSep;
            }
        }

        for (int i = wildcardArg; i < iMax; i++) {
            String arg = myArgs.get(i);

            if (arg.isEmpty()) continue;

            if (i < myCommandType.myDefaultArgs.length && myCommandType.myDefaultArgs[i].equals(arg)) {
                // its a default, we replace values in it
                arg = CMakeListsTxtBuilder.Companion.replacedCommandParams(arg, valueSet);
            }

            if (!myCommandType.isNoDupeArgs() || !argValues.contains(arg)) {
                argValues.add(arg);

                out.append(sep);
                sep = useSep;
                out.append(CMakeParser.getArgText(arg));
            }
        }

        if (!useSep.equals(" ")) {
            // add sep before )
            out.append(useSep);
        }

        if (myAddEOL) {
            out.append(")\n");
        } else {
            out.append(")");
        }
    }

    @NotNull
    public CMakeCommandType getCommandType() {
        return myCommandType;
    }

    public boolean isOfType(CMakeCommandType commandType) {
        return myCommandType.isOfType(commandType);
    }

    public List<String> getArgs() {
        return myArgs;
    }

    public int getArgCount() {
        return myArgs.size();
    }

    public @NotNull String getArg(int index) {
        return myArgs.get(index);
    }

    public @NotNull String getRawArg(int index) {
        @NotNull String[] fixedArgs = myCommandType.getFixedArgs();
        if (index < fixedArgs.length) {
            return fixedArgs[index];
        } else {
            return arg(index - fixedArgs.length);
        }
    }

    // CAUTION: does not work with wildcard fixed args
    public @NotNull List<String> getRawArgs() {
        @NotNull String[] fixedArgs = myCommandType.getFixedArgs();
        ArrayList<String> args = new ArrayList<>(fixedArgs.length + myArgs.size());
        args.addAll(Arrays.asList(fixedArgs));
        args.addAll(myArgs);
        return args;
    }

    public @NotNull String arg(int index) {
        return index < myArgs.size() ? myArgs.get(index) : "";
    }

    public void extendArgs(int index) {
        while (index >= myArgs.size()) myArgs.add("");
    }

    public void setArgsWithDefaults(@NotNull Collection<String> args) {
        myArgs.clear();

        int i = 0;
        int j = 0; // default args
        String[] defaultArgs = myCommandType.getDefaultArgs();
        for (String arg : args) {
            if (arg != null) {
                extendArgs(i);
                myArgs.set(i, arg);
            } else {
                if (j < defaultArgs.length) {
                    extendArgs(i);
                    myArgs.set(i, defaultArgs[j]);
                }
            }
            j++;
            i++;
        }

        while (j < defaultArgs.length) {
            extendArgs(i);
            myArgs.set(i, defaultArgs[j]);
            j++;
            i++;
        }
    }

    public boolean allArgsEqual(final Collection<String> args) {
        return allArgsEqual(args, 0, args.size());
    }

    public boolean allArgsEqual(final Collection<String> args, int start, int end) {
        int i = 0;
        for (String arg : args) {
            if (i >= end) return true;

            if (i >= start) {
                if (arg != null) {
                    if (i < myArgs.size()) {
                        if (!arg.equals(myArgs.get(i))) {
                            return false;
                        }
                    } else {
                        if (!arg.isEmpty()) {
                            return false;
                        }
                    }
                } else {
                    if (!myArgs.get(i).isEmpty()) {
                        return false;
                    }
                }
            }
            i++;
        }
        return true;
    }

    // clear to defaults
    public void clearToDefaults() {
        myArgs.clear();
        Collections.addAll(myArgs, myCommandType.myDefaultArgs);
    }

    // raw manipulation
    public void setArg(int index, @NotNull String arg) {
        if (index == getArgCount()) {
            myArgs.add(arg);
        } else {
            myArgs.set(index, arg);
        }
    }

    public void addArgs(@NotNull Collection<String> args) {
        for (String arg : args) {
            if (arg != null) {
                myArgs.add(arg);
            }
        }
    }

    public void clearArgs() {
        myArgs.clear();
    }

    public void addArg(@NotNull String arg) {
        myArgs.add(arg);
    }

    public void addArg(int index, @NotNull String arg) {
        myArgs.set(index, arg);
    }

    public void removeArg(int index) {
        myArgs.remove(index);
    }
}
