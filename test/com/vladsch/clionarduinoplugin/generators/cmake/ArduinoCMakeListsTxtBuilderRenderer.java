package com.vladsch.clionarduinoplugin.generators.cmake;

import com.intellij.openapi.util.Comparing;
import com.vladsch.clionarduinoplugin.generators.cmake.ast.CMakeFile;
import com.vladsch.clionarduinoplugin.generators.cmake.commands.CMakeCommand;
import com.vladsch.clionarduinoplugin.generators.cmake.commands.CMakeCommandType;
import com.vladsch.clionarduinoplugin.generators.cmake.commands.CMakeElement;
import com.vladsch.flexmark.test.util.TestUtils;
import com.vladsch.flexmark.test.util.spec.IRenderBase;
import com.vladsch.flexmark.util.ast.Node;
import com.vladsch.flexmark.util.data.DataHolder;
import com.vladsch.flexmark.util.data.DataKey;
import com.vladsch.flexmark.util.data.DataKeyBase;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

class ArduinoCMakeListsTxtBuilderRenderer extends IRenderBase {
    public ArduinoCMakeListsTxtBuilderRenderer() {
        this(null);
    }

    public ArduinoCMakeListsTxtBuilderRenderer(DataHolder options) {
        super(options);
    }

    final static public DataKey<Map<String, String>> VALUE_MAP = new DataKey<>("VALUE_MAP", LinkedHashMap::new);
    final static public DataKey<Set<String>> SUPPRESS_COMMENTED_SET = new DataKey<>("SUPPRESS_COMMENTED_SET", HashSet::new);
    final static public DataKey<Boolean> DUMP_ELEMENTS_BEFORE = new DataKey<>("DUMP_ELEMENTS_BEFORE", false);
    final static public DataKey<Boolean> DUMP_ELEMENTS_AFTER = new DataKey<>("DUMP_ELEMENTS_AFTER", false);
    final static public DataKey<Boolean> DUMP_VARIABLE_MAP = new DataKey<>("DUMP_VARIABLE_MAP", false);
    final static public DataKey<Boolean> SET_OR_ADD = new DataKey<>("SET_OR_ADD", false);
    final static public DataKey<Boolean> SUPPRESS_COMMENTED = new DataKey<>("SUPPRESS_COMMENTED", false);
    final static public DataKey<Boolean> USE_UNMODIFIED_ORIGINAL = new DataKey<>("USE_UNMODIFIED_ORIGINAL", false);
    final static public DataKey<Boolean> RESET_PROJECT_TO_DEFAULTS = new DataKey<>("RESET_PROJECT_TO_DEFAULTS", false);

