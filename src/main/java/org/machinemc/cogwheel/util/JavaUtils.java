package org.machinemc.cogwheel.util;

import org.jetbrains.annotations.Nullable;

import java.lang.reflect.*;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Utility class for various Java-related operations.
 */
public final class JavaUtils {

    // Mapping of primitive types to their wrapped types
    private static final Map<Class<?>, Class<?>> primitiveToWrapped = Map.of(
            byte.class, Byte.class,
            short.class, Short.class,
            int.class, Integer.class,
            long.class, Long.class,
            float.class, Float.class,
            double.class, Double.class,
            boolean.class, Boolean.class,
            char.class, Character.class,
            void.class, Void.class
    );

    private static byte DEFAULT_BYTE;
    private static short DEFAULT_SHORT;
    private static int DEFAULT_INT;
    private static long DEFAULT_LONG;
    private static float DEFAULT_FLOAT;
    private static double DEFAULT_DOUBLE;
    private static boolean DEFAULT_BOOLEAN;
    private static char DEFAULT_CHAR;

    // Mapping of primitive types to their default values
    private static final Map<Class<?>, Object> defaultValues = Map.of(
            byte.class, DEFAULT_BYTE,
            short.class, DEFAULT_SHORT,
            int.class, DEFAULT_INT,
            long.class, DEFAULT_LONG,
            float.class, DEFAULT_FLOAT,
            double.class, DEFAULT_DOUBLE,
            boolean.class, DEFAULT_BOOLEAN,
            char.class, DEFAULT_CHAR
    );

    private JavaUtils() {
        throw new UnsupportedOperationException();
    }

    /**
     * Wraps a primitive type with its corresponding wrapped type.
     *
     * @param primitive The primitive class to wrap.
     * @return The wrapped class for the given primitive class.
     * @throws IllegalArgumentException if the provided class is not a primitive class.
     */
    public static Class<?> wrapPrimitiveClass(Class<?> primitive) throws IllegalArgumentException {
        if (!primitive.isPrimitive()) throw new IllegalArgumentException(primitive + " is not a primitive class");
        return primitiveToWrapped.get(primitive);
    }

    /**
     * Gets the default value for a given class.
     *
     * @param cls The class for which to retrieve the default value.
     * @return The default value for the provided class.
     */
    @SuppressWarnings("unchecked")
    public static <T> T getDefaultValue(Class<T> cls) {
        return (T) defaultValues.get(cls);
    }

    public static boolean hasConstructor(Class<?> cls, Class<?>... parameters) {
        try {
            cls.getDeclaredConstructor(parameters);
            return true;
        } catch (NoSuchMethodException e) {
            return false;
        }
    }

    public static <T> T newInstance(Class<T> cls) {
        return newInstance(cls, ArrayUtils.array());
    }

    public static <T> T newInstance(Class<T> cls, Class<?>[] parameters, Object... arguments) {
        try {
            Constructor<T> constructor = cls.getDeclaredConstructor(parameters);
            constructor.setAccessible(true);
            return constructor.newInstance(arguments);
        } catch (NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException e) {
            return null;
        }
    }

    public static Field getField(Class<?> cls, String name) {
        try {
            return cls.getDeclaredField(name);
        } catch (NoSuchFieldException e) {
            return null;
        }
    }

    public static Object getValue(Field field, @Nullable Object holder) {
        try {
            field.setAccessible(true);
            return field.get(holder);
        } catch (IllegalAccessException ignored) {
            return null;
        }
    }

    public static Object getValue(RecordComponent recordComponent, Object holder) {
        try {
            Method accessor = recordComponent.getAccessor();
            accessor.setAccessible(true);
            return accessor.invoke(holder);
        } catch (InvocationTargetException | IllegalAccessException ignored) {
            return null;
        }
    }

    /**
     * Retrieves an enum constant by its name from the given enum type.
     *
     * @param type   The enum class from which to retrieve the constant.
     * @param string The name of the enum constant.
     * @param <T>    The type of the enum.
     * @return The enum constant or null if the type is not an enum or the constant is not found.
     */
    @SuppressWarnings("unchecked")
    public static <T> @Nullable T getEnumConstant(Class<T> type, String string) {
        if (!type.isEnum()) return null;
        return (T) Enum.valueOf(type.asSubclass(Enum.class), string);
    }

    /**
     * Gets the class representation of a given type, which may be a {@link Class}, {@link ParameterizedType}, or {@link GenericArrayType}.
     *
     * @param type The type to extract the class from.
     * @param <T>  The resulting class type.
     * @return The class representation of the provided type.
     * @throws IllegalStateException if the provided type is not a {@link Class}, {@link ParameterizedType}, or {@link GenericArrayType}.
     */
    @SuppressWarnings("unchecked")
    public static <T> Class<T> asClass(Type type) {
        return (Class<T>) switch (type) {
            case Class<?> cls -> cls;
            case ParameterizedType parameterized -> parameterized.getRawType();
            case GenericArrayType arrayType -> {
                List<Integer> dimensions = new LinkedList<>();
                Type currentType = arrayType;
                do {
                    currentType = ((GenericArrayType) currentType).getGenericComponentType();
                    dimensions.add(0);
                } while (!(currentType instanceof ParameterizedType));
                int[] dimensionsArray = new int[dimensions.size()];
                for (int i = 0; i < dimensionsArray.length; i++)
                    dimensionsArray[i] = dimensions.get(i);
                yield ArrayUtils.newArrayInstance(asClass(currentType), dimensionsArray).getClass();
            }
            default -> throw new IllegalStateException("Unexpected type: " + type);
        };
    }

    public static String toString(Object object, String defaultIfNull) {
        if (object == null) return defaultIfNull;
        return toString(object);
    }

    public static String toString(Object object) {
        return switch (object) {
            case Object[] array -> Arrays.deepToString(array);
            case byte[] array -> Arrays.toString(array);
            case short[] array -> Arrays.toString(array);
            case int[] array -> Arrays.toString(array);
            case long[] array -> Arrays.toString(array);
            case float[] array -> Arrays.toString(array);
            case double[] array -> Arrays.toString(array);
            case boolean[] array -> Arrays.toString(array);
            case char[] array -> Arrays.toString(array);
            default -> String.valueOf(object);
        };
    }

}
