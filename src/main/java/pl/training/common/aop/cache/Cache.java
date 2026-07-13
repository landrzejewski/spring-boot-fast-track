package pl.training.common.aop.cache;

import java.util.Optional;

public interface Cache<K, V> {

    void put(K key, V value);

    Optional<V> get(K key);

}