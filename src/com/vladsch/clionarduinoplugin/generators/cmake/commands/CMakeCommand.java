package com.vladsch.clionarduinoplugin.generators.cmake.commands;

import com.vladsch.flexmark.util.sequence.BasedSequence;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;

public class CMakeCommand extends CMakeCommandBase {

    public CMakeCommand(@NotNull final CMakeCommandType commandType, @NotNull final List<CommandArgument> args, boolean isAddEOL, boolean commented, boolean suppressibleCommented, @NotNull BasedSequence addEolBeforeClosing) {
        super(commandType, args, isAddEOL, commented, suppressibleCommented, addEolBeforeClosing);
    }

    public CMakeCommand(@NotNull final CMakeCommandType commandType, @NotNull final List<CommandArgument> args, boolean isAddEOL, @NotNull BasedSequence addEolBeforeClosing) {
        this(commandType, args, isAddEOL, false, false, addEolBeforeClosing);
    }

    public CMakeCommand(@NotNull final CMakeCommandType commandType, boolean isAddEOL, @NotNull BasedSequence addEolBeforeClosing) {
        this(commandType, Collections.emptyList(), isAddEOL, false, false, addEolBeforeClosing);
    }

    public CMakeCommand(@NotNull final CMakeCommand other) {
        this(other.myCommandType, other.myArgs, other.myAddEOL, other.myCommented, other.mySuppressibleCommented, other.myAddBeforeClosing);
    }

    @Override
    public String toString() {
        return "CMakeCommand{" +
                "" + myCommandType +
                ", =" + myArgs +
                '}';
    }
}
