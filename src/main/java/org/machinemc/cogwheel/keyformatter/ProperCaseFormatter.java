package org.machinemc.cogwheel.keyformatter;

public class ProperCaseFormatter implements KeyFormatter {

    @Override
    public String format(String key) {
        return Character.toUpperCase(key.charAt(0))
                + CAMEL_CASE_PATTERN.matcher(key.substring(1)).replaceAll("$1 $2");
    }

}
