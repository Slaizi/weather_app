package ru.bogachev.weatherApp.service.impl;

import lombok.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import ru.bogachev.weatherApp.service.TokenStorageService;

import java.util.concurrent.TimeUnit;

@Service
public class TokenStorageServiceImpl implements TokenStorageService {

    private final RedisTemplate<String, Object> redisTemplate;
    private final String keyTokenStorageTmp;
    private final Long refreshDuration;


    public TokenStorageServiceImpl(
            @Autowired final RedisTemplate<String, Object> redisTemplate,
            @Value("${token.jwt.secret.refresh.storage.tmp}")
            @NonNull final String keyTokenStorageTmp,
            @Value("${token.jwt.secret.refresh.duration}")
            @NonNull final Long refreshDuration) {
        this.redisTemplate = redisTemplate;
        this.keyTokenStorageTmp = keyTokenStorageTmp;
        this.refreshDuration = refreshDuration;
    }

    @Override
    public void save(
            @NonNull final Long userId,
            @NonNull final String token) {
        String key = String.format(keyTokenStorageTmp, userId);
        redisTemplate.opsForValue()
                .set(key, token, refreshDuration, TimeUnit.DAYS);
    }

    @Override
    public String get(
            @NonNull final Long userId) {
        String key = String.format(keyTokenStorageTmp, userId);
        return String.valueOf(redisTemplate.opsForValue().get(key));
    }

    @Override
    public void delete(
            @NonNull final Long userId) {
        String key = String.format(keyTokenStorageTmp, userId);
        redisTemplate.delete(key);
    }
}
