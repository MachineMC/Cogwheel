package org.machinemc.cogwheel.config;

import org.machinemc.cogwheel.serialization.SerializerContext;
import org.machinemc.cogwheel.util.JavaUtils;

import java.lang.reflect.AnnotatedType;
import java.lang.reflect.Field;
import java.util.function.Function;

public final class FieldNode extends ConfigNode<Field> {

    public FieldNode(Field field, Function<ConfigNode<Field>, SerializerContext> contextFunction) {
        super(field, contextFunction);
    }

    @Override
    public String getName() {
        return element.getName();
    }

    @Override
    public Object getValue(Object holder) {
        return JavaUtils.getValue(element, holder);
    }

    @Override
    public Field getAnnotatedElement() {
        return element;
    }

    @Override
    public AnnotatedType getAnnotatedType() {
        return element.getAnnotatedType();
    }

}
