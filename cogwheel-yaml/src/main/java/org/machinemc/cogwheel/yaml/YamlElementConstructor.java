package org.machinemc.cogwheel.yaml;

import org.machinemc.cogwheel.yaml.wrapper.*;
import org.snakeyaml.engine.v2.api.ConstructNode;
import org.snakeyaml.engine.v2.api.LoadSettings;
import org.snakeyaml.engine.v2.comments.CommentLine;
import org.snakeyaml.engine.v2.constructor.BaseConstructor;
import org.snakeyaml.engine.v2.exceptions.*;
import org.snakeyaml.engine.v2.nodes.*;

import java.util.*;

public class YamlElementConstructor extends BaseConstructor {

    public YamlElementConstructor(LoadSettings settings) {
        super(settings);

        tagConstructors.put(Tag.NULL, node -> YamlNull.INSTANCE);
        tagConstructors.put(Tag.BOOL, node -> {
            Boolean value = Boolean.valueOf(((ScalarNode) node).getValue());
            YamlElement element = new YamlPrimitive(value);
            if (settings.getParseComments())
                setComments(element, node);
            return element;
        });
        tagConstructors.put(Tag.STR, node -> {
            YamlElement element = new YamlPrimitive(((ScalarNode) node).getValue());
            if (settings.getParseComments())
                setComments(element, node);
            return element;
        });
        tagConstructors.put(Tag.INT, node -> {
            Integer value = Integer.valueOf(((ScalarNode) node).getValue());
            YamlElement element = new YamlPrimitive(value);
            if (settings.getParseComments())
                setComments(element, node);
            return element;
        });
        tagConstructors.put(Tag.FLOAT, node -> {
            Float value = Float.valueOf(((ScalarNode) node).getValue());
            YamlElement element = new YamlPrimitive(value);
            if (settings.getParseComments())
                setComments(element, node);
            return element;
        });
        tagConstructors.put(Tag.SEQ, new ConstructYamlSeq());
        tagConstructors.put(Tag.MAP, new ConstructYamlMap());
    }

    protected void flattenMapping(MappingNode node) {
        processDuplicateKeys(node);
    }

    protected void processDuplicateKeys(MappingNode node) {
        List<NodeTuple> nodeValue = node.getValue();
        Map<Object, Integer> keys = new HashMap<>(nodeValue.size());
        TreeSet<Integer> toRemove = new TreeSet<>();
        int i = 0;
        for (NodeTuple tuple : nodeValue) {
            Node keyNode = tuple.getKeyNode();
            Object key = constructObject(keyNode);
            Integer prevIndex = keys.put(key, i);
            if (prevIndex != null) {
                if (!settings.getAllowDuplicateKeys()) {
                    throw new DuplicateKeyException(node.getStartMark(), key,
                            tuple.getKeyNode().getStartMark());
                }
                toRemove.add(prevIndex);
            }
            i = i + 1;
        }

        Iterator<Integer> indices2remove = toRemove.descendingIterator();
        while (indices2remove.hasNext()) {
            nodeValue.remove(indices2remove.next().intValue());
        }
    }

    @Override
    protected void constructMapping2ndStep(MappingNode node, Map<Object, Object> mapping) {
        flattenMapping(node);
        super.constructMapping2ndStep(node, mapping);
    }

    public class ConstructYamlSeq implements ConstructNode {

        @Override
        @SuppressWarnings("unchecked")
        public Object construct(Node node) {
            SequenceNode seqNode = (SequenceNode) node;
            YamlElement element;
            if (node.isRecursive())
                element = new YamlArray((List<YamlElement>) (List<?>) createEmptyListForNode(seqNode));
            else
                element = new YamlArray((List<YamlElement>) (List<?>) constructSequence(seqNode));
            if (settings.getParseComments())
                setComments(element, node);
            return element;
        }

        @Override
        @SuppressWarnings("unchecked")
        public void constructRecursive(Node node, Object data) {
            if (node.isRecursive()) {
                constructSequenceStep2((SequenceNode) node, (List<Object>) data);
            } else {
                throw new YamlEngineException("Unexpected recursive sequence structure. Node: " + node);
            }
        }
    }

    public class ConstructYamlMap implements ConstructNode {

        @Override
        public Object construct(Node node) {
            MappingNode mappingNode = (MappingNode) node;
            YamlObject yamlObject = new YamlObject();
            Map<?, ?> map;
            if (node.isRecursive()) {
                map = createEmptyMapFor(mappingNode);
            } else {
                map = constructMapping(mappingNode);
            }
            map.forEach((key, value) -> yamlObject.add(((YamlPrimitive) key).getAsString(), (YamlElement) value));
            if (settings.getParseComments())
                setComments(yamlObject, node);
            return yamlObject;
        }

        @Override
        @SuppressWarnings("unchecked")
        public void constructRecursive(Node node, Object object) {
            if (node.isRecursive()) {
                constructMapping2ndStep((MappingNode) node, (Map<Object, Object>) object);
            } else {
                throw new YamlEngineException("Unexpected recursive mapping structure. Node: " + node);
            }
        }

    }

    private void setComments(YamlElement element, Node node) {
        if (node.getBlockComments() == null || node.getBlockComments().isEmpty()) return;
        element.setComments(node.getBlockComments().stream().map(CommentLine::getValue).toArray(String[]::new));
    }

}
