package org.machinemc.cogwheel;

import java.lang.reflect.RecordComponent;
import java.util.stream.Stream;

/**
 * Interface for disassembling components of a record class.
 */
public interface RecordDisassembler {

    RecordDisassembler DEFAULT = new DefaultRecordDisassembler();

    /**
     * Disassembles the components of a record class and returns a stream of record components.
     *
     * @param recordClass The class of the record to disassemble.
     * @return A stream of RecordComponent objects representing the components of the record.
     */
    Stream<RecordComponent> disassemble(Class<? extends Record> recordClass);

}
