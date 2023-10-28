package org.machinemc.cogwheel.yaml;

import org.machinemc.cogwheel.config.ConfigProperties;
import org.snakeyaml.engine.v2.api.Dump;
import org.snakeyaml.engine.v2.api.DumpSettings;
import org.snakeyaml.engine.v2.api.Load;
import org.snakeyaml.engine.v2.api.LoadSettings;
import org.snakeyaml.engine.v2.common.FlowStyle;

public class YamlConfigProperties extends ConfigProperties {

    Dump dump;
    Load load;

    {
        DumpSettings dumpSettings = DumpSettings.builder()
                .setDefaultFlowStyle(FlowStyle.BLOCK)
                .setIndent(2)
                .setDumpComments(true)
                .build();
        dump = new Dump(dumpSettings, new YamlElementRepresenter(dumpSettings));

        LoadSettings loadSettings = LoadSettings.builder()
                .setParseComments(true)
                .build();
        load = new Load(loadSettings, new YamlElementConstructor(loadSettings));
    }

    public Dump dump() {
        return dump;
    }

    public Load load() {
        return load;
    }

}
