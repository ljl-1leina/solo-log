package com.github.ljl1leina.sololog.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.ljl1leina.sololog.dto.PostSaveDTO;
import com.github.ljl1leina.sololog.entity.Post;
import com.github.ljl1leina.sololog.mapper.PostMapper;
import com.github.ljl1leina.sololog.service.PostService;
import com.github.ljl1leina.sololog.vo.PostListVO;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.List;

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

    //查看文章列表
    @Override
    public IPage<PostListVO> listPublished(int page, int size) {
        Page<Post> p = postMapper.selectPage(
                new Page<>(page, size),
                new LambdaQueryWrapper<Post>()
                        .eq(Post::getStatus, 1)
                        .orderByDesc(Post::getCreatedAt)
        );
        // 实体 -> 列表VO（不复制 content，列表页不需要）
        List<PostListVO> records = p.getRecords().stream().map(post -> {
            PostListVO vo = new PostListVO();
            BeanUtils.copyProperties(post, vo);
            return vo;
        }).toList();
        Page<PostListVO> result = new Page<>(p.getCurrent(), p.getSize(), p.getTotal());
        result.setRecords(records);
        return result;
    }


}
