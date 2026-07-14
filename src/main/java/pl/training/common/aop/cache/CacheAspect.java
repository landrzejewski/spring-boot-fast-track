package pl.training.common.aop.cache;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.logging.Logger;

@Aspect
@Component
public class CacheAspect {

    private static final Logger LOGGER = Logger.getLogger(CacheAspect.class.getName());

    private final Map<String, Cache<Object, Object>> caches = new ConcurrentHashMap<>();
    private Function<Integer, Cache<Object, Object>> cacheSupplier = LinkedHashMapCache::new;
    private KeyGenerator keyGenerator = new CacheKeyGenerator();

    @Around("@annotation(fromCache)")
    public Object read(ProceedingJoinPoint joinPoint, Cacheable fromCache) throws Throwable {
        var cacheName = fromCache.value();
        var capacity = fromCache.size();
        var cache = caches.computeIfAbsent(cacheName, name -> cacheSupplier.apply(capacity));
        var key = keyGenerator.generate(joinPoint.getSignature(), joinPoint.getArgs());
        var cached = cache.get(key);
        if (cached.isPresent()) {
            LOGGER.info("Reading from cache");
            return cached.get();
        }
        var result = joinPoint.proceed();
        cache.put(key, result);
        return result;
    }

    public void setCacheSupplier(Function<Integer, Cache<Object, Object>> cacheSupplier) {
        this.cacheSupplier = cacheSupplier;
    }

    public void setKeyGenerator(KeyGenerator keyGenerator) {
        this.keyGenerator = keyGenerator;
    }

}