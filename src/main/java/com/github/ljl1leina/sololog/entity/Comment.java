package com.github.ljl1leina.sololog.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("comment")
public class Comment {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long postId;
    private Long userId;
    private String content;
    private Long parentId;        // 0=顶级评论（楼），非0=挂在某楼下的回复
    private Long replyToUserId;    // 0=无被回复人，纯展示用（前端显示@某人）
    private Integer status;        // 0正常 1已删除（软删）
    private LocalDateTime createdAt;
}
