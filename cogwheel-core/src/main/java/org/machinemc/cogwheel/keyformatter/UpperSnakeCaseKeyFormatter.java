package org.machinemc.cogwheel.keyformatter;

import java.util.Locale;

public class UpperSnakeCaseKeyFormatter extends SnakeCaseKeyFormatter {

    @Override
    public String format(String key) {
        return super.format(key).toUpperCase(Locale.ENGLISH);
    }

}
