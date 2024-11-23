package org.machinemc.cogwheel.yaml;

import org.machinemc.cogwheel.config.ConfigAdapter;
import org.machinemc.cogwheel.config.ConfigProperties;
import org.machinemc.cogwheel.config.ConfigSerializer;
import org.machinemc.cogwheel.util.FileUtils;
import org.machinemc.cogwheel.yaml.wrapper.YamlObject;
import org.snakeyaml.engine.v2.api.Dump;
import org.snakeyaml.engine.v2.api.Load;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class YamlConfigSerializer extends ConfigSerializer<YamlObject> {

    protected YamlConfigSerializer(ConfigProperties properties) {
        super(properties);
    }

    @Override
    protected ConfigAdapter<YamlObject> newAdapter() {
        return new YamlConfigAdapter();
    }

    @Override
    protected void save(File file, YamlObject yamlObject) {
        FileUtils.createIfAbsent(file);
        try (FileWriter writer = new FileWriter(file)) {
            Dump dump = getProperties(YamlConfigProperties.class).dump();
            writer.write(dump.dumpToString(yamlObject));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public YamlObject load(File file) {
        try (FileReader reader = new FileReader(file)) {
            Load load = getProperties(YamlConfigProperties.class).load();
            return (YamlObject) load.loadFromReader(reader);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static YamlConfigSerializer newDefault() {
        return builder().build();
    }

    public static BuilderImpl builder() {
        return new BuilderImpl(new YamlConfigProperties());
    }

    public static class BuilderImpl extends Builder<YamlConfigSerializer, YamlConfigProperties, BuilderImpl> {

        protected BuilderImpl(YamlConfigProperties properties) {
            super(properties);
        }

        public BuilderImpl dump(Dump dump) {
            properties.dump = dump;
            return getThis();
        }

        public BuilderImpl load(Load load) {
            properties.load = load;
            return getThis();
        }

        @Override
        protected BuilderImpl getThis() {
            return this;
        }

        @Override
        public YamlConfigSerializer build() {
            return new YamlConfigSerializer(properties);
        }

    }

}
