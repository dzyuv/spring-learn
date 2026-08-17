package com.dzy.userservice8092.service.impl;

import com.dzy.common.entity.User;
import com.dzy.common.exception.BusinessException;
import com.dzy.userservice8092.dto.ChangePasswordRequest;
import com.dzy.userservice8092.dto.LoginRequest;
import com.dzy.userservice8092.dto.RegisterRequest;
import com.dzy.userservice8092.dto.TokenPair;
import com.dzy.userservice8092.mapper.UserMapper;
import com.dzy.userservice8092.service.TokenService;
import com.dzy.userservice8092.service.UserService;
import com.dzy.userservice8092.util.UserServiceJwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private UserServiceJwtUtil jwtUtil;

    @Autowired
    private TokenService tokenService;

    @Override
    public TokenPair login(LoginRequest request) {
        User user = userMapper.selectByUsername(request.getUsername());
        if (user == null || user.getStatus() == 0 || !BCrypt.checkpw(request.getPassword(), user.getPassword())) {
            throw new BusinessException("用户名或密码错误");
        }

        String accessToken = jwtUtil.generateAccessToken(user.getId(), user.getUsername(), user.getRole());
        String refreshToken = tokenService.generateRefreshToken();
        tokenService.storeRefreshToken(refreshToken, user.getId(), 604800000); // 7天

        return new TokenPair(accessToken, refreshToken);
    }

    @Override
    public User register(RegisterRequest request) {

        // 1. 检查手机号唯一性
        if (userMapper.countByPhone(request.getPhone()) > 0) {
            throw new BusinessException("手机号已注册");
        }

        // 2. BCrypt 加密密码
        String encodedPassword = BCrypt.hashpw(request.getPassword(), BCrypt.gensalt());

        // 3. 构建 User 对象（设置默认值）
        User user = new User();
        user.setUsername(request.getPhone());
        user.setPassword(encodedPassword);
        user.setPhone(request.getPhone());
        user.setRole(request.getRole() != null ? request.getRole() : "CUSTOMER");
        user.setStatus(1);

        // 4. 插入数据库
        userMapper.insert(user);

        // 5. 返回用户信息（隐藏密码）
        user.setPassword(null);
        return user;
    }

    @Override
    public User getCurrentUser(Long userId) {
        // 1. 查询用户
        User user = userMapper.selectById(userId);

        if (user == null || user.getStatus()== 0) {
            throw new BusinessException("用户异常");
        }

        user.setPassword(null);

        return user;
    }

    @Override
    public void changePassword(Long userId, ChangePasswordRequest request) {
        User user = userMapper.selectById(userId);
        if (user == null || user.getStatus() == 0) {
            throw new BusinessException("账户异常");
        }
        if(!BCrypt.checkpw(request.getOldPassword(),user.getPassword())){
            throw new BusinessException("密码错误");
        }
        if (BCrypt.checkpw(request.getNewPassword(), user.getPassword())) {
            throw new BusinessException("新密码不能与旧密码相同");
        }
        String newHash=BCrypt.hashpw(request.getNewPassword(),BCrypt.gensalt());
        userMapper.updatePassword(userId, newHash);
    }

}
