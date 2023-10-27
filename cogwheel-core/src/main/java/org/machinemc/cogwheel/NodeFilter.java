package org.machinemc.cogwheel;

import org.machinemc.cogwheel.config.ConfigNode;
import org.machinemc.cogwheel.config.FieldNode;
import org.machinemc.cogwheel.config.RecordComponentNode;

import java.util.function.Predicate;

public interface NodeFilter extends Predicate<ConfigNode<?>> {

    NodeFilter DEFAULT = new DefaultNodeFilter();

    default boolean check(ConfigNode<?> node) {
        return switch (node) {
            case FieldNode fieldNode -> check(fieldNode);
            case RecordComponentNode recordComponentNode -> check(recordComponentNode);
        };
    }

    boolean check(FieldNode node);

    boolean check(RecordComponentNode node);

    @Override
    default boolean test(ConfigNode<?> node) {
        return check(node);
    }

}
