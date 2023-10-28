package org.machinemc.cogwheel;

import org.machinemc.cogwheel.config.ConfigAdapter;

import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class SingletonDataVisitor implements DataVisitor {

    private final Deque<Object> stack;
    private final int flags;
    private String currentKey;

    public SingletonDataVisitor(Object object) {
        this();
        stack.push(object);
    }

    public SingletonDataVisitor() {
        this(new LinkedList<>(), null, FULL_ACCESS);
    }

    private SingletonDataVisitor(Deque<Object> stack, String key, int flags) {
        this.stack = stack;
        this.currentKey = key;
        this.flags = flags;
    }

    @Override
    public DataVisitor visit(String key) {
        checkIsRoot();
        currentKey = key;
        return this;
    }

    @Override
    public DataVisitor visitSection(String key) {
        return visit(key).enterSection();
    }

    @Override
    public SingletonDataVisitor enterSection() {
        Map<String, Object> inner = new LinkedHashMap<>();
        writeMap(inner);
        stack.push(inner);
        currentKey = null;
        return this;
    }

    @Override
    public DataVisitor exitSection() {
        checkIsRoot();
        stack.pop();
        currentKey = null;
        return this;
    }

    @Override
    public DataVisitor visitRoot() {
        stack.clear();
        currentKey = null;
        return this;
    }

    @Override
    public boolean isPresent() {
        return getCurrentObject() != null;
    }

    @Override
    public Optional<Number> readNumber() {
        return read(Number.class);
    }

    @Override
    public Optional<String> readString() {
        return read(String.class);
    }

    @Override
    public Optional<Boolean> readBoolean() {
        return read(Boolean.class);
    }

    @Override
    public Optional<Object[]> readArray() {
        return read(Object[].class);
    }

    @Override
    public <T extends Collection<Object>> Optional<T> readCollection(Supplier<T> factory) {
        return readArray().map(array -> Arrays.stream(array).collect(Collectors.toCollection(factory)));
    }

    @Override
    public Optional<Map<String, Object>> readMap() {
        return read(Map.class);
    }

    @Override
    public Optional<ConfigAdapter<?>> readConfig() {
        return read(ConfigAdapter.class);
    }

    @Override
    public DataVisitor writeNull() {
        return write(null);
    }

    @Override
    public DataVisitor writeNumber(Number number) {
        return write(number);
    }

    @Override
    public DataVisitor writeString(String string) {
        return write(string);
    }

    @Override
    public DataVisitor writeBoolean(Boolean bool) {
        return write(bool);
    }

    @Override
    public DataVisitor writeArray(Object[] array) {
        return write(array);
    }

    @Override
    public DataVisitor writeCollection(Collection<?> collection) {
        return writeArray(collection.toArray());
    }

    @Override
    public DataVisitor writeMap(Map<String, Object> map) {
        return write(map);
    }

    @Override
    public DataVisitor writeConfig(ConfigAdapter<?> configAdapter) {
        return write(configAdapter);
    }

    @Override
    public String getCurrentKey() {
        return currentKey;
    }

    @Override
    public int getFlags() {
        return flags;
    }

    @Override
    public SingletonDataVisitor withFlags(int newFlags) {
        return new SingletonDataVisitor(stack, currentKey, newFlags);
    }

    public Object get() {
        return stack.peekLast();
    }

    private Object getCurrentObject() {
        return stack.peek();
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    private <T, R extends T> Optional<R> read(Class<T> type) {
        checkReadAccess();
        Object current = getCurrentObject();
        Object object;

        if (stack.size() > 1 && current instanceof Map map) object = map.get(currentKey);
        else if (stack.size() > 1 && current instanceof ConfigAdapter<?> adapter)
            object = adapter.getPrimitive(currentKey).orElse(null);
        else object = current;

        if (object == null) return Optional.empty();
        return (Optional<R>) Optional.of(object)
                .filter(type::isInstance)
                .map(type::cast);
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private DataVisitor write(Object object) {
        checkWriteAccess();
        if (stack.size() > 1 && getCurrentObject() instanceof Map map) {
            map.put(currentKey, object);
        } else {
            stack.pollFirst();
            stack.push(object);
        }
        return this;
    }

    private void checkReadAccess() {
        if (!canRead()) throw new UnsupportedOperationException();
    }

    private void checkWriteAccess() {
        if (!canWrite()) throw new UnsupportedOperationException();
    }

    private void checkIsRoot() {
        if (stack.size() == 1)
            throw new IllegalStateException("Cannot move out of the root key");
    }

}
