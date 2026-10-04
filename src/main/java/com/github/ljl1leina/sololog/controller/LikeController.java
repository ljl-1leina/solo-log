package com.github.ljl1leina.sololog.controller;


import com.github.ljl1leina.sololog.common.Result;
import com.github.ljl1leina.sololog.service.LikeService;
import com.github.ljl1leina.sololog.vo.LikeVO;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@Tag(name = "点赞")
@RestController
@RequestMapping("/api/posts/likes")
@RequiredArgsConstructor
public class LikeController {
    private final LikeService likeService;

    @GetMapping("/{postId}")
    public Result<LikeVO> isLike(@PathVariable Long postId) {
        return Result.ok(likeService.isLike(postId));
    }

    @PostMapping("/{postId}")
    public Result<LikeVO> like(@PathVariable Long postId) {
        return Result.ok(likeService.like(postId));
    }
}
