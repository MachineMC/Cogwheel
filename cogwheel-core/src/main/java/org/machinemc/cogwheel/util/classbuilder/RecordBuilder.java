package org.machinemc.cogwheel.util.classbuilder;

import org.jetbrains.annotations.Nullable;
import org.machinemc.cogwheel.util.JavaUtils;

import java.lang.reflect.RecordComponent;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class RecordBuilder<T extends Record> extends ClassBuilder<T> {

    private final Map<String, RecordComponent> components;
    private boolean checkedDefault;
    private @Nullable T defaultRecord;

    public RecordBuilder(Class<T> cls) {
        super(cls);
        RecordComponent[] components = cls.getRecordComponents();
        this.components = LinkedHashMap.newLinkedHashMap(components.length);
        for (RecordComponent component : components)
            this.components.put(component.getName(), component);
    }

    @Override
    public boolean componentExists(String name) {
        return components.containsKey(name);
    }

    @Override
    public T build() {
        if (!checkedDefault) {
            checkedDefault = true;
            defaultRecord = JavaUtils.hasConstructor(cls) ? JavaUtils.newInstance(cls) : null;
        }
        List<Component<?>> componentList = new ArrayList<>();
        components.forEach((name, component) -> componentList.add(getComponent(defaultRecord, component)));
        Class<?>[] parameters = componentList.stream()
                .map(Component::getType)
                .toArray(Class[]::new);
        Object[] arguments = componentList.stream()
                .map(Component::getValue)
                .toArray();
        return JavaUtils.newInstance(cls, parameters, arguments);
    }

    @SuppressWarnings("unchecked")
    private <C> Component<C> getComponent(@Nullable T defaultRecord, RecordComponent recordComponent) {
        String name = recordComponent.getName();
        Class<C> type = (Class<C>) recordComponent.getType();
        Component<C> component = getComponent(name, type);
        if (component != null) return component;
        if (defaultRecord == null) return createComponent(name, type);
        return new Component<>(name, type, (C) JavaUtils.getValue(recordComponent, defaultRecord));
    }

}
