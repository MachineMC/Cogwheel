package org.machinemc.cogwheel.util.classbuilder;

import org.jetbrains.annotations.Nullable;
import org.machinemc.cogwheel.ClassInitiator;
import org.machinemc.cogwheel.util.JavaUtils;

import java.lang.reflect.Field;

public class ObjectBuilder<T> extends ClassBuilder<T> {

    private final ClassInitiator classInitiator;

    public ObjectBuilder(Class<T> cls, ClassInitiator classInitiator) {
        super(cls);
        this.classInitiator = classInitiator;
    }

    @Override
    public boolean componentExists(String name) {
        return getField(name) != null;
    }

    @Override
    public T build() {
        T object = classInitiator.newInstance(cls);
        getComponents().forEach(component -> {
            Field field = getField(component.getName());
            setField(field, object, component.getValue());
        });
        return object;
    }

    private Field getField(String name) {
        Field field = null;
        Class<?> current = cls;
        while (!current.equals(Object.class) && field == null) {
            field = JavaUtils.getField(current, name);
            current = current.getSuperclass();
        }
        return field;
    }

    private static void setField(@Nullable Field field, Object holder, Object value) {
        if (field == null) return;
        field.setAccessible(true);
        try {
            field.set(holder, value);
        } catch (IllegalAccessException ignored) {}
    }

}
