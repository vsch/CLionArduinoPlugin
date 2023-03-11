package com.vladsch.clionarduinoplugin.generators.cmake;

import com.vladsch.flexmark.test.util.ComboSpecTestCase;
import com.vladsch.flexmark.test.util.FlexmarkSpecExampleRenderer;
import com.vladsch.flexmark.test.util.SpecExampleRenderer;
import com.vladsch.flexmark.test.util.spec.ResourceLocation;
import com.vladsch.flexmark.test.util.spec.SpecExample;
import com.vladsch.flexmark.util.data.DataHolder;
import com.vladsch.flexmark.util.data.MutableDataSet;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.junit.runners.Parameterized;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CMakeParserSpecTest extends ComboSpecTestCase {
    private static final DataHolder OPTIONS = new MutableDataSet()
            .set(CMakeParser.AUTO_CONFIG, false);

    protected static final Map<String, DataHolder> optionsMap = new HashMap<String, DataHolder>();
    static {
        TestResourceUrlResolver.registerResourceUrlResolvers();
        
        optionsMap.put("auto-config", new MutableDataSet().set(CMakeParser.AUTO_CONFIG, true));
        optionsMap.put("bracket-comments", new MutableDataSet().set(CMakeParser.BRACKET_COMMENTS, true));
        optionsMap.put("line-cont", new MutableDataSet().set(CMakeParser.LINE_CONTINUATION, true));

        optionsMap.put("ast-line-eol", new MutableDataSet().set(CMakeParser.AST_LINE_END_EOL, true));
        optionsMap.put("ast-comments", new MutableDataSet().set(CMakeParser.AST_COMMENTS, true));
        optionsMap.put("ast-blank", new MutableDataSet().set(CMakeParser.AST_BLANK_LINES, true));
        optionsMap.put("ast-arg-seps", new MutableDataSet().set(CMakeParser.AST_ARGUMENT_SEPARATORS, true));
        optionsMap.put("commented-out", new MutableDataSet().set(CMakeParser.AST_COMMENTED_OUT_COMMANDS, true));

        optionsMap.put("dump-options", new MutableDataSet().set(ExtraRenderer.DUMP_OPTIONS, true));
    }
    
    private static final String SPEC_RESOURCE = "cmake_parser_spec.md";
    private static final ResourceLocation RESOURCE_LOCATION = ResourceLocation.of(CMakeParserSpecTest.class, SPEC_RESOURCE);

    private static DataHolder optionsSet(String optionSet) {
        return optionsMap.get(optionSet);
    }

    public CMakeParserSpecTest(SpecExample example) {
        super(example, optionsMap, OPTIONS);
    }

    @Parameterized.Parameters(name = "{0}")
    public static List<Object[]> data() {
        return getTestData(RESOURCE_LOCATION);
    }

    @Override
    public DataHolder options(@NotNull String optionSet) {
        return optionsSet(optionSet);
    }

    @Override
    public @NotNull SpecExampleRenderer getSpecExampleRenderer(@NotNull final SpecExample example, @Nullable final DataHolder exampleOptions) {
        DataHolder combinedOptions = aggregate(myDefaultOptions, exampleOptions);
        return new FlexmarkSpecExampleRenderer(example, combinedOptions, new CMakeIParser(combinedOptions), new ExtraRenderer(combinedOptions), true);
    }

    @Override
    public @NotNull SpecExample checkExample(@NotNull final SpecExample example) {
        return example;
    }
}
