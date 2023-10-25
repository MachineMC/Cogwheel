package org.machinemc.cogwheel.util.classbuilder;

import org.jetbrains.annotations.Nullable;
import org.machinemc.cogwheel.util.JavaUtils;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.StringJoiner;
import java.util.function.Supplier;

public abstract class ClassBuilder<T> {

    protected final Class<T> cls;
    private final Map<String, Component<?>> components;

    public ClassBuilder(Class<T> cls) {
        this(cls, HashMap::new);
    }

    protected ClassBuilder(Class<T> cls, Supplier<Map<String, Component<?>>> supplier) {
        this(cls, supplier.get());
    }

    protected ClassBuilder(Class<T> cls, Map<String, Component<?>> components) {
        this.cls = cls;
        this.components = components;
    }

    public Class<T> getType() {
        return cls;
    }

    @SuppressWarnings("unchecked")
    public <C> Component<C> getComponent(String name, Class<C> type) {
        Component<?> component = getComponent(name);
        return component != null ? component.getType().isAssignableFrom(type) ? (Component<C>) component : null : null;
    }

    public Component<?> getComponent(String name) {
        return components.get(name);
    }

    protected <C> Component<C> getOrCreateComponent(String name, Class<C> type) {
        if (!componentExists(name))
            throw new IllegalArgumentException("Component '" + name + "' in class '" + cls.getName() + "' doesn't exist");
        Component<C> component = getComponent(name, type);
        return component != null ? component : createComponent(name, type);
    }

    protected <C> Component<C> createComponent(String name, Class<C> type) {
        if (getComponent(name, type) != null) return null;
        setComponent(name, type, null);
        return getComponent(name, type);
    }

    public <C> void setComponent(String name, Class<C> type, @Nullable C value) {
        components.put(name, new Component<>(name, type, value));
    }

    public Collection<Component<?>> getComponents() {
        return components.values();
    }

    public abstract boolean componentExists(String name);

    public abstract T build();

    public static class Component<T> {

        private final String name;
        private final Class<T> type;
        private @Nullable T value;

        public Component(String name, Class<T> type) {
            this(name, type, null);
        }

        public Component(String name, Class<T> type, @Nullable T value) {
            this.name = name;
            this.type = type;
            this.value = value != null ? value : JavaUtils.getDefaultValue(type);
        }

        public String getName() {
            return name;
        }

        public Class<T> getType() {
            return type;
        }

        public @Nullable T getValue() {
            return value;
        }

        public void setValue(@Nullable T value) {
            this.value = value;
        }

        @Override
        public String toString() {
            return new StringJoiner(", ", Component.class.getSimpleName() + "[", "]")
                    .add("name='" + name + "'")
                    .add("type=" + type)
                    .add("value=" + value)
                    .toString();
        }

    }

}
