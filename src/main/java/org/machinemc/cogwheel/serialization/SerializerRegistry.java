package org.machinemc.cogwheel.serialization;

import org.machinemc.cogwheel.config.Configuration;
import org.machinemc.cogwheel.serialization.Serializers.*;
import org.machinemc.cogwheel.util.ArrayUtils;

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

    public SerializerRegistry() {
        this(HashMap::new);
    }

    public SerializerRegistry(Supplier<Map<Class<?>, SerializerFactory<?>>> factory) {
        this(factory.get());
    }

    public SerializerRegistry(Map<Class<?>, SerializerFactory<?>> serializerMap) {
        this.serializerMap = serializerMap;
    }

    @SuppressWarnings("unchecked")
    public <T> void addSerializer(Class<T> type, Class<? extends T>[] subtypes, Serializer<T> serializer) {
        addSerializer(type, serializer);
        for (Class<? extends T> subtype : subtypes)
            addSerializer((Class<T>) subtype, serializer);
    }

    public <T> void addSerializer(Class<T> type, Serializer<T> serializer) {
        Objects.requireNonNull(serializer, "serializer");
        addSerializer(type, context -> serializer);
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
        SerializerFactory<T> factory = getSerializer(type);
        return factory != null ? factory.newInstance(context) : null;
    }

    @SuppressWarnings("unchecked")
    public <T> SerializerFactory<T> getSerializer(Class<T> type) {
        if (type == null) return null;
        SerializerFactory<T> serializer = (SerializerFactory<T>) serializerMap.get(type);
        return serializer != null ? serializer : DEFAULT_REGISTRY.getSerializer(type);
    }

    private static class DefaultSerializerRegistry extends SerializerRegistry {

        private boolean allowRegistration;

        {
            allowRegistration = true;

            addSerializer(Byte.class, new NumberSerializer<>(Number::byteValue, Byte::parseByte));
            addSerializer(Short.class, new NumberSerializer<>(Number::shortValue, Short::parseShort));
            addSerializer(Integer.class, new NumberSerializer<>(Number::intValue, Integer::parseInt));
            addSerializer(Long.class, new NumberSerializer<>(Number::longValue, Long::parseLong));
            addSerializer(Float.class, new NumberSerializer<>(Number::floatValue, Float::parseFloat));
            addSerializer(Double.class, new NumberSerializer<>(Number::doubleValue, Double::parseDouble));
            addSerializer(BigInteger.class, new NumberSerializer<>(number -> BigInteger.valueOf(number.longValue()), BigInteger::new));
            addSerializer(BigDecimal.class, new NumberSerializer<>(number -> BigDecimal.valueOf(number.doubleValue()), BigDecimal::new));

            addSerializer(Boolean.class, new BooleanSerializer());

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

        private <N extends Number> void addNumber(Class<N> type, NumberSerializer<N> serializer) {
            addSerializer(type, serializer);
        }

        @Override
        public <T> void addSerializer(Class<T> type, SerializerFactory<T> serializer) {
            if (!allowRegistration) throw new UnsupportedOperationException("Cannot modify the default registry");
            super.addSerializer(type, serializer);
        }

        @Override
        @SuppressWarnings("unchecked")
        public <T> SerializerFactory<T> getSerializer(Class<T> type) {
            SerializerFactory<T> factory = (SerializerFactory<T>) serializerMap.get(type);
            if (factory != null) return factory;
            if (type.isEnum()) return context -> (Serializer<T>) new EnumSerializer<>(type.asSubclass(Enum.class));
            if (type.isArray()) return context -> (Serializer<T>) new ArraySerializer<>(context);
            if (Configuration.class.isAssignableFrom(type))
                return context -> (Serializer<T>) new ConfigurationSerializer<>(context);
            return null;
        }

    }

}
