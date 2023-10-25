package org.machinemc.cogwheel.config;

import org.machinemc.cogwheel.serialization.SerializerContext;
import org.machinemc.cogwheel.util.JavaUtils;

import java.lang.reflect.AnnotatedType;
import java.lang.reflect.RecordComponent;
import java.util.function.Function;

public final class RecordComponentNode extends ConfigNode<RecordComponent> {

    public RecordComponentNode(RecordComponent recordComponent, Function<ConfigNode<RecordComponent>, SerializerContext> contextFunction) {
        super(recordComponent, contextFunction);
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
    public RecordComponent getAnnotatedElement() {
        return element;
    }

    @Override
    public AnnotatedType getAnnotatedType() {
        return element.getAnnotatedType();
    }

}
