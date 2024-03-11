package org.machinemc.cogwheel.yaml;

import org.jetbrains.annotations.Nullable;
import org.machinemc.cogwheel.config.ConfigAdapter;
import org.machinemc.cogwheel.yaml.wrapper.*;

import java.util.*;

public class YamlConfigAdapter extends ConfigAdapter<YamlObject> {

    private YamlObject yamlObject = new YamlObject();

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
                .map(YamlArray::asRawObject);
    }

    @Override
    public Optional<Map<String, Object>> getMap(String key) {
        return Optional.ofNullable(yamlObject.get(key))
                .filter(YamlElement::isYamlObject)
                .map(YamlElement::getAsYamlObject)
                .map(YamlObject::asRawObject);
    }

    @Override
    public void setNull(String key) {
        yamlObject.add(key, new YamlNull());
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
        yamlObject.add(key, YamlArray.of(array));
    }

    @Override
    public void setMap(String key, Map<String, Object> map) {
        yamlObject.add(key, YamlObject.of(map));
    }

    @Override
    public void setConfig(String key, YamlObject config) {
        yamlObject.add(key, config.deepCopy());
    }

    @Override
    public void setComments(String key, @Nullable String[] comments) {
        YamlElement element = yamlObject.get(key);
        if (element == null) return;
        for (int i = 0; i < comments.length; i++) {
            if (comments[i] == null) continue;
            comments[i] = " " + comments[i];
        }
        element.setComments(comments);
    }

    @Override
    public void setInlineComment(String key, String comment) {
        YamlElement element = yamlObject.get(key);
        if (element == null) return;
        element.setInlineComment(" " + comment);
    }

    @Override
    public void load(YamlObject yamlObject) {
        this.yamlObject = yamlObject;
    }

}
