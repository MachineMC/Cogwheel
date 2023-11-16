package org.machinemc.cogwheel.serialization;

import org.machinemc.cogwheel.config.Configuration;
import org.machinemc.cogwheel.serialization.Serializers.*;
import org.machinemc.cogwheel.util.ArrayUtils;
import org.machinemc.cogwheel.util.NumberUtils;

import java.io.File;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.URI;
import java.net.URL;
import java.nio.file.Path;
import java.time.Instant;
import java.util.*;
import java.util.function.Supplier;

public class SerializerRegistry {

    public static final SerializerRegistry DEFAULT_REGISTRY = new DefaultSerializerRegistry();
    final Map<Class<?>, SerializerFactory<?>> serializerMap;
    private final boolean useDefaults;

    public SerializerRegistry() {
        this(true);
    }

    public SerializerRegistry(boolean useDefaults) {
        this(HashMap::new, useDefaults);
    }

    public SerializerRegistry(Supplier<Map<Class<?>, SerializerFactory<?>>> factory, boolean useDefaults) {
        this(factory.get(), useDefaults);
    }

    public SerializerRegistry(Map<Class<?>, SerializerFactory<?>> serializerMap, boolean useDefaults) {
        this.serializerMap = serializerMap;
        this.useDefaults = useDefaults;
    }

    @SuppressWarnings("unchecked")
    public <T> void addSerializer(Class<T> type, Class<? extends T>[] subtypes, Serializer<T> serializer) {
        addSerializer(type, serializer);
        for (Class<? extends T> subtype : subtypes)
            addSerializer((Class<T>) subtype, serializer);
    }

    public <T> void addSerializer(Class<T> type, Serializer<T> serializer) {
        Objects.requireNonNull(serializer, "serializer");
        addSerializer(type, SerializerFactory.of(serializer));
    }

    @SuppressWarnings("unchecked")
    public <T> void addSerializer(Class<T> type, Class<? extends T>[] subtypes, SerializerFactory<T> serializer) {
        addSerializer(type, serializer);
        for (Class<? extends T> subtype : subtypes)
            addSerializer((Class<T>) subtype, serializer);
    }

    public <T> void addSerializer(Class<T> type, SerializerFactory<T> serializer) {
        if (serializerExists(Objects.requireNonNull(type, "type")))
            throw new IllegalArgumentException("Type '" + type + "' already has a registered serializer");
        serializerMap.put(type, Objects.requireNonNull(serializer, "serializer"));
    }

    public boolean serializerExists(Class<?> type) {
        return serializerMap.containsKey(type);
    }

    public <T> Serializer<T> getSerializer(Class<T> type, SerializerContext context) {
        SerializerFactory<T> factory = getSerializerFactory(type);
        return factory != null ? factory.newInstance(context) : null;
    }

    @SuppressWarnings("unchecked")
    public <T> SerializerFactory<T> getSerializerFactory(Class<T> type) {
        if (type == null) return null;
        SerializerFactory<T> serializer = (SerializerFactory<T>) serializerMap.get(type);
        if (serializer != null || !useDefaults) return serializer;
        return DEFAULT_REGISTRY.getSerializerFactory(type);
    }

    private static class DefaultSerializerRegistry extends SerializerRegistry {

        private DefaultSerializerRegistry() {
            super(false);
        }

        private boolean allowRegistration;

        {
            allowRegistration = true;

            addSerializer(Byte.class, new NumberSerializer<>(Byte.class, Number::byteValue));
            addSerializer(Short.class, new NumberSerializer<>(Short.class, Number::shortValue));
            addSerializer(Integer.class, new NumberSerializer<>(Integer.class, Number::intValue));
            addSerializer(Long.class, new NumberSerializer<>(Long.class, Number::longValue));
            addSerializer(Float.class, new NumberSerializer<>(Float.class, Number::floatValue));
            addSerializer(Double.class, new NumberSerializer<>(Double.class, Number::doubleValue));
            addSerializer(BigInteger.class, new NumberSerializer<>(BigInteger.class,
                    number -> NumberUtils.parseInteger(number.toString())));
            addSerializer(BigDecimal.class, new NumberSerializer<>(BigDecimal.class,
                    number -> NumberUtils.parseDecimal(number.toString())));
            addSerializer(Number.class, new NumberSerializer<>(Number.class, number -> number));

            addSerializer(Boolean.class, new BooleanSerializer());
            addSerializer(String.class, new StringSerializer());

            addSerializer(UUID.class, new UUIDSerializer());
            addSerializer(File.class, new FileSerializer());
            addSerializer(Path.class, new PathSerializer());
            addSerializer(URL.class, new URLSerializer());
            addSerializer(URI.class, new URISerializer());

            addSerializer(Instant.class, new InstantSerializer());

            addSerializer(byte[].class, PrimitiveByteArraySerializer::new);
            addSerializer(short[].class, PrimitiveShortArraySerializer::new);
            addSerializer(int[].class, PrimitiveIntArraySerializer::new);
            addSerializer(long[].class, PrimitiveLongArraySerializer::new);
            addSerializer(float[].class, PrimitiveFloatArraySerializer::new);
            addSerializer(double[].class, PrimitiveDoubleArraySerializer::new);
            addSerializer(boolean[].class, PrimitiveBooleanArraySerializer::new);

            addSerializer(Collection.class, ArrayUtils.array(List.class, SequencedCollection.class, ArrayList.class),
                    context -> new CollectionSerializer<>(ArrayList::new, context));
            addSerializer(Set.class, ArrayUtils.array(SequencedSet.class, HashSet.class, LinkedHashSet.class),
                    context -> new CollectionSerializer<>(LinkedHashSet::newLinkedHashSet, context));
            addSerializer(Queue.class, ArrayUtils.array(Deque.class, LinkedList.class),
                    context -> new CollectionSerializer<>(length -> new LinkedList<>(), context));

            //noinspection unchecked, rawtypes
            addSerializer(Map.class, ArrayUtils.array(SequencedMap.class, HashMap.class, LinkedHashMap.class),
                    context -> new MapSerializer(context));

            allowRegistration = false;
        }

        @Override
        public <T> void addSerializer(Class<T> type, SerializerFactory<T> serializer) {
            if (!allowRegistration) throw new UnsupportedOperationException("Cannot modify the default registry");
            super.addSerializer(type, serializer);
        }

        @Override
        @SuppressWarnings("unchecked")
        public <T> SerializerFactory<T> getSerializerFactory(Class<T> type) {
            SerializerFactory<T> factory = super.getSerializerFactory(type);
            if (factory != null) return factory;
            if (type.isEnum()) return SerializerFactory.of((Serializer<T>) new EnumSerializer<>(type.asSubclass(Enum.class)));
            if (type.isArray()) return context -> (Serializer<T>) new ArraySerializer<>(context);
            if (Configuration.class.isAssignableFrom(type))
                return context -> (Serializer<T>) new ConfigurationSerializer<>(context);
            return null;
        }

    }

}
