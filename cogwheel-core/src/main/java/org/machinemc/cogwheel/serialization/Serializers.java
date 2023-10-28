package org.machinemc.cogwheel.serialization;

import org.jetbrains.annotations.Nullable;
import org.machinemc.cogwheel.DataVisitor;
import org.machinemc.cogwheel.ErrorHandler;
import org.machinemc.cogwheel.config.*;
import org.machinemc.cogwheel.util.ArrayUtils;
import org.machinemc.cogwheel.util.JavaUtils;
import org.machinemc.cogwheel.util.classbuilder.ClassBuilder;
import org.machinemc.cogwheel.util.classbuilder.ObjectBuilder;
import org.machinemc.cogwheel.util.classbuilder.RecordBuilder;
import org.machinemc.cogwheel.util.error.ErrorContainer;
import org.machinemc.cogwheel.util.error.ErrorEntry;
import org.machinemc.cogwheel.util.error.ErrorType;

import java.io.File;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;
import java.time.Instant;
import java.util.*;
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.stream.Stream;

public class Serializers {

    public static <T extends Serializer<?>> T newSerializer(Class<T> serializerClass, SerializerContext context) {
        if (JavaUtils.hasConstructor(serializerClass, SerializerContext.class))
            return JavaUtils.newInstance(serializerClass, ArrayUtils.array(SerializerContext.class), context);
        return JavaUtils.newInstance(serializerClass);
    }

    private static Type[] validateParameterTypes(Type[] parameters) {
        for (Type parameter : parameters)
            validateParameterType(parameter);
        return parameters;
    }

    private static void validateParameterType(Type parameter) {
        switch (parameter) {
            case Class<?> ignore -> {}
            case ParameterizedType ignore -> {}
            case GenericArrayType ignore -> {}
            default -> throw new UnsupportedOperationException("Cannot serialize type '" + parameter + "'");
        }
    }

    private static Object deserialize(Serializer<?> deserializer, Object primitive, ErrorContainer errorContainer, Class<?> as) {
        ErrorContainer temp = new ErrorContainer();
        Object deserialized = deserializer != null ? Serializer.deserialize(deserializer, primitive, temp) : primitive;
        if (deserialized == null && !temp.hasErrors()) {
            assert primitive != null;
            temp.error(ErrorType.MISMATCHED_TYPES, "Could not deserialize (%s) '%s' as %s".formatted(
                    primitive.getClass().getSimpleName(),
                    JavaUtils.toString(primitive),
                    as.getSimpleName())
            );
        }
        errorContainer.merge(temp);
        return deserialized;
    }

    public static class NumberSerializer<N extends Number> implements Serializer<N> {

        private final Function<Number, N> numberFunction;
        private final Function<String, N> stringFunction;

        public NumberSerializer(Function<Number, N> numberFunction, Function<String, N> stringFunction) {
            this.numberFunction = numberFunction;
            this.stringFunction = stringFunction;
        }

        @Override
        public void serialize(N number, DataVisitor visitor) {
            visitor.writeNumber(number);
        }

        @Override
        public @Nullable N deserialize(DataVisitor visitor, ErrorContainer errorContainer) {
            N number = visitor.readNumber()
                    .map(numberFunction)
                    .orElse(null);
            if (number != null) return number;
            return visitor.readString()
                    .map(string -> {
                        block: try {
                            return stringFunction.apply(string);
                        } catch (NumberFormatException e) {
                            if (string.indexOf('.') == -1) break block;
                            try {
                                // Couldn't deserialize
                                // Try our luck by deserialize again but stripping the decimal part
                                return stringFunction.apply(string.substring(0, string.indexOf('.')));
                            } catch (NumberFormatException ignored) {}
                        }
                        errorContainer.error(ErrorType.CUSTOM, "Could not parse '" + string + "' as a number");
                        return null;
                    })
                    .orElse(null);
        }

    }

    public static class BooleanSerializer implements Serializer<Boolean> {

        @Override
        public void serialize(Boolean bool, DataVisitor visitor) {
            visitor.writeBoolean(bool);
        }

        @Override
        public @Nullable Boolean deserialize(DataVisitor visitor, ErrorContainer errorContainer) {
            return visitor.readBoolean()
                    .map(String::valueOf)
                    .or(visitor::readString)
                    .map(BooleanSerializer::parseBoolean)
                    .orElse(null);
        }

