package pl.training.common.aop;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

import static java.util.Arrays.stream;
import static java.util.stream.Collectors.joining;

@Aspect
@Component
public class CacheAspect {

    private final Map<String, Cache<String, Object>> caches = new ConcurrentHashMap<>();
    private Function<Integer, Cache<String, Object>> cacheSupplier = LinkedHashMapCache::new;

    @Around("@annotation(fromCache)")
    public Object read(ProceedingJoinPoint joinPoint, Cacheable fromCache) throws Throwable {
        var cacheName = fromCache.value();
        var capacity = fromCache.size();
        caches.putIfAbsent(cacheName, cacheSupplier.apply(capacity));
        var cache = caches.get(cacheName);
        var key = generateKey(joinPoint);
        var value = cache.get(key);
        if (value.isPresent()) {
            return value.get();
        }
        var result = joinPoint.proceed();
        cache.put(key, result);
        return result;
    }

    private String generateKey(ProceedingJoinPoint joinPoint) {
        return stream(joinPoint.getArgs())
                .map(Object::toString)
                .collect(joining());
    }

    public void setCacheSupplier(Function<Integer, Cache<String, Object>> cacheSupplier) {
        this.cacheSupplier = cacheSupplier;
    }

}