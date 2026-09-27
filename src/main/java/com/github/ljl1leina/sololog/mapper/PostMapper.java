package com.github.ljl1leina.sololog.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.github.ljl1leina.sololog.entity.Post;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface PostMapper extends BaseMapper<Post> {
    // 空的。继承了 BaseMapper，单表的增删改查、分页全都白送
}
