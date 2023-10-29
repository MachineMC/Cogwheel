package org.machinemc.cogwheel.config;

import org.machinemc.cogwheel.keyformatter.KeyFormatter;
import org.machinemc.cogwheel.*;
import org.machinemc.cogwheel.serialization.Serializer;
import org.machinemc.cogwheel.serialization.SerializerContext;
import org.machinemc.cogwheel.serialization.SerializerFactory;
import org.machinemc.cogwheel.serialization.SerializerRegistry;
import org.machinemc.cogwheel.serialization.*;

import java.io.File;
import java.util.LinkedHashMap;

public abstract class ConfigSerializer<T> {

    private final ConfigProperties properties;

    protected ConfigSerializer(ConfigProperties properties) {
        this.properties = properties;
    }

    protected abstract ConfigAdapter<T> newAdapter();

    protected abstract void save(File file, T t);

    public void save(File file, Configuration configuration) {
        save(file, serialize(configuration).getConfig());
    }

    @SuppressWarnings("unchecked")
    public <C extends Configuration> ConfigAdapter<T> serialize(C configuration) {
        Serializers.ConfigurationSerializer<C> serializer = newSerializer(configuration);
        return (ConfigAdapter<T>) Serializer.serialize(serializer, configuration);
    }

    public abstract T load(File file);

    public <C extends Configuration> C load(File file, Class<C> configurationClass) {
        return load(load(file), configurationClass);
    }

    public <C extends Configuration> C load(T config, Class<C> configurationClass) {
        ConfigAdapter<T> adapter = newAdapter();
        adapter.load(config);
        return load(adapter, configurationClass);
    }

    private <C extends Configuration> C load(ConfigAdapter<T> adapter, Class<C> configurationClass) {
        Serializers.ConfigurationSerializer<C> serializer = newSerializer(configurationClass);
        C configuration = Serializer.deserialize(serializer, adapter);
        if (configuration == null)
            throw new IllegalArgumentException("Could not load configuration: " + configurationClass);
        return configuration;
    }

    @SuppressWarnings("unchecked")
    private <C extends Configuration> Serializers.ConfigurationSerializer<C> newSerializer(C configuration) {
        return (Serializers.ConfigurationSerializer<C>) newSerializer(configuration.getClass());
    }

    private <C extends Configuration> Serializers.ConfigurationSerializer<C> newSerializer(Class<C> configurationClass) {
        return new Serializers.ConfigurationSerializer<>(configurationClass, new SerializerContext(properties, this::newAdapter));
    }

    public ConfigProperties getProperties() {
        return properties;
    }

    public <P extends ConfigProperties> P getProperties(Class<P> type) {
        return type.cast(getProperties());
    }

    protected abstract static class Builder<S extends ConfigSerializer<?>, P extends ConfigProperties, B extends Builder<S, P, B>> {

        protected final P properties;

        protected Builder(P properties) {
            this.properties = properties;
        }

        public B registry(SerializerRegistry registry) {
            properties.serializerRegistry = registry;
            return getThis();
        }

        public <T> B addSerializer(Class<T> type, Class<? extends T>[] subtypes, Serializer<T> serializer) {
            properties.serializerRegistry().addSerializer(type, subtypes, serializer);
            return getThis();
        }

        public <T> B addSerializer(Class<T> type, Serializer<T> serializer) {
            properties.serializerRegistry().addSerializer(type, serializer);
            return getThis();
        }

        public <T> B addSerializer(Class<T> type, Class<? extends T>[] subtypes, SerializerFactory<T> serializer) {
            properties.serializerRegistry().addSerializer(type, subtypes, serializer);
            return getThis();
        }

        public <T> B addSerializer(Class<T> type, SerializerFactory<T> serializer) {
            properties.serializerRegistry().addSerializer(type, serializer);
            return getThis();
        }

        public B classInitiator(ClassInitiator classInitiator) {
            properties.classInitiator = classInitiator;
            return getThis();
        }

        public B keyFormatter(KeyFormatter keyFormatter) {
            properties.keyFormatter = keyFormatter;
            return getThis();
        }

        public B nodeFilter(NodeFilter nodeFilter) {
            properties.nodeFilter = nodeFilter;
            return getThis();
        }

        public B fieldExtractor(FieldExtractor fieldExtractor) {
            properties.fieldExtractor = fieldExtractor;
            return getThis();
        }

        public B recordDisassembler(RecordDisassembler recordDisassembler) {
            properties.recordDisassembler = recordDisassembler;
            return getThis();
        }

        public B errorHandler(ErrorHandler errorHandler) {
            properties.errorHandler = errorHandler;
            return getThis();
        }

        protected abstract B getThis();

        protected abstract S build();

    }

}
