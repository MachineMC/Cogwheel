package org.machinemc.cogwheel;

import org.machinemc.cogwheel.annotations.Ignore;
import org.machinemc.cogwheel.config.ConfigNode;
import org.machinemc.cogwheel.config.FieldNode;
import org.machinemc.cogwheel.config.RecordComponentNode;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

class DefaultNodeFilter implements NodeFilter {

    private static final int flags = Modifier.FINAL | Modifier.STATIC | Modifier.TRANSIENT;

    @Override
    public boolean check(FieldNode node) {
        Field field = node.getAnnotatedElement();
        return (field.getModifiers() & flags) == 0 && !field.isSynthetic() && notIgnored(node);
    }

    @Override
    public boolean check(RecordComponentNode node) {
        return notIgnored(node);
    }

    private static boolean notIgnored(ConfigNode<?> node) {
        return node.getAnnotation(Ignore.class).isEmpty();
    }

}
