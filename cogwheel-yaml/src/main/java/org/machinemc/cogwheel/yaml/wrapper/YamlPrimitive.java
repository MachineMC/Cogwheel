package org.machinemc.cogwheel.yaml.wrapper;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Objects;

public final class YamlPrimitive extends YamlElement {

    private final Object value;

    public YamlPrimitive(Boolean bool) {
        this((Object) bool);
    }

    public YamlPrimitive(Number number) {
        this((Object) number);
    }

    public YamlPrimitive(String string) {
        this((Object) string);
    }

    public YamlPrimitive(Character c) {
        this(Objects.requireNonNull(c).toString());
    }

    private YamlPrimitive(Object object) {
        value = Objects.requireNonNull(object);
    }

    @Override
    public YamlPrimitive deepCopy() {
        YamlPrimitive result = new YamlPrimitive(value);
        copyComments(result);
        return result;
    }

    public boolean isBoolean() {
        return value instanceof Boolean;
    }

    @Override
    public boolean getAsBoolean() {
        if (isBoolean()) return (Boolean) value;
        return Boolean.parseBoolean(getAsString());
    }

    public boolean isNumber() {
        return value instanceof Number;
    }

    @Override
    public Number getAsNumber() {
        if (value instanceof Number) {
            return (Number) value;
        } else if (value instanceof String s) {
            try {
                return Double.parseDouble(s);
            } catch (Exception ignored) { }
        }
        throw new UnsupportedOperationException("Primitive is neither a number nor a number string");
    }

    public boolean isString() {
        return value instanceof String;
    }

    @Override
    public String getAsString() {
        return value.toString();
    }

    @Override
    public double getAsDouble() {
        return isNumber() ? getAsNumber().doubleValue() : Double.parseDouble(getAsString());
    }

    @Override
    public BigDecimal getAsBigDecimal() {
        return value instanceof BigDecimal ? (BigDecimal) value : new BigDecimal(getAsString());
    }

    @Override
    public BigInteger getAsBigInteger() {
        return value instanceof BigInteger ? (BigInteger) value : new BigInteger(getAsString());
    }

    @Override
    public float getAsFloat() {
        return isNumber() ? getAsNumber().floatValue() : Float.parseFloat(getAsString());
    }

    @Override
    public long getAsLong() {
        return isNumber() ? getAsNumber().longValue() : Long.parseLong(getAsString());
    }

    @Override
    public short getAsShort() {
        return isNumber() ? getAsNumber().shortValue() : Short.parseShort(getAsString());
    }

    @Override
    public Object asRawObject() {
        return value;
    }

    @Override
    public int getAsInt() {
        return isNumber() ? getAsNumber().intValue() : Integer.parseInt(getAsString());
    }

    @Override
    public byte getAsByte() {
        return isNumber() ? getAsNumber().byteValue() : Byte.parseByte(getAsString());
    }

    @Override
    public int hashCode() {
        if (value == null) {
            return 31;
        }
        // Using the recommended hashing algorithm from Effective Java for longs and doubles
        if (isIntegral(this)) {
            long value = getAsNumber().longValue();
            return (int) (value ^ (value >>> 32));
        }
        if (value instanceof Number) {
            long value = Double.doubleToLongBits(getAsNumber().doubleValue());
            return (int) (value ^ (value >>> 32));
        }
        return value.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        YamlPrimitive other = (YamlPrimitive) obj;
        if (value == null) {
            return other.value == null;
        }
        if (isIntegral(this) && isIntegral(other)) {
            return getAsNumber().longValue() == other.getAsNumber().longValue();
        }
        if (value instanceof Number && other.value instanceof Number) {
            double a = getAsNumber().doubleValue();
            double b = other.getAsNumber().doubleValue();
            return a == b || (Double.isNaN(a) && Double.isNaN(b));
        }
        return value.equals(other.value);
    }

    private static boolean isIntegral(YamlPrimitive primitive) {
        if (primitive.value instanceof Number number) {
            return number instanceof BigInteger || number instanceof Long || number instanceof Integer
                    || number instanceof Short || number instanceof Byte;
        }
        return false;
    }

    private static boolean isPrimitive(Object object) {
        return switch (object) {
            case Boolean b -> true;
            case Character c -> true;
            case Number n -> true;
            case String s -> true;
            default -> false;
        };
    }

    public static YamlPrimitive of(Object primitive) {
        if (!isPrimitive(primitive)) throw new IllegalArgumentException(primitive + " is not a primitive value");
        return new YamlPrimitive(primitive);
    }

}
