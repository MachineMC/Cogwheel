package org.machinemc.cogwheel.serialization;

import org.jetbrains.annotations.Nullable;

public interface SerializerFactory<T> {

    @Nullable Serializer<T> newInstance(SerializerContext context);

    static <T> SerializerFactory<T> of(Serializer<T> serializer) {
        return context -> serializer;
    }

}
