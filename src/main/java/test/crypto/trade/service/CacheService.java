package test.crypto.trade.service;

import com.github.benmanes.caffeine.cache.Cache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CacheService {

    @Autowired
    private Cache<String, Object> cache;

    public <T> T get(String key, Class<T> type) {
        Object value = cache.getIfPresent(key);
        if (value != null && type.isInstance(value)) {
            return type.cast(value);
        }
        return null;
    }

    public void put(String key, Object value) {
        cache.put(key, value);
    }

    public void clear(String key) {
        cache.invalidate(key);
    }
}
