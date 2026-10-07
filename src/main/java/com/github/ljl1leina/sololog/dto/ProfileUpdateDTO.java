package com.github.ljl1leina.sololog.dto;

import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 修改个人资料：三个字段都是可选项，null 表示"不改这一项"（部分更新）。
 * 所以只加长度校验，不加 @NotBlank —— 加了就变成必传了。
 * "传了但全是空白"的情况由业务层 trim 后拒绝（DTO 校验拦不住 " " 这种长度>=1的值）。
 */
@Data
public class ProfileUpdateDTO {

    @Size(max = 50, message = "昵称不能超过50字")
    private String nickname;

    @Size(max = 200, message = "简介不能超过200字")
    private String bio;

    @Size(max = 255, message = "头像路径过长")
    private String avatar;
}
