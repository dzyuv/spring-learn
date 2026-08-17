package com.dzy.userservice8092.service;

import com.dzy.common.entity.User;
import com.dzy.common.exception.BusinessException;
import com.dzy.userservice8092.dto.TokenPair;
import com.dzy.userservice8092.mapper.UserMapper;
import com.dzy.userservice8092.util.UserServiceJwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
public class TokenService {

    @Autowired
    private RedisTemplate<String, String> redisTemplate;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private UserServiceJwtUtil jwtUtil;

    // 生成 Refresh Token（随机字符串）
    public String generateRefreshToken() {
        SecureRandom random = new SecureRandom();
        return UUID.randomUUID().toString() + "-" + random.nextLong();
    }

    // 存储 Refresh Token 到 Redis
    public void storeRefreshToken(String refreshToken, Long userId, long expirationMs) {
        String key = "refresh:" + refreshToken;
        redisTemplate.opsForValue().set(key, userId.toString(), expirationMs, TimeUnit.MILLISECONDS);
    }

    // 刷新令牌：验证 Refresh Token，生成新 Token 对，旧 Refresh Token 作废
    public TokenPair refreshTokens(String oldRefreshToken) {
        String key = "refresh:" + oldRefreshToken;
        String userIdStr = redisTemplate.opsForValue().getAndDelete(key);
        if (userIdStr == null) {
            throw new BusinessException("Refresh Token 无效或已过期");
        }
        Long userId = Long.valueOf(userIdStr);
        User user = userMapper.selectById(userId);
        if (user == null || user.getStatus() == 0) {
            throw new BusinessException("用户状态异常");
        }

        String newAccessToken = jwtUtil.generateAccessToken(userId, user.getUsername(), user.getRole());
        String newRefreshToken = generateRefreshToken();
        storeRefreshToken(newRefreshToken, userId, 604800000); // 7天

        return new TokenPair(newAccessToken, newRefreshToken);
    }

    // 删除 Refresh Token（退出登录时）
    public void removeRefreshToken(String refreshToken) {
        redisTemplate.delete("refresh:" + refreshToken);
    }

}