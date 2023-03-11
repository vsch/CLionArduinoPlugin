package com.vladsch.clionarduinoplugin.generators.cmake;

import com.vladsch.clionarduinoplugin.generators.cmake.CMakeFormatter.CMakeFormatterContext;
import com.vladsch.clionarduinoplugin.generators.cmake.ast.*;
import com.vladsch.flexmark.formatter.MarkdownWriter;
import com.vladsch.flexmark.formatter.NodeFormatter;
import com.vladsch.flexmark.formatter.NodeFormatterContext;
import com.vladsch.flexmark.formatter.NodeFormattingHandler;
import com.vladsch.flexmark.util.ast.Node;
import com.vladsch.flexmark.util.data.DataHolder;
import com.vladsch.flexmark.util.sequence.BasedSequence;
import com.vladsch.flexmark.util.sequence.RepeatedSequence;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

@SuppressWarnings("MethodMayBeStatic")
public class CMakeNodeFormatter implements NodeFormatter {

    private final CMakeFormatterOptions formatterOptions;
    private final int blankLines;

    public CMakeNodeFormatter(DataHolder options) {
        formatterOptions = new CMakeFormatterOptions(options);
        blankLines = 0;
    }

    @Override
    public Set<Class<?>> getNodeClasses() {
        return null;
    }

    private void render(final Node node, final NodeFormatterContext context, final MarkdownWriter markdown) {
        markdown.append(node.getChars());
    }

    private void render(final BlankLine node, final NodeFormatterContext context, final MarkdownWriter markdown) {
        if (formatterOptions.preserveLineBreaks) {
            markdown.blankLine();
        }
    }

    private void render(final CommandBlock node, final NodeFormatterContext context, final MarkdownWriter markdown) {
        markdown.pushPrefix();
        markdown.addPrefix(RepeatedSequence.repeatOf(' ', formatterOptions.indentSpaces));
        context.renderChildren(node);
        markdown.popPrefix();
    }

    private void render(final Command node, final NodeFormatterContext context, final MarkdownWriter markdown) {
        if (node instanceof CommentedOutCommand) {
            if (formatterOptions.preserveWhitespace) {
                markdown.append('#');
                CMakeFormatterContext.appendWhiteSpaceBetween(markdown, ((CommentedOutCommand) node).getCommentMarker(), node.getCommand(), formatterOptions.preserveWhitespace, formatterOptions.collapseWhitespace, true);
            } else if (formatterOptions.spaceAfterCommandName.contains(node.getCommand().toString())) {
                markdown.append("# ");
            }
        }
        markdown.append(node.getCommand());
        if (formatterOptions.preserveWhitespace) {
            CMakeFormatterContext.appendWhiteSpaceBetween(markdown, node.getCommand(), node.getOpeningMarker(), formatterOptions.preserveWhitespace, formatterOptions.collapseWhitespace, true);
        } else if (formatterOptions.spaceAfterCommandName.contains(node.getCommand().toString())) {
            markdown.append(' ');
        }
        markdown.append(node.getOpeningMarker());
        if (!formatterOptions.preserveWhitespace) {
            markdown.pushPrefix();
            if (formatterOptions.indentSpaces > 0) markdown.addPrefix(RepeatedSequence.repeatOf(' ', formatterOptions.indentSpaces));
            context.renderChildren(node);
            markdown.popPrefix();
        } else {
            context.renderChildren(node);
        }
        if (formatterOptions.preserveWhitespace) {
            Node lastNode = node.getLastChild();
            BasedSequence prevSeq = lastNode == null ? node.getOpeningMarker() : lastNode.getChars();
            CMakeFormatterContext.appendWhiteSpaceBetween(markdown, prevSeq, node.getClosingMarker(), formatterOptions.preserveWhitespace, formatterOptions.collapseWhitespace, true);
        }
        markdown.append(node.getClosingMarker()).line();
    }

