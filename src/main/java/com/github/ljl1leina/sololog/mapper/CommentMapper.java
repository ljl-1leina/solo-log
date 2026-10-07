package com.github.ljl1leina.sololog.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.github.ljl1leina.sololog.entity.Comment;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface CommentMapper extends BaseMapper<Comment> {
}
