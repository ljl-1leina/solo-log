package com.github.ljl1leina.sololog.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.github.ljl1leina.sololog.entity.User;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper extends BaseMapper<User> {
}
