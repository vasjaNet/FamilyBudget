package org.s3m.userservice.config;

import org.springframework.cache.Cache;

import java.util.concurrent.Callable;

public class TwoLevelCache implements Cache {

    private final String name;
    private final Cache l1; // Caffeine
    private final Cache l2; // Redis

    public TwoLevelCache(String name, Cache l1, Cache l2) {
        this.name = name;
        this.l1 = l1;
        this.l2 = l2;
    }

    @Override
    public String getName() { return name; }

    @Override
    public Object getNativeCache() { return this; }

    @Override
    public ValueWrapper get(Object key) {
        // Check L1 first
        ValueWrapper val = l1.get(key);
        if (val != null) return val;

        // Fallback to L2
        val = l2.get(key);
        if (val != null) {
            l1.put(key, val.get()); // Populate L1
        }
        return val;
    }

    @Override
    public <T> T get(Object key, Class<T> type) {
        T val = l1.get(key, type);
        if (val != null) return val;

        val = l2.get(key, type);
        if (val != null) l1.put(key, val);
        return val;
    }

    @Override
    public <T> T get(Object key, Callable<T> valueLoader) {
        T val = l1.get(key, (Class<T>) null);
        if (val != null) return val;

        try {
            val = l2.get(key, valueLoader);
            if (val != null) l1.put(key, val);
            return val;
        } catch (Exception e) {
            throw new ValueRetrievalException(key, valueLoader, e);
        }
    }

    @Override
    public void put(Object key, Object value) {
        l1.put(key, value);
        l2.put(key, value);
    }

    @Override
    public void evict(Object key) {
        l1.evict(key);
        l2.evict(key);
    }

    @Override
    public void clear() {
        l1.clear();
        l2.clear();
    }
}