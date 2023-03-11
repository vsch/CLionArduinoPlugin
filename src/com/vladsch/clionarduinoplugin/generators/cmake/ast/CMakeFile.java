package com.vladsch.clionarduinoplugin.generators.cmake.ast;

import com.vladsch.flexmark.util.ast.Document;
import com.vladsch.flexmark.util.data.DataHolder;
import com.vladsch.flexmark.util.misc.Pair;
import com.vladsch.flexmark.util.sequence.BasedSequence;

import java.util.List;

public class CMakeFile extends Document {
    final private List<Pair<String, BasedSequence>> errors;

    public CMakeFile(final DataHolder options, final BasedSequence chars, List<Pair<String, BasedSequence>> errors) {
        super(options, chars);
        this.errors = errors;
    }

    public List<Pair<String, BasedSequence>> getErrors() {
        return errors;
    }
}