        private static Boolean parseBoolean(String string) {
            return switch (string.toLowerCase(Locale.ENGLISH)) {
                case "true" -> true;
                case "false" -> false;
                default -> null;
            };
        }

    }

    public static class UUIDSerializer implements Serializer<UUID> {

        @Override
        public void serialize(UUID uuid, DataVisitor visitor) {
            visitor.writeString(uuid.toString());
        }

        @Override
        public @Nullable UUID deserialize(DataVisitor visitor, ErrorContainer errorContainer) {
            try {
                return visitor.readString()
                        .map(UUID::fromString)
                        .orElse(null);
            } catch (IllegalArgumentException e) {
                errorContainer.error(e.getMessage());
                return null;
            }
        }

    }

    public static class FileSerializer implements Serializer<File> {

        @Override
        public void serialize(File file, DataVisitor visitor) {
            visitor.writeString(file.toString());
        }

        @Override
        public @Nullable File deserialize(DataVisitor visitor, ErrorContainer errorContainer) {
            return visitor.readString()
                    .map(File::new)
                    .orElse(null);
        }

    }

    public static class PathSerializer implements Serializer<Path> {

        @Override
        public void serialize(Path path, DataVisitor visitor) {
            visitor.writeString(path.toString());
        }

        @Override
        public @Nullable Path deserialize(DataVisitor visitor, ErrorContainer errorContainer) {
            return visitor.readString()
                    .map(Path::of)
                    .orElse(null);
        }

    }

    public static class URLSerializer implements Serializer<URL> {

        @Override
        public void serialize(URL url, DataVisitor visitor) {
            visitor.writeString(url.toString());
        }

        @Override
        public @Nullable URL deserialize(DataVisitor visitor, ErrorContainer errorContainer) {
            return visitor.readString()
                    .map(string -> {
                        try {
                            return new URI(string).toURL();
                        } catch (MalformedURLException | URISyntaxException ignored) {}
                        errorContainer.error("Malformed URL: " + string);
                        return null;
                    })
                    .orElse(null);
        }

    }

    public static class URISerializer implements Serializer<URI> {

        @Override
        public void serialize(URI uri, DataVisitor visitor) {
            visitor.writeString(uri.toString());
        }

        @Override
        public @Nullable URI deserialize(DataVisitor visitor, ErrorContainer errorContainer) {
            return visitor.readString()
                    .map(string -> {
                        try {
                            return new URI(string);
                        } catch (URISyntaxException ignored) {}
                        errorContainer.error("Malformed URI: " + string);
                        return null;
                    })
                    .orElse(null);
        }

    }

    public static class InstantSerializer implements Serializer<Instant> {

        @Override
        public void serialize(Instant instant, DataVisitor visitor) {
            visitor.writeNumber(instant.toEpochMilli());
        }

        @Override
        public @Nullable Instant deserialize(DataVisitor visitor, ErrorContainer errorContainer) {
            return visitor.readNumber()
                    .map(Number::longValue)
                    .map(Instant::ofEpochMilli)
                    .orElse(null);
        }

    }

    public static class CollectionSerializer<C extends Collection<T>, T> implements Serializer<C> {

        private final IntFunction<C> factory;
        private final @Nullable Serializer<T> serializer;

        public CollectionSerializer(IntFunction<C> factory, SerializerContext context) {
            this.factory = factory;
            ParameterizedType type = (ParameterizedType) context.type();
            Type argument = validateParameterTypes(type.getActualTypeArguments())[0];
            SerializerRegistry registry = context.registry();
            this.serializer = registry.getSerializer(JavaUtils.asClass(argument), context.withType(argument));
        }

        @Override
        public void serialize(C collection, DataVisitor visitor) {
            visitor.writeArray(collection.stream()
                    .map(object -> serializer != null ? Serializer.serialize(serializer, object) : object)
                    .toArray());
        }

        @Override
        @SuppressWarnings("unchecked")
        public @Nullable C deserialize(DataVisitor visitor, ErrorContainer errorContainer) {
            return visitor.readArray()
                    .map(array -> {
                        C collection = factory.apply(array.length);
                        for (Object primitive : array) {
                            T object = serializer == null ? (T) primitive : Serializer.deserialize(serializer, primitive, errorContainer);
                            collection.add(object);
                        }
                        return collection;
                    })
                    .orElse(null);
        }

    }

