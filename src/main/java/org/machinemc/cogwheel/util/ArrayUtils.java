package org.machinemc.cogwheel.util;

import java.lang.reflect.Array;

public final class ArrayUtils {

    private ArrayUtils() {
        throw new UnsupportedOperationException();
    }

    /**
     * Creates a new array instance of the specified component type and length.
     *
     * @param componentType The component type of the array.
     * @param length        The length of the array.
     * @param <T>           The type of elements in the array.
     * @return A new array instance.
     */
    @SuppressWarnings("unchecked")
    public static <T> T[] newArrayInstance(Class<T> componentType, int length) {
        return (T[]) Array.newInstance(componentType, length);
    }

    /**
     * Creates a new array instance of the specified component type and dimensions.
     *
     * @param componentType The component type of the array.
     * @param dimensions    The dimensions of the array.
     * @param <T>           The type of elements in the array.
     * @return A new array instance.
     */
    @SuppressWarnings("unchecked")
    public static <T> T[] newArrayInstance(Class<T> componentType, int... dimensions) {
        return (T[]) Array.newInstance(componentType, dimensions);
    }

    @SafeVarargs
    public static <T> T[] array(T... array) {
        return array;
    }

    public static Byte[] wrapArray(byte[] array) {
        Byte[] wrapped = new Byte[array.length];
        for (int i = 0; i < array.length; i++)
            wrapped[i] = array[i];
        return wrapped;
    }

    public static Short[] wrapArray(short[] array) {
        Short[] wrapped = new Short[array.length];
        for (int i = 0; i < array.length; i++)
            wrapped[i] = array[i];
        return wrapped;
    }

    public static Integer[] wrapArray(int[] array) {
        Integer[] wrapped = new Integer[array.length];
        for (int i = 0; i < array.length; i++)
            wrapped[i] = array[i];
        return wrapped;
    }

    public static Long[] wrapArray(long[] array) {
        Long[] wrapped = new Long[array.length];
        for (int i = 0; i < array.length; i++)
            wrapped[i] = array[i];
        return wrapped;
    }

    public static Float[] wrapArray(float[] array) {
        Float[] wrapped = new Float[array.length];
        for (int i = 0; i < array.length; i++)
            wrapped[i] = array[i];
        return wrapped;
    }

    public static Double[] wrapArray(double[] array) {
        Double[] wrapped = new Double[array.length];
        for (int i = 0; i < array.length; i++)
            wrapped[i] = array[i];
        return wrapped;
    }

    public static Boolean[] wrapArray(boolean[] array) {
        Boolean[] wrapped = new Boolean[array.length];
        for (int i = 0; i < array.length; i++)
            wrapped[i] = array[i];
        return wrapped;
    }

    public static Character[] wrapArray(char[] array) {
        Character[] wrapped = new Character[array.length];
        for (int i = 0; i < array.length; i++)
            wrapped[i] = array[i];
        return wrapped;
    }

    public static byte[] unwrapArray(Byte[] array) {
        byte[] primitive = new byte[array.length];
        for (int i = 0; i < array.length; i++) {
            if (array[i] == null) continue;
            primitive[i] = array[i];
        }
        return primitive;
    }

    public static short[] unwrapArray(Short[] array) {
        short[] primitive = new short[array.length];
        for (int i = 0; i < array.length; i++) {
            if (array[i] == null) continue;
            primitive[i] = array[i];
        }
        return primitive;
    }

    public static int[] unwrapArray(Integer[] array) {
        int[] primitive = new int[array.length];
        for (int i = 0; i < array.length; i++) {
            if (array[i] == null) continue;
            primitive[i] = array[i];
        }
        return primitive;
    }

    public static long[] unwrapArray(Long[] array) {
        long[] primitive = new long[array.length];
        for (int i = 0; i < array.length; i++) {
            if (array[i] == null) continue;
            primitive[i] = array[i];
        }
        return primitive;
    }

    public static float[] unwrapArray(Float[] array) {
        float[] primitive = new float[array.length];
        for (int i = 0; i < array.length; i++) {
            if (array[i] == null) continue;
            primitive[i] = array[i];
        }
        return primitive;
    }

    public static double[] unwrapArray(Double[] array) {
        double[] primitive = new double[array.length];
        for (int i = 0; i < array.length; i++) {
            if (array[i] == null) continue;
            primitive[i] = array[i];
        }
        return primitive;
    }

    public static boolean[] unwrapArray(Boolean[] array) {
        boolean[] primitive = new boolean[array.length];
        for (int i = 0; i < array.length; i++) {
            if (array[i] == null) continue;
            primitive[i] = array[i];
        }
        return primitive;
    }

    public static char[] unwrapArray(Character[] array) {
        char[] primitive = new char[array.length];
        for (int i = 0; i < array.length; i++) {
            if (array[i] == null) continue;
            primitive[i] = array[i];
        }
        return primitive;
    }

}
