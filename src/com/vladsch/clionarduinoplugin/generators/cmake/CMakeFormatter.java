package com.vladsch.clionarduinoplugin.generators.cmake;

import com.vladsch.clionarduinoplugin.generators.cmake.ast.BlankLine;
import com.vladsch.clionarduinoplugin.generators.cmake.ast.CMakeFile;
import com.vladsch.clionarduinoplugin.generators.cmake.ast.LineEnding;
import com.vladsch.flexmark.formatter.*;
import com.vladsch.flexmark.html.renderer.HtmlIdGenerator;
import com.vladsch.flexmark.html.renderer.LinkType;
import com.vladsch.flexmark.html.renderer.ResolvedLink;
import com.vladsch.flexmark.util.ast.Document;
import com.vladsch.flexmark.util.ast.IRender;
import com.vladsch.flexmark.util.ast.Node;
import com.vladsch.flexmark.util.ast.NodeCollectingVisitor;
import com.vladsch.flexmark.util.collection.SubClassingBag;
import com.vladsch.flexmark.util.data.DataHolder;
import com.vladsch.flexmark.util.data.DataKey;
import com.vladsch.flexmark.util.data.DataSet;
import com.vladsch.flexmark.util.data.MutableDataHolder;
import com.vladsch.flexmark.util.data.MutableDataSet;
import com.vladsch.flexmark.util.format.TrackedOffsetList;
import com.vladsch.flexmark.util.html.Attributes;
import com.vladsch.flexmark.util.misc.CharPredicate;
import com.vladsch.flexmark.util.sequence.BasedSequence;
import com.vladsch.flexmark.util.sequence.LineAppendable;
import com.vladsch.flexmark.util.sequence.builder.ISequenceBuilder;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Supplier;

import static com.vladsch.flexmark.formatter.Formatter.NULL_ITERABLE;

// TODO: add a node post processor to move nodes between if() ... else() ... endif() to be child nodes of if() and else() with block wrapper around them
//       otherwise indentation will not work because all commands are at the root level.
public class CMakeFormatter implements IRender {

    // syntax options
    final static public DataKey<Set<String>> SPACE_AFTER_COMMAND_NAME = new DataKey<>("SPACE_AFTER_COMMAND_NAME", () -> new HashSet<>(Arrays.asList(
            "if",
            "elseif",
            //"else",
            //"endif",
            "foreach",
            //"endforeach",
            "while"
            //"endwhile"
    )));

    final static public DataKey<Integer> INDENT_SPACES = new DataKey<>("INDENT_SPACES", 4);
    final static public DataKey<String> ARGUMENT_LIST_PREFIX = new DataKey<>("ARGUMENT_LIST_PREFIX", " ");
    final static public DataKey<String> ARGUMENT_LIST_SUFFIX = new DataKey<>("ARGUMENT_LIST_SUFFIX", " ");
    final static public DataKey<String> ARGUMENT_SEPARATOR = new DataKey<>("ARGUMENT_SEPARATOR", " ");
    final static public DataKey<String> ARGUMENT_PARENS_SEPARATOR = new DataKey<>("ARGUMENT_PARENS_SEPARATOR", " ");
    final static public DataKey<Integer> ARGUMENT_LIST_MAX_LINE = new DataKey<>("ARGUMENT_LIST_MAX_LINE", 80);
    final static public DataKey<Boolean> COLLAPSE_WHITESPACE = new DataKey<>("COLLAPSE_WHITESPACE", false);
    final static public DataKey<Boolean> PRESERVE_WHITESPACE = new DataKey<>("PRESERVE_WHITESPACE", true);
    final static public DataKey<Boolean> PRESERVE_ARGUMENT_SEPARATOR = new DataKey<>("PRESERVE_ARGUMENT_SEPARATOR", true);
    final static public DataKey<Boolean> PRESERVE_LINE_BREAKS = new DataKey<>("PRESERVE_LINE_BREAKS", true);

    // convenience copies
    final static public DataKey<Integer> FORMAT_FLAGS = Formatter.FORMAT_FLAGS;
    final static public DataKey<Integer> MAX_BLANK_LINES = Formatter.MAX_BLANK_LINES;
    final static public DataKey<Integer> MAX_TRAILING_BLANK_LINES = Formatter.MAX_TRAILING_BLANK_LINES;

    final private DataHolder options;
    final private CMakeFormatterOptions formatterOptions;

    private CMakeFormatter(final DataHolder options) {
        this.options = options == null ? new DataSet() : new DataSet(options);
        this.formatterOptions = new CMakeFormatterOptions(options);
    }

    public static CMakeFormatter build(@Nullable DataHolder options) {
        return new CMakeFormatter(options);
    }

