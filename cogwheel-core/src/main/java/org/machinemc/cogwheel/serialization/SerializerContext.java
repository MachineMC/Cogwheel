package org.machinemc.cogwheel.serialization;

import org.machinemc.cogwheel.config.ConfigAdapter;
import org.machinemc.cogwheel.config.ConfigNode;
import org.machinemc.cogwheel.config.ConfigProperties;
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
