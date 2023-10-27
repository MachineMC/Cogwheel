package org.machinemc.cogwheel.util.pair;

import java.util.Map;
import java.util.Objects;

class PairImpl<F, S> implements Pair<F, S> {

    static final int NULLABLE = 0x01;
    static final int MUTABLE = 0x02;

    private F first;
    private S second;
    private final int flags;

    PairImpl(F first, S second, int flags) {
        if (isNotNull()) {
            Objects.requireNonNull(first, "first");
            Objects.requireNonNull(second, "second");
        }
        this.first = first;
        this.second = second;
        this.flags = flags;
    }

    @Override
    public F first() {
        return first;
    }

    @Override
    public S second() {
        return second;
    }

    @Override
    public void first(F first) {
        if (isImmutable()) throw new UnsupportedOperationException();
        if (isNotNull()) Objects.requireNonNull(first, "first");
        this.first = first;
    }

    @Override
    public void second(S second) {
        if (isImmutable()) throw new UnsupportedOperationException();
        if (isNotNull()) Objects.requireNonNull(second, "second");
        this.second = second;
    }

    @Override
    public Map.Entry<F, S> asEntry() {
        return new Map.Entry<F, S>() {
            @Override
            public F getKey() {
                return first();
            }

            @Override
            public S getValue() {
                return second();
            }

            @Override
            public S setValue(S value) {
                S previous = getValue();
                second(value);
                return previous;
            }

            @Override
            public String toString() {
                return PairImpl.this + "(Entry)";
            }

            @Override
            public boolean equals(Object obj) {
                return PairImpl.this.equals(obj);
            }

            @Override
            public int hashCode() {
                return PairImpl.this.hashCode();
            }
        };
    }

    private boolean isNotNull() {
        return (flags & NULLABLE) == 0;
    }

    private boolean isImmutable() {
        return (flags & MUTABLE) == 0;
    }

    @Override
    public String toString() {
        return "Pair[first=%s, second=%s]".formatted(first, second);
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (!(object instanceof PairImpl<?, ?> pair)) return false;
        if (flags != pair.flags) return false;
        if (!Objects.equals(first, pair.first)) return false;
        return Objects.equals(second, pair.second);
    }

    @Override
    public int hashCode() {
        int result = first != null ? first.hashCode() : 0;
        result = 31 * result + (second != null ? second.hashCode() : 0);
        result = 31 * result + flags;
        return result;
    }

}
