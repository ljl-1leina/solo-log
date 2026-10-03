package com.github.ljl1leina.sololog.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.github.ljl1leina.sololog.common.BusinessException;
import com.github.ljl1leina.sololog.common.JwtUtil;
import com.github.ljl1leina.sololog.dto.LoginDTO;
import com.github.ljl1leina.sololog.dto.RegisterDTO;
import com.github.ljl1leina.sololog.entity.User;
import com.github.ljl1leina.sololog.mapper.UserMapper;
import com.github.ljl1leina.sololog.service.AuthService;
import com.github.ljl1leina.sololog.vo.LoginVO;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    @Override
    public void register(RegisterDTO dto) {
        // 查重：用户名唯一索引在数据库兜底，这里先查一遍给用户友好提示
        Long count = userMapper.selectCount(
                new LambdaQueryWrapper<User>().eq(User::getUsername, dto.getUsername()));
        if (count > 0) {
            throw new BusinessException("用户名已被占用");
        }
        User user = new User();
        user.setUsername(dto.getUsername());
        user.setNickname(dto.getNickname() == null || dto.getNickname().isBlank()
                ? dto.getUsername() : dto.getNickname());
        // 核心安全点：密码绝不存明文
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        user.setRole("USER");   // 注册永远只能创建USER，没有后门
        userMapper.insert(user);
    }

    @Override
    public LoginVO login(LoginDTO dto) {
        User user = userMapper.selectOne(
                new LambdaQueryWrapper<User>().eq(User::getUsername, dto.getUsername()));
        // 用户不存在和密码错误给同一个提示，不告诉对方"这个用户名存在"
        if (user == null || !passwordEncoder.matches(dto.getPassword(), user.getPassword())) {
            throw new BusinessException("用户名或密码错误");
        }
        LoginVO vo = new LoginVO();
        vo.setToken(jwtUtil.generate(user.getId(), user.getUsername(), user.getRole()));
        vo.setUserId(user.getId());
        vo.setUsername(user.getUsername());
        vo.setNickname(user.getNickname());
        vo.setRole(user.getRole());
        return vo;
    }
}
