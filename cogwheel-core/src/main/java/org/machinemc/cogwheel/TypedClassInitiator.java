package org.machinemc.cogwheel;

import org.machinemc.cogwheel.util.JavaUtils;

import java.util.function.Supplier;

public class TypedClassInitiator implements ClassInitiator {

    private final Class<?>[] types;
    private final Supplier<Object[]> arguments;

    public <T> TypedClassInitiator(Class<T> type, T argument) {
        this(new Class[]{type}, argument);
    }

    public <T> TypedClassInitiator(Class<T> type, Supplier<T> argumentSupplier) {
        this(new Class[]{type}, () -> new Object[]{argumentSupplier.get()});
    }

    public TypedClassInitiator(Class<?>[] types, Object... arguments) {
        this(types, () -> arguments);
    }

    public TypedClassInitiator(Class<?>[] types, Supplier<Object[]> argumentsSupplier) {
        this.types = types;
        this.arguments = argumentsSupplier;
    }

    @Override
    public <T> T newInstance(Class<T> type) {
        if (!JavaUtils.hasConstructor(type, types))
            throw new IllegalArgumentException("Cannot instantiate '" + type + "'. "
                    + "No appropriate constructor found");
        return JavaUtils.newInstance(type, types, arguments.get());
    }

}
