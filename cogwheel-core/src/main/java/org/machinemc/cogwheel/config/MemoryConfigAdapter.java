package org.machinemc.cogwheel.config;

import org.jetbrains.annotations.Nullable;

import java.util.*;

public class MemoryConfigAdapter extends ConfigAdapter<Map<String, Object>> {

    private final Map<String, Object> map;

    public MemoryConfigAdapter() {
        this(new LinkedHashMap<>());
    }

    protected MemoryConfigAdapter(Map<String, Object> map) {
        this.map = map;
    }

    @Override
    public Map<String, Object> getConfig() {
        return map;
    }

    @Override
    public Set<String> keys() {
        return map.keySet();
    }

    @Override
    public Map<String, Object> asMapView() {
        return map;
    }

    @Override
    public Optional<Number> getNumber(String key) {
        return getAs(key, Number.class);
    }

    @Override
    public Optional<String> getString(String key) {
        return getAs(key, String.class);
    }

    @Override
    public Optional<Boolean> getBoolean(String key) {
        return getAs(key, Boolean.class);
    }

    @Override
    public Optional<Object[]> getArray(String key) {
        return getAs(key, Object[].class);
    }

    @Override
    public Optional<Map<String, Object>> getMap(String key) {
        return getAs(key, Map.class);
    }

    @Override
    public void setNull(String key) {
        map.put(key, null);
    }

    @Override
    public void setNumber(String key, Number number) {
        map.put(key, number);
    }

    @Override
    public void setString(String key, String string) {
        map.put(key, string);
    }

    @Override
    public void setBoolean(String key, Boolean bool) {
        map.put(key, bool);
    }

    @Override
    public void setArray(String key, Object[] array) {
        map.put(key, array);
    }

    @Override
    public void setMap(String key, Map<String, Object> map) {
        this.map.put(key, map);
    }

    @Override
    public void setConfig(String key, Map<String, Object> config) {
        setMap(key, Map.copyOf(config));
    }

    @Override
    public void setComments(String key, @Nullable String[] comments) {}

    @Override
    public void setInlineComment(String key, String comment) {}

    @SuppressWarnings("unchecked")
    private <T, R extends T> Optional<R> getAs(String key, Class<T> type) {
        return (Optional<R>) Optional.ofNullable(map.get(key))
                .filter(type::isInstance)
                .map(type::cast);
    }

}
