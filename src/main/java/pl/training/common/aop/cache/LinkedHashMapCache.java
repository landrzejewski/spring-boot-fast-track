package pl.training.common.aop.cache;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class LinkedHashMapCache<K, V> implements Cache<K, V> {

    private final Map<K, V> cache;
    private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();

    public LinkedHashMapCache(int capacity) {
        cache = new LinkedHashMap<>(capacity, 1, true) {

            @Override
            protected boolean removeEldestEntry(Map.Entry<K, V> eldest) {
                return size() > capacity;
            }

        };

    }

    @Override
    public void put(K key, V value) {
        lock.writeLock().lock();
        cache.put(key, value);
        lock.writeLock().unlock();
    }

    @Override
    public Optional<V> get(K key) {
        lock.readLock().lock();
        var value = cache.get(key);
        lock.readLock().unlock();
        return Optional.ofNullable(value);
    }

}