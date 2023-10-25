package org.machinemc.cogwheel.util.classbuilder;

import org.machinemc.cogwheel.util.JavaUtils;

import java.lang.reflect.RecordComponent;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class RecordBuilder<T extends Record> extends ClassBuilder<T> {

    private final Map<String, RecordComponent> components;

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
        List<Component<?>> componentList = new ArrayList<>();
        components.forEach((name, component) -> componentList.add(getOrCreateComponent(name, component.getType())));
        Class<?>[] parameters = componentList.stream()
                .map(Component::getType)
                .toArray(Class[]::new);
        Object[] arguments = componentList.stream()
                .map(Component::getValue)
                .toArray();
        return JavaUtils.newInstance(cls, parameters, arguments);
    }

}
