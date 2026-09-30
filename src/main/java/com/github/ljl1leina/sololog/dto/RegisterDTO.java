package com.github.ljl1leina.sololog.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class RegisterDTO {
    @NotBlank(message = "用户名不能为空")
    @Size(min = 3, max = 50, message = "用户名长度3~50")
    @Pattern(regexp = "^[a-zA-Z0-9_]+$", message = "用户名只能是字母数字下划线")
    private String username;

    @NotBlank(message = "密码不能为空")
    @Size(min = 6, max = 50, message = "密码长度6~50")
    private String password;

    @Size(max = 50, message = "昵称最长50字")
    private String nickname;   // 可空，为空时默认等于username
}
