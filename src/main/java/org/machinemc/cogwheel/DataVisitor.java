package org.machinemc.cogwheel;

import org.jetbrains.annotations.Contract;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;

public interface DataVisitor {

    int READ_ACCESS = 0x01, WRITE_ACCESS = 0x02, FULL_ACCESS = READ_ACCESS | WRITE_ACCESS;

    @Contract("_ -> this")
    DataVisitor visit(String key);

    @Contract("_ -> this")
    DataVisitor visitSection(String key);

    /**
     * @return this
     */
    DataVisitor enterSection();

    /**
     * @return this
     */
    DataVisitor exitSection();

    /**
     * @return this
     */
    DataVisitor visitRoot();

    boolean isPresent();

    Optional<Number> readNumber();

    Optional<String> readString();

    Optional<Boolean> readBoolean();

    Optional<Object[]> readArray();

    <T extends Collection<Object>> Optional<T> readCollection(Supplier<T> factory);

    Optional<Map<String, Object>> readMap();

    @SuppressWarnings({"unchecked", "rawtypes"})
    default Optional<Object> readObject() {
        if (!isPresent()) return Optional.empty();
        return ((Optional) readNumber())
                .or(this::readString)
                .or(this::readBoolean)
                .or(this::readArray)
                .or(this::readMap);
    }

    /**
     * @return this
     */
    DataVisitor writeNull();

    @Contract("_ -> this")
    DataVisitor writeNumber(Number number);

    @Contract("_ -> this")
    DataVisitor writeString(String string);

    @Contract("_ -> this")
    DataVisitor writeBoolean(Boolean bool);

    @Contract("_ -> this")
    DataVisitor writeArray(Object[] array);

    @Contract("_ -> this")
    DataVisitor writeCollection(Collection<?> collection);

    @Contract("_ -> this")
    DataVisitor writeMap(Map<String, Object> map);

    String getCurrentKey();

    int getFlags();

    DataVisitor withFlags(int newFlags);

    default boolean canRead() {
        return (getFlags() & READ_ACCESS) != 0;
    }

    default boolean canWrite() {
        return (getFlags() & WRITE_ACCESS) != 0;
    }

}
