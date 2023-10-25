package org.machinemc.cogwheel.config;

import org.jetbrains.annotations.NotNull;

import java.util.*;

public class ConfigAdapterMap extends AbstractMap<String, Object> {

    private final ConfigAdapter<?> adapter;

    public ConfigAdapterMap(ConfigAdapter<?> adapter) {
        this.adapter = adapter;
    }

    @Override
    public Object get(Object key) {
        if (!(key instanceof String string))
            return null;
        return adapter.getPrimitive(string).orElse(null);
    }

    @Override
    public Object put(String key, Object value) {
        Object previous = get(key);
        adapter.setPrimitive(key, value);
        return previous;
    }

    @Override
    public Object remove(Object key) {
        if (!(key instanceof String string))
            return null;
        return put(string, null);
    }

    @Override
    public @NotNull Set<String> keySet() {
        return adapter.keys();
    }

    @Override
    public @NotNull Set<Map.Entry<String, Object>> entrySet() {
        return new AbstractSet<>() {

            private final Set<String> keys = keySet();

            @Override
            public boolean add(Map.Entry<String, Object> entry) {
                if (keys.contains(entry.getKey()))
                    return false;
                put(entry.getKey(), entry.getValue());
                return true;
            }

            @Override
            public boolean remove(Object o) {
                if (!(o instanceof String key) || !keys.contains(key))
                    return false;
                ConfigAdapterMap.this.remove(key);
                return true;
            }

            @Override
            public Iterator<Map.Entry<String, Object>> iterator() {
                return new Iterator<>() {

                    private final Iterator<String> iterator = keys.iterator();

                    @Override
                    public boolean hasNext() {
                        return iterator.hasNext();
                    }

                    @Override
                    public Map.Entry<String, Object> next() {
                        return new Entry(iterator.next());
                    }

                };
            }

            @Override
            public int size() {
                return keys.size();
            }
        };
    }

    private class Entry implements Map.Entry<String, Object> {

        private final String key;

        private Entry(String key) {
            this.key = key;
        }

        @Override
        public String getKey() {
            return key;
        }

        @Override
        public Object getValue() {
            return get(key);
        }

        @Override
        public Object setValue(Object value) {
            return put(key, value);
        }

    }

}
