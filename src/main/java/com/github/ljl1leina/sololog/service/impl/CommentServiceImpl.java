package com.github.ljl1leina.sololog.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.ljl1leina.sololog.common.BusinessException;
import com.github.ljl1leina.sololog.common.UserContext;
import com.github.ljl1leina.sololog.dto.CommentCreateDTO;
import com.github.ljl1leina.sololog.entity.Comment;
import com.github.ljl1leina.sololog.entity.Post;
import com.github.ljl1leina.sololog.entity.User;
import com.github.ljl1leina.sololog.mapper.CommentMapper;
import com.github.ljl1leina.sololog.mapper.PostMapper;
import com.github.ljl1leina.sololog.mapper.UserMapper;
import com.github.ljl1leina.sololog.service.CommentService;
import com.github.ljl1leina.sololog.vo.CommentFloorVO;
import com.github.ljl1leina.sololog.vo.CommentVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {

    private final CommentMapper commentMapper;
    private final PostMapper postMapper;
    private final UserMapper userMapper;

    // 发评论 / 回复
    @Transactional
    @Override
    public Long create(Long postId, CommentCreateDTO dto) {
        // 1. 文章存在性校验
        if (postMapper.selectById(postId) == null) {
            throw new BusinessException("文章不存在");
        }

        long parentId = dto.getParentId() == null ? 0L : dto.getParentId();
        long replyToUserId = dto.getReplyToUserId() == null ? 0L : dto.getReplyToUserId();

        // 2. parentId 非0时：父必须存在、必须是"楼"、必须属于这篇文章、且未被删除
        //    （楼中楼不能再被回复——这是两级结构不退化成树的关键防线）
        if (parentId != 0) {
            Comment parent = commentMapper.selectById(parentId);
            if (parent == null) {
                throw new BusinessException("被回复的评论不存在");
            }
            if (parent.getParentId() != 0) {
                throw new BusinessException("只能回复顶级评论（楼）");
            }
            if (!Objects.equals(parent.getPostId(), postId)) {
                throw new BusinessException("被回复的评论不属于该文章");
            }
            if (parent.getStatus() != 0) {
                throw new BusinessException("该评论已被删除，无法回复");
            }
        }

        // 3. 被回复人存在性校验（字段纯展示用，但别让前端@出一个不存在的昵称）
        if (replyToUserId != 0 && userMapper.selectById(replyToUserId) == null) {
            throw new BusinessException("被回复的用户不存在");
        }

        // 4. 插入 + comment_count 原子自增（同一事务；createdAt 由数据库默认值填）
        Comment comment = new Comment();
        comment.setPostId(postId);
        comment.setUserId(UserContext.currentUserId());
        comment.setContent(dto.getContent());
        comment.setParentId(parentId);
        comment.setReplyToUserId(replyToUserId);
        comment.setStatus(0);
        commentMapper.insert(comment);

        postMapper.update(null, new UpdateWrapper<Post>()
                .setSql("comment_count = comment_count + 1").eq("id", postId));
        return comment.getId();   // MyBatis-Plus 插入后回填自增id
    }

    // 评论列表：按"楼"分页，两级内存组装，全程固定3条SQL
    @Override
    public IPage<CommentFloorVO> list(Long postId, int page, int size) {
        if (postMapper.selectById(postId) == null) {
            throw new BusinessException("文章不存在");
        }

        // 第1条SQL：查顶级评论（楼），分页。
        // 注意：不过滤 status——已删的楼保留骨架，它的回复才能继续显示（方案A）
        Page<Comment> floorPage = commentMapper.selectPage(
                new Page<>(page, size),
                new LambdaQueryWrapper<Comment>()
                        .eq(Comment::getPostId, postId)
                        .eq(Comment::getParentId, 0)
                        .orderByAsc(Comment::getCreatedAt)
                        .orderByAsc(Comment::getId));   // 同秒创建的楼，用id保证排序稳定
        List<Comment> floors = floorPage.getRecords();

        if (floors.isEmpty()) {
            Page<CommentFloorVO> empty = new Page<>(floorPage.getCurrent(), floorPage.getSize(), floorPage.getTotal());
            empty.setRecords(List.of());
            return empty;
        }

        // 第2条SQL：一次查回这批楼下的所有回复（已删回复直接过滤掉）
        List<Long> floorIds = floors.stream().map(Comment::getId).toList();
        List<Comment> replies = commentMapper.selectList(
                new LambdaQueryWrapper<Comment>()
                        .eq(Comment::getPostId, postId)
                        .in(Comment::getParentId, floorIds)
                        .eq(Comment::getStatus, 0)
                        .orderByAsc(Comment::getId));   // id正序 = 插入顺序
        Map<Long, List<Comment>> repliesByFloor = replies.stream()
                .collect(Collectors.groupingBy(Comment::getParentId));

        // 收集所有涉及到的userId：楼作者 + 回复作者 + 被回复人
        Set<Long> userIds = new HashSet<>();
        for (Comment f : floors) {
            userIds.add(f.getUserId());
        }
        for (Comment r : replies) {
            userIds.add(r.getUserId());
            if (r.getReplyToUserId() != null && r.getReplyToUserId() != 0) {
                userIds.add(r.getReplyToUserId());
            }
        }

        // 第3条SQL：一次批量查用户——N+1问题就防在这一步
        Map<Long, User> userMap = userIds.isEmpty() ? Map.of()
                : userMapper.selectBatchIds(userIds).stream()
                .collect(Collectors.toMap(User::getId, Function.identity()));

        // 内存组装：楼 + 挂回复
        List<CommentFloorVO> records = floors.stream().map(floor -> {
            CommentFloorVO vo = new CommentFloorVO();
            fill(vo, floor, userMap);
            vo.setReplies(repliesByFloor.getOrDefault(floor.getId(), List.of()).stream()
                    .map(r -> {
                        CommentVO rvo = new CommentVO();
                        fill(rvo, r, userMap);
                        return rvo;
                    })
                    .toList());
            return vo;
        }).toList();

        Page<CommentFloorVO> result = new Page<>(floorPage.getCurrent(), floorPage.getSize(), floorPage.getTotal());
        result.setRecords(records);
        return result;   // total = 楼数（分页按楼算）
    }

    // 实体 -> VO 的公共填装
    private void fill(CommentVO vo, Comment c, Map<Long, User> userMap) {
        vo.setId(c.getId());
        vo.setDeleted(c.getStatus() != 0);
        vo.setContent(c.getStatus() == 0 ? c.getContent() : null);   // 已删的不吐内容，前端显示占位
        vo.setUserId(c.getUserId());
        vo.setCreatedAt(c.getCreatedAt());
        User u = userMap.get(c.getUserId());
        if (u != null) {
            vo.setNickname(u.getNickname());
            vo.setAvatar(u.getAvatar());
        }
        if (c.getParentId() != 0) {   // 回复专属字段
            vo.setParentId(c.getParentId());
            vo.setReplyToUserId(c.getReplyToUserId());
            User replyTo = c.getReplyToUserId() == null ? null : userMap.get(c.getReplyToUserId());
            if (replyTo != null) {
                vo.setReplyToNickname(replyTo.getNickname());
            }
        }
    }

    // 删除评论：自己的 或 AUTHOR；软删 + 幂等扣减
    @Transactional
    @Override
    public void delete(Long commentId) {
        Comment comment = commentMapper.selectById(commentId);
        if (comment == null) {
            throw new BusinessException("评论不存在");
        }

        // 权限：本人 或 AUTHOR（AUTHOR可删任何人的）
        Long userId = UserContext.currentUserId();
        if (!Objects.equals(userId, comment.getUserId())
                && !"AUTHOR".equals(UserContext.currentUserRole())) {
            throw new BusinessException("无权删除该评论");
        }

        // 条件软删：set status=1 where id=? and status=0
        // 影响行数为0 = 已经删过了（重复点击/并发），直接返回，count不会双扣
        int rows = commentMapper.update(null, new UpdateWrapper<Comment>()
                .set("status", 1)
                .eq("id", commentId)
                .eq("status", 0));
        if (rows > 0) {
            postMapper.update(null, new UpdateWrapper<Post>()
                    .setSql("comment_count = comment_count - 1").eq("id", comment.getPostId()));
        }
        // 楼的回复不级联处理：留着，前端对已删的楼显示"该评论已删除"
    }
}