    private void render(final Argument node, final NodeFormatterContext context, final MarkdownWriter markdown) {
        boolean hadLine = false;
        Node prevArg = node.getPreviousAny(Argument.class);
        Node nextArg = node.getNextAny(Argument.class);

        if (!formatterOptions.preserveWhitespace && prevArg == null) {
            // first arg
            markdown.append(formatterOptions.argumentListPrefix);
        }

        if (!formatterOptions.preserveLineBreaks) {
            if (formatterOptions.argumentListMaxLine > 0 && formatterOptions.argumentListMaxLine < 1000) {
                int col = markdown.column() + node.getChars().length();
                if (col > formatterOptions.argumentListMaxLine) {
                    // we break, parent should have setup indent prefix
                    markdown.line();
                    hadLine = true;
                }
            }
        }

        if (!formatterOptions.preserveWhitespace) {
            if (!hadLine && prevArg != null && !(node.getPrevious() instanceof Separator && formatterOptions.preserveArgumentSeparator)) {
                // add separator
                if (prevArg.getChars().equals("(") || node.getChars().equals(")")) {
                    markdown.append(formatterOptions.argumentParensSeparator);
                } else {
                    markdown.append(formatterOptions.argumentSeparator);
                }
            }
        }

        // if quoted or bracketed then need to output first line with prefix and the rest with 0 prefix
        CMakeFormatterContext.appendQuotedContent(markdown, node.getChars());

        if (!formatterOptions.preserveWhitespace && nextArg == null) {
            // last arg
            markdown.append(formatterOptions.argumentListSuffix);
        }
    }

    private void render(final BracketComment node, final NodeFormatterContext context, final MarkdownWriter markdown) {
        if (!formatterOptions.preserveWhitespace) {
            // need to preserve whitespace between us and surrounding nodes if not already done
            CMakeFormatterContext.appendWhiteSpaceBetween(markdown, node.getPrevious(), node, formatterOptions.preserveWhitespace, formatterOptions.collapseWhitespace, true);
            markdown.append(node.getChars());
            if (!(node.getNext() instanceof LineComment)) {
                CMakeFormatterContext.appendWhiteSpaceBetween(markdown, node, node.getNext(), formatterOptions.preserveWhitespace, formatterOptions.collapseWhitespace, true);
            }
        } else {
            markdown.append(node.getChars());
        }
    }

    private void render(final CMakeFile node, final NodeFormatterContext context, final MarkdownWriter markdown) {
        context.renderChildren(node);
    }

    private void render(final LineComment node, final NodeFormatterContext context, final MarkdownWriter markdown) {
        if (!formatterOptions.preserveWhitespace) {
            // need to preserve whitespace between us and previous node if not already done
            CMakeFormatterContext.appendWhiteSpaceBetween(markdown, node.getPrevious(), node, formatterOptions.preserveWhitespace, formatterOptions.collapseWhitespace, true);
        }
        markdown.append(node.getChars());
    }

    private void render(final LineEnding node, final NodeFormatterContext context, final MarkdownWriter markdown) {
        if (formatterOptions.preserveLineBreaks) {
            markdown.line();
        }
    }

    private void render(final Separator node, final NodeFormatterContext context, final MarkdownWriter markdown) {
        if (formatterOptions.preserveArgumentSeparator) {
            markdown.append(node.getChars());
        }
    }

    private void render(final UnrecognizedInput node, final NodeFormatterContext context, final MarkdownWriter markdown) {
        markdown.append(node.getChars());
    }

    @Override
    public Set<NodeFormattingHandler<?>> getNodeFormattingHandlers() {
        return new HashSet<>(Arrays.asList(
                // Generic unknown node formatter
                new NodeFormattingHandler<>(Node.class, this::render),
                new NodeFormattingHandler<>(BlankLine.class, this::render),
                new NodeFormattingHandler<>(Command.class, this::render),
                new NodeFormattingHandler<>(CommentedOutCommand.class, this::render),
                new NodeFormattingHandler<>(Argument.class, this::render),
                new NodeFormattingHandler<>(BracketComment.class, this::render),
                new NodeFormattingHandler<>(CMakeFile.class, this::render),
                new NodeFormattingHandler<>(LineComment.class, this::render),
                new NodeFormattingHandler<>(LineEnding.class, this::render),
                new NodeFormattingHandler<>(Separator.class, this::render),
                new NodeFormattingHandler<>(CommandBlock.class, this::render),
                new NodeFormattingHandler<>(UnrecognizedInput.class, this::render)
        ));
    }
}
