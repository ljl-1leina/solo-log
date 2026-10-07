package com.github.ljl1leina.sololog.service;

import com.github.ljl1leina.sololog.dto.ProfileUpdateDTO;
import com.github.ljl1leina.sololog.vo.UserVO;

public interface UserService {

    /** 查当前登录用户的资料（"是谁"由 token 决定） */
    UserVO getMyProfile();

    /** 修改当前登录用户的资料，null 字段不动（部分更新） */
    void updateMyProfile(ProfileUpdateDTO dto);
}
