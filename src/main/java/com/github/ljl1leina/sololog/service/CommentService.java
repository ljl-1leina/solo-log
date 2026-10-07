package com.github.ljl1leina.sololog.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.github.ljl1leina.sololog.dto.CommentCreateDTO;
import com.github.ljl1leina.sololog.vo.CommentFloorVO;

public interface CommentService {

    /** 发表评论或回复，返回评论id */
    Long create(Long postId, CommentCreateDTO dto);

    /** 评论列表：按"楼"分页，楼内带出全部回复 */
    IPage<CommentFloorVO> list(Long postId, int page, int size);

    /** 删除评论（自己的 或 AUTHOR删任何人的），软删 */
    void delete(Long commentId);
}