    public static class MapSerializer<K, V> implements Serializer<Map<K, V>> {

        private final Class<K> keyType;
        private final @Nullable Serializer<V> serializer;

        @SuppressWarnings("unchecked")
        public MapSerializer(SerializerContext context) {
            ParameterizedType type = (ParameterizedType) context.type();
            Type[] parameters = validateParameterTypes(type.getActualTypeArguments());
            if (!(parameters[0] instanceof Class<?> keyClass) || (!String.class.isAssignableFrom(keyClass) && !keyClass.isEnum()))
                throw new IllegalArgumentException("Map keys may only be of types '%s' or '%s'. Not '%s'"
                        .formatted(String.class.getTypeName(), Enum.class.getTypeName(), parameters[0].getTypeName()));
            SerializerRegistry registry = context.registry();
            context = context.withType(parameters[1]);
            this.keyType = (Class<K>) keyClass;
            this.serializer = registry.getSerializer(JavaUtils.asClass(parameters[1]), context);
        }

        @Override
        public void serialize(Map<K, V> map, DataVisitor visitor) {
            Map<String, Object> serializedMap = HashMap.newHashMap(map.size());
            map.forEach((key, value) -> {
                Object serialized = serializer != null ? Serializer.serialize(serializer, value) : value;
                serializedMap.put((key + "").toLowerCase(Locale.ENGLISH), serialized);
            });
            visitor.writeMap(serializedMap);
        }

        @Override
        @SuppressWarnings("unchecked")
        public @Nullable Map<K, V> deserialize(DataVisitor visitor, ErrorContainer errorContainer) {
            Map<String, Object> serialized = visitor.readMap().orElse(null);
            if (serialized == null) return null;
            Map<K, V> map = LinkedHashMap.newLinkedHashMap(serialized.size());
            serialized.forEach((key, value) -> {
                K actualKey;
                if (keyType.isEnum()) {
                    actualKey = JavaUtils.getEnumConstant(keyType, key.toUpperCase(Locale.ENGLISH));
                } else if (String.class.isAssignableFrom(keyType)) {
                    actualKey = (K) key;
                } else {
                    throw new UnsupportedOperationException("Cannot deserialize key of type: " + keyType.getTypeName());
                }
                map.put(actualKey, serializer == null ? (V) value : Serializer.deserialize(serializer, value, errorContainer));
            });
            return map;
        }

    }

    public static class ArraySerializer<T> implements Serializer<T[]> {

        private final Class<T[]> arrayType;
        private final IntFunction<T[]> arrayFactory;
        private final @Nullable Serializer<T> serializer;

        public ArraySerializer(SerializerContext context) {
            this(context, context.type());
        }

        @SuppressWarnings("unchecked")
        private ArraySerializer(SerializerContext context, Type type) {
            SerializerRegistry registry = context.registry();
            context = context.withType(getComponentType(type));
            this.arrayType = JavaUtils.asClass(type);
            this.serializer = registry.getSerializer((Class<T>) arrayType.componentType(), context);
            this.arrayFactory = length -> (T[]) ArrayUtils.newArrayInstance(arrayType.componentType(), length);
        }

        @Override
        public void serialize(T[] array, DataVisitor visitor) {
            Object[] serialized = new Object[array.length];
            for (int i = 0; i < array.length; i++)
                serialized[i] = serializer != null ? Serializer.serialize(serializer, array[i]) : array[i];
            visitor.writeArray(serialized);
        }

        @Override
        @SuppressWarnings("unchecked")
        public T @Nullable [] deserialize(DataVisitor visitor, ErrorContainer errorContainer) {
            Object[] array = visitor.readArray().orElse(null);
            if (array == null) return null;
            return Arrays.stream(array)
                    .map(object -> (T) (serializer == null ? object : Serializers.deserialize(
                            serializer,
                            object,
                            errorContainer,
                            arrayType.componentType()
                    )))
                    .filter(Objects::nonNull)
                    .toArray(arrayFactory);
        }

        private static Type getComponentType(Type type) {
            return switch (type) {
                case Class<?> cls -> cls.componentType();
                case GenericArrayType arrayType -> arrayType.getGenericComponentType();
                default -> null;
            };
        }

    }

    public abstract static class PrimitiveArraySerializer<P, W> implements Serializer<P> {

