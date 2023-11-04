package org.machinemc.cogwheel.yaml.wrapper;

public final class YamlNull extends YamlElement {

    @Override
    public YamlElement deepCopy() {
        YamlNull copy = new YamlNull();
        copyComments(copy);
        return copy;
    }

    @Override
    public Object asRawObject() {
        return null;
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof YamlNull;
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

}
