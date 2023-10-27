package org.machinemc.cogwheel;

import java.lang.reflect.RecordComponent;
import java.util.Arrays;
import java.util.stream.Stream;

class DefaultRecordDisassembler implements RecordDisassembler {

    @Override
    public Stream<RecordComponent> disassemble(Class<? extends Record> recordClass) {
        return Arrays.stream(recordClass.getRecordComponents());
    }

}
