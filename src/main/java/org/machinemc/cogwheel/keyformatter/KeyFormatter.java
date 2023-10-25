package org.machinemc.cogwheel.keyformatter;

import java.util.regex.Pattern;

public interface KeyFormatter {

    Pattern CAMEL_CASE_PATTERN = Pattern.compile("([a-z])([A-Z])");

    String format(String key);

}
