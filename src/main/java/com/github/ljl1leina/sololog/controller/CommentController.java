package com.github.ljl1leina.sololog.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.github.ljl1leina.sololog.common.Result;
import com.github.ljl1leina.sololog.dto.CommentCreateDTO;
import com.github.ljl1leina.sololog.service.CommentService;
import com.github.ljl1leina.sololog.vo.CommentFloorVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@Tag(name = "评论")
@RestController
@RequestMapping("/api")   // 评论和删除两个入口路径前缀不同，方法上写全
@RequiredArgsConstructor
public class CommentController {
    private final CommentService commentService;

    @Operation(summary = "评论列表（按楼分页，楼内带全部回复，游客可看）")
    @GetMapping("/posts/comments/{postId}")
    public Result<IPage<CommentFloorVO>> list(@PathVariable Long postId,
                                              @RequestParam(defaultValue = "1") int page,
                                              @RequestParam(defaultValue = "10") int size) {
        return Result.ok(commentService.list(postId, page, size));
    }

    @Operation(summary = "发表评论/回复（需登录）")
    @PostMapping("/posts/comments/{postId}")
    public Result<Long> create(@PathVariable Long postId,
                               @Valid @RequestBody CommentCreateDTO dto) {
        return Result.ok(commentService.create(postId, dto));
    }

    @Operation(summary = "删除评论（本人或AUTHOR）")
    @DeleteMapping("/comments/{commentId}")
    public Result<Void> delete(@PathVariable Long commentId) {
        commentService.delete(commentId);
        return Result.ok();
    }
}
