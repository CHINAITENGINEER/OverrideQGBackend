package com.repair.service.impl;

import com.repair.entity.User;
import com.repair.mapper.UserMapper;
import com.repair.service.UserService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * 用户服务实现类（Spring 管理，Mapper 注入；密码与 Spring Security 共用 BCrypt）
 */
@Service
public class UserServiceImpl implements UserService {

    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    public UserServiceImpl(UserMapper userMapper, PasswordEncoder passwordEncoder) {
        this.userMapper = userMapper;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public boolean register(String account, String password, int role) {
        User existing = userMapper.selectByAccount(account);
        if (existing != null) {
            return false;
        }

        User user = new User();
        user.setAccount(account);
        user.setPassword(passwordEncoder.encode(password));
        user.setRole(role);

        userMapper.insert(user);
        return true;
    }

    @Override
    public User login(String account, String password) {
        User user = userMapper.selectByAccount(account);
        if (user == null) {
            return null;
        }
        if (!passwordEncoder.matches(password, user.getPassword())) {
            return null;
        }
        return user;
    }

    @Override
    public boolean changePassword(Long userId, String oldPassword, String newPassword) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            return false;
        }
        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            return false;
        }
        user.setPassword(passwordEncoder.encode(newPassword));
        userMapper.updateById(user);
        return true;
    }

    @Override
    public void bindDorm(Long userId, String building, String roomNo) {
        User user = new User();
        user.setId(userId);
        user.setBuilding(building);
        user.setRoomNo(roomNo);
        userMapper.updateById(user);
    }

    @Override
    public User getById(Long userId) {
        return userMapper.selectById(userId);
    }
}
