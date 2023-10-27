package org.machinemc.cogwheel;

import java.lang.reflect.Field;
import java.util.stream.Stream;

public interface FieldExtractor {

    FieldExtractor DEFAULT = new DefaultFieldExtractor();

    Stream<Field> extract(Class<?> type);

}
