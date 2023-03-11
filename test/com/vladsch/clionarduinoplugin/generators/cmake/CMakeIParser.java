package com.vladsch.clionarduinoplugin.generators.cmake;

import com.vladsch.flexmark.test.util.spec.IParseBase;
import com.vladsch.flexmark.util.ast.Node;
import com.vladsch.flexmark.util.data.DataHolder;
import com.vladsch.flexmark.util.sequence.BasedSequence;
import org.jetbrains.annotations.NotNull;

class CMakeIParser extends IParseBase {
    public CMakeIParser() {
        this(null);
    }

    public CMakeIParser(DataHolder options) {
        super(options);
    }

    @NotNull
    @Override
    public Node parse(@NotNull BasedSequence input) {
        // here we make the lexer parse the input sequence from start to finish and accumulate everything in custom nodes
        CMakeParser parser = new CMakeParser(input, getOptions());
        return parser.getDocument();
    }
}
