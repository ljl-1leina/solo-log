package com.github.ljl1leina.sololog.vo;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * 一楼 = 一条顶级评论 + 楼内全部回复。
 * 继承 CommentVO 让 JSON 平铺（和文章/点赞接口的响应示例结构一致）。
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class CommentFloorVO extends CommentVO {
    private List<CommentVO> replies;
}
