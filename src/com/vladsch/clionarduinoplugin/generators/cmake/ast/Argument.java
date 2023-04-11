package com.vladsch.clionarduinoplugin.generators.cmake.ast;

import com.vladsch.flexmark.util.ast.Node;
import com.vladsch.flexmark.util.sequence.BasedSequence;

public class Argument extends Node {
    private BasedSequence openingMarker = BasedSequence.NULL;
    private BasedSequence text = BasedSequence.NULL;
    private BasedSequence closingMarker = BasedSequence.NULL;

    public Argument() {
    }

    public Argument(final BasedSequence chars) {
        super(chars);
    }

    public Argument(final BasedSequence chars, final BasedSequence openingMarker, final BasedSequence text, final BasedSequence closingMarker) {
        super(chars);
        this.openingMarker = openingMarker;
        this.text = text;
        this.closingMarker = closingMarker;
    }
    
    public BasedSequence getLeadingSpaces() {
        Node previous = getPrevious();
        int startOffset = getStartOffset();

        if (previous == null) {
            if (getParent() instanceof Command) {
                startOffset = ((Command) getParent()).getOpeningMarker().getEndOffset();
            }
        } else {
            startOffset = previous.getEndOffset();
        } 
        
        return baseSubSequence(startOffset, getStartOffset());
    }

    @Override
    public BasedSequence[] getSegments() {
        return new BasedSequence[] { openingMarker, text, closingMarker };
    }

    @Override
    public void getAstExtra(StringBuilder out) {
        segmentSpanChars(out, openingMarker, "open");
        if (text.isNotNull()) segmentSpanChars(out, text, "text");
        segmentSpanChars(out, closingMarker, "close");
    }

    public BasedSequence getOpeningMarker() {
        return openingMarker;
    }

    public void setOpeningMarker(final BasedSequence openingMarker) {
        this.openingMarker = openingMarker;
    }

    public BasedSequence getText() {
        return text;
    }

    public void setText(final BasedSequence text) {
        this.text = text;
    }

    public BasedSequence getClosingMarker() {
        return closingMarker;
    }

    public void setClosingMarker(final BasedSequence closingMarker) {
        this.closingMarker = closingMarker;
    }
}
