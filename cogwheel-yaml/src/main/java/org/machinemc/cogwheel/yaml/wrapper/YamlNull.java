package org.machinemc.cogwheel.yaml.wrapper;

public final class YamlNull extends YamlElement {

    public static final YamlNull INSTANCE = new YamlNull();

    private YamlNull () {
    }

    @Override
    public YamlElement deepCopy() {
        return INSTANCE;
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
