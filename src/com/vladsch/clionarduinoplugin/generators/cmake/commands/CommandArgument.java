// Copyright (c) 2015-2023 Vladimir Schneider <vladimir.schneider@gmail.com> Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package com.vladsch.clionarduinoplugin.generators.cmake.commands;

import com.vladsch.flexmark.util.sequence.BasedSequence;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public class CommandArgument {
    final protected @NotNull BasedSequence myValue;
    final protected @NotNull BasedSequence myLeadingSpaces;

    protected CommandArgument(@NotNull BasedSequence value, @NotNull BasedSequence leadingSpaces) {
        myValue = value;
        myLeadingSpaces = leadingSpaces;
    }

    public @NotNull BasedSequence getValue() {
        return myValue;
    }

    public @NotNull BasedSequence getLeadingSpaces() {
        return myLeadingSpaces;
    }

    public boolean isEqualValue(@NotNull CommandArgument arg) {
        return Objects.equals(arg.myValue, myValue);
    }

    public boolean isEqualValue(@NotNull BasedSequence value) {
        return Objects.equals(value, myValue);
    }

    public boolean isEqualValue(@Nullable String value) {
        return isEqualValue(BasedSequence.of(value));
    }

    public boolean isEmpty() {
        return myValue.isEmpty();
    }

    public boolean isNull() {
        return this == NULL;
    }

    public boolean isNotNull() {
        return this != NULL;
    }

    public CommandArgument withValue(@Nullable String value) {
        return copyOf(this, value, myLeadingSpaces);
    }

    public CommandArgument withLeadingSpaces(@NotNull BasedSequence leadingSpaces) {
        return copyOf(this, myValue, leadingSpaces);
    }

    final public static CommandArgument NULL = new CommandArgument(BasedSequence.NULL, BasedSequence.NULL);

    public static CommandArgument of(@NotNull BasedSequence arg, @NotNull BasedSequence leadingSpaces) {
        return new CommandArgument(arg, leadingSpaces);
    }

    public static CommandArgument copyOf(@NotNull CommandArgument other, @NotNull BasedSequence arg, @NotNull BasedSequence leadingSpaces) {
        return other.isEqualValue(arg) && Objects.equals(other.myLeadingSpaces, leadingSpaces) ? other : new CommandArgument(arg, leadingSpaces);
    }

    public static CommandArgument copyOf(@NotNull CommandArgument other, @Nullable String arg, @NotNull BasedSequence leadingSpaces) {
        return copyOf(other, BasedSequence.of(arg), leadingSpaces);
    }

    public static CommandArgument of(@NotNull BasedSequence arg) {
        return new CommandArgument(arg, BasedSequence.NULL);
    }

    public static CommandArgument of(@Nullable String arg, @NotNull BasedSequence leadingSpaces) {
        return of(BasedSequence.of(arg), leadingSpaces);
    }

    public static CommandArgument of(@Nullable String arg) {
        return of(BasedSequence.of(arg));
    }
}
