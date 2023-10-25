package org.machinemc.cogwheel.keyformatter;

import java.util.Locale;

public class UpperCaseKeyFormatter implements KeyFormatter {

    @Override
    public String format(String key) {
        return CAMEL_CASE_PATTERN.matcher(key).replaceAll("$1 $2").toUpperCase(Locale.ENGLISH);
    }

}
