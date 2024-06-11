package org.machinemc.cogwheel.config;

import org.machinemc.cogwheel.keyformatter.IdentifierKeyFormatter;
import org.machinemc.cogwheel.keyformatter.KeyFormatter;
import org.machinemc.cogwheel.serialization.SerializerRegistry;
import org.machinemc.cogwheel.*;

public class ConfigProperties implements Cloneable {

    SerializerRegistry serializerRegistry = new SerializerRegistry();
    ClassInitiator classInitiator = ClassInitiator.DEFAULT;
    KeyFormatter keyFormatter = new IdentifierKeyFormatter();
    NodeFilter nodeFilter = NodeFilter.DEFAULT;
    FieldExtractor fieldExtractor = FieldExtractor.DEFAULT;
    RecordDisassembler recordDisassembler = RecordDisassembler.DEFAULT;
    ErrorHandler errorHandler = ErrorHandler.NORMAL;

    public SerializerRegistry serializerRegistry() {
        return serializerRegistry;
    }

    public ClassInitiator classInitiator() {
        return classInitiator;
    }

    public KeyFormatter keyFormatter() {
        return keyFormatter;
    }

    public NodeFilter nodeFilter() {
        return nodeFilter;
    }

    public FieldExtractor fieldExtractor() {
        return fieldExtractor;
    }

    public RecordDisassembler recordDisassembler() {
        return recordDisassembler;
    }

    public ErrorHandler errorHandler() {
        return errorHandler;
    }

    @Override
    public ConfigProperties clone() {
        try {
            return (ConfigProperties) super.clone();
        } catch (CloneNotSupportedException exception) {
            throw new AssertionError();
        }
    }

}
