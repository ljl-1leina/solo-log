package com.github.ljl1leina.sololog.service.impl;

import com.github.ljl1leina.sololog.dto.PostSaveDTO;
import com.github.ljl1leina.sololog.entity.Post;
import com.github.ljl1leina.sololog.mapper.PostMapper;
import com.github.ljl1leina.sololog.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PostServiceImpl implements PostService {

    private final PostMapper postMapper;
    //发布文章
    @Override
    public Long create(PostSaveDTO dto) {
        Post post = new Post();
        BeanUtils.copyProperties(dto, post);
        post.setStatus(dto.getStatus() == null ? 1 : dto.getStatus());
        postMapper.insert(post);
        return post.getId();  // 插入后 MyBatis-Plus 自动回填自增 id
    }
}