        private final IntFunction<W[]> wrapperFactory;
        private final Function<P, W[]> wrapper;
        private final Function<W[], P> unwrapper;
        private final Serializer<W> componentSerializer;

        public PrimitiveArraySerializer(
                Class<W> wrapperType,
                IntFunction<W[]> wrapperFactory,
                Function<P, W[]> wrapper,
                Function<W[], P> unwrapper,
                SerializerContext context
        ) {
            this.wrapperFactory = wrapperFactory;
            this.wrapper = wrapper;
            this.unwrapper = unwrapper;
            this.componentSerializer = context.registry().getSerializer(wrapperType, context);
        }

        @Override
        public void serialize(P primitiveArray, DataVisitor visitor) {
            visitor.writeArray(wrapper.apply(primitiveArray));
        }

        @Override
        public @Nullable P deserialize(DataVisitor visitor, ErrorContainer errorContainer) {
            return visitor.readArray().map(objects -> unwrapper.apply(Arrays.stream(objects)
                            .map(object -> Serializer.deserialize(componentSerializer, object, errorContainer))
                            .filter(Objects::nonNull)
                            .toArray(wrapperFactory)))
                    .orElse(null);
        }

    }

    public static class PrimitiveByteArraySerializer extends PrimitiveArraySerializer<byte[], Byte> {

        public PrimitiveByteArraySerializer(SerializerContext context) {
            super(Byte.class, Byte[]::new, ArrayUtils::wrapArray, ArrayUtils::unwrapArray, context);
        }

    }

    public static class PrimitiveShortArraySerializer extends PrimitiveArraySerializer<short[], Short> {

        public PrimitiveShortArraySerializer(SerializerContext context) {
            super(Short.class, Short[]::new, ArrayUtils::wrapArray, ArrayUtils::unwrapArray, context);
        }

    }

    public static class PrimitiveIntArraySerializer extends PrimitiveArraySerializer<int[], Integer> {

        public PrimitiveIntArraySerializer(SerializerContext context) {
            super(Integer.class, Integer[]::new, ArrayUtils::wrapArray, ArrayUtils::unwrapArray, context);
        }

    }

    public static class PrimitiveLongArraySerializer extends PrimitiveArraySerializer<long[], Long> {

        public PrimitiveLongArraySerializer(SerializerContext context) {
            super(Long.class, Long[]::new, ArrayUtils::wrapArray, ArrayUtils::unwrapArray, context);
        }

    }

    public static class PrimitiveFloatArraySerializer extends PrimitiveArraySerializer<float[], Float> {

        public PrimitiveFloatArraySerializer(SerializerContext context) {
            super(Float.class, Float[]::new, ArrayUtils::wrapArray, ArrayUtils::unwrapArray, context);
        }

    }

    public static class PrimitiveDoubleArraySerializer extends PrimitiveArraySerializer<double[], Double> {

        public PrimitiveDoubleArraySerializer(SerializerContext context) {
            super(Double.class, Double[]::new, ArrayUtils::wrapArray, ArrayUtils::unwrapArray, context);
        }

    }

    public static class PrimitiveBooleanArraySerializer extends PrimitiveArraySerializer<boolean[], Boolean> {

        public PrimitiveBooleanArraySerializer(SerializerContext context) {
            super(Boolean.class, Boolean[]::new, ArrayUtils::wrapArray, ArrayUtils::unwrapArray, context);
        }

    }

    public static class EnumSerializer<E extends Enum<E>> implements Serializer<E> {

        private final Class<E> enumType;

        public EnumSerializer(Class<E> enumType) {
            this.enumType = enumType;
        }

        @Override
        public void serialize(E e, DataVisitor visitor) {
            visitor.writeString(e.toString().toLowerCase(Locale.ENGLISH));
        }

        @Override
        public @Nullable E deserialize(DataVisitor visitor, ErrorContainer errorContainer) {
            return visitor.readString()
                    .map(string -> {
                        try {
                            return Enum.valueOf(enumType, string.toUpperCase(Locale.ENGLISH));
                        } catch (IllegalArgumentException ignored) {}
                        errorContainer.error(ErrorType.CUSTOM,
                                "No enum constant " + enumType.getCanonicalName() + "." + string);
                        return null;
                    })
                    .orElse(null);
        }

    }

    public static class ConfigurationSerializer<C extends Configuration> implements Serializer<C> {

