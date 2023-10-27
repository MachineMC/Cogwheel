package org.machinemc.cogwheel.util.pair;

import org.jetbrains.annotations.Nullable;

import java.util.Map;

/**
 *
 * @param <F> first value
 * @param <S> second value
 */
public interface Pair<F, S> {

    F first();

    S second();

    void first(F f);

    void second(S s);

    Map.Entry<F, S> asEntry();

    static <F, S> Pair<F, S> of(F first, S second) {
        return new PairImpl<>(first, second, 0);
    }

    static <F, S> Pair<F, S> ofNullable(@Nullable F first, @Nullable S second) {
        return new PairImpl<>(first, second, PairImpl.NULLABLE);
    }

    static <F, S> Pair<F, S> mutable(F first, S second) {
        return new PairImpl<>(first, second, PairImpl.MUTABLE);
    }

    static <F, S> Pair<F, S> mutableNullable(@Nullable F first, @Nullable S second) {
        return new PairImpl<>(first, second, PairImpl.NULLABLE | PairImpl.MUTABLE);
    }

}
