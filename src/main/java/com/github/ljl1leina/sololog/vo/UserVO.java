package com.github.ljl1leina.sololog.vo;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 用户资料展示对象。
 * 第一红线：永远不包含 password 字段 —— 密文也不该出门。
 */
@Data
public class UserVO {
    private Long userId;
    private String username;
    private String nickname;
    private String avatar;
    private String bio;
    private String role;
    private LocalDateTime createdAt;
}
