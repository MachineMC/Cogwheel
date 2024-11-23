package org.machinemc.cogwheel.properties;

import org.machinemc.cogwheel.config.ConfigAdapter;
import org.machinemc.cogwheel.config.ConfigProperties;
import org.machinemc.cogwheel.config.ConfigSerializer;
import org.machinemc.cogwheel.util.FileUtils;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class PropertiesConfigSerializer extends ConfigSerializer<CommentedProperties> {

    protected PropertiesConfigSerializer(ConfigProperties properties) {
        super(properties);
    }

    @Override
    protected ConfigAdapter<CommentedProperties> newAdapter() {
        return new PropertiesConfigAdapter();
    }

    @Override
    protected void save(File file, CommentedProperties properties) {
        FileUtils.createIfAbsent(file);
        try (FileWriter writer = new FileWriter(file)) {
            PropertiesConfigProperties configProperties = getProperties(PropertiesConfigProperties.class);
            properties.store(
                    writer,
                    configProperties.colonSeparator(),
                    configProperties.exclamationMarkComments(),
                    configProperties.spacesBetweenSeparator(),
                    configProperties.emptyLineBetweenEntries()
            );
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }
    }

    @Override
    public CommentedProperties load(File file) {
        try (FileReader reader = new FileReader(file)) {
            CommentedProperties properties = new CommentedProperties();
            properties.load(reader);
            return properties;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static PropertiesConfigSerializer newDefault() {
        return builder().build();
    }

    public static BuilderImpl builder() {
        return new BuilderImpl(new PropertiesConfigProperties());
    }

    public static class BuilderImpl extends Builder<PropertiesConfigSerializer, PropertiesConfigProperties, BuilderImpl> {

        protected BuilderImpl(PropertiesConfigProperties properties) {
            super(properties);
        }

        public BuilderImpl colonSeparator(boolean colonSeparator) {
            properties.colonSeparator = colonSeparator;
            return getThis();
        }

        public BuilderImpl exclamationMarkComments(boolean exclamationMarkComments) {
            properties.exclamationMarkComments = exclamationMarkComments;
            return getThis();
        }

        public BuilderImpl spacesBetweenSeparator(boolean spacesBetweenSeparator) {
            properties.spacesBetweenSeparator = spacesBetweenSeparator;
            return getThis();
        }

        public BuilderImpl emptyLineBetweenEntries(boolean emptyLineBetweenEntries) {
            properties.emptyLineBetweenEntries = emptyLineBetweenEntries;
            return getThis();
        }

        @Override
        protected BuilderImpl getThis() {
            return this;
        }

        @Override
        public PropertiesConfigSerializer build() {
            return new PropertiesConfigSerializer(properties);
        }

    }

}
