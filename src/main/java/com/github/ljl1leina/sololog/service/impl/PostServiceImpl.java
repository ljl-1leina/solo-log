package com.github.ljl1leina.sololog.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.ljl1leina.sololog.common.BusinessException;
import com.github.ljl1leina.sololog.dto.PostSaveDTO;
import com.github.ljl1leina.sololog.entity.Post;
import com.github.ljl1leina.sololog.mapper.PostMapper;
import com.github.ljl1leina.sololog.service.PostService;
import com.github.ljl1leina.sololog.vo.PostDetailVO;
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

    //查看文章详情
    @Override
    public PostDetailVO getDetail(Long id) {
        Post post = postMapper.selectById(id);
        if (post == null) {
            throw new BusinessException("文章不存在");
        }
        // 浏览量 +1（原子操作，数据库里自增，不怕并发）
        postMapper.update(null,
                new UpdateWrapper<Post>().setSql("view_count = view_count + 1").eq("id", id));
        post.setViewCount(post.getViewCount() + 1);
        PostDetailVO vo = new PostDetailVO();
        BeanUtils.copyProperties(post, vo);
        return vo;
    }


}
