package org.machinemc.cogwheel.serialization;

import org.machinemc.cogwheel.annotations.ReadWith;
import org.machinemc.cogwheel.annotations.SerializeWith;
import org.machinemc.cogwheel.annotations.WriteWith;
import org.machinemc.cogwheel.config.ConfigAdapter;
import org.machinemc.cogwheel.config.ConfigNode;
import org.machinemc.cogwheel.config.ConfigProperties;
import org.machinemc.cogwheel.util.JavaUtils;
import org.machinemc.cogwheel.util.error.ErrorContainer;
import org.machinemc.cogwheel.util.error.ErrorType;

import java.lang.reflect.AnnotatedType;
import java.lang.reflect.Type;
import java.util.Optional;
import java.util.function.Supplier;

public record SerializerContext(
        ConfigNode<?> node,
        AnnotatedType annotatedType,
        ConfigProperties properties,
        ErrorContainer errorContainer,
        Supplier<ConfigAdapter<?>> configAdapter
) {

    public SerializerContext(ConfigProperties properties, Supplier<ConfigAdapter<?>> configAdapter) {
        this(null, null, properties, new ErrorContainer(), configAdapter);
    }

    public boolean hasNode() {
        return node() != null;
    }

    public Type type() {
        return Optional.ofNullable(annotatedType).map(AnnotatedType::getType).orElse(null);
    }

    public SerializerRegistry registry() {
        return properties().serializerRegistry();
    }

    @SuppressWarnings("unchecked")
    public <T> Serializer<T> writeWith() {
        Serializer<T> serializer = Optional.ofNullable(annotatedType().getAnnotation(WriteWith.class))
                .map(WriteWith::value)
                .map(aClass -> Serializers.newSerializer(aClass, this))
                .orElse(null);
        if (serializer == null) serializer = serializeWith();
        return serializer;
    }

    @SuppressWarnings("unchecked")
    public <T> Serializer<T> readWith() {
        Serializer<T> serializer = Optional.ofNullable(annotatedType().getAnnotation(ReadWith.class))
                .map(ReadWith::value)
                .map(aClass -> Serializers.newSerializer(aClass, this))
                .orElse(null);
        if (serializer == null) serializer = serializeWith();
        return serializer;
    }

    @SuppressWarnings("unchecked")
    public <T> Serializer<T> serializeWith() {
        AnnotatedType type = annotatedType();
        Serializer<T> serializer = Optional.ofNullable(type.getAnnotation(SerializeWith.class))
                .map(SerializeWith::value)
                .map(aClass -> Serializers.newSerializer(aClass, this))
                .orElse(null);
        if (serializer == null) {
            Class<T> cls = JavaUtils.asClass(type);
            if (cls.isPrimitive()) cls = (Class<T>) JavaUtils.wrapPrimitiveClass(cls);
            return registry().getSerializer(cls, this);
        }
        return serializer;
    }

    public void error(ErrorType type, String message) {
        errorContainer().error(type, message);
    }

    public SerializerContext withNode(ConfigNode<?> node) {
        return new SerializerContext(
                node,
                node != null ? node.getAnnotatedType() : null,
                properties(),
                errorContainer(),
                configAdapter()
        );
    }

    public SerializerContext withType(AnnotatedType annotatedType) {
        return new SerializerContext(node(), annotatedType, properties(), errorContainer(), configAdapter());
    }

}
