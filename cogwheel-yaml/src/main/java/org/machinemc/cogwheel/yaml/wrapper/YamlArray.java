package org.machinemc.cogwheel.yaml.wrapper;

import org.jetbrains.annotations.NotNull;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

public final class YamlArray extends YamlElement implements Iterable<YamlElement> {

    private final ArrayList<YamlElement> elements;

    public YamlArray() {
        elements = new ArrayList<>();
    }

    public YamlArray(int capacity) {
        elements = new ArrayList<>(capacity);
    }

    public YamlArray(Collection<YamlElement> elements) {
        this.elements = new ArrayList<>(elements);
    }

    @Override
    public YamlArray deepCopy() {
        if (elements.isEmpty()) return new YamlArray();
        YamlArray result = new YamlArray(elements.size());
        for (YamlElement element : elements)
            result.add(element.deepCopy());
        copyComments(result);
        return result;
    }

    public void add(Boolean bool) {
        elements.add(bool == null ? new YamlNull() : new YamlPrimitive(bool));
    }

    public void add(Character character) {
        elements.add(character == null ? new YamlNull() : new YamlPrimitive(character));
    }

    public void add(Number number) {
        elements.add(number == null ? new YamlNull() : new YamlPrimitive(number));
    }

    public void add(String string) {
        elements.add(string == null ? new YamlNull() : new YamlPrimitive(string));
    }

    public void add(YamlElement element) {
        if (element == null) element = new YamlNull();
        elements.add(element);
    }

    public void addAll(YamlArray array) {
        elements.addAll(array.elements);
    }

    public YamlElement set(int index, YamlElement element) {
        return elements.set(index, element == null ? new YamlNull() : element);
    }

    public boolean remove(YamlElement element) {
        return elements.remove(element);
    }

    public YamlElement remove(int index) {
        return elements.remove(index);
    }

    public boolean contains(YamlElement element) {
        return elements.contains(element);
    }

    public int size() {
        return elements.size();
    }

    public boolean isEmpty() {
        return elements.isEmpty();
    }

    @Override
    public @NotNull Iterator<YamlElement> iterator() {
        return elements.iterator();
    }

    public YamlElement get(int i) {
        return elements.get(i);
    }

    private YamlElement getAsSingleElement() {
        int size = elements.size();
        if (size == 1) return elements.getFirst();
        throw new IllegalStateException("Array must have size 1, but has size " + size);
    }

    @Override
    public Number getAsNumber() {
        return getAsSingleElement().getAsNumber();
    }

    @Override
    public String getAsString() {
        return getAsSingleElement().getAsString();
    }

    @Override
    public double getAsDouble() {
        return getAsSingleElement().getAsDouble();
    }

    @Override
    public BigDecimal getAsBigDecimal() {
        return getAsSingleElement().getAsBigDecimal();
    }

    @Override
    public BigInteger getAsBigInteger() {
        return getAsSingleElement().getAsBigInteger();
    }

    @Override
    public float getAsFloat() {
        return getAsSingleElement().getAsFloat();
    }

    @Override
    public long getAsLong() {
        return getAsSingleElement().getAsLong();
    }

    @Override
    public int getAsInt() {
        return getAsSingleElement().getAsInt();
    }

    @Override
    public byte getAsByte() {
        return getAsSingleElement().getAsByte();
    }

    @Override
    public short getAsShort() {
        return getAsSingleElement().getAsShort();
    }

    @Override
    public Object[] asRawObject() {
        Object[] array = new Object[elements.size()];
        for (int i = 0; i < array.length; i++)
            array[i] = elements.get(i).asRawObject();
        return array;
    }

    @Override
    public boolean getAsBoolean() {
        return getAsSingleElement().getAsBoolean();
    }

    @Override
    public boolean equals(Object o) {
        return (o == this) || (o instanceof YamlElement && ((YamlArray) o).elements.equals(elements));
    }

    @Override
    public int hashCode() {
        return elements.hashCode();
    }

    public static YamlArray of(Object[] array) {
        YamlArray yamlArray = new YamlArray(array.length);
        for (Object object : array)
            yamlArray.add(YamlElement.of(object));
        return yamlArray;
    }

}
