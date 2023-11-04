package org.machinemc.cogwheel.properties;

import org.machinemc.cogwheel.config.ConfigProperties;

public class PropertiesConfigProperties extends ConfigProperties {

    boolean semicolonSeparator = false;
    boolean exclamationMarkComments = false;
    boolean spacesBetweenSeparator = false;
    boolean emptyLineBetweenEntries = false;

    public boolean semicolonSeparator() {
        return semicolonSeparator;
    }

    public boolean exclamationMarkComments() {
        return exclamationMarkComments;
    }

    public boolean spacesBetweenSeparator() {
        return spacesBetweenSeparator;
    }

    public boolean emptyLineBetweenEntries() {
        return emptyLineBetweenEntries;
    }

}
