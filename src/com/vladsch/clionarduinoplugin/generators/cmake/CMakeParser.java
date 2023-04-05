package com.vladsch.clionarduinoplugin.generators.cmake;

import com.vladsch.clionarduinoplugin.generators.cmake.ast.*;
import com.vladsch.flexmark.util.ast.Node;
import com.vladsch.flexmark.util.misc.CharPredicate;
import com.vladsch.flexmark.util.misc.Pair;
import com.vladsch.flexmark.util.data.DataKey;
import com.vladsch.flexmark.util.data.MutableDataSet;
import com.vladsch.plugin.util.SemanticVersion;
import com.vladsch.flexmark.util.data.DataHolder;
import com.vladsch.flexmark.util.sequence.BasedSequence;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.MatchResult;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CMakeParser {
    final static public String _EOL = "(?:\r\n|\r|\n|$)";
    final static public String _ESCAPABLE = "[()#\"\\\\ $@^trn;]";
    final static public String _NEEDS_QUOTING = "[()#\"\\\\ ^\t\r\n;]";
    final static public String _NEEDS_QUOTED_ESCAPING = "[\"\\\\\t\n\r]";
    final static public String _COMMAND = "(?:[A-Za-z_][A-Za-z0-9_]*)";
    final static public String _ESCAPED_CHAR = "(?:\\\\" + _ESCAPABLE + ")";
    final static public String _QUOTED_ARGUMENT_LINE_CONT = "(?:\"(?:" + _ESCAPED_CHAR + "|\\\\" + _EOL + "|[^\"\\x00\\\\])*\")";
    final static public String _QUOTED_ARGUMENT_NO_LINE_CONT = "(?:\"(?:" + _ESCAPED_CHAR + "|[^\"\\x00\\\\])*\")";
    final static public String _BRACKET_ARGUMENT = "(?:(\\[(=*)\\[)((?:.|[\\r\\n])*?)(\\]\\2]))";
    final static public String _UNQUOTED_ARGUMENT = "(?:(?:[^()#\"\\\\ \t\r\n;]|" + _ESCAPED_CHAR + ")+)";
    public static final String _LEGACY_CHARS = "[^ \\\\\t\r\n\";()]";
    final static public String _UNQUOTED_LEGACY = "(?:" + _LEGACY_CHARS + "(?:" + _QUOTED_ARGUMENT_NO_LINE_CONT + "|" + _ESCAPED_CHAR + "|" + _LEGACY_CHARS + "|\\(" + _LEGACY_CHARS + "+\\))*)";

    final static public Pattern SP = Pattern.compile("^(?:[ \t])*");
    final static public Pattern NEEDS_QUOTING = Pattern.compile(_NEEDS_QUOTING);
    final static public Pattern NEEDS_ESCAPING = Pattern.compile(_NEEDS_QUOTED_ESCAPING);
    final static public Pattern REST_OF_LINE = Pattern.compile("^.*" + _EOL);
    final static public Pattern COMMAND = Pattern.compile("^" + _COMMAND);
    final static public Pattern EOL = Pattern.compile("^" + _EOL);
    final static public Pattern BRACKET_ARGUMENT = Pattern.compile("^" + _BRACKET_ARGUMENT);
    final static public Pattern QUOTED_ARGUMENT_LINE_CONT = Pattern.compile("^" + _QUOTED_ARGUMENT_LINE_CONT);
    final static public Pattern QUOTED_ARGUMENT_NO_LINE_CONT = Pattern.compile("^" + _QUOTED_ARGUMENT_NO_LINE_CONT);
    final static public Pattern UNQUOTED_ARGUMENT = Pattern.compile("^" + _UNQUOTED_ARGUMENT);
    final static public Pattern UNQUOTED_LEGACY = Pattern.compile("^" + _UNQUOTED_LEGACY);

    // syntax options
    final static public DataKey<Integer> MAX_ERROR_LINE_RECOVERIES = new DataKey<>("MAX_ERROR_LINE_RECOVERIES", 5);
    final static public DataKey<Boolean> AUTO_CONFIG = new DataKey<>("AUTO_CONFIG", true);
    final static public DataKey<Boolean> BRACKET_COMMENTS = new DataKey<>("BRACKET_COMMENTS", false);
    final static public DataKey<Boolean> LINE_CONTINUATION = new DataKey<>("LINE_CONTINUATION", false);

    // optional ast node inclusion
    final static public DataKey<Boolean> AST_COMMAND_BLOCKS = new DataKey<>("AST_COMMAND_BLOCKS", false);
    final static public DataKey<Boolean> AST_COMMENTS = new DataKey<>("AST_COMMENTS", false);
    final static public DataKey<Boolean> AST_BLANK_LINES = new DataKey<>("AST_BLANK_LINES", false);
    final static public DataKey<Boolean> AST_LINE_END_EOL = new DataKey<>("AST_LINE_END_EOL", false);
    final static public DataKey<Boolean> AST_ARGUMENT_SEPARATORS = new DataKey<>("AST_ARGUMENT_SEPARATORS", false);
    final static public DataKey<Boolean> AST_COMMENTED_OUT_COMMANDS = new DataKey<>("AST_COMMENTED_OUT_COMMANDS", false);

    final private CMakeFile document;
    final private BasedSequence input;
    final private List<Pair<String, BasedSequence>> errors;
    final private CMakeParserOptions options;

    private int index;

    public CMakeParser(@NotNull final BasedSequence input, @Nullable DataHolder options) {
        this.input = input;
        index = 0;
        errors = new ArrayList<>();
        document = new CMakeFile(options == null ? new MutableDataSet() : new MutableDataSet(options), input, errors);
        this.options = new CMakeParserOptions(options);
        parse();
    }

    public CMakeFile getDocument() {
        return document;
    }

    public static @NotNull String getArgText(@NotNull String arg) {
        return getArgText((CharSequence) arg).toString();
    }

    public static @NotNull CharSequence getArgText(@NotNull CharSequence arg) {
        // wrap in quotes or brackets as needed
        if (NEEDS_QUOTING.matcher(arg).find()) {
            StringBuffer sb = new StringBuffer();
            sb.append("\"");

            Matcher matcher = NEEDS_ESCAPING.matcher(arg);
            while (matcher.find()) {
                char c = arg.charAt(matcher.start());
                switch (c) {
                    case '\t':
                        matcher.appendReplacement(sb, "\\\\t");
                        break;
                    case '\r':
                        matcher.appendReplacement(sb, "\\\\r");
                        break;
                    case '\n':
                        matcher.appendReplacement(sb, "\\\\n");
                        break;
                    default:
                        matcher.appendReplacement(sb, "");
                        sb.append("\\").append(matcher.group());
                        break;
                }
            }
            matcher.appendTail(sb);

            sb.append("\"");

            return sb;
        } else {
            return arg;
        }
    }

    protected void addError(final Node parent, @NotNull String message) {
        // take from input to end of line
        BasedSequence location = toEOL();
        if (location == null) {
            location = input.subSequence(input.length());
        }

        Node node = new UnrecognizedInput(location);
        parent.appendChild(node);
        addError(message, location);
    }

    protected void addError(@NotNull String message, BasedSequence location) {
        errors.add(Pair.of(message, location));
    }

    private boolean parse() {
        boolean res;

        do {
            res = parseElement();
            if (!res) {
                // see if parsed the whole file
                if (peek() != '\0') {
                    // try to continue by removing lines until we succeed up to a max count
                    int errorLines = options.maxErrorLineRecoveries;

                    while (!res && errorLines-- > 0) {
                        addError(document, "Unrecognized input");

                        if (peek() == '\0') {
                            break;
                        }

                        res = parseElement();
                    }

                    if (!res) break;
                }
            }
        } while (res);

        return errors.isEmpty();
    }

    /**
     * Parse the next inline element in subject, advancing input index.
     * On success, add the result to block's children and return true.
     * On failure, return false.
     *
     * @return false on failure true on success
     */
    protected boolean parseElement() {
        boolean res;
        int start = index;

        sp();

        char c = peek();
        if (c == '\0') {
            return false;
        }

        switch (c) {
            case '#':
                if (options.astCommentedOutCommands) {
                    // possible commented out command
                    int saved = index;
                    index++;
                    res = parseCommandInvocation(document, true);
                    if (res) {
                        // it is
                        CommentedOutCommand node = (CommentedOutCommand) document.getLastChildAny(CommentedOutCommand.class);
                        if (node != null) {
                            node.setLeadingSpaces(input.subSequence(start, saved));
                            node.setCommentMarker(input.subSequence(saved, saved + 1));
                            node.setCharsFromContent();
                        }
                        break;
                    } else {
                        index = saved;
                    }
                }

                res = parseLineEnding(document, start);
                break;
            case '\r':
            case '\n':
                res = parseEOL(document);
                break;

            default: {
                // must be command invocation
                res = parseCommandInvocation(document, false);
            }
            break;
        }

        return res;
    }

    protected boolean parseCommandInvocation(Node parent, final boolean isCommentedOut) {
        sp();

        int saved = index;
        BasedSequence command = match(COMMAND);
        if (command != null) {
            int start = index;

            sp();
            char c = peek();
            if (c == '(') {
                // possible command
                Command commandNode = isCommentedOut ? new CommentedOutCommand() : new Command();
                parent.appendChild(commandNode);

                BasedSequence open = input.subSequence(index, index + 1);
                index++;

                sp();

                if (parseArguments(commandNode)) {
                    // if we have ) lineEnding then all good
                    sp();
                    c = peek();
                    if (c == ')') {
                        BasedSequence close = input.subSequence(index, index + 1);
                        index++;

                        if (parseLineEnding(parent, index)) {
                            // we are good
                            commandNode.setLeadingSpaces(input.subSequence(start, command.getEndOffset()));
                            commandNode.setCommand(command);
                            commandNode.setOpeningMarker(open);
                            commandNode.setArguments(input.subSequence(open.getEndOffset(), close.getStartOffset()));
                            commandNode.setClosingMarker(close);
                            commandNode.setCharsFromContent();

                            if (options.autoConfig) {
                                // if this is cmake_minimum_required(VERSION v.v.v) configure parser flags for it
                                if (command.equalsIgnoreCase("cmake_minimum_required")) {
                                    Node firstArg = commandNode.getFirstChildAny(Argument.class);
                                    if (firstArg != null && firstArg.getChars().equals("VERSION")) {
                                        Node minVersionArg = firstArg.getNext();
                                        if (minVersionArg != null) {
                                            // TODO: figure out if need to unescape
                                            BasedSequence version = minVersionArg.getChars();
                                            BasedSequence[] minMaxVersions = version.split("...", 2, 0);

                                            // use max version if given
                                            SemanticVersion minVersion = new SemanticVersion(minMaxVersions[0]);
                                            SemanticVersion maxVersion = new SemanticVersion(minMaxVersions.length > 1 ? minMaxVersions[1] : minMaxVersions[0]);
                                            if (minVersion.compareTo(maxVersion) < 0) minVersion = maxVersion;

                                            if (minVersion.compareTo("3.0.0") >= 0) {
                                                // all on
                                                options.lineContinuation = true;
                                                options.blockComments = true;
                                            } else {
                                                options.lineContinuation = false;
                                                options.blockComments = false;
                                                if (minVersion.compareTo("2.8.12") < 0) {
                                                    // TODO: warning on quoted followed by quoted or unquoted without space
                                                }
                                            }

                                            // save options
                                            options.setIn(document);
                                        }
                                    }
                                }
                            }
                            return true;
                        } else {
                            if (!isCommentedOut) {
                                addError(parent, "Line Ending expected");
                            }
                        }
                    } else {
                        if (!isCommentedOut) {
                            addError(parent, "Closing ) expected");
                            // TODO: skip to unescaped and unquoted )
                        }
                    }
                }

                // false alarm, not a command invocation
                commandNode.unlink();
            }
        }

        index = saved;
        return false;
    }

    protected boolean parseArguments(Node parent) {
        while (true) {
            int start = index;
            sp();

            char c = peek();
            if (c == '\0') {
                break;
            }

            BasedSequence peekedChar = input.subSequence(index, index + 1);
            if (c == ';') {
                // separator
                index++;
                if (options.astArgumentSeparators) {
                    Node node = new Separator(peekedChar);
                    parent.appendChild(node);
                }
            } else if (c == '#') {
                // could be comment
                parseComment(parent, index);
            } else if (c == '(') {
                // could be arguments if matching )
                Node node = new Argument(peekedChar, BasedSequence.NULL, peekedChar, BasedSequence.NULL);
                index++;

                // see if we have arguments matching
                if (parseArguments(node)) {
                    // see if we have )
                    sp();
                    c = peek();
                    if (c == ')') {
                        // we have it
                        parent.appendChild(node);
                        parent.takeChildren(node);
                        peekedChar = input.subSequence(index, index + 1);
                        node = new Argument(peekedChar, BasedSequence.NULL, peekedChar, BasedSequence.NULL);
                        index++;
                        parent.appendChild(node);
                    } else {
                        addError(parent, "Closing ) expected");
                        break;
                    }
                } else {
                    addError(parent, "Argument expected");
                    break;
                }
            } else if (!parseLineEnding(parent, start)) {
                if (!parseArgument(parent)) {
                    break;
                }
            }
        }
        return true;
    }

    protected boolean parseArgument(Node parent) {
        char c = peek();

        if (c == '[') {
            // possible bracket argument
            Node node = parseBracketArgument();
            if (node != null) {
                parent.appendChild(node);
                return true;
            }
        }

        if (c == '"') {
            // quoted argument
            BasedSequence arg = match(options.lineContinuation ? QUOTED_ARGUMENT_LINE_CONT : QUOTED_ARGUMENT_NO_LINE_CONT);
            if (arg != null) {
                Argument node = new Argument(arg, arg.subSequence(0, 1), arg.subSequence(1, arg.length() - 1), arg.subSequence(arg.length() - 1));
                parent.appendChild(node);
                return true;
            }
        }

        // legacy unquoted
        BasedSequence arg = match(UNQUOTED_LEGACY);
        if (arg != null) {
            Argument node = new Argument(arg, BasedSequence.NULL, arg, BasedSequence.NULL);
            parent.appendChild(node);
            return true;
        }

        arg = match(UNQUOTED_ARGUMENT);
        if (arg != null) {
            Argument node = new Argument(arg, BasedSequence.NULL, arg, BasedSequence.NULL);
            parent.appendChild(node);
            return true;
        }

        return false;
    }

    @Nullable
    protected Node parseBracketArgument() {
        BasedSequence[] arg = matchWithGroups(BRACKET_ARGUMENT);
        if (arg != null) {
            Argument node = new Argument(arg[0], arg[1], arg[3], arg[4]);
            return node;
        }
        return null;
    }

    public boolean parseLineEnding(Node parent, final int start) {
        BasedSequence chars;

        sp();

        char c = peek();
        if (c == '\0') {
            return true;        // valid end of line for the file
        } else if (c == '#') {
            return parseComment(parent, start);
        } else {
            return parseEOL(parent);
        }
    }

    public boolean parseComment(final Node parent, final int start) {
        BasedSequence chars;
        if (peek(1) == '[' && options.blockComments) {
            int saved = index++;

            BasedSequence[] arg = matchWithGroups(BRACKET_ARGUMENT);
            if (arg != null) {
                // block comment
                if (options.astComments) {
                    BracketComment node = new BracketComment(input.subSequence(saved, arg[0].getEndOffset()), input.subSequence(saved, arg[1].getEndOffset()), arg[3], arg[4]);
                    parent.appendChild(node);
                }
                return true;
            }

            index--;
        }

        chars = toEOL();
        int offset = chars.charAt(chars.length() - 1) == '\n' ? 1 : 0;

        if (options.astComments) {
            LineComment node = new LineComment(chars.baseSubSequence(start, chars.getEndOffset()));
            parent.appendChild(node);
        } else if (options.astLineEndEol && offset > 0 && !(parent.getLastChild() instanceof LineEnding)) {
            // remove trailing EOL from comment and add it as separate
            LineEnding node = new LineEnding(chars.subSequence(chars.length() - offset));
            parent.appendChild(node);
        }
        return true;
    }

    public boolean parseEOL(Node parent) {
        BasedSequence chars;

        char c = peek();

        if (c == '\0') {
            return true;        // valid end of line for the file
        } else {
            int start = index;
            chars = match(EOL);
            if (chars != null) {
                if (options.astBlankLines || options.astLineEndEol) {
                    // only blank line if did not have input before
                    int startOfLine = input.startOfLine(start);
                    int pos = input.indexOfAnyNot(CharPredicate.WHITESPACE, startOfLine, start);
                    if (pos == -1) {
                        // blank line
                        if (options.astBlankLines) {
                            Node node = new BlankLine(chars);
                            parent.appendChild(node);
                        }
                    } else {
                        if (options.astLineEndEol) {
                            Node node = new LineEnding(chars);
                            parent.appendChild(node);
                        }
                    }
                }
                return true;
            }
        }
        return false;
    }

    public BasedSequence match(Pattern re) {
        if (index >= input.length()) {
            return null;
        }
        Matcher matcher = re.matcher(input);
        matcher.region(index, input.length());
        boolean m = matcher.find();
        if (m) {
            index = matcher.end();
            MatchResult result = matcher.toMatchResult();
            return input.subSequence(result.start(), result.end());
        } else {
            return null;
        }
    }

    /**
     * If RE matches at current index in the input, advance index and return the match; otherwise return null.
     *
     * @param re pattern to match
     * @return sequence matched or null
     */
    public BasedSequence[] matchWithGroups(Pattern re) {
        if (index >= input.length()) {
            return null;
        }
        Matcher matcher = re.matcher(input);
        matcher.region(index, input.length());
        boolean m = matcher.find();
        if (m) {
            index = matcher.end();
            MatchResult result = matcher.toMatchResult();
            final int iMax = matcher.groupCount() + 1;
            BasedSequence[] results = new BasedSequence[iMax];
            results[0] = input.subSequence(result.start(), result.end());
            for (int i = 1; i < iMax; i++) {
                if (matcher.group(i) != null) {
                    results[i] = input.subSequence(result.start(i), result.end(i));
                } else {
                    results[i] = BasedSequence.NULL;
                }
            }
            return results;
        } else {
            return null;
        }
    }

    /**
     * If RE matches at current index in the input, advance index and return the match; otherwise return null.
     *
     * @param re pattern to match
     * @return matched matcher or null
     */
    public Matcher matcher(Pattern re) {
        if (index >= input.length()) {
            return null;
        }
        Matcher matcher = re.matcher(input);
        matcher.region(index, input.length());
        boolean m = matcher.find();
        if (m) {
            index = matcher.end();
            return matcher;
        } else {
            return null;
        }
    }

    /**
     * Parse to end of line, including EOL
     *
     * @return characters parsed or null if no end of line
     */
    public BasedSequence toEOL() {
        return match(REST_OF_LINE);
    }

    public char peek() {
        if (index < input.length()) {
            return input.charAt(index);
        } else {
            return '\0';
        }
    }

    public char peek(int ahead) {
        if (index + ahead < input.length()) {
            return input.charAt(index + ahead);
        } else {
            return '\0';
        }
    }

    /**
     * Parse zero or more spaces
     */
    public void sp() {
        match(SP);
    }
}
