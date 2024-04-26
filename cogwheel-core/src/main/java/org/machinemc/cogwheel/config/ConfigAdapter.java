package org.machinemc.cogwheel.config;

import org.jetbrains.annotations.Nullable;
import org.machinemc.cogwheel.util.ArrayUtils;

import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public abstract class ConfigAdapter<T> {

    private transient Map<String, Object> mapView = null;

    public abstract T getConfig();

    public abstract Set<String> keys();

    public boolean containsKey(String key) {
        return keys().contains(key);
    }

    public Map<String, Object> asMapView() {
        if (mapView == null) mapView = new ConfigAdapterMap(this);
        return mapView;
    }

    public abstract Optional<Number> getNumber(String key);

    public abstract Optional<String> getString(String key);

    public abstract Optional<Boolean> getBoolean(String key);

    public abstract Optional<Object[]> getArray(String key);

    public <C extends Collection<Object>> Optional<C> getCollection(String key, Supplier<C> collectionFactory) {
        return getArray(key).map(array -> Arrays.stream(array).collect(Collectors.toCollection(collectionFactory)));
    }

    public abstract Optional<Map<String, Object>> getMap(String key);

    public Optional<Object> getPrimitive(String key) {
        if (!containsKey(key)) return Optional.empty();
        Object object = getNumber(key).orElse(null);
        if (object == null) object = getString(key).orElse(null);
        if (object == null) object = getBoolean(key).orElse(null);
        if (object == null) object = getArray(key).orElse(null);
        if (object == null) object = getMap(key).orElse(null);
        return Optional.ofNullable(object);
    }

    public abstract void setNull(String key);

    public abstract void setNumber(String key, Number number);

    public abstract void setString(String key, String string);

    public abstract void setBoolean(String key, Boolean bool);

    public abstract void setArray(String key, Object[] array);

    public void setCollection(String key, Collection<?> collection) {
        setArray(key, collection.toArray());
    }

    public abstract void setMap(String key, Map<String, Object> map);

    public abstract void setConfig(String key, T config);

    public abstract void setComments(String key, @Nullable String[] comments);

    public abstract void setInlineComment(String key, String comment);

    @SuppressWarnings("unchecked")
    public boolean setPrimitive(String key, Object object) {
        switch (object) {
            case null -> setNull(key);
            case Number number -> setNumber(key, number);
            case String string -> setString(key, string);
            case Boolean bool -> setBoolean(key, bool);
            case byte[] array -> setArray(key, ArrayUtils.wrapArray(array));
            case short[] array -> setArray(key, ArrayUtils.wrapArray(array));
            case int[] array -> setArray(key, ArrayUtils.wrapArray(array));
            case long[] array -> setArray(key, ArrayUtils.wrapArray(array));
            case float[] array -> setArray(key, ArrayUtils.wrapArray(array));
            case double[] array -> setArray(key, ArrayUtils.wrapArray(array));
            case boolean[] array -> setArray(key, ArrayUtils.wrapArray(array));
            case Object[] array -> setArray(key, array);
            case Collection<?> collection -> setCollection(key, collection);
            case Map<?, ?> map -> setMap(key, (Map<String, Object>) map);
            case ConfigAdapter<?> adapter -> setPrimitive(key, adapter.getConfig());
            default -> {
                if (!getConfig().getClass().isInstance(object)) return false;
                setConfig(key, (T) object);
            }
        }
        return true;
    }

    public abstract void load(T t);

    public void load(Map<String, Object> map) {
        map.forEach(this::setPrimitive);
    }

}
