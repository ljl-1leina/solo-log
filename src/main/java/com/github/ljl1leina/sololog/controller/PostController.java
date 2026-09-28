package com.github.ljl1leina.sololog.controller;

import com.github.ljl1leina.sololog.common.Result;
import com.github.ljl1leina.sololog.dto.PostSaveDTO;
import com.github.ljl1leina.sololog.service.PostService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
