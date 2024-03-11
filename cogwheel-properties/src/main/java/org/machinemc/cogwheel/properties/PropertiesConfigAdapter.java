package org.machinemc.cogwheel.properties;

import org.jetbrains.annotations.Nullable;
import org.machinemc.cogwheel.config.ConfigAdapter;
import org.machinemc.cogwheel.util.NumberUtils;

import java.util.*;
import java.util.stream.Collectors;

public class PropertiesConfigAdapter extends ConfigAdapter<CommentedProperties> {

    private CommentedProperties properties = new CommentedProperties();

    @Override
    public CommentedProperties getConfig() {
        return properties;
    }

    @Override
    public Set<String> keys() {
        return properties.keySet().stream().map(String::valueOf).collect(Collectors.toSet());
    }

    @Override
    public Optional<Number> getNumber(String key) {
        if (!properties.containsKey(key)) return Optional.empty();
        Object o = properties.get(key);
        if (o instanceof Number n) return Optional.of(n);
        try {
            Number number = NumberUtils.parse(o.toString());
            properties.put(key, number);
            return Optional.of(number);
        } catch (Exception exception) {
            return Optional.empty();
        }
    }

    @Override
    public Optional<String> getString(String key) {
        if (!properties.containsKey(key)) return Optional.empty();
        return properties.get(key).toString().describeConstable();
    }

    @Override
    public Optional<Boolean> getBoolean(String key) {
        if (!properties.containsKey(key)) return Optional.empty();
        Object o = properties.get(key);
        if (o instanceof Boolean b) return Optional.of(b);
        try {
            Boolean b = switch (o.toString().toLowerCase(Locale.ENGLISH)) {
                case "true" -> true;
                case "false" -> false;
                default -> throw new IllegalArgumentException(o + " isn't a valid boolean");
            };
            properties.put(key, b);
            return Optional.of(b);
        } catch (Exception exception) {
            return Optional.empty();
        }
    }

    @Override
    public Optional<Object[]> getArray(String key) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Optional<Map<String, Object>> getMap(String key) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setNull(String key) {
        properties.put(key, null);
    }

    @Override
    public void setNumber(String key, Number number) {
        properties.put(key, number);
    }

    @Override
    public void setString(String key, String string) {
        properties.put(key, string);
    }

    @Override
    public void setBoolean(String key, Boolean bool) {
        properties.put(key, bool);
    }

    @Override
    public void setArray(String key, Object[] array) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setMap(String key, Map<String, Object> map) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setConfig(String key, CommentedProperties config) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setComments(String key, @Nullable String[] comments) {
        properties.setComments(key, comments);
    }

    @Override
    public void setInlineComment(String key, String comment) { }

    @Override
    public void load(CommentedProperties properties) {
        this.properties = properties;
    }

}
