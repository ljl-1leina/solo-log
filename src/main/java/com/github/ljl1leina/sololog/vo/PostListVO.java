package com.github.ljl1leina.sololog.vo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class PostListVO {
    private Long id;
    private String title;
    private String summary;
    private Integer viewCount;
    private Integer likeCount;
    private Integer commentCount;
    private LocalDateTime createdAt;
}
