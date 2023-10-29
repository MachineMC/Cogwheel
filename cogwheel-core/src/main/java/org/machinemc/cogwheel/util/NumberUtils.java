package org.machinemc.cogwheel.util;

import org.jetbrains.annotations.Contract;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Objects;

public final class NumberUtils {

    private NumberUtils() {
        throw new UnsupportedOperationException();
    }

    @Contract("null -> fail")
    public static Number parse(String string) throws NumberFormatException {
        if (isInteger(string)) return parseInteger(string);
        return parseDecimal(string);
    }

    @Contract("null -> fail")
    public static BigInteger parseInteger(String string) throws NumberFormatException {
        return new BigInteger(string);
    }

    @Contract("null -> fail")
    public static BigDecimal parseDecimal(String string) throws NumberFormatException {
        return new BigDecimal(string);
    }

    @Contract("null, _, _ -> fail")
    public static Number clamp(Number number, long min, long max) {
        return switch (Objects.requireNonNull(number, "number")) {
            case BigInteger bigInteger -> {
                try {
                    yield Math.clamp(bigInteger.longValueExact(), min, max);
                } catch (ArithmeticException e) {
                    yield bigInteger.signum() == -1 ? min : max;
                }
            }
            case BigDecimal bigDecimal -> clamp(bigDecimal.toBigInteger(), min, max);
            default -> {
                long longValue = number.longValue();
                double doubleValue = number.doubleValue();
                if (longValue != doubleValue)
                    yield doubleValue < 0 ? min : max;
                yield Math.clamp(longValue, min, max);
            }
        };
    }

    @Contract("null -> fail")
    public static ClampedNumber clamped(Number number) {
        return new ClampedNumber(number);
    }

    private static boolean isInteger(Number number) {
        return isInteger(Objects.requireNonNull(number, "number").toString());
    }

    private static boolean isInteger(String string) {
        return string.indexOf('.') == -1;
    }

    public static class ClampedNumber extends Number {

        private final Number number;
        private Byte clampedByte;
        private Short clampedShort;
        private Integer clampedInt;
        private Long clampedLong;

        @Contract("null -> fail")
        public ClampedNumber(Number number) {
            this.number = Objects.requireNonNull(number, "number");
        }

        @Override
        public byte byteValue() {
            if (clampedByte == null) clampedByte = clamp(number, Byte.MIN_VALUE, Byte.MAX_VALUE).byteValue();
            return clampedByte;
        }

        @Override
        public short shortValue() {
            if (clampedShort == null) clampedShort = clamp(number, Short.MIN_VALUE, Short.MAX_VALUE).shortValue();
            return clampedShort;
        }

        @Override
        public int intValue() {
            if (clampedInt == null) clampedInt = clamp(number, Integer.MIN_VALUE, Integer.MAX_VALUE).intValue();
            return clampedInt;
        }

        @Override
        public long longValue() {
            if (clampedLong == null) clampedLong = clamp(number, Long.MIN_VALUE, Long.MAX_VALUE).longValue();
            return clampedLong;
        }

        @Override
        public float floatValue() {
            return number.floatValue();
        }

        @Override
        public double doubleValue() {
            return number.doubleValue();
        }

        @Override
        public String toString() {
            return number.toString();
        }

        @Override
        public boolean equals(Object object) {
            if (this == object) return true;
            if (object == null || getClass() != object.getClass()) return false;

            ClampedNumber that = (ClampedNumber) object;

            return number.equals(that.number);
        }

        @Override
        public int hashCode() {
            return number.hashCode();
        }

    }

}
