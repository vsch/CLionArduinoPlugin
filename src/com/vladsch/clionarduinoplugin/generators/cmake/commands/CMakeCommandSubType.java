package com.vladsch.clionarduinoplugin.generators.cmake.commands;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;

public class CMakeCommandSubType extends CMakeCommandType {
    final protected @NotNull CMakeCommandType mySpecializationOf;

    static String[] combineArgs(@NotNull CMakeCommandType specializationOf, String[] args1, String[] args2) {
        String[] result = new String[args1.length + Math.max(args2.length - specializationOf.myWildcardCount, 0)];

        int i = 0;
        int j = 0;

        // now we copy from args1 but first replace args1's wildcards with values from args2
        for (String arg : args1) {
            int pos = 0;

            while (pos < arg.length()) {
                pos = arg.indexOf(WILDCARD_ARG_MARKER, pos);
                if (pos == -1) break;
                if (j >= args2.length) break;

                String replacement = args2[j++];
                arg = arg.substring(0, pos) + replacement + arg.substring(pos + WILDCARD_ARG_MARKER.length());
                pos += replacement.length();
            }

            result[i++] = arg;
        }

        // copy remaining args2
        if (j < args2.length) System.arraycopy(args2, j, result, i, args2.length - j);
        return result;
    }

    public CMakeCommandSubType(@NotNull final String name, @NotNull final CMakeCommandType specializationOf, final @NotNull String[] fixedArgs, final int minArgs, final int maxArgs) {
        this(name, specializationOf, fixedArgs, minArgs, maxArgs, false, true, true, null);
    }

    public CMakeCommandSubType(@NotNull final String name, @NotNull final CMakeCommandType specializationOf, final @NotNull String[] fixedArgs, final int minArgs, final int maxArgs, final boolean noDupeArgs) {
        this(name, specializationOf, fixedArgs, minArgs, maxArgs, noDupeArgs, true, true, null);
    }

    public CMakeCommandSubType(@NotNull final String name, @NotNull final CMakeCommandType specializationOf, final @NotNull String[] fixedArgs, final int minArgs, final int maxArgs, final boolean noDupeArgs, final boolean isMultiple, final boolean isKeepLast) {
        this(name, specializationOf, fixedArgs, minArgs, maxArgs, noDupeArgs, isMultiple, isKeepLast, null);
    }

    public CMakeCommandSubType(@NotNull final String name, @NotNull final CMakeCommandType specializationOf, final @NotNull String[] fixedArgs, final int minArgs, final int maxArgs, final boolean noDupeArgs, final boolean isMultiple, final boolean isKeepLast, @Nullable String[] defaults) {
        super(name, specializationOf.myCommand, combineArgs(specializationOf, specializationOf.myFixedArgs, fixedArgs), minArgs, maxArgs, noDupeArgs, isMultiple, isKeepLast, defaults, 0);
        mySpecializationOf = specializationOf;
    }

    public CMakeCommandType getSpecializationOf() {
        return mySpecializationOf;
    }

    @Override
    public int getAncestors() {
        return mySpecializationOf.getAncestors() + 1;
    }

    @Override
    public boolean isSubTypeOf(CMakeCommandType commandType) {
        return mySpecializationOf == commandType || mySpecializationOf.isSubTypeOf(commandType);
    }

    @Override
    public boolean isOfType(CMakeCommandType commandType) {
        return this == commandType || isSubTypeOf(commandType);
    }

    @Override
    public String toString() {
        return "CMakeCommandTypeSpecialization{" +
                "'" + myName + '\'' +
                " of '" + mySpecializationOf.myName + '\'' +
                ", '" + myCommand + '\'' +
                ", " + Arrays.toString(myFixedArgs) +
                ", [" + myMinArgs +
                ", " + myMaxArgs +
                "], NoDupes " + myNoDupeArgs +
                ", IsMulti " + myIsMultiple +
                '}';
    }
}
