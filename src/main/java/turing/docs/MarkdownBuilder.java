package turing.docs;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;

public class MarkdownBuilder implements Appendable, CharSequence, Serializable {
    private final StringBuilder builder;

    public MarkdownBuilder() {
        this(16);
    }

    public MarkdownBuilder(int capacity) {
        this.builder = new StringBuilder(capacity);
    }

    @Override
    public Appendable append(CharSequence csq) {
        return builder.append(csq);
    }

    @Override
    public Appendable append(CharSequence csq, int start, int end) {
        return builder.append(csq, start, end);
    }

    @Override
    public Appendable append(char c) {
        return builder.append(c);
    }

    public MarkdownBuilder append(String s) {
        builder.append(s);
        return this;
    }

    public MarkdownBuilder append(int i) {
        builder.append(i);
        return this;
    }

    public MarkdownBuilder appendItalic(String s) {
        return append("*").append(s).append("*");
    }

    public MarkdownBuilder appendBold(String s) {
        return append("**").append(s).append("**");
    }

    public MarkdownBuilder appendBoldItalic(String s) {
        return append("***").append(s).append("***");
    }

    public MarkdownBuilder appendStrikethrough(String s) {
        return append("~~").append(s).append("~~");
    }

    public MarkdownBuilder appendSubscript(String s) {
        return append("~").append(s).append("~");
    }

    public MarkdownBuilder appendSuperscript(String s) {
        return append("^").append(s).append("^");
    }

    public MarkdownBuilder appendBlockquote(String s) {
        return append("> ").append(s);
    }

    public MarkdownBuilder appendCode(String s) {
        return append("`").append(s).append("`");
    }

    public MarkdownBuilder appendLink(String s, String link) {
        return append("[").append(s).append("](").append(link).append(")");
    }

    public MarkdownBuilder appendImage(String s, String image) {
        return append("!").appendLink(s, image);
    }

    public MarkdownBuilder appendHorizontalRule() {
        return newLine().append("---").newLine();
    }

    public MarkdownBuilder startCodeBlock() {
        return append("```").newLine();
    }

    public MarkdownBuilder endCodeBlock() {
        return newLine().append("```").newLine();
    }

    public MarkdownBuilder appendHeader(String s, int level, @Nullable String id) {
        id = id != null ? id : "";
        level = Math.max(Math.min(level, 3), 1);

        for (int i = 0; i < level; i++) {
            append("#");
        }
        append(" ").append(s);

        if (!id.isEmpty()) {
            append(" {#").append(id).append("}");
        }

        return this;
    }

    public MarkdownBuilder appendHeader(String s, int level) {
        return appendHeader(s, level, null);
    }

    public MarkdownBuilder appendHeader(String s) {
        return appendHeader(s, 1);
    }

    public MarkdownBuilder linkToHeader(String s, String id) {
        return appendLink(s, "#" + id);
    }

    public MarkdownBuilder appendList(boolean ordered, String... strings) {
        for (int i = 0; i < strings.length; i++) {
            append(ordered ? i + "." : "-").append(" ").append(strings[i]);
        }
        return this;
    }

    public MarkdownBuilder appendList(String... strings) {
        return appendList(false, strings);
    }

    public MarkdownBuilder appendList(List<String> strings) {
        for (int i = 0; i < strings.size(); i++) {
            append(i).append(". ").append(strings.get(i));
        }
        return this;
    }

    public MarkdownBuilder appendList(Collection<String> strings) {
        for (String s : strings) {
            append("- ").append(s);
        }
        return this;
    }

    public MarkdownBuilder newLine() {
        return append("\n");
    }

    public void trimToSize() {
        builder.trimToSize();
    }

    @Override
    public int length() {
        return builder.length();
    }

    @Override
    public char charAt(int index) {
        return builder.charAt(index);
    }

    @Override
    public @NotNull CharSequence subSequence(int start, int end) {
        return builder.subSequence(start, end);
    }

    @Override
    public @NotNull String toString() {
        return builder.toString();
    }
}
