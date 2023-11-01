package org.machinemc.cogwheel.yaml;

import org.snakeyaml.engine.v2.nodes.Tag;

public final class YamlTags {

    public static Tag BIG_INTEGER = new Tag(Tag.PREFIX + "bigint");
    public static Tag BIG_DECIMAL = new Tag(Tag.PREFIX + "bigdecimal");

    private YamlTags() {
        throw new UnsupportedOperationException();
    }

}
