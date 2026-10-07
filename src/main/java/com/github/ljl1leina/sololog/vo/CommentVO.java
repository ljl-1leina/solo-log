package com.github.ljl1leina.sololog.vo;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)   // 顶级评论不吐 parentId/replyToUserId 等回复专属字段
public class CommentVO {
    private Long id;
    private String content;
    private Long userId;
    private String nickname;
    private String avatar;
    private Long parentId;          // 回复才有
    private Long replyToUserId;     // 回复才有
    private String replyToNickname; // 回复才有
    private Boolean deleted;        // status=1 的楼：content 为 null，前端凭此字段显示"该评论已删除"
    private LocalDateTime createdAt;
}