        private static final Function<Class<?>, String> COULD_NOT_SERIALIZE = as ->
                "Couldn't serialize type '" + as.getName() + "'. Did you register a serializer for it?";
        private static final Function<Class<?>, String> COULD_NOT_DESERIALIZE = as ->
                "Couldn't deserialize type '" + as.getName() + "'. Did you register a serializer for it?";

        private final Class<C> type;
        private final SerializerContext context;
        private final ConfigProperties properties;

        public ConfigurationSerializer(SerializerContext context) {
            this(JavaUtils.asClass(context.type()), context);
        }

        public ConfigurationSerializer(Class<C> type, SerializerContext context) {
            this.type = type;
            this.context = context;
            this.properties = context.properties();
        }

        @Override
        public void serialize(C configuration, DataVisitor visitor) {
            ConfigAdapter<?> configAdapter = context.configAdapter().get();
            nodeStream(configuration.getClass()).forEach(node -> {
                Object serialized = node.getSerialized(configuration);
                if (serialized == null && node.isHidden()) return;
                if (!configAdapter.setPrimitive(node.getFormattedName(), serialized)) {
                    handleError(
                            node,
                            new ErrorEntry(ErrorType.SERIALIZER_NOT_FOUND, COULD_NOT_SERIALIZE.apply(node.getActualType()))
                    );
                    return;
                }
                String[] comments = node.getComments();
                String inlineComment = node.getInlineComment();
                if (comments != null) configAdapter.setComments(node.getFormattedName(), comments);
                if (inlineComment != null) configAdapter.setInlineComment(node.getFormattedName(), inlineComment);
            });
            visitor.writeConfig(configAdapter);
        }

        @Override
        @SuppressWarnings({"unchecked", "rawtypes"})
        public @Nullable C deserialize(DataVisitor visitor, ErrorContainer errorContainer) {
            Map<String, Object> config = visitor.readConfig().map(ConfigAdapter::getAsMap).orElse(null);
            if (config == null) return null;
            Set<String> unhandledKeys = new LinkedHashSet<>(config.keySet());
            ErrorHandler errorHandler = properties.errorHandler();
            ClassBuilder<C> builder = classBuilder();
            nodeStream(builder.getType()).forEach(node -> {
                String key = node.getFormattedName();
                Object primitive = config.get(key);
                unhandledKeys.remove(key);
                if (primitive == null) {
                    if (node.isOptional()) return;
                    handleError(node, new ErrorEntry(ErrorType.KEY_NOT_FOUND, "Required key '" + key + "' is missing"));
                    return;
                }
                Serializer<?> deserializer = node.getReadWith();
                if (deserializer == null && !node.getActualType().isInstance(primitive)) {
                    handleError(node, new ErrorEntry(ErrorType.SERIALIZER_NOT_FOUND, COULD_NOT_DESERIALIZE.apply(node.getActualType())));
                    return;
                }
                Object deserialized = Serializers.deserialize(deserializer, primitive, errorContainer, node.getActualType());
                errorContainer.handleErrors(context);
                if (deserialized == null) return;
                ((ClassBuilder) builder).setComponent(node.getName(), node.getActualType(), deserialized);
            });
            unhandledKeys.forEach(key -> errorHandler.handle(
                    context.withNode(null),
                    new ErrorEntry(ErrorType.UNEXPECTED_KEY, "Unexpected key '" + key + "' was found")
            ));
            return builder.build();
        }

        private void handleError(ConfigNode<?> node, ErrorEntry error) {
            context.properties().errorHandler().handle(context.withNode(node), error);
        }

        private ClassBuilder<C> classBuilder() {
            if (type.isRecord()) {
                //noinspection unchecked,rawtypes
                return new RecordBuilder<>((Class) type);
            } else {
                return new ObjectBuilder<>(type, properties.classInitiator());
            }
        }

        private Stream<? extends ConfigNode<?>> nodeStream(Class<?> cls) {
            if (cls.isRecord()) return recordNodeStream(cls.asSubclass(Record.class));
            return properties.fieldExtractor().extract(cls)
                    .map(field -> new FieldNode(field, context::withNode))
                    .filter(properties.nodeFilter());
        }

        private Stream<RecordComponentNode> recordNodeStream(Class<? extends Record> recordClass) {
            return properties.recordDisassembler().disassemble(recordClass)
                    .map(component -> new RecordComponentNode(component, context::withNode))
                    .filter(properties.nodeFilter());
        }

    }

}
