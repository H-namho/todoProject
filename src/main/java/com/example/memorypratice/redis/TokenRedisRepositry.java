package com.example.memorypratice.redis;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

import java.time.Duration;

@Repository
@RequiredArgsConstructor
public class TokenRedisRepositry {

    private final StringRedisTemplate redisTemplate;

    public String refreshKey(Long userId){
        return "refresh:"+userId;
    }

    public String blackKey(String token){
        return "blackList:"+token;
    }

    public boolean hasBlackKey(String token){
        return redisTemplate.hasKey(blackKey(token));
    }

    public void saveBlackList(String token,long ttl){
        redisTemplate.opsForValue().set(blackKey(token),"logout",Duration.ofMillis(ttl));
    }

    public void saveRefreshToken(Long userId, String refreshToken, long ttl) {
        redisTemplate.opsForValue().set(refreshKey(userId),refreshToken, Duration.ofSeconds(ttl));
    }
    public String getRefreshToken(Long userId){
        return redisTemplate.opsForValue().get(refreshKey(userId));
    }

    public void removeRefresh(Long userId) {
        redisTemplate.delete(refreshKey(userId));
    }
}
