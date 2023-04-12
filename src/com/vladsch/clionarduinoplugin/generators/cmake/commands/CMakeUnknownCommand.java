package com.vladsch.clionarduinoplugin.generators.cmake.commands;

import com.vladsch.flexmark.util.sequence.BasedSequence;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;

public class CMakeUnknownCommand extends CMakeCommandBase {
    final protected @NotNull String myCommandName;

    public CMakeUnknownCommand(@NotNull final String commandName, @NotNull final List<CommandArgument> args, boolean isAddEOL, boolean commented, boolean suppressibleCommented, @NotNull BasedSequence addEolBeforeClosing) {
        super(CMakeCommandType.UNKNOWN, args, isAddEOL, commented, suppressibleCommented, addEolBeforeClosing);
        myCommandName = commandName;
    }

    public CMakeUnknownCommand(@NotNull final String commandName, @NotNull final List<CommandArgument> args, boolean isAddEOL, @NotNull BasedSequence addEolBeforeClosing) {
        this(commandName, args, isAddEOL, false, false, addEolBeforeClosing);
    }

    public CMakeUnknownCommand(@NotNull final String commandName, boolean isAddEOL, @NotNull BasedSequence addEolBeforeClosing) {
        this(commandName, Collections.emptyList(), isAddEOL, false, false, addEolBeforeClosing);
    }

    public CMakeUnknownCommand(@NotNull final CMakeUnknownCommand other) {
        this(other.myCommandName, other.myArgs, other.myAddEOL, other.myCommented, other.mySuppressibleCommented, other.myAddBeforeClosing);
    }

    @Override
    @NotNull
    public String getCommandName() {
        return myCommandName;
    }

    @Override
    public String toString() {
        return "CMakeUnknownCommand{" +
                "" + myCommandName +
                ", =" + myArgs +
                '}';
    }
}