    @Override
    public void render(Node node, Appendable output) {
        assert node instanceof CMakeFile;
        CMakeFile cMakeFile = (CMakeFile) node;
        Map<String, String> values = VALUE_MAP.get(getOptions());
        Map<String, Object> valueSet = new HashMap<>(VALUE_MAP.get(getOptions()));
        Set<String> suppressSet = new HashSet<>(SUPPRESS_COMMENTED_SET.get(getOptions()));
        CMakeListsTxtBuilder builder = new ArduinoCMakeListsTxtBuilder(cMakeFile, valueSet);
        boolean setOrAdd = SET_OR_ADD.get(node.getDocument());
        boolean suppressCommented = SUPPRESS_COMMENTED.get(node.getDocument());

        try {
            if (DUMP_VARIABLE_MAP.get(cMakeFile)) {
                // dump the variables tree
                output.append("# cMakeProjectNameMacro = ").append(builder.getCMakeProjectNameMacro()).append("\n");
                output.append("# outputCMakeProjectNameMacro = ").append(builder.getOutputCMakeProjectNameMacro()).append("\n");
                output.append("# cMakeProjectName = ").append(builder.getCMakeProjectName()).append("\n");
                output.append("# ").append(builder.getCMakeVariableValues().toString().replace("\n", "\n# ")).append("\n");
            }

            if (DUMP_ELEMENTS_BEFORE.get(cMakeFile)) {
                // dump the element tree
                for (CMakeElement element : builder.getElements()) {
                    String s = element.toString();
                    output.append("# ").append(TestUtils.fromVisibleSpecText(s.replace('\n', '\r'))).append("\n");
                }
            }

            LinkedHashMap<String, ArrayList<String>> commandArgs = new LinkedHashMap<>();

            // prepare all argument lists for the commands
            for (Map.Entry<String, String> entry : values.entrySet()) {
                String name = entry.getKey();
                int index = 0;
                int pos = name.indexOf('[');
                if (pos != -1) {
                    try {
                        index = Integer.parseUnsignedInt(name.substring(pos + 1, name.length() - 1));
                    } catch (NumberFormatException ignored) {
                    }
                    name = name.substring(0, pos);
                }

                ArrayList<String> args = commandArgs.computeIfAbsent(name, (n) -> new ArrayList<>());
                while (index >= args.size()) args.add(null);
                args.set(index, entry.getValue());
            }

            // set project first
            builder.setWantCommented(true);

            boolean projectFirst = false;
            String projectName = "PROJECT";

            if (projectFirst && commandArgs.containsKey(projectName)) {
                String name = projectName;
                ArrayList<String> args = commandArgs.get(name);

                if (setOrAdd) {
                    CMakeCommandType commandType = builder.getCommandType(name);
                    //noinspection VariableNotUsedInsideIf
                    if (commandType != null) {
                        CMakeCommand command = builder.setOrAddCommand(name, args);
                        valueSet.remove(name);
                    }
                } else {
                    CMakeCommand command = builder.setCommand(name, args);
                    //noinspection VariableNotUsedInsideIf
                    if (command != null) {
                        valueSet.remove(name);
                    }
                }
            }

            for (Map.Entry<String, ArrayList<String>> entry : commandArgs.entrySet()) {
                String name = entry.getKey();
                if (projectFirst && projectName.equals(name)) continue;

                ArrayList<String> args = entry.getValue();

                if (setOrAdd) {
                    CMakeCommandType commandType = builder.getCommandType(name);
                    //noinspection VariableNotUsedInsideIf
                    if (commandType != null) {
                        CMakeCommand command = builder.setOrAddCommand(name, args);
                        valueSet.remove(entry.getKey());
                    }
                } else {
                    CMakeCommand command = builder.setCommand(name, args);
                    //noinspection VariableNotUsedInsideIf
                    if (command != null) {
                        valueSet.remove(entry.getKey());
                    }
                }
            }

            for (String name : suppressSet) {
                CMakeCommand command = builder.getCommand(name);
                if (command != null) {
                    command.setCommentOut(true);
                    command.setSuppressibleCommented(true);
                }
            }

            if (RESET_PROJECT_TO_DEFAULTS.getFrom(getOptions())) {
                builder.setOrAddCommand(CMakeListsTxtBuilder.Companion.getPROJECT()).clearToDefaults();
            }

            String contents = builder.getCMakeContents(valueSet, suppressCommented, USE_UNMODIFIED_ORIGINAL.getFrom(cMakeFile));
            output.append(contents);

            if (ExtraRenderer.DUMP_OPTIONS.getFrom(cMakeFile)) {
                // dump the options
                Map<? extends DataKeyBase<?>, Object> all = ((CMakeFile) node).getAll();
                ArrayList<? extends DataKeyBase<?>> keys = new ArrayList<>(all.keySet());
                keys.sort((o1, o2) -> Comparing.compare(o1.getName(), o2.getName()));

                for (DataKeyBase<?> key : keys) {
                    output.append("# ").append(key.getName()).append("->").append(String.valueOf(all.get(key))).append("\n");
                }
            }
            if (DUMP_ELEMENTS_AFTER.getFrom(cMakeFile)) {
                // dump the element tree
                for (CMakeElement element : builder.getElements()) {
                    String s = element.toString();
                    output.append("# ").append(TestUtils.fromVisibleSpecText(s.replace('\n', '\r'))).append("\n");
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
