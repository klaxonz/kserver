package com.klaxon.kserver.module.spider.proxy;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;
import java.util.Objects;
import java.util.Set;

@Slf4j
@Component
public class ProxyCacheManager {

    private static final String PROXY_KEY = "proxy::used";
    private static final String PROXY_HIGH_KEY = "proxy::high";
    private static final String PROXY_FAILED_KEY = "proxy::failed";
    private static final String PROXY_COUNTRY_KEY = "proxy::country";
    private static final Integer DEFAULT_SCORE = 5;
    private static final Integer MIN_SCORE = 0;
    private static final Integer HIGH_SCORE = 10;
    private static final Integer MAX_SCORE = 100;

    @Resource
    private RedisTemplate<String, String> redisTemplate;

    public void addProxy(String proxy) {
        addProxy(proxy, DEFAULT_SCORE);
    }

    public void addProxy(String proxy, int score) {
        Double proxyScore = redisTemplate.opsForZSet().score(PROXY_KEY, proxy);
        if (Objects.isNull(proxyScore)) {
            redisTemplate.opsForZSet().addIfAbsent(PROXY_KEY, proxy, score);
        } else {
            log.info("proxy: {} exist", proxy);
        }
    }

    public void removeProxy(String proxy) {
        redisTemplate.opsForZSet().remove(PROXY_KEY, proxy);
    }

    public void setScore(String proxy, int value) {
        redisTemplate.opsForZSet().add(PROXY_KEY, proxy, value);
    }

    public int getScore(String proxy) {
        Double score = redisTemplate.opsForZSet().score(PROXY_KEY, proxy);
        if (Objects.nonNull(score)) {
            return score.intValue();
        } else {
            return 0;
        }
    }

    public void incrScore(String proxy) {
        if (getScore(proxy) < MAX_SCORE) {
            redisTemplate.opsForZSet().incrementScore(PROXY_KEY, proxy, 1);
        }
    }

    public void decrScore(String proxy) {
        if (getScore(proxy) > MIN_SCORE) {
            redisTemplate.opsForZSet().incrementScore(PROXY_KEY, proxy, -1);
        }
    }

    public Set<String> getProxiesByScoreRange(int min, int max) {
        return redisTemplate.opsForZSet().rangeByScore(PROXY_KEY, min, max);
    }

    public void setFailed(String proxy) {
        Object value = redisTemplate.opsForHash().get(PROXY_FAILED_KEY, proxy);
        if (Objects.isNull(value)) {
            redisTemplate.opsForHash().put(PROXY_FAILED_KEY, proxy, 1);
        } else {
            redisTemplate.opsForHash().increment(PROXY_FAILED_KEY, proxy, 1);
        }
    }

    public void setFailed(String proxy, int value) {
        redisTemplate.opsForHash().put(PROXY_FAILED_KEY, proxy, value);
    }

    public void removeFailed(String proxy) {
        redisTemplate.opsForHash().delete(PROXY_FAILED_KEY, proxy);
    }

    public int getFailed(String proxy) {
        Object value = redisTemplate.opsForHash().get(PROXY_FAILED_KEY, proxy);
        if (Objects.isNull(value)) {
            return 0;
        } else {
            return (int) value;
        }
    }

    public void setCountry(String proxy, String country) {
        redisTemplate.opsForHash().put(PROXY_COUNTRY_KEY, proxy, country);
    }


    public String getCountry(String proxy) {
        Object o = redisTemplate.opsForHash().get(PROXY_COUNTRY_KEY, proxy);
        if (Objects.nonNull(o)) {
            return (String) o;
        }
        return null;
    }

    public void removeCountry(String proxy) {
        redisTemplate.opsForHash().delete(PROXY_COUNTRY_KEY, proxy);
    }

    public void setHighProxy(String proxy) {
        int score = getScore(proxy);
        if (score > HIGH_SCORE) {
            redisTemplate.opsForZSet().add(PROXY_HIGH_KEY, proxy, score);
        }
    }

    public void removeHighProxy(String proxy) {
        redisTemplate.opsForZSet().remove(PROXY_HIGH_KEY, proxy);
    }

    public String getRandomProxy() {
        List<String> proxies = redisTemplate.opsForZSet().randomMembers(PROXY_HIGH_KEY, 1);
        if (proxies == null || proxies.isEmpty()) {
            return null;
        }
        return proxies.get(0);
    }
}
