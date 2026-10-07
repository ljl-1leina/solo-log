package com.github.ljl1leina.sololog.service.impl;

import com.github.ljl1leina.sololog.common.BusinessException;
import com.github.ljl1leina.sololog.common.UserContext;
import com.github.ljl1leina.sololog.config.AvatarProperties;
import com.github.ljl1leina.sololog.dto.ProfileUpdateDTO;
import com.github.ljl1leina.sololog.entity.User;
import com.github.ljl1leina.sololog.mapper.UserMapper;
import com.github.ljl1leina.sololog.service.UserService;
import com.github.ljl1leina.sololog.vo.UserVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserMapper userMapper;
    private final AvatarProperties avatarProperties;

    @Override
    public UserVO getMyProfile() {
        User user = userMapper.selectById(UserContext.currentUserId());
        if (user == null) {
            // 理论上到不了这：token 能过 JwtAuthFilter 说明用户存在。防御性兜底。
            throw new BusinessException("用户不存在");
        }
        UserVO vo = new UserVO();
        vo.setUserId(user.getId());
        vo.setUsername(user.getUsername());
        vo.setNickname(user.getNickname());
        vo.setAvatar(user.getAvatar());
        vo.setBio(user.getBio());
        vo.setRole(user.getRole());
        vo.setCreatedAt(user.getCreatedAt());
        return vo;   // 全程不碰 getPassword()，password 密文不出门
    }

    @Override
    public void updateMyProfile(ProfileUpdateDTO dto) {
        boolean hasNickname = dto.getNickname() != null;
        boolean hasBio = dto.getBio() != null;
        boolean hasAvatar = dto.getAvatar() != null;
        if (!hasNickname && !hasBio && !hasAvatar) {
            throw new BusinessException("无修改项：nickname、bio、avatar 至少传一个");
        }

        User user = new User();   // 只 set 要改的字段，其余保持 null
        user.setId(UserContext.currentUserId());
        if (hasNickname) {
            String nickname = dto.getNickname().trim();
            if (nickname.isEmpty()) {
                throw new BusinessException("昵称不能为纯空白");
            }
            user.setNickname(nickname);
        }
        if (hasBio) {
            String bio = dto.getBio().trim();
            if (bio.isEmpty()) {
                throw new BusinessException("简介不能为纯空白");
            }
            user.setBio(bio);
        }
        if (hasAvatar) {
            String avatar = dto.getAvatar().trim();
            validateAvatar(avatar);
            user.setAvatar(avatar);
        }
        // MyBatis-Plus updateById 默认跳过 null 字段 —— 部分更新天然成立，
        // SQL 只 SET 这次真正要改的列
        userMapper.updateById(user);
    }

    /** 校验 avatar 确实是头像库里的合法路径，防止前端渲染出破图 */
    private void validateAvatar(String avatar) {
        String prefix = avatarProperties.getPrefix();
        String suffix = "." + avatarProperties.getExtension();
        if (!avatar.startsWith(prefix) || !avatar.endsWith(suffix)) {
            throw new BusinessException("头像路径不合法");
        }
        String numPart = avatar.substring(prefix.length(), avatar.length() - suffix.length());
        int num;
        try {
            num = Integer.parseInt(numPart);
        } catch (NumberFormatException e) {
            throw new BusinessException("头像路径不合法");
        }
        if (num < 1 || num > avatarProperties.getCount()) {
            throw new BusinessException("头像不存在");
        }
    }
}
