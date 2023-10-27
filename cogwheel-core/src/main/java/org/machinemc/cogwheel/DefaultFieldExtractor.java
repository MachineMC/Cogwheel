package org.machinemc.cogwheel;

import org.machinemc.cogwheel.config.Configuration;

import java.lang.reflect.Field;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.SequencedSet;
import java.util.stream.Stream;

class DefaultFieldExtractor implements FieldExtractor {

    @Override
    public Stream<Field> extract(Class<?> type) {
        SequencedSet<FieldContainer> fields = new LinkedHashSet<>();
        while (Configuration.class.isAssignableFrom(type)) {
            for (Field field : List.of(type.getDeclaredFields()).reversed()) {
                FieldContainer container = new FieldContainer(field);
                fields.addFirst(container);
            }
            type = type.getSuperclass();
        }
        return fields.stream().map(FieldContainer::field);
    }

    private record FieldContainer(Field field) {

        @Override
        public boolean equals(Object obj) {
            return obj instanceof FieldContainer other && field.getName().equals(other.field().getName());
        }

        @Override
        public int hashCode() {
            return field.getName().hashCode();
        }

    }

}
