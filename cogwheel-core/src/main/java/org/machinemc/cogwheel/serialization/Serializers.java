package org.machinemc.cogwheel.serialization;

import org.jetbrains.annotations.Nullable;
import org.machinemc.cogwheel.DataVisitor;
import org.machinemc.cogwheel.ErrorHandler;
import org.machinemc.cogwheel.config.*;
import org.machinemc.cogwheel.util.ArrayUtils;
import org.machinemc.cogwheel.util.JavaUtils;
import org.machinemc.cogwheel.util.NumberUtils;
import org.machinemc.cogwheel.util.classbuilder.ClassBuilder;
import org.machinemc.cogwheel.util.classbuilder.ObjectBuilder;
import org.machinemc.cogwheel.util.classbuilder.RecordBuilder;
import org.machinemc.cogwheel.util.error.ErrorContainer;
import org.machinemc.cogwheel.util.error.ErrorEntry;
import org.machinemc.cogwheel.util.error.ErrorType;

import java.io.File;
import java.lang.reflect.*;
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
        if (JavaUtils.hasConstructor(serializerClass))
            return JavaUtils.newInstance(serializerClass);
        throw new IllegalArgumentException("Cannot instantiate serializer '" + serializerClass + "'. " +
                "No appropriate constructor found");
    }

    private static AnnotatedType[] validateParameterTypes(AnnotatedType[] parameters) {
        for (AnnotatedType parameter : parameters)
            validateParameterType(parameter);
        return parameters;
    }

    private static AnnotatedType validateParameterType(AnnotatedType parameter) {
        switch (parameter.getType()) {
            case Class<?> ignore -> {}
            case ParameterizedType ignore -> {}
            case GenericArrayType ignore -> {}
            default -> throw new UnsupportedOperationException("Cannot serialize type '" + parameter + "'");
        }
        return parameter;
    }

    private static <T> T deserialize(Serializer<T> deserializer, Object primitive, Class<T> as, ErrorContainer errorContainer) {
        if (primitive == null) return null;
        ErrorContainer childContainer = new ErrorContainer(errorContainer);
        Object deserialized = deserializer != null ? Serializer.deserialize(deserializer, primitive, childContainer) : primitive;
        if (!as.isInstance(primitive) && deserialized == null && !childContainer.hasErrors()) {
            childContainer.error(ErrorType.MISMATCHED_TYPES, "Could not deserialize (%s) '%s' as %s".formatted(
                    primitive.getClass().getSimpleName(),
                    JavaUtils.toString(primitive),
                    as.getSimpleName())
            );
            return null;
        }
        return as.cast(deserialized);
    }

    public static class NumberSerializer<N extends Number> implements Serializer<N> {

        private final Class<N> type;
        private final Function<Number, N> numberFunction;

        public NumberSerializer(Class<N> type, Function<Number, N> numberFunction) {
            this.type = type;
            this.numberFunction = numberFunction;
        }

        @Override
        public void serialize(N number, DataVisitor visitor) {
            visitor.writeNumber(number);
        }

        @Override
        public @Nullable N deserialize(DataVisitor visitor, ErrorContainer errorContainer) {
            Number number = visitor.readNumber().orElse(null);
            if (type.isInstance(number)) return type.cast(number);
            return Optional.ofNullable(number)
                    .map(String::valueOf)
                    .or(visitor::readString)
                    .map(string -> {
                        try {
                            Number parsed = NumberUtils.parse(string);
                            if (type.isInstance(parsed)) return type.cast(parsed);
                            return numberFunction.apply(new NumberUtils.ClampedNumber(parsed));
                        } catch (NumberFormatException e) {
                            errorContainer.error(ErrorType.CUSTOM, "Could not parse '" + string + "' as a number");
                            return null;
                        }
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
        private final SerializerContext context;

        public CollectionSerializer(IntFunction<C> factory, SerializerContext context) {
            AnnotatedParameterizedType type = (AnnotatedParameterizedType) context.annotatedType();
            AnnotatedType argument = validateParameterType(type.getAnnotatedActualTypeArguments()[0]);
            this.factory = factory;
            this.context = context.withType(argument);
        }

        @Override
        public void serialize(C collection, DataVisitor visitor) {
            Serializer<T> serializer = context.writeWith();
            visitor.writeArray(collection.stream()
                    .map(object -> serializer != null ? Serializer.serialize(serializer, object) : object)
                    .toArray());
        }

        @Override
        public @Nullable C deserialize(DataVisitor visitor, ErrorContainer errorContainer) {
            Class<T> type = JavaUtils.asClass(context.type());
            Serializer<T> deserializer = context.readWith();
            return visitor.readArray()
                    .map(array -> {
                        C collection = factory.apply(array.length);
                        for (Object primitive : array) {
                            T object = Serializers.deserialize(deserializer, primitive, type, errorContainer);
                            collection.add(object);
                        }
                        return collection;
                    })
                    .orElse(null);
        }

    }

    public static class MapSerializer<K, V> implements Serializer<Map<K, V>> {

        private final Class<K> keyType;
        private final SerializerContext context;

        @SuppressWarnings("unchecked")
        public MapSerializer(SerializerContext context) {
            AnnotatedParameterizedType type = (AnnotatedParameterizedType) context.annotatedType();
            AnnotatedType[] parameters = validateParameterTypes(type.getAnnotatedActualTypeArguments());
            if (!(parameters[0].getType() instanceof Class<?> keyClass) || (!String.class.isAssignableFrom(keyClass) && !keyClass.isEnum()))
                throw new IllegalArgumentException("Map keys may only be of types '%s' or '%s'. Not '%s'".formatted(
                        String.class.getTypeName(),
                        Enum.class.getTypeName(),
                        parameters[0].getType().getTypeName()
                ));
            this.keyType = (Class<K>) keyClass;
            this.context = context.withType(parameters[1]);
        }

        @Override
        public void serialize(Map<K, V> map, DataVisitor visitor) {
            Map<String, Object> serializedMap = HashMap.newHashMap(map.size());
            Serializer<V> serializer = context.writeWith();
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
            Class<V> valueType = JavaUtils.asClass(context.type());
            Serializer<V> deserializer = context.readWith();
            serialized.forEach((key, value) -> {
                K actualKey;
                if (keyType.isEnum()) {
                    actualKey = JavaUtils.getEnumConstant(keyType, key.toUpperCase(Locale.ENGLISH));
                } else if (String.class.isAssignableFrom(keyType)) {
                    actualKey = (K) key;
                } else {
                    throw new UnsupportedOperationException("Cannot deserialize key of type: " + keyType.getTypeName());
                }
                map.put(actualKey, Serializers.deserialize(deserializer, value, valueType, errorContainer));
            });
            return map;
        }

    }

    public static class ArraySerializer<T> implements Serializer<T[]> {

        private final Class<T[]> arrayType;
        private final IntFunction<T[]> arrayFactory;
        private final SerializerContext context;

        @SuppressWarnings("unchecked")
        public ArraySerializer(SerializerContext context) {
            AnnotatedArrayType type = (AnnotatedArrayType) context.annotatedType();
            this.arrayType = JavaUtils.asClass(type);
            this.arrayFactory = length -> (T[]) ArrayUtils.newArrayInstance(arrayType.componentType(), length);
            this.context = context.withType(type.getAnnotatedGenericComponentType());
        }

        @Override
        public void serialize(T[] array, DataVisitor visitor) {
            Serializer<T> serializer = context.writeWith();
            Object[] serialized = new Object[array.length];
            for (int i = 0; i < array.length; i++)
                serialized[i] = serializer != null ? Serializer.serialize(serializer, array[i]) : array[i];
            visitor.writeArray(serialized);
        }

        @Override
        public T @Nullable [] deserialize(DataVisitor visitor, ErrorContainer errorContainer) {
            Class<T> type = JavaUtils.asClass(context.type());
            Serializer<T> deserializer = context.readWith();
            Object[] array = visitor.readArray().orElse(null);
            if (array == null) return null;
            return Arrays.stream(array)
                    .map(object -> Serializers.deserialize(
                            deserializer,
                            object,
                            type,
                            errorContainer
                    ))
                    .filter(Objects::nonNull)
                    .toArray(arrayFactory);
        }

    }

    public abstract static class PrimitiveArraySerializer<P, W> implements Serializer<P> {

        private final IntFunction<W[]> wrapperFactory;
        private final Function<P, W[]> wrapper;
        private final Function<W[], P> unwrapper;
        private final SerializerContext context;

        public PrimitiveArraySerializer(
                IntFunction<W[]> wrapperFactory,
                Function<P, W[]> wrapper,
                Function<W[], P> unwrapper,
                SerializerContext context
        ) {
            AnnotatedArrayType type = (AnnotatedArrayType) context.annotatedType();
            this.wrapperFactory = wrapperFactory;
            this.wrapper = wrapper;
            this.unwrapper = unwrapper;
            this.context = context.withType(type.getAnnotatedGenericComponentType());
        }

        @Override
        public void serialize(P primitiveArray, DataVisitor visitor) {
            Serializer<W> serializer = context.writeWith();
            if (serializer == null) {
                visitor.writeArray(wrapper.apply(primitiveArray));
                return;
            }
            W[] wrapped = wrapper.apply(primitiveArray);
            Object[] serialized = new Object[wrapped.length];
            for (int i = 0; i < wrapped.length; i++)
                serialized[i] = Serializer.serialize(serializer, wrapped[i]);
            visitor.writeArray(serialized);
        }

        @Override
        public @Nullable P deserialize(DataVisitor visitor, ErrorContainer errorContainer) {
            Class<W> type = JavaUtils.asClass(context.type());
            Serializer<W> deserializer = context.readWith();
            return visitor.readArray().map(objects -> unwrapper.apply(Arrays.stream(objects)
                            .map(object -> Serializers.deserialize(deserializer, object, type, errorContainer))
                            .filter(Objects::nonNull)
                            .toArray(wrapperFactory)))
                    .orElse(null);
        }

    }

    public static class PrimitiveByteArraySerializer extends PrimitiveArraySerializer<byte[], Byte> {

        public PrimitiveByteArraySerializer(SerializerContext context) {
            super(Byte[]::new, ArrayUtils::wrapArray, ArrayUtils::unwrapArray, context);
        }

    }

    public static class PrimitiveShortArraySerializer extends PrimitiveArraySerializer<short[], Short> {

        public PrimitiveShortArraySerializer(SerializerContext context) {
            super(Short[]::new, ArrayUtils::wrapArray, ArrayUtils::unwrapArray, context);
        }

    }

    public static class PrimitiveIntArraySerializer extends PrimitiveArraySerializer<int[], Integer> {

        public PrimitiveIntArraySerializer(SerializerContext context) {
            super(Integer[]::new, ArrayUtils::wrapArray, ArrayUtils::unwrapArray, context);
        }

    }

    public static class PrimitiveLongArraySerializer extends PrimitiveArraySerializer<long[], Long> {

        public PrimitiveLongArraySerializer(SerializerContext context) {
            super(Long[]::new, ArrayUtils::wrapArray, ArrayUtils::unwrapArray, context);
        }

    }

    public static class PrimitiveFloatArraySerializer extends PrimitiveArraySerializer<float[], Float> {

        public PrimitiveFloatArraySerializer(SerializerContext context) {
            super(Float[]::new, ArrayUtils::wrapArray, ArrayUtils::unwrapArray, context);
        }

    }

    public static class PrimitiveDoubleArraySerializer extends PrimitiveArraySerializer<double[], Double> {

        public PrimitiveDoubleArraySerializer(SerializerContext context) {
            super(Double[]::new, ArrayUtils::wrapArray, ArrayUtils::unwrapArray, context);
        }

    }

    public static class PrimitiveBooleanArraySerializer extends PrimitiveArraySerializer<boolean[], Boolean> {

        public PrimitiveBooleanArraySerializer(SerializerContext context) {
            super(Boolean[]::new, ArrayUtils::wrapArray, ArrayUtils::unwrapArray, context);
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
            this(JavaUtils.asClass(context.annotatedType()), context);
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
                Object primitive = node.getValue(configuration);
                Serializer<Object> writeWith = context.withNode(node).writeWith();
                Object serialized = writeWith == null ? primitive : Serializer.serialize(writeWith, primitive);
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
                if (comments != null) {
                    for (int i = 0; i < comments.length; i++) if (comments[i].isEmpty()) comments[i] = null;
                    configAdapter.setComments(node.getFormattedName(), comments);
                }
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
                Class<?> type = node.getActualType();
                Serializer<?> readWith = context.withNode(node).readWith();
                if (readWith == null && !type.isInstance(primitive)) {
                    handleError(node, new ErrorEntry(ErrorType.SERIALIZER_NOT_FOUND, COULD_NOT_DESERIALIZE.apply(type)));
                    return;
                }
                Object deserialized = Serializers.deserialize((Serializer) readWith, primitive, type, errorContainer);
                errorContainer.handleErrors(context);
                if (deserialized == null) return;
                ((ClassBuilder) builder).setComponent(node.getName(), type, deserialized);
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
