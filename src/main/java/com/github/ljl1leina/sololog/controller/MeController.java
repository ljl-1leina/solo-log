package com.github.ljl1leina.sololog.controller;

import com.github.ljl1leina.sololog.common.Result;
import com.github.ljl1leina.sololog.dto.ProfileUpdateDTO;
import com.github.ljl1leina.sololog.service.UserService;
import com.github.ljl1leina.sololog.vo.UserVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * "我的"资料：是谁由 token 决定，而不是由 URL 决定，
 * 接口压根不接受用户 id 参数，从根上杜绝了改别人资料的可能。
 */
@Tag(name = "我的资料")
@RestController
@RequestMapping("/api/me")
@RequiredArgsConstructor
public class MeController {

    private final UserService userService;

    @Operation(summary = "查看我的资料（需登录）")
    @GetMapping
    public Result<UserVO> me() {
        return Result.ok(userService.getMyProfile());
    }

    @Operation(summary = "修改资料（需登录，传哪个改哪个，不传的不动）")
    @PutMapping
    public Result<Void> update(@Valid @RequestBody ProfileUpdateDTO dto) {
        userService.updateMyProfile(dto);
        return Result.ok();
    }
}
