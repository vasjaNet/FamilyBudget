package org.s3m.userservice.config;

import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class TwoLevelCacheManager implements CacheManager {

    private final CacheManager caffeineCacheManager;
    private final CacheManager redisCacheManager;
    private final Map<String, Cache> cacheMap = new ConcurrentHashMap<>();

    public TwoLevelCacheManager(CacheManager caffeineCacheManager,
                                 CacheManager redisCacheManager) {
        this.caffeineCacheManager = caffeineCacheManager;
        this.redisCacheManager = redisCacheManager;
    }

    @Override
    public Cache getCache(@NonNull String name) {
        return cacheMap.computeIfAbsent(name, n ->
            new TwoLevelCache(
                n,
                caffeineCacheManager.getCache(n),
                redisCacheManager.getCache(n)
            )
        );
    }

    @Override
    public Collection<String> getCacheNames() {
        return cacheMap.keySet();
    }
}