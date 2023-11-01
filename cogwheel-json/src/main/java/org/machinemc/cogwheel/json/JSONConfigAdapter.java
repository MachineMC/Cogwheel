package org.machinemc.cogwheel.json;

import org.machinemc.cogwheel.config.ConfigAdapter;
import com.google.gson.*;

import java.util.*;

public class JSONConfigAdapter extends ConfigAdapter<JsonObject> {

    private JsonObject jsonObject = new JsonObject();

    @Override
    public ConfigAdapter<JsonObject> newConfigInstance() {
        return new JSONConfigAdapter();
    }

    @Override
    public JsonObject getConfig() {
        return jsonObject;
    }

    @Override
    public Set<String> keys() {
        return jsonObject.keySet();
    }

    @Override
    public Optional<Number> getNumber(String key) {
        return Optional.ofNullable(jsonObject.get(key))
                .filter(JsonElement::isJsonPrimitive)
                .map(JsonElement::getAsJsonPrimitive)
                .filter(JsonPrimitive::isNumber)
                .map(JsonPrimitive::getAsNumber);
    }

    @Override
    public Optional<String> getString(String key) {
        return Optional.ofNullable(jsonObject.get(key))
                .filter(JsonElement::isJsonPrimitive)
                .map(JsonElement::getAsJsonPrimitive)
                .filter(JsonPrimitive::isString)
                .map(JsonPrimitive::getAsString);
    }

    @Override
    public Optional<Boolean> getBoolean(String key) {
        return Optional.ofNullable(jsonObject.get(key))
                .filter(JsonElement::isJsonPrimitive)
                .map(JsonElement::getAsJsonPrimitive)
                .filter(JsonPrimitive::isBoolean)
                .map(JsonPrimitive::getAsBoolean);
    }

    @Override
    public Optional<Object[]> getArray(String key) {
        return Optional.ofNullable(jsonObject.get(key))
                .filter(JsonElement::isJsonArray)
                .map(JsonElement::getAsJsonArray)
                .map(JSONConfigAdapter::mapJSONArray);
    }

    @Override
    public Optional<Map<String, Object>> getMap(String key) {
        return Optional.ofNullable(jsonObject.get(key))
                .filter(JsonElement::isJsonObject)
                .map(JsonElement::getAsJsonObject)
                .map(JSONConfigAdapter::mapJSONObject);
    }

    @Override
    public void setNull(String key) {
        jsonObject.add(key, JsonNull.INSTANCE);
    }

    @Override
    public void setNumber(String key, Number number) {
        jsonObject.addProperty(key, number);
    }

    @Override
    public void setString(String key, String string) {
        jsonObject.addProperty(key, string);
    }

    @Override
    public void setBoolean(String key, Boolean bool) {
        jsonObject.addProperty(key, bool);
    }

    @Override
    public void setArray(String key, Object[] array) {
        jsonObject.add(key, getAsJSONArray(array));
    }

    @Override
    public void setMap(String key, Map<String, Object> map) {
        jsonObject.add(key, getAsJSONObject(map));
    }

    @Override
    public void setConfig(String key, JsonObject config) {
        jsonObject.add(key, config.deepCopy());
    }

    @Override
    public void setComments(String key, String[] comments) { }

    @Override
    public void setInlineComment(String key, String comment) { }

    @Override
    public void load(JsonObject jsonObject) {
        this.jsonObject = jsonObject;
    }

    private static Object mapJSONElement(JsonElement jsonElement) {
        if (jsonElement instanceof JsonPrimitive jsonPrimitive) return mapJSONPrimitive(jsonPrimitive);
        if (jsonElement instanceof JsonArray jsonArray) return mapJSONArray(jsonArray);
        if (jsonElement instanceof JsonObject jsonObject) return mapJSONObject(jsonObject);
        if (jsonElement instanceof JsonNull) return null;
        throw new IllegalArgumentException("Unexpected JsonElement '" + jsonElement.getClass() + "'");
    }

    private static Object mapJSONPrimitive(JsonPrimitive jsonPrimitive) {
        if (jsonPrimitive.isNumber()) return jsonPrimitive.getAsNumber();
        if (jsonPrimitive.isBoolean()) return jsonPrimitive.getAsBoolean();
        return jsonPrimitive.getAsString();
    }

    private static Object[] mapJSONArray(JsonArray jsonArray) {
        Object[] array = new Object[jsonArray.size()];
        for (int i = 0; i < array.length; i++) array[i] = mapJSONElement(jsonArray.get(i));
        return array;
    }

    private static Map<String, Object> mapJSONObject(JsonObject jsonObject) {
        Map<String, Object> map = new HashMap<>(jsonObject.size());
        jsonObject.asMap().forEach((entry, jsonElement) -> map.put(entry, mapJSONElement(jsonElement)));
        return map;
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private static JsonElement getAsJSONElement(Object object) {
        if (object == null) return JsonNull.INSTANCE;
        else if (object.getClass().isArray()) return getAsJSONArray((Object[]) object);
        else if (object instanceof Collection<?> collection) return getAsJSONArray(collection.toArray());
        else if (object instanceof Map map) return getAsJSONObject(map);
        return getAsJSONPrimitive(object);
    }

    private static JsonArray getAsJSONArray(Object[] objects) {
        JsonArray array = new JsonArray(objects.length);
        for (Object object : objects)
            array.add(getAsJSONElement(object));
        return array;
    }

    private static JsonObject getAsJSONObject(Map<String, Object> map) {
        JsonObject json = new JsonObject();
        map.forEach((key, object) -> json.add(key, getAsJSONElement(object)));
        return json;
    }

    private static JsonElement getAsJSONPrimitive(Object object) {
        if (object instanceof String string) return new JsonPrimitive(string);
        else if (object instanceof Number number) return new JsonPrimitive(number);
        else if (object instanceof Boolean bool) return new JsonPrimitive(bool);
        else if (object instanceof Character c) return new JsonPrimitive(c);
        throw new IllegalArgumentException("Object '" + object + "' is not a primitive value");
    }

}
