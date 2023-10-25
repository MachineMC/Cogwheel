package org.machinemc.cogwheel.keyformatter;

import java.util.Locale;

public class SnakeCaseKeyFormatter implements KeyFormatter {

    @Override
    public String format(String key) {
        return CAMEL_CASE_PATTERN.matcher(key).replaceAll("$1_$2").toLowerCase(Locale.ENGLISH);
    }

}
