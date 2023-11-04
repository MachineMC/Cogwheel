package org.machinemc.cogwheel.yaml.wrapper;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

public non-sealed class YamlObject extends YamlElement {

    private final Map<String, YamlElement> members = new LinkedHashMap<>();

    public YamlObject() {
    }

    @Override
    public YamlObject deepCopy() {
        YamlObject result = new YamlObject();
        for (Map.Entry<String, YamlElement> entry : members.entrySet()) {
            result.add(entry.getKey(), entry.getValue().deepCopy());
        }
        copyComments(result);
        return result;
    }

    @Override
    public Map<String, Object> asRawObject() {
        Map<String, Object> map = new LinkedHashMap<>();
        for (Map.Entry<String, YamlElement> entry : members.entrySet())
            map.put(entry.getKey(), entry.getValue().asRawObject());
        return map;
    }

    public void add(String property, YamlElement value) {
        members.put(property, value == null ? new YamlNull() : value);
    }

    public YamlElement remove(String property) {
        return members.remove(property);
    }

    public void addProperty(String property, String value) {
        add(property, value == null ? new YamlNull() : new YamlPrimitive(value));
    }

    public void addProperty(String property, Number value) {
        add(property, value == null ? new YamlNull() : new YamlPrimitive(value));
    }

    public void addProperty(String property, Boolean value) {
        add(property, value == null ? new YamlNull() : new YamlPrimitive(value));
    }

    public void addProperty(String property, Character value) {
        add(property, value == null ? new YamlNull() : new YamlPrimitive(value));
    }

    public Set<Map.Entry<String, YamlElement>> entrySet() {
        return members.entrySet();
    }

    public Set<String> keySet() {
        return members.keySet();
    }

    public int size() {
        return members.size();
    }

    public boolean isEmpty() {
        return members.isEmpty();
    }

    public boolean has(String memberName) {
        return members.containsKey(memberName);
    }

    public YamlElement get(String memberName) {
        return members.get(memberName);
    }

    public YamlPrimitive getAsYamlPrimitive(String memberName) {
        return (YamlPrimitive) members.get(memberName);
    }

    public YamlArray getAsYamlArray(String memberName) {
        return (YamlArray) members.get(memberName);
    }

    public YamlObject getAsYamlObject(String memberName) {
        return (YamlObject) members.get(memberName);
    }

    public Map<String, YamlElement> asMap() {
        return Collections.unmodifiableMap(members);
    }

    @Override
    public boolean equals(Object o) {
        return (o == this) || (o instanceof YamlObject && ((YamlObject) o).members.equals(members));
    }

    @Override
    public int hashCode() {
        return members.hashCode();
    }

    public static YamlObject of(Map<String, ?> map) {
        YamlObject yamlObject = new YamlObject();
        map.forEach((key, value) -> yamlObject.add(key, YamlElement.of(value)));
        return yamlObject;
    }

}
