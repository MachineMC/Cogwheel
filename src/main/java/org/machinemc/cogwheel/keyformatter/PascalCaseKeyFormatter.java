package org.machinemc.cogwheel.keyformatter;

public class PascalCaseKeyFormatter implements KeyFormatter {

    @Override
    public String format(String key) {
        return Character.toUpperCase(key.charAt(0)) + key.substring(1);
    }

}
