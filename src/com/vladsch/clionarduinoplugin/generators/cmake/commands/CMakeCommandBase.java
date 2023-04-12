package com.vladsch.clionarduinoplugin.generators.cmake.commands;

import com.vladsch.clionarduinoplugin.generators.cmake.CMakeListsTxtBuilder;
import com.vladsch.clionarduinoplugin.generators.cmake.CMakeParser;
import com.vladsch.flexmark.util.sequence.BasedSequence;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

public abstract class CMakeCommandBase implements CMakeElement {
    final protected @NotNull CMakeCommandType myCommandType;
    final protected @NotNull ArrayList<CommandArgument> myArgs;
    protected boolean myAddEOL;
    protected boolean myCommented;
    protected boolean mySuppressibleCommented;
    protected BasedSequence myLeadingSpaces;
    protected BasedSequence myAddBeforeClosing;

    public CMakeCommandBase(@NotNull final CMakeCommandType commandType, @NotNull final List<CommandArgument> args, boolean isAddEOL, boolean commented, boolean suppressibleCommented, @NotNull BasedSequence addBeforeClosing) {
        myCommandType = commandType;
        myAddEOL = isAddEOL;
        myCommented = commented;
        mySuppressibleCommented = suppressibleCommented;
        myAddBeforeClosing = addBeforeClosing;
        myLeadingSpaces = BasedSequence.NULL;

        myArgs = new ArrayList<>(args);

        String[] defaults = commandType.getDefaultArgs();

        // set defaults for missing or null values
        int iMax = defaults.length;
        for (int i = 0; i < iMax; i++) {
            if (i >= myArgs.size() || myArgs.get(i) == null) {
                setOrAddArg(i, CommandArgument.of(defaults[i]));
            }
        }

        iMax = myArgs.size();
        for (int i = 0; i < iMax; i++) {
            if (myArgs.get(i) == null) {
                myArgs.set(i, CommandArgument.NULL);
            }
        }
    }

    public BasedSequence getLeadingSpaces() {
        return myLeadingSpaces;
    }

    public void setLeadingSpaces(BasedSequence spaces) {
        myLeadingSpaces = spaces;
    }

    public boolean isSuppressibleCommented() {
        return mySuppressibleCommented;
    }

    public @NotNull BasedSequence getAddBeforeClosing() {
        return myAddBeforeClosing;
    }

    public void setAddBeforeClosing(@NotNull BasedSequence addBeforeClosing) {
        myAddBeforeClosing = addBeforeClosing;
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
            out.append(myLeadingSpaces);
            out.append("# ");
        } else {
            out.append(myLeadingSpaces);
        }

        out.append(getCommandName());
        out.append("(");
        String sep = "";

        HashSet<String> argValues = new HashSet<>();

        int wildcardArg = 0;
        for (String arg : myCommandType.getFixedArgs()) {
            int pos = 0;
            BasedSequence argLeadingSpaces = BasedSequence.NULL;

            while (pos < arg.length()) {
                pos = arg.indexOf(CMakeCommandType.WILDCARD_ARG_MARKER, pos);
                if (pos == -1) break;
                if (wildcardArg >= myArgs.size()) break;

                CommandArgument cmdArg = myArgs.get(wildcardArg);
                String replacement = cmdArg.getValue().toString();
                argLeadingSpaces = cmdArg.getLeadingSpaces();

                if (wildcardArg < myCommandType.myDefaultArgs.length && replacement.equals(myCommandType.myDefaultArgs[wildcardArg])) {
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

                if (argLeadingSpaces.indexOf('\n') != -1) {
                    // use what was there before if it had a line break
                    out.append(argLeadingSpaces);
                } else {
                    out.append(sep);
                }
                sep = " ";

                out.append(CMakeParser.getArgText(CMakeListsTxtBuilder.Companion.replacedCommandParams(arg, valueSet)));
            }
        }

        int iMax = myArgs.size();

        for (int i = wildcardArg; i < iMax; i++) {
            CommandArgument cmdArg = myArgs.get(i);
            String arg = cmdArg.getValue().toString();
            if (arg.isEmpty()) continue;

            if (i < myCommandType.myDefaultArgs.length && myCommandType.myDefaultArgs[i].equals(arg)) {
                // its a default, we replace values in it
                arg = CMakeListsTxtBuilder.Companion.replacedCommandParams(arg, valueSet);
            }

            if (!myCommandType.isNoDupeArgs() || !argValues.contains(arg)) {
                argValues.add(arg);

                if (cmdArg.getLeadingSpaces().isNotEmpty()) out.append(cmdArg.getLeadingSpaces());
                else out.append(sep);
                sep = " ";

                out.append(CMakeParser.getArgText(arg));
            }
        }

        out.append(myAddBeforeClosing);

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

    public @NotNull List<CommandArgument> getArgs() {
        return myArgs;
    }

    // get leading spaces for matching args, BasedSequence.NULL for the rest
    public List<CommandArgument> getArgsWithLeadingSpaces(Collection<CommandArgument> args) {
        return getArgsWithLeadingSpaces(args, 0, args.size());
    }

    // get leading spaces for matching args, BasedSequence.NULL for the rest
    public List<CommandArgument> getArgsWithLeadingSpaces(Collection<CommandArgument> args, int start, int end) {
        ArrayList<CommandArgument> argsWithLeadingSpaces = new ArrayList<>();

        int i = 0;
        for (CommandArgument arg : args) {
            if (i >= end) return argsWithLeadingSpaces;

            if (i >= start) {
                CommandArgument cmdArg = i < myArgs.size() ? myArgs.get(i) : CommandArgument.NULL;

                if (arg != null) {
                    if (!cmdArg.isNull()) {
                        if (!cmdArg.isEqualValue(arg)) {
                            argsWithLeadingSpaces.add(arg.withLeadingSpaces(BasedSequence.NULL));
                        } else {
                            argsWithLeadingSpaces.add(arg.withLeadingSpaces(cmdArg.getLeadingSpaces()));
                        }
                    } else {
                        argsWithLeadingSpaces.add(arg.withLeadingSpaces(BasedSequence.NULL));
                    }
                } else {
                    if (cmdArg.isEmpty()) {
                        argsWithLeadingSpaces.add(CommandArgument.NULL);
                    } else {
                        argsWithLeadingSpaces.add(cmdArg.withValue(null));
                    }
                }
            }
            i++;
        }
        return argsWithLeadingSpaces;
    }

    public int getArgCount() {
        return myArgs.size();
    }

    public @NotNull CommandArgument getArg(int index) {
        return myArgs.get(index);
    }

    public @NotNull CommandArgument arg(int index) {
        return index < myArgs.size() ? myArgs.get(index) : CommandArgument.NULL;
    }

    public void extendArgs(int index) {
        while (index >= myArgs.size()) {
            myArgs.add(CommandArgument.NULL);
        }
    }

    public void setArgsWithDefaults(@NotNull Collection<CommandArgument> args) {
        myArgs.clear();

        int i = 0;
        int j = 0; // default args
        String[] defaultArgs = myCommandType.getDefaultArgs();

        for (CommandArgument arg : args) {
            if (arg.isNotNull()) {
                extendArgs(i);
                myArgs.set(i, arg);
            } else {
                if (j < defaultArgs.length) {
                    extendArgs(i);
                    myArgs.set(i, CommandArgument.of(defaultArgs[j]));
                }
            }
            j++;
            i++;
        }

        while (j < defaultArgs.length) {
            extendArgs(i);
            myArgs.set(i, CommandArgument.of(defaultArgs[j]));
            j++;
            i++;
        }
    }

    public boolean allArgsEqual(final Collection<CommandArgument> args) {
        return allArgsEqual(args, 0, args.size());
    }

    public boolean allArgsEqual(final Collection<CommandArgument> args, int start, int end) {
        int i = 0;
        for (CommandArgument arg : args) {
            if (i >= end) return true;

            if (i >= start) {
                if (arg != null) {
                    if (i < myArgs.size()) {
                        if (!myArgs.get(i).isEqualValue(arg)) {
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

        for (String arg : myCommandType.myDefaultArgs) {
            myArgs.add(CommandArgument.of(arg));
        }
    }

    // raw manipulation
    public void setOrAddArg(int index, @NotNull CommandArgument arg) {
        if (index == getArgCount()) {
            myArgs.add(arg);
        } else {
            myArgs.set(index, arg);
        }
    }

    public void clearArgs() {
        myArgs.clear();
    }

    public void addArg(@NotNull CommandArgument arg) {
        myArgs.add(arg);
    }

    public void removeArg(int index) {
        myArgs.remove(index);
    }
}
