package com.github.ljl1leina.sololog.vo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class PostDetailVO {
    private Long id;
    private String title;
    private String summary;
    private String content;
    private Integer status;
    private Integer viewCount;
    private Integer likeCount;
    private Integer commentCount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
