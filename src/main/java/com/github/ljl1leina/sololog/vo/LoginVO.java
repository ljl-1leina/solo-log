package com.github.ljl1leina.sololog.vo;

import lombok.Data;

@Data
public class LoginVO {
    private String token;
    private Long userId;
    private String username;
    private String nickname;
    private String role;
}
