package com.vladsch.clionarduinoplugin.generators.cmake.ast;

import com.vladsch.flexmark.util.ast.Node;
import com.vladsch.flexmark.util.misc.CharPredicate;
import com.vladsch.flexmark.util.sequence.BasedSequence;

public class Command extends Node {
    protected BasedSequence leadingSpaces = BasedSequence.NULL;
    protected BasedSequence command = BasedSequence.NULL;
    protected BasedSequence openingMarker = BasedSequence.NULL;
    protected BasedSequence arguments = BasedSequence.NULL;
    protected BasedSequence closingMarker = BasedSequence.NULL;

    public Command() {
    }

    public Command(final BasedSequence chars) {
        super(chars);
    }

    @Override
    public BasedSequence[] getSegments() {
        return new BasedSequence[] { leadingSpaces, command, openingMarker, arguments, closingMarker };
    }

    @Override
    public void getAstExtra(StringBuilder out) {
        segmentSpanChars(out, leadingSpaces, "spaces");
        segmentSpanChars(out, command, "text");
        segmentSpanChars(out, openingMarker, "open");
        if (arguments.isNotNull()) segmentSpanChars(out, arguments, "arguments");
        segmentSpanChars(out, closingMarker, "close");
    }

    public BasedSequence getLeadingSpaces() {
        return leadingSpaces;
    }
    
    public void setLeadingSpaces(BasedSequence leadingSpaces) {
        // need to trim spaces before EOL, these are previous node's trailing spaces
        if (leadingSpaces.indexOf('\n') > 0) {
            leadingSpaces = leadingSpaces.subSequence(leadingSpaces.countLeading(CharPredicate.SPACE_TAB));
        }
        this.leadingSpaces = leadingSpaces.isEmpty() ? BasedSequence.NULL : leadingSpaces;
    }

    public BasedSequence getCommand() {
        return command;
    }

    public void setCommand(final BasedSequence command) {
        this.command = command;
    }

    public BasedSequence getOpeningMarker() {
        return openingMarker;
    }

    public void setOpeningMarker(final BasedSequence openingMarker) {
        this.openingMarker = openingMarker;
    }

    public BasedSequence getArguments() {
        return arguments;
    }

    public void setArguments(final BasedSequence arguments) {
        this.arguments = arguments;
    }

    public BasedSequence getClosingMarker() {
        return closingMarker;
    }

    public void setClosingMarker(final BasedSequence closingMarker) {
        this.closingMarker = closingMarker;
    }
}
