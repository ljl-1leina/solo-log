package com.github.ljl1leina.sololog.controller;

import com.github.ljl1leina.sololog.common.Result;
import com.github.ljl1leina.sololog.config.AvatarProperties;
import com.github.ljl1leina.sololog.vo.AvatarVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@Tag(name = "头像库")
@RestController
@RequestMapping("/api/avatars")
@RequiredArgsConstructor
public class AvatarController {

    private final AvatarProperties avatarProperties;

    @Operation(summary = "预置头像列表（无需登录），前端渲染九宫格用")
    @GetMapping
    public Result<List<AvatarVO>> list() {
        List<AvatarVO> list = new ArrayList<>();
        for (int i = 1; i <= avatarProperties.getCount(); i++) {
            list.add(new AvatarVO(String.valueOf(i),
                    avatarProperties.getPrefix() + i + "." + avatarProperties.getExtension()));
        }
        return Result.ok(list);
    }
}
