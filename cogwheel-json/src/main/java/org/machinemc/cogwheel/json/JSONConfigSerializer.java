package org.machinemc.cogwheel.json;

import org.machinemc.cogwheel.config.ConfigAdapter;
import org.machinemc.cogwheel.config.ConfigProperties;
import org.machinemc.cogwheel.config.ConfigSerializer;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.machinemc.cogwheel.util.FileUtils;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class JSONConfigSerializer extends ConfigSerializer<JsonObject> {

    protected JSONConfigSerializer(ConfigProperties properties) {
        super(properties);
    }

    @Override
    protected ConfigAdapter<JsonObject> newAdapter() {
        return new JSONConfigAdapter();
    }

    @Override
    protected void save(File file, JsonObject jsonObject) {
        FileUtils.createIfAbsent(file);
        try (FileWriter writer = new FileWriter(file)) {
            getProperties(JSONConfigProperties.class).gson().toJson(jsonObject, writer);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public JsonObject load(File file) {
        try (FileReader reader = new FileReader(file)) {
            JsonElement jsonElement = JsonParser.parseReader(reader);
            if (jsonElement.isJsonObject()) return jsonElement.getAsJsonObject();
            throw new IllegalArgumentException("Couldn't read JSON in '" + file + "'");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static JSONConfigSerializer newDefault() {
        return builder().build();
    }

    public static BuilderImpl builder() {
        return new BuilderImpl(new JSONConfigProperties());
    }

    public static class BuilderImpl extends Builder<JSONConfigSerializer, JSONConfigProperties, BuilderImpl> {

        protected BuilderImpl(JSONConfigProperties properties) {
            super(properties);
        }

        public BuilderImpl gson(Gson gson) {
            properties.gson = gson;
            return getThis();
        }

        @Override
        protected BuilderImpl getThis() {
            return this;
        }

        @Override
        public JSONConfigSerializer build() {
            return new JSONConfigSerializer(properties);
        }

    }

}
