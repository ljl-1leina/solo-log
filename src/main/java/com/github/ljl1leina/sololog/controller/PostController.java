package com.github.ljl1leina.sololog.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.github.ljl1leina.sololog.common.Result;
import com.github.ljl1leina.sololog.dto.PostSaveDTO;
import com.github.ljl1leina.sololog.service.PostService;
import com.github.ljl1leina.sololog.vo.PostDetailVO;
import com.github.ljl1leina.sololog.vo.PostListVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@Tag(name = "文章")
@RestController
@RequestMapping("/api/posts")
@RequiredArgsConstructor
public class PostController {
    private final PostService postService;

    @Operation(summary = "发布文章（暂未鉴权，登录功能做好后加保护）")
    @PostMapping
    public Result<Long> create(@Valid @RequestBody PostSaveDTO dto) {
        return Result.ok(postService.create(dto));
    }

    @Operation(summary = "文章列表（分页，只含已发布）")
    @GetMapping
    public Result<IPage<PostListVO>> list(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        return Result.ok(postService.listPublished(page, size));
    }

    @Operation(summary = "文章详情")
    @GetMapping("/{id}")
    public Result<PostDetailVO> detail(@PathVariable Long id) {
        return Result.ok(postService.getDetail(id));
    }
}
