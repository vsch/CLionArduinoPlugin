package com.vladsch.clionarduinoplugin.generators.cmake.commands;

import com.vladsch.clionarduinoplugin.generators.cmake.CMakeListsTxtBuilder;
import com.vladsch.clionarduinoplugin.generators.cmake.CMakeParser;
import com.vladsch.flexmark.util.sequence.BasedSequence;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public abstract class CMakeCommandBase implements CMakeElement {
    final protected @NotNull CMakeCommandType myCommandType;
    final protected @NotNull ArrayList<String> myArgs;
    final protected @NotNull ArrayList<BasedSequence> myArgsLeadingSpaces;
    protected boolean myAddEOL;
    protected boolean myCommented;
    protected boolean mySuppressibleCommented;
    protected BasedSequence myLeadingSpaces = BasedSequence.NULL;
    protected BasedSequence myAddBeforeClosing = BasedSequence.NULL;

    public CMakeCommandBase(@NotNull final CMakeCommandType commandType, @NotNull final List<String> args, @Nullable List<BasedSequence> argsLeadingSpaces, boolean isAddEOL, boolean commented, boolean suppressibleCommented, @NotNull BasedSequence addBeforeClosing) {
        myCommandType = commandType;
        myAddEOL = isAddEOL;
        myCommented = commented;
        mySuppressibleCommented = suppressibleCommented;
        myAddBeforeClosing = addBeforeClosing;

        myArgs = new ArrayList<>(args);
        myArgsLeadingSpaces = argsLeadingSpaces == null ? new ArrayList<>() : new ArrayList<>(argsLeadingSpaces);
        if (argsLeadingSpaces == null) {
            int iMax = args.size();
            for (int i = 0; i < iMax; i++) {
                myArgsLeadingSpaces.add(BasedSequence.NULL);
            }
        }
        
        String[] defaults = commandType.getDefaultArgs();
        int iMax = defaults.length;

        // set defaults for missing or null values
        for (int i = 0; i < iMax; i++) {
            if (i >= myArgs.size() || myArgs.get(i) == null) {
                addArg(defaults[i], BasedSequence.NULL);
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

                String replacement = myArgs.get(wildcardArg);
                argLeadingSpaces = myArgsLeadingSpaces.get(wildcardArg);

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
            String arg = myArgs.get(i);
            if (arg.isEmpty()) continue;

            BasedSequence argLeadingSpaces = myArgsLeadingSpaces.get(i);

            if (i < myCommandType.myDefaultArgs.length && myCommandType.myDefaultArgs[i].equals(arg)) {
                // its a default, we replace values in it
                arg = CMakeListsTxtBuilder.Companion.replacedCommandParams(arg, valueSet);
            }

            if (!myCommandType.isNoDupeArgs() || !argValues.contains(arg)) {
                argValues.add(arg);

                if (!argLeadingSpaces.isEmpty()) out.append(argLeadingSpaces); 
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

    public List<String> getArgs() {
        return myArgs;
    }

    public List<BasedSequence> getArgsLeadingSpaces() {
        return myArgsLeadingSpaces;
    }

    // get leading spaces for matching args, BasedSequence.NULL for the rest
    public List<BasedSequence> getArgsLeadingSpaces(Collection<String> args) {
        return getArgsLeadingSpaces(args, 0, args.size());
    }
    
    // get leading spaces for matching args, BasedSequence.NULL for the rest
    public List<BasedSequence> getArgsLeadingSpaces(Collection<String> args, int start, int end) {
        ArrayList<BasedSequence> argsLeadingSpaces = new ArrayList<>();
        
        int i = 0;
        for (String arg : args) {
            if (i >= end) return argsLeadingSpaces;

            if (i >= start) {
                if (arg != null) {
                    if (i < myArgs.size()) {
                        if (!arg.equals(myArgs.get(i))) {
                            argsLeadingSpaces.add(BasedSequence.NULL);
                        } else {
                            argsLeadingSpaces.add(myArgsLeadingSpaces.get(i));   
                        }
                    } else {
                        argsLeadingSpaces.add(BasedSequence.NULL);
                    }
                } else {
                    if (i >= myArgs.size() || !myArgs.get(i).isEmpty()) {
                        argsLeadingSpaces.add(BasedSequence.NULL);
                    } else {
                        argsLeadingSpaces.add(myArgsLeadingSpaces.get(i));
                    }
                }
            }
            i++;
        }
        return argsLeadingSpaces;
    }

    public int getArgCount() {
        return myArgs.size();
    }

    public @NotNull String getArg(int index) {
        return myArgs.get(index);
    }

    public @NotNull BasedSequence getArgLeadingSpaces(int index) {
        return myArgsLeadingSpaces.get(index);
    }

    public @NotNull String arg(int index) {
        return index < myArgs.size() ? myArgs.get(index) : "";
    }

    public @NotNull BasedSequence argLeadingSpaces(int index) {
        return index < myArgsLeadingSpaces.size() ? myArgsLeadingSpaces.get(index) : BasedSequence.NULL;
    }

    public void extendArgs(int index) {
        while (index >= myArgs.size()) {
            myArgs.add("");
            myArgsLeadingSpaces.add(BasedSequence.NULL);
        }
    }

    public void setArgsWithDefaults(@NotNull Collection<String> args, @Nullable Collection<BasedSequence> argsLeadingSpaces) {
        myArgs.clear();

        int i = 0;
        int j = 0; // default args
        String[] defaultArgs = myCommandType.getDefaultArgs();
        Iterator<BasedSequence> leadingSpacesIterator = argsLeadingSpaces == null ? null : argsLeadingSpaces.iterator();

        for (String arg : args) {
            if (arg != null) {
                extendArgs(i);
                myArgs.set(i, arg);
                BasedSequence leadingSpaces = leadingSpacesIterator == null /*|| !leadingSpacesIterator.hasNext()*/ ? BasedSequence.NULL : leadingSpacesIterator.next();
                myArgsLeadingSpaces.set(i, leadingSpaces == null ? BasedSequence.NULL : leadingSpaces);
            } else {
                if (j < defaultArgs.length) {
                    extendArgs(i);
                    myArgs.set(i, defaultArgs[j]);
                    myArgsLeadingSpaces.set(i, BasedSequence.NULL);
                }
            }
            j++;
            i++;
        }

        while (j < defaultArgs.length) {
            extendArgs(i);
            myArgs.set(i, defaultArgs[j]);
            myArgsLeadingSpaces.set(i, BasedSequence.NULL);
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
        myArgsLeadingSpaces.clear();

        Collections.addAll(myArgs, myCommandType.myDefaultArgs);

        for (String arg : myCommandType.myDefaultArgs) {
            myArgsLeadingSpaces.add(BasedSequence.NULL);
        }
    }

    // raw manipulation
    public void setArg(int index, @NotNull String arg, @NotNull BasedSequence leadingSpaces) {
        if (index == getArgCount()) {
            myArgs.add(arg);
            myArgsLeadingSpaces.add(leadingSpaces);
        } else {
            myArgs.set(index, arg);
            myArgsLeadingSpaces.set(index, leadingSpaces);
        }
    }

    public void clearArgs() {
        myArgs.clear();
        myArgsLeadingSpaces.clear();
    }

    public void addArg(@NotNull String arg, @NotNull BasedSequence argLeadingSpaces) {
        myArgs.add(arg);
        myArgsLeadingSpaces.add(argLeadingSpaces);
    }

    public void addArg(int index, @NotNull String arg, @NotNull BasedSequence argLeadingSpaces) {
        myArgs.set(index, arg);
        myArgsLeadingSpaces.set(index, argLeadingSpaces);
    }

    public void removeArg(int index) {
        myArgs.remove(index);
        myArgsLeadingSpaces.remove(index);
    }
}
