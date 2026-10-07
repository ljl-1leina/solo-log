package com.github.ljl1leina.sololog.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CommentCreateDTO {

    @NotBlank(message = "评论内容不能为空")
    @Size(max = 1000, message = "评论内容不能超过1000字")
    private String content;

    /** 顶级评论不传或传0；楼内回复传楼的id */
    private Long parentId;

    /** 被回复人的userId（纯展示用），顶级评论不传 */
    private Long replyToUserId;
}
