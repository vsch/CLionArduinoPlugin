package com.vladsch.clionarduinoplugin.generators.cmake;

import com.vladsch.flexmark.test.util.spec.IRenderBase;
import com.vladsch.flexmark.util.ast.Node;
import com.vladsch.flexmark.util.data.DataHolder;
import org.jetbrains.annotations.NotNull;

class NullRenderer extends IRenderBase {
    public NullRenderer() {
        this(null);
    }

    public NullRenderer(DataHolder options) {
        super(options);
    }

    @Override
    public void render(@NotNull Node node, @NotNull Appendable output) {

    }
}
