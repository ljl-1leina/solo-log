package com.github.ljl1leina.sololog.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.github.ljl1leina.sololog.common.BusinessException;
import com.github.ljl1leina.sololog.common.UserContext;
import com.github.ljl1leina.sololog.entity.Post;
import com.github.ljl1leina.sololog.entity.PostLike;
import com.github.ljl1leina.sololog.mapper.PostLikeMapper;
import com.github.ljl1leina.sololog.mapper.PostMapper;
import com.github.ljl1leina.sololog.service.LikeService;
import com.github.ljl1leina.sololog.vo.LikeVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class LikeServiceImpl implements LikeService {
    private final PostLikeMapper postLikeMapper;

    private final PostMapper postMapper;
    //查看是否点赞
    @Override
    public LikeVO isLike(Long postId) {
        //判断文章是否存在
        Post post = postMapper.selectById(postId);
        if (post == null) {
            throw new BusinessException("文章不存在");
        }

        //获取当前用户id
        Long userId = UserContext.currentUserId();

        //如果是游客 返回点赞数即可
        if(userId==null){
            LikeVO likeVO = new LikeVO();
            likeVO.setLiked(false);
            likeVO.setLikeCount(post.getLikeCount());
            return likeVO;
        }
        //查询是否点赞
        LikeVO likeVO = new LikeVO();
        boolean like=postLikeMapper.selectCount(new LambdaQueryWrapper<PostLike>()
                .eq(PostLike::getPostId, postId)
                .eq(PostLike::getUserId, userId))>0;
        likeVO.setLiked(like);
        Integer likeCount = post.getLikeCount();
        likeVO.setLikeCount(likeCount);
        return likeVO;
    }

    //点赞
    @Transactional
    @Override
    public LikeVO like(Long postId) {
        //判断文章是否存在
        Post post = postMapper.selectById(postId);
        if (post == null) {
            throw new BusinessException("文章不存在");
        }
        Long userId = UserContext.currentUserId();
        //先查询有没有点赞过
        boolean like=postLikeMapper.selectCount(new LambdaQueryWrapper<PostLike>()
                .eq(PostLike::getPostId, postId)
                .eq(PostLike::getUserId, userId))>0;
        //封装的返回值
        Integer likeCount = post.getLikeCount();
        boolean liked=false;
        //已点赞 执行取消点赞操作
        if(like){
            postLikeMapper.delete(new LambdaQueryWrapper<PostLike>()
                    .eq(PostLike::getPostId, postId)
                    .eq(PostLike::getUserId, userId));
            postMapper.update(null,new UpdateWrapper<Post>().
                    setSql("like_count = like_count - 1").eq("id", postId));
            likeCount--;
            liked=false;
        }else{
            //没点赞 执行点赞操作
            PostLike postLike = new PostLike();
            postLike.setPostId(postId);
            postLike.setUserId(userId);
            postLikeMapper.insert(postLike);
            postMapper.update(null,new UpdateWrapper<Post>().
                    setSql("like_count = like_count + 1").eq("id", postId));
            likeCount++;
            liked=true;
        }
        return new LikeVO(liked,likeCount);
    }
}
