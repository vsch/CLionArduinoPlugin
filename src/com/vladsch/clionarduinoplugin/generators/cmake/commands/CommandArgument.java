/*
 * Copyright (c) 2016-2023 Vladimir Schneider <vladimir.schneider@gmail.com>
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

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
