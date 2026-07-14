package pl.training.common.aop.cache;

import java.util.Arrays;

public final class CacheKey {

    public static final CacheKey EMPTY = new CacheKey();

    private final Object[] params;
    private final int hashCode;

    public CacheKey(Object... params) {
        this.params = params.clone();
        this.hashCode = Arrays.deepHashCode(this.params);
    }

    @Override
    public boolean equals(Object other) {
        return this == other
                || (other instanceof CacheKey key && Arrays.deepEquals(params, key.params));
    }

    @Override
    public int hashCode() {
        return hashCode;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " " + Arrays.deepToString(params);
    }

}