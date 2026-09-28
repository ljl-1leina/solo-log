package com.github.ljl1leina.sololog.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "文章")
@RestController
@RequestMapping("/api/posts")
@RequiredArgsConstructor
public class PostController {
}
