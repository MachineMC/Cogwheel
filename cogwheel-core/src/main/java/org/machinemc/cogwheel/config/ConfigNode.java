package org.machinemc.cogwheel.config;

import org.jetbrains.annotations.Nullable;
import org.machinemc.cogwheel.annotations.Comment;
import org.machinemc.cogwheel.annotations.FormatKeyWith;
import org.machinemc.cogwheel.annotations.Hidden;
import org.machinemc.cogwheel.annotations.Key;
import org.machinemc.cogwheel.keyformatter.KeyFormatter;
import org.machinemc.cogwheel.serialization.SerializerContext;
import org.machinemc.cogwheel.util.JavaUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.AnnotatedType;
import java.util.Optional;
import java.util.StringJoiner;
import java.util.function.Function;

public sealed abstract class ConfigNode<A extends AnnotatedElement> permits FieldNode, RecordComponentNode {

    protected final A element;
    private final String formattedName;
    private final String @Nullable [] comments;
    private final @Nullable String inlineComment;
    private final boolean optional, hidden;

    public ConfigNode(A element, Function<ConfigNode<A>, SerializerContext> contextFunction) {
        this.element = element;
        SerializerContext context = contextFunction.apply(this);
        String name = getAnnotation(Key.class).map(Key::value).orElse(getName());
        KeyFormatter formatter = getAnnotation(FormatKeyWith.class)
                .map(FormatKeyWith::value)
                .map(JavaUtils::newInstance)
                .orElse(null);
        if (formatter == null) formatter = context.properties().keyFormatter();
        this.formattedName = formatter != null ? formatter.format(name) : name;

        this.comments = getAnnotation(Comment.class).map(Comment::value).orElse(null);
        this.inlineComment = getAnnotation(Comment.Inline.class).map(Comment.Inline::value).orElse(null);

        boolean optional = getAnnotation(org.machinemc.cogwheel.annotations.Optional.class).isPresent();
        optional |= getAnnotation(Nullable.class).isPresent();
        this.optional = optional;
        this.hidden = getAnnotation(Hidden.class).isPresent();
    }

    public Class<?> getActualType() {
        return JavaUtils.asClass(getAnnotatedType());
    }

    public Class<?> getType() {
        Class<?> type = getActualType();
        return type.isPrimitive() ? JavaUtils.wrapPrimitiveClass(type) : type;
    }

    public String getFormattedName() {
        return formattedName;
    }

    public abstract String getName();

    public abstract Object getValue(Object holder);

    public <T extends Annotation> Optional<T> getAnnotation(Class<T> annotationClass) {
        return Optional.ofNullable(getAnnotatedElement().getAnnotation(annotationClass));
    }

    public abstract AnnotatedElement getAnnotatedElement();

    public abstract AnnotatedType getAnnotatedType();

    public String @Nullable [] getComments() {
        return comments;
    }

    public @Nullable String getInlineComment() {
        return inlineComment;
    }

    public boolean isOptional() {
        return optional || isHidden();
    }

    public boolean isHidden() {
        return hidden;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", getClass().getSimpleName() + "[", "]")
                .add("type=" + getActualType())
                .add("formattedName='" + formattedName + "'")
                .add("optional=" + optional)
                .add("hidden=" + hidden)
                .toString();
    }

}