    @NotNull
    public String render(@NotNull final Node node) {
        StringBuilder sb = new StringBuilder();
        render(node, sb);
        return sb.toString();
    }

    @Override
    public DataHolder getOptions() {
        return new DataSet(options);
    }

    @Override
    public void render(final Node node, @NotNull final Appendable output) {
        CMakeFormatterContext renderer = new CMakeFormatterContext(options, new MarkdownWriter(formatterOptions.formatFlags), (CMakeFile) node.getDocument());
        //renderer.getMarkdown().openPreFormatted(true);
        renderer.render(node);
        //renderer.getMarkdown().closePreFormatted();
        try {
            renderer.getMarkdown().appendTo(output, formatterOptions.maxBlankLines, formatterOptions.maxTrailingBlankLines);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static class CMakeFormatterContext extends NodeFormatterSubContext {
        // inner stuff
        final private Map<Class<?>, NodeFormattingHandler<?>> renderers;
        final private CMakeFormatterOptions formatterOptions;
        final private DataHolder options;
        final private CMakeFile document;
        final private SubClassingBag<Node> collectedNodes;
        final private Set<FormattingPhase> renderingPhases;
        final private List<PhasedNodeFormatter> phasedFormatters;
        private FormattingPhase phase;

        private Node lastRenderedNode = null;

        CMakeFormatterContext(@Nullable DataHolder options, @NotNull MarkdownWriter out, final CMakeFile document) {
            super(out);
            this.options = options == null ? new MutableDataSet() : new MutableDataSet(options);
            this.document = document;
            this.formatterOptions = new CMakeFormatterOptions(options);
            this.renderers = new HashMap<>(32);
            NodeFormatter nodeFormatter = new CMakeNodeFormatter(this.options);
            final Set<Class<?>> collectNodeTypes = new HashSet<>(20);
            this.renderingPhases = new HashSet<FormattingPhase>(FormattingPhase.values().length);
            this.phasedFormatters = new ArrayList<PhasedNodeFormatter>(1);

            //noinspection ThisEscapedInObjectConstruction
            out.setContext(this);

            final Set<NodeFormattingHandler<?>> formattingHandlers = nodeFormatter.getNodeFormattingHandlers();
            if (formattingHandlers != null) {
                for (NodeFormattingHandler<?> nodeType : formattingHandlers) {
                    // Overwrite existing renderer
                    renderers.put(nodeType.getNodeType(), nodeType);
                }
            }

            // get nodes of interest
            Set<Class<?>> nodeClasses = nodeFormatter.getNodeClasses();
            if (nodeClasses != null) {
                collectNodeTypes.addAll(nodeClasses);
            }

            // collect nodes of interest from document
            if (!collectNodeTypes.isEmpty()) {
                NodeCollectingVisitor collectingVisitor = new NodeCollectingVisitor(collectNodeTypes);
                collectingVisitor.collect(document);
                collectedNodes = collectingVisitor.getSubClassingBag();
            } else {
                collectedNodes = null;
            }
        }

        @NotNull
        @Override
        public final Iterable<? extends Node> nodesOfType(@NotNull final Class<?>[] classes) {
            return collectedNodes == null ? NULL_ITERABLE : collectedNodes.itemsOfType(Node.class, classes);
        }

        @NotNull
        @Override
        public final Iterable<? extends Node> nodesOfType(@NotNull final Collection<Class<?>> classes) {
            return collectedNodes == null ? NULL_ITERABLE : collectedNodes.itemsOfType(Node.class, classes);
        }

        @NotNull
        @Override
        public final Iterable<? extends Node> reversedNodesOfType(@NotNull final Class<?>[] classes) {
            return collectedNodes == null ? NULL_ITERABLE : collectedNodes.reversedItemsOfType(Node.class, classes);
        }

        @NotNull
        @Override
        public final Iterable<? extends Node> reversedNodesOfType(@NotNull final Collection<Class<?>> classes) {
            return collectedNodes == null ? NULL_ITERABLE : collectedNodes.reversedItemsOfType(Node.class, classes);
        }

        @Override
        public NodeFormatterContext getSubContext() {
            //MarkdownWriter writer = new MarkdownWriter(out, getMarkdown().getOptions());
            //writer.setContext(this);
            ////noinspection ReturnOfInnerClass
            //return new SubNodeFormatter(this, writer);
            return null;
        }

        @Override
        public void render(@NotNull final Node node) {
            renderNode(node, this);
        }

        @Override
        public void addExplicitId(@NotNull final Node node, final String id, @NotNull final NodeFormatterContext context, @NotNull final MarkdownWriter markdown) {

        }

        @Override
        public HtmlIdGenerator getIdGenerator() {
            return null;
        }

        @Override
        public void postProcessNonTranslating(@NotNull final Function<String, CharSequence> postProcessor, @NotNull final Runnable scope) {

        }

        @NotNull
        @Override
        public <T> T postProcessNonTranslating(@NotNull final Function<String, CharSequence> postProcessor, @NotNull final Supplier<T> scope) {
            throw new IllegalStateException("Not Implemented");
        }

        @Override
        public void delegateRender() {
            throw new IllegalStateException("Not Implemented");
        }

        @Override
        public @NotNull CharPredicate getBlockQuoteLikePrefixPredicate() {
            return CharPredicate.NONE;
        }

        @Override
        public @NotNull BasedSequence getBlockQuoteLikePrefixChars() {
            return BasedSequence.NULL;
        }

        @Override
        public @NotNull TrackedOffsetList getTrackedOffsets() {
            return TrackedOffsetList.EMPTY_LIST;
        }

        @Override
        public boolean isRestoreTrackedSpaces() {
            return false;
        }

        @Override
        public @NotNull BasedSequence getTrackedSequence() {
            return getDocument().getChars();
        }

        @Override
        public NodeFormatterContext getSubContext(final DataHolder holder) {
            throw new IllegalStateException("Not Implemented");
        }

        @Override
        public NodeFormatterContext getSubContext(final DataHolder holder, @NotNull final ISequenceBuilder<?, ?> builder) {
            throw new IllegalStateException("Not Implemented");
        }

        @Override
        public boolean isPostProcessingNonTranslating() {
            return false;
        }

        @Override
        public MergeContext getMergeContext() {
            return null;
        }

        @NotNull
        @Override
        public String encodeUrl(final CharSequence sequence) {
            return sequence.toString();
        }

        @NotNull
        @Override
        public ResolvedLink resolveLink(@NotNull final LinkType type, @NotNull final CharSequence sequence, final Boolean aBoolean) {
            throw new IllegalStateException("Not Implemented");
        }

        @NotNull
        @Override
        public ResolvedLink resolveLink(@NotNull final LinkType type, @NotNull final CharSequence sequence, final Attributes attributes, final Boolean aBoolean) {
            throw new IllegalStateException("Not Implemented");
        }

        void renderNode(Node node, NodeFormatterSubContext subContext) {
            if (node instanceof Document) {
                // here we render multiple phases
                for (FormattingPhase phase : FormattingPhase.values()) {
                    if (phase != FormattingPhase.DOCUMENT && !renderingPhases.contains(phase)) { continue; }
                    this.phase = phase;
                    // here we render multiple phases
                    if (this.phase == FormattingPhase.DOCUMENT) {
                        NodeFormattingHandler<?> nodeRenderer = renderers.get(node.getClass());
                        if (nodeRenderer != null) {
                            Node savedNode = subContext.getRenderingNode();
                            subContext.setRenderingNode(node);
                            nodeRenderer.render(node, subContext, subContext.getMarkdown());
                            subContext.setRenderingNode(savedNode);
                        }
                    } else {
                        // go through all renderers that want this phase
                        for (PhasedNodeFormatter phasedFormatter : phasedFormatters) {
                            if (phasedFormatter.getFormattingPhases().contains(phase)) {
                                Node savedNode = subContext.getRenderingNode();
                                subContext.setRenderingNode(node);
                                phasedFormatter.renderDocument(subContext, subContext.getMarkdown(), (Document) node, phase);
                                subContext.setRenderingNode(savedNode);
                            }
                        }
                    }
                }
            } else {
                NodeFormattingHandler<?> nodeRenderer = renderers.get(node.getClass());

                if (nodeRenderer == null) {
                    nodeRenderer = renderers.get(Node.class);
                }

                if (nodeRenderer != null) {
                    Node oldNode = getRenderingNode();
                    subContext.setRenderingNode(node);

                    if (formatterOptions.preserveWhitespace) {
                        appendWhiteSpaceBetween(markdown, lastRenderedNode, node, true, false, false);
                    }

                    nodeRenderer.render(node, subContext, subContext.getMarkdown());
                    lastRenderedNode = node;

                    subContext.setRenderingNode(oldNode);
                } else {
                    // default behavior is controlled by generic Node.class that is implemented in CoreNodeFormatter
                    throw new IllegalStateException("Core Node Formatter should implement generic Node renderer");
                }
            }
        }

        public void renderChildren(Node parent) {
            renderChildrenNode(parent, this);
        }

        @SuppressWarnings("WeakerAccess")
        protected void renderChildrenNode(Node parent, NodeFormatterSubContext subContext) {
            Node node = parent.getFirstChild();
            while (node != null) {
                Node next = node.getNext();
                renderNode(node, subContext);
                node = next;
            }
        }

        @NotNull
        @Override
        public FormattingPhase getFormattingPhase() {
            return FormattingPhase.DOCUMENT;
        }

        @NotNull
        @Override
        public DataHolder getOptions() {
            return options;
        }

        @NotNull
        @Override
        public FormatterOptions getFormatterOptions() {
            throw new IllegalStateException("Not Implemented");
        }

        public CMakeFormatterOptions getCMakeOptions() {
            return formatterOptions;
        }

        @NotNull
        @Override
        public Document getDocument() {
            return document;
        }

        @NotNull
        @Override
        public Node getCurrentNode() {
            Node node = getRenderingNode();
            assert node != null;
            return node;
        }

        public static void appendWhiteSpaceBetween(
                final MarkdownWriter markdown,
                Node prev,
                Node next,
                boolean preserve,
                boolean collapse,
                boolean collapseToEOL
        ) {
            if (next != null && prev != null && (preserve || collapse) && !(next instanceof BlankLine && prev instanceof LineEnding)) {
                appendWhiteSpaceBetween(markdown, prev.getChars(), next.getChars(), preserve, collapse, collapseToEOL);
            }
        }

        public static void appendWhiteSpaceBetween(
                final MarkdownWriter markdown,
                BasedSequence prev,
                BasedSequence next,
                boolean preserve,
                boolean collapse,
                boolean collapseToEOL
        ) {
            if (next != null && prev != null && (preserve || collapse)) {
                if (prev.getEndOffset() <= next.getStartOffset()) {
                    BasedSequence sequence = prev.baseSubSequence(prev.getEndOffset(), next.getStartOffset());
                    if (!sequence.isEmpty() && sequence.isBlank()) {
                        if (!preserve) {
                            if (collapseToEOL && sequence.indexOfAny(CharPredicate.ANY_EOL) != -1) {
                                markdown.append('\n');
                            } else {
                                markdown.append(' ');
                            }
                        } else {
                            // need to set pre-formatted or spaces after eol are ignored assuming prefixes are used
                            int saved = markdown.getOptions();
                            markdown.setOptions(saved & ~(LineAppendable.F_COLLAPSE_WHITESPACE | LineAppendable.F_TRIM_LEADING_WHITESPACE));
                            markdown.append(sequence);
                            markdown.setOptions(saved);
                        }
                    }
                } else {
                    // nodes reversed due to children being rendered before the parent
                }
            }
        }

        /**
         * Output first line as is, the rest with no prefix
         *
         * @param markdown
         * @param text
         */
        public static void appendQuotedContent(final MarkdownWriter markdown, BasedSequence text) {
            int pos = text.indexOfAny(CharPredicate.ANY_EOL);
            if (pos == -1) {
                markdown.append(text);
            } else {
                markdown.append(text, 0, pos); // output up to eol
                // output the rest without prefix
                markdown.pushPrefix();
                markdown.setPrefix("");
                markdown.append(text, pos, text.length());
                markdown.popPrefix();
            }
        }

        // Unused

        @NotNull
        @Override
        public RenderPurpose getRenderPurpose() {
            throw new IllegalStateException("Not Implemented");
        }

        @NotNull
        @Override
        public MutableDataHolder getTranslationStore() {
            throw new IllegalStateException("Not Implemented");
        }

        @Override
        public boolean isTransformingText() {
            return false;
        }

        @NotNull
        @Override
        public CharSequence transformNonTranslating(
                final CharSequence prefix,
                @NotNull final CharSequence nonTranslatingText,
                final CharSequence suffix,
                final CharSequence suffix2
        ) {
            throw new IllegalStateException("Not Implemented");
        }

        @NotNull
        @Override
        public CharSequence transformTranslating(
                final CharSequence prefix,
                @NotNull final CharSequence translatingText,
                final CharSequence suffix,
                final CharSequence suffix2
        ) {
            throw new IllegalStateException("Not Implemented");
        }

        @NotNull
        @Override
        public CharSequence transformAnchorRef(@NotNull final CharSequence pageRef, @NotNull final CharSequence anchorRef) {
            throw new IllegalStateException("Not Implemented");
        }

        @Override
        public void translatingSpan(@NotNull final TranslatingSpanRender render) {

        }

        @Override
        public void nonTranslatingSpan(@NotNull final TranslatingSpanRender render) {

        }

        @Override
        public void translatingRefTargetSpan(final Node target, @NotNull final TranslatingSpanRender render) {

        }

        @Override
        public void customPlaceholderFormat(
                @NotNull final TranslationPlaceholderGenerator generator,
                @NotNull final TranslatingSpanRender render
        ) {

        }
    }
}
