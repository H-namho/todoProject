package com.example.memorypractice.redis;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

import java.time.Duration;

@Repository
@RequiredArgsConstructor
public class TokenRedisRepositry {

    private final StringRedisTemplate redisTemplate;

    // 리프레시토큰 키값
    public String refreshKey(Long userId){
        return "refresh:"+userId;
    }

    // 블랙리스트 키값
    public String blackKey(String token){
        return "blackList:"+token;
    }

    // 블랙리스트 여부확인
    public boolean hasBlackKey(String token){
        return redisTemplate.hasKey(blackKey(token));
    }

    // 블랙리스트 저장
    public void saveBlackList(String token,long ttl){
        redisTemplate.opsForValue().set(blackKey(token),"logout",Duration.ofMillis(ttl));
    }

    // 리프레시토큰 저장
    public void saveRefreshToken(Long userId, String refreshToken, long ttl) {
        redisTemplate.opsForValue().set(refreshKey(userId),refreshToken, Duration.ofSeconds(ttl));
    }
    // 리프레시토큰 조회
    public String getRefreshToken(Long userId){
        return redisTemplate.opsForValue().get(refreshKey(userId));
    }
    // 리프레시토큰 제거
    public void removeRefresh(Long userId) {
        redisTemplate.delete(refreshKey(userId));
    }
}
