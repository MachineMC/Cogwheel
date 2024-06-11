package org.machinemc.cogwheel.yaml;

import org.machinemc.cogwheel.yaml.wrapper.*;
import org.snakeyaml.engine.v2.api.DumpSettings;
import org.snakeyaml.engine.v2.common.FlowStyle;
import org.snakeyaml.engine.v2.common.NonPrintableStyle;
import org.snakeyaml.engine.v2.common.ScalarStyle;
import org.snakeyaml.engine.v2.exceptions.YamlEngineException;
import org.snakeyaml.engine.v2.nodes.MappingNode;
import org.snakeyaml.engine.v2.nodes.Node;
import org.snakeyaml.engine.v2.nodes.ScalarNode;
import org.snakeyaml.engine.v2.nodes.Tag;
import org.snakeyaml.engine.v2.representer.BaseRepresenter;
import org.snakeyaml.engine.v2.scanner.StreamReader;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.regex.Pattern;

public class YamlElementRepresenter extends BaseRepresenter {

    public static final Pattern MULTILINE_PATTERN = Pattern.compile("[\n\u0085]");

    protected Map<Class<?>, Tag> classTags;

    protected DumpSettings settings;

    public YamlElementRepresenter(DumpSettings settings) {
        defaultFlowStyle = settings.getDefaultFlowStyle();
        defaultScalarStyle = settings.getDefaultScalarStyle();

        nullRepresenter = data -> representScalar(Tag.NULL, "null");
        representers.put(YamlNull.class, data -> representScalar(Tag.NULL, "null"));

        representers.put(YamlPrimitive.class, data -> {
            YamlPrimitive element = (YamlPrimitive) data;

            Tag tag = switch (element.asRawObject()) {
                case Boolean b -> Tag.BOOL;
                case String s -> Tag.STR;

                case Byte b -> Tag.INT;
                case Short s -> Tag.INT;
                case Integer i -> Tag.INT;
                case Long l -> Tag.INT;

                case Float f -> Tag.FLOAT;
                case Double d -> Tag.FLOAT;

                case BigInteger bi -> Tag.INT;
                case BigDecimal bd -> Tag.FLOAT;

                default -> throw new IllegalStateException("Unexpected value: " + element.asRawObject());
            };

            ScalarStyle style = ScalarStyle.PLAIN;

            String value = element.getAsString();

            if (settings.getNonPrintableStyle() == NonPrintableStyle.BINARY && !StreamReader.isPrintable(value)) {
                tag = Tag.BINARY;
                final byte[] bytes = value.getBytes(StandardCharsets.UTF_8);
                final String checkValue = new String(bytes, StandardCharsets.UTF_8);
                if (!checkValue.equals(value)) throw new YamlEngineException("invalid string value has occurred");
                value = Base64.getEncoder().encodeToString(bytes);
                style = ScalarStyle.LITERAL;
            }

            if (defaultScalarStyle == ScalarStyle.PLAIN && MULTILINE_PATTERN.matcher(value).find())
                style = ScalarStyle.LITERAL;

            return representScalar(tag, value, style);
        });

        representers.put(YamlArray.class, data -> {
            YamlArray element = (YamlArray) data;
            return representSequence(Tag.SEQ, element, settings.getDefaultFlowStyle());
        });

        representers.put(YamlObject.class, data -> {
            YamlObject yaml = (YamlObject) data;
            MappingNode node = (MappingNode) representMapping(
                    Tag.MAP,
                    yaml.asMap(),
                    settings.getDefaultFlowStyle()
            );

            if (!settings.getDumpComments())
                return node;

            node.getValue().forEach(nodeTuple -> {
                if (!(nodeTuple.getKeyNode() instanceof ScalarNode keyNode)) return;
                YamlElement element = yaml.get(keyNode.getValue());
                if (element == null) return;
                keyNode.setBlockComments(element.getComments());
                nodeTuple.getValueNode().setInLineComments(element.getInlineComment());
            });

            return node;
        });

        representers.put(String.class, data -> {
            YamlPrimitive wrapped = new YamlPrimitive((String) data);
            return representers.get(YamlPrimitive.class).representData(wrapped);
        });

        classTags = new LinkedHashMap<>();
        this.settings = settings;
    }

    @Override
    protected Node representSequence(Tag tag, Iterable<?> sequence, FlowStyle flowStyle) {
        Node node = super.representSequence(tag, sequence, flowStyle);
        representedObjects.clear();
        return node;
    }

    @Override
    protected Node representMapping(Tag tag, Map<?, ?> mapping, FlowStyle flowStyle) {
        Node node = super.representMapping(tag, mapping, flowStyle);
        representedObjects.clear();
        return node;
    }

}
