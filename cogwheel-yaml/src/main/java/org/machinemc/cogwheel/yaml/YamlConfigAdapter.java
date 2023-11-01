package org.machinemc.cogwheel.yaml;

import org.machinemc.cogwheel.config.ConfigAdapter;
import org.machinemc.cogwheel.yaml.wrapper.*;

import java.util.*;

public class YamlConfigAdapter extends ConfigAdapter<YamlObject> {

    private YamlObject yamlObject = new YamlObject();

    @Override
    public ConfigAdapter<YamlObject> newConfigInstance() {
        return new YamlConfigAdapter();
    }

    @Override
    public YamlObject getConfig() {
        return yamlObject;
    }

    @Override
    public Set<String> keys() {
        return yamlObject.keySet();
    }

    @Override
    public Optional<Number> getNumber(String key) {
        return Optional.ofNullable(yamlObject.get(key))
                .filter(YamlElement::isYamlPrimitive)
                .map(YamlElement::getAsYamlPrimitive)
                .filter(YamlPrimitive::isNumber)
                .map(YamlPrimitive::getAsNumber);
    }

    @Override
    public Optional<String> getString(String key) {
        return Optional.ofNullable(yamlObject.get(key))
                .filter(YamlElement::isYamlPrimitive)
                .map(YamlElement::getAsYamlPrimitive)
                .filter(YamlPrimitive::isString)
                .map(YamlPrimitive::getAsString);
    }

    @Override
    public Optional<Boolean> getBoolean(String key) {
        return Optional.ofNullable(yamlObject.get(key))
                .filter(YamlElement::isYamlPrimitive)
                .map(YamlElement::getAsYamlPrimitive)
                .filter(YamlPrimitive::isBoolean)
                .map(YamlPrimitive::getAsBoolean);
    }

    @Override
    public Optional<Object[]> getArray(String key) {
        return Optional.ofNullable(yamlObject.get(key))
                .filter(YamlElement::isYamlArray)
                .map(YamlElement::getAsYamlArray)
                .map(YamlConfigAdapter::mapYAMLArray);
    }

    @Override
    public Optional<Map<String, Object>> getMap(String key) {
        return Optional.ofNullable(yamlObject.get(key))
                .filter(YamlElement::isYamlObject)
                .map(YamlElement::getAsYamlObject)
                .map(YamlConfigAdapter::mapYAMLObject);
    }

    @Override
    public void setNull(String key) {
        yamlObject.add(key, YamlNull.INSTANCE);
    }

    @Override
    public void setNumber(String key, Number number) {
        yamlObject.addProperty(key, number);
    }

    @Override
    public void setString(String key, String string) {
        yamlObject.addProperty(key, string);
    }

    @Override
    public void setBoolean(String key, Boolean bool) {
        yamlObject.addProperty(key, bool);
    }

    @Override
    public void setArray(String key, Object[] array) {
        yamlObject.add(key, getAsYAMLArray(array));
    }

    @Override
    public void setMap(String key, Map<String, Object> map) {
        yamlObject.add(key, getAsYAMLObject(map));
    }

    @Override
    public void setConfig(String key, YamlObject config) {
        yamlObject.add(key, config.deepCopy());
    }

    @Override
    public void setComments(String key, String[] comments) {
        YamlElement element = yamlObject.get(key);
        if (element == null) return;
        element.setComments(comments);
    }

    @Override
    public void setInlineComment(String key, String comment) {
        YamlElement element = yamlObject.get(key);
        if (element == null) return;;
        element.setInlineComment(comment);
    }

    @Override
    public void load(YamlObject yamlObject) {
        this.yamlObject = yamlObject;
    }

    private static Object mapYAMLElement(YamlElement yamlElement) {
        if (yamlElement instanceof YamlPrimitive yamlPrimitive) return mapYAMLPrimitive(yamlPrimitive);
        if (yamlElement instanceof YamlArray yamlArray) return mapYAMLArray(yamlArray);
        if (yamlElement instanceof YamlObject yamlObject) return mapYAMLObject(yamlObject);
        if (yamlElement instanceof YamlNull) return null;
        throw new IllegalArgumentException("Unexpected YamlElement '" + yamlElement.getClass() + "'");
    }

    private static Object mapYAMLPrimitive(YamlPrimitive yamlPrimitive) {
        if (yamlPrimitive.isNumber()) return yamlPrimitive.getAsNumber();
        if (yamlPrimitive.isBoolean()) return yamlPrimitive.getAsBoolean();
        return yamlPrimitive.getAsString();
    }

    private static Object[] mapYAMLArray(YamlArray yamlArray) {
        Object[] array = new Object[yamlArray.size()];
        for (int i = 0; i < array.length; i++) array[i] = mapYAMLElement(yamlArray.get(i));
        return array;
    }

    private static Map<String, Object> mapYAMLObject(YamlObject yamlObject) {
        Map<String, Object> map = new HashMap<>(yamlObject.size());
        yamlObject.asMap().forEach((entry, yamlElement) -> map.put(entry, mapYAMLElement(yamlElement)));
        return map;
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private static YamlElement getAsYAMLElement(Object object) {
        if (object == null) return YamlNull.INSTANCE;
        else if (object.getClass().isArray()) return getAsYAMLArray((Object[]) object);
        else if (object instanceof Collection<?> collection) return getAsYAMLArray(collection.toArray());
        else if (object instanceof Map map) return getAsYAMLObject(map);
        return getAsYAMLPrimitive(object);
    }

    private static YamlArray getAsYAMLArray(Object[] objects) {
        YamlArray array = new YamlArray(objects.length);
        for (Object object : objects)
            array.add(getAsYAMLElement(object));
        return array;
    }

    private static YamlObject getAsYAMLObject(Map<String, Object> map) {
        YamlObject yaml = new YamlObject();
        map.forEach((key, object) -> yaml.add(key, getAsYAMLElement(object)));
        return yaml;
    }

    private static YamlElement getAsYAMLPrimitive(Object object) {
        if (object instanceof String string) return new YamlPrimitive(string);
        else if (object instanceof Number number) return new YamlPrimitive(number);
        else if (object instanceof Boolean bool) return new YamlPrimitive(bool);
        else if (object instanceof Character c) return new YamlPrimitive(c);
        throw new IllegalArgumentException("Object '" + object + "' is not a primitive value");
    }

}
