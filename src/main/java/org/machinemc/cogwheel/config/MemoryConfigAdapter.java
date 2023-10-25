package org.machinemc.cogwheel.config;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public class MemoryConfigAdapter extends ConfigAdapter<Map<String, Object>> {

    private final Map<String, Object> map;

    public MemoryConfigAdapter() {
        this(new HashMap<>());
    }

    protected MemoryConfigAdapter(Map<String, Object> map) {
        this.map = map;
    }

    @Override
    public ConfigAdapter<Map<String, Object>> newConfigInstance() {
        return new MemoryConfigAdapter();
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
    public Map<String, Object> getAsMap() {
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
    public void setComments(String key, String[] comments) {}

    @Override
    public void setInlineComment(String key, String comment) {}

    @SuppressWarnings("unchecked")
    private <T, R extends T> Optional<R> getAs(String key, Class<T> type) {
        return (Optional<R>) Optional.ofNullable(map.get(key))
                .filter(type::isInstance)
                .map(type::cast);
    }

}
