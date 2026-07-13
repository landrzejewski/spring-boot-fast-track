package pl.training.common.aop.cache;

import java.util.Arrays;

public final class SimpleKey {

    public static final SimpleKey EMPTY = new SimpleKey();

    private final Object[] params;
    private final int hashCode;

    public SimpleKey(Object... params) {
        this.params = params.clone();
        this.hashCode = Arrays.deepHashCode(this.params);
    }

    @Override
    public boolean equals(Object other) {
        return this == other
                || (other instanceof SimpleKey key && Arrays.deepEquals(params, key.params));
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