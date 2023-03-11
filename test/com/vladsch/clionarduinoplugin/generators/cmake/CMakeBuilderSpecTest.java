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
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class CMakeBuilderSpecTest extends ComboSpecTestCase {
    private static final DataHolder OPTIONS = new MutableDataSet()
            .set(CMakeParser.AUTO_CONFIG, true)
            .set(CMakeParser.AST_LINE_END_EOL, true)
            .set(CMakeParser.AST_COMMENTS, true)
            .set(CMakeParser.AST_BLANK_LINES, true)
            .set(CMakeParser.AST_ARGUMENT_SEPARATORS, true)
            .set(CMakeParser.AST_COMMENTED_OUT_COMMANDS, true)
            //.set(ExtraRenderer.DUMP_OPTIONS, true)
            ;

    protected static final Map<String, DataHolder> optionsMap = new HashMap<>();

    static Map<String, String> valueSet(String... values) {
        assert (values.length & 1) == 0;
        int i = values.length;
        HashMap<String, String> valueSet = new HashMap<>();

        while (i > 1) {
            String value = values[--i];
            String key = (String) values[--i];
            valueSet.put(key, value);
        }
        return valueSet;
    }

    static Set<String> suppressSet(String... values) {
        int i = values.length;
        HashSet<String> valueSet = new HashSet<>();

        while (i > 1) {
            String value = values[--i];
            valueSet.add(value);
        }
        return valueSet;
    }

    static {
        TestResourceUrlResolver.registerResourceUrlResolvers();

        optionsMap.put("board-pro", new MutableDataSet().set(ArduinoCMakeListsTxtBuilderRenderer.VALUE_MAP, valueSet("SET_BOARD", "pro", "SET_CPU", "8MHzatmega328", "PROJECT", "${MAKE_PROJECT_NAME}")));
        optionsMap.put("change-all", new MutableDataSet().set(ArduinoCMakeListsTxtBuilderRenderer.VALUE_MAP, valueSet(
                "CMAKE_MINIMUM_REQUIRED_VERSION", "Maj.Min.Rev.Twk",
                "SET_CMAKE_TOOLCHAIN_FILE", "setCmakeToolchainFile",
                "SET_CMAKE_CXX_STANDARD", "setCmakeCxxStandard",
                "SET_PROJECT_NAME", "setProjectName",
                "PROJECT", "${MAKE_PROJECT_NAME}",
                "SET_BOARD", "setBoard",
                "SET_CPU", "setCpu",
                "SET_SKETCH", "setSketch",
                "SET_PROGRAMMER", "setProgrammer",
                "SET_PORT", "setPort",
                "SET_AFLAGS", "setAflags",
                "SET_HDRS", "setHdrs",
                "SET_SRCS", "setSrcs",
                "SET_UPLOAD_SPEED", "setUploadSpeed",
                "SET_LIB_NAME_RECURSE", "setLibNameRecurse",
                "LIB_NAME", "libName",
                "SET_UPLOAD_SPEED", "setUploadSpeed",
                "LINK_DIRECTORIES", "linkDirectories",
                "ADD_SUBDIRECTORY", "addSubdirectory",
                "GENERATE_ARDUINO_LIBRARY", "${CMAKE_PROJECT_NAME}",
                "GENERATE_ARDUINO_FIRMWARE", "${CMAKE_PROJECT_NAME}",
                "", ""
        )));
        optionsMap.put("add-project", new MutableDataSet().set(ArduinoCMakeListsTxtBuilderRenderer.VALUE_MAP, valueSet(
                "PROJECT", "${A_PROJECT_NAME}",
                "SET_PROGRAMMER", "setProgrammer",
                "", ""
        )));
        optionsMap.put("no-comment-unused", new MutableDataSet()
                .set(ArduinoCMakeListsTxtBuilderRenderer.SUPPRESS_COMMENTED_SET, suppressSet(
                        "SET_PROJECT_NAME",
                        "SET_PORT",
                        ""
                ))
                .set(ArduinoCMakeListsTxtBuilderRenderer.SUPPRESS_COMMENTED, true)
        );
        optionsMap.put("dump-options", new MutableDataSet().set(ExtraRenderer.DUMP_OPTIONS, true));
        optionsMap.put("dump-after", new MutableDataSet().set(ArduinoCMakeListsTxtBuilderRenderer.DUMP_ELEMENTS_AFTER, true));
        optionsMap.put("dump-before", new MutableDataSet().set(ArduinoCMakeListsTxtBuilderRenderer.DUMP_ELEMENTS_BEFORE, true));
        optionsMap.put("set-or-add", new MutableDataSet().set(ArduinoCMakeListsTxtBuilderRenderer.SET_OR_ADD, true));
        optionsMap.put("unmodified-original", new MutableDataSet().set(ArduinoCMakeListsTxtBuilderRenderer.USE_UNMODIFIED_ORIGINAL, true));
        optionsMap.put("dump-variables", new MutableDataSet().set(ArduinoCMakeListsTxtBuilderRenderer.DUMP_VARIABLE_MAP, true));
        optionsMap.put("reset-project", new MutableDataSet().set(ArduinoCMakeListsTxtBuilderRenderer.RESET_PROJECT_TO_DEFAULTS, true));
    }
    
    private static final String SPEC_RESOURCE = "cmake_builder_spec.md";
    private static final ResourceLocation RESOURCE_LOCATION = ResourceLocation.of(CMakeBuilderSpecTest.class, SPEC_RESOURCE);

    private static DataHolder optionsSet(String optionSet) {
        return optionsMap.get(optionSet);
    }

    public CMakeBuilderSpecTest(SpecExample example) {
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
        return new FlexmarkSpecExampleRenderer(example, combinedOptions, new CMakeIParser(combinedOptions), new ArduinoCMakeListsTxtBuilderRenderer(combinedOptions), true);
    }

    @Override
    public @NotNull SpecExample checkExample(@NotNull final SpecExample example) {
        return example;
    }
}
