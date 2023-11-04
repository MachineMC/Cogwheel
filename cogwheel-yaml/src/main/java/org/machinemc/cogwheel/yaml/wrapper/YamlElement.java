package org.machinemc.cogwheel.yaml.wrapper;

import org.jetbrains.annotations.Nullable;
import org.snakeyaml.engine.v2.comments.CommentLine;
import org.snakeyaml.engine.v2.comments.CommentType;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.*;

public sealed abstract class YamlElement permits YamlArray, YamlNull, YamlObject, YamlPrimitive {

    private @Nullable String @Nullable [] comments;
    private @Nullable String inlineComment;

    public abstract YamlElement deepCopy();

    protected void copyComments(YamlElement destination) {
        if (comments != null)
            destination.comments = Arrays.copyOf(comments, comments.length);
        if (inlineComment != null)
            destination.inlineComment = inlineComment; // Strings are immutable, no need to clone it
    }

    public boolean isYamlArray() {
        return this instanceof YamlArray;
    }

    public boolean isYamlObject() {
        return this instanceof YamlObject;
    }

    public boolean isYamlPrimitive() {
        return this instanceof YamlPrimitive;
    }

    public boolean isYamlNull() {
        return this instanceof YamlNull;
    }

    public YamlObject getAsYamlObject() {
        if (isYamlObject()) return (YamlObject) this;
        throw new IllegalStateException("Not a Yaml Object: " + this);
    }

    public YamlArray getAsYamlArray() {
        if (isYamlArray()) return (YamlArray) this;
        throw new IllegalStateException("Not a Yaml Array: " + this);
    }

    public YamlPrimitive getAsYamlPrimitive() {
        if (isYamlPrimitive()) return (YamlPrimitive) this;
        throw new IllegalStateException("Not a Yaml Primitive: " + this);
    }

    public YamlNull getAsYamlNull() {
        if (isYamlNull()) return (YamlNull) this;
        throw new IllegalStateException("Not a Yaml Null: " + this);
    }

    public boolean getAsBoolean() {
        throw new UnsupportedOperationException(getClass().getSimpleName());
    }

    public Number getAsNumber() {
        throw new UnsupportedOperationException(getClass().getSimpleName());
    }

    public String getAsString() {
        throw new UnsupportedOperationException(getClass().getSimpleName());
    }

    public double getAsDouble() {
        throw new UnsupportedOperationException(getClass().getSimpleName());
    }

    public float getAsFloat() {
        throw new UnsupportedOperationException(getClass().getSimpleName());
    }

    public long getAsLong() {
        throw new UnsupportedOperationException(getClass().getSimpleName());
    }

    public int getAsInt() {
        throw new UnsupportedOperationException(getClass().getSimpleName());
    }

    public byte getAsByte() {
        throw new UnsupportedOperationException(getClass().getSimpleName());
    }

    public BigDecimal getAsBigDecimal() {
        throw new UnsupportedOperationException(getClass().getSimpleName());
    }

    public BigInteger getAsBigInteger() {
        throw new UnsupportedOperationException(getClass().getSimpleName());
    }

    public short getAsShort() {
        throw new UnsupportedOperationException(getClass().getSimpleName());
    }

    public abstract Object asRawObject();

    public List<CommentLine> getComments() {
        if (comments == null) return Collections.emptyList();
        return Arrays.stream(comments)
                .map(comment -> {
                    if (comment == null)
                        return new CommentLine(Optional.empty(), Optional.empty(), "", CommentType.BLANK_LINE);
                    return new CommentLine(Optional.empty(), Optional.empty(), comment, CommentType.BLOCK);
                })
                .toList();
    }

    public void setComments(@Nullable String @Nullable [] comments) {
        this.comments = comments;
    }

    public List<CommentLine> getInlineComment() {
        if (inlineComment == null) return Collections.emptyList();
        return Collections.singletonList(new CommentLine(Optional.empty(), Optional.empty(), inlineComment, CommentType.IN_LINE));
    }

    public void setInlineComment(@Nullable String comment) {
        this.inlineComment = comment;
    }

    @SuppressWarnings("unchecked")
    public static YamlElement of(Object object) {
        if (object == null) return new YamlNull();
        if (object.getClass().isArray()) return YamlArray.of((Object[]) object);
        if (object instanceof Map<?, ?> map) return YamlObject.of((Map<String, ?>) map);
        return YamlPrimitive.of(object);
    }

}
