package org.machinemc.cogwheel.serialization;

import org.jetbrains.annotations.Nullable;
import org.machinemc.cogwheel.DataVisitor;
import org.machinemc.cogwheel.SingletonDataVisitor;
import org.machinemc.cogwheel.util.error.ErrorContainer;

public interface Serializer<T> {

    void serialize(T t, DataVisitor visitor);

    @Nullable T deserialize(DataVisitor visitor, ErrorContainer errorContainer);

    static <T> Object serialize(Serializer<T> serializer, T t) {
        SingletonDataVisitor visitor = new SingletonDataVisitor();
        serializer.serialize(t, visitor);
        return visitor.get();
    }

    static <T> @Nullable T deserialize(Serializer<T> serializer, Object object, ErrorContainer errorContainer) {
        SingletonDataVisitor visitor = new SingletonDataVisitor(object).withFlags(DataVisitor.READ_ACCESS);
        return serializer.deserialize(visitor, errorContainer);
    }

    static <T> @Nullable T deserialize(Serializer<T> serializer, Object object) {
        return deserialize(serializer, object, new ErrorContainer());
    }

}
