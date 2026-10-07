package com.github.ljl1leina.sololog.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 头像库配置：第一版是"预置头像 + 数字范围"，范围放配置文件，
 * 以后加头像只需要改 yaml + 扔图片，不用动代码。
 */
@Data
@Component
@ConfigurationProperties(prefix = "avatar")
public class AvatarProperties {

    /** 头像张数，对应 static/avatars/1.png ~ {count}.png */
    private int count = 24;

    /** 头像 URL 前缀（同时是静态资源访问路径） */
    private String prefix = "/avatars/";

    /** 图片后缀，第一版统一 png */
    private String extension = "png";
}
