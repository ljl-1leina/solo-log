package com.github.ljl1leina.sololog.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class PostSaveDTO {
    @NotBlank(message = "标题不能为空")
    @Size(max = 200, message = "标题最长200字")
    private String title;

    @Size(max = 500, message = "摘要最长500字")
    private String summary;      // 可空，为空时前端可以截取正文

    @NotBlank(message = "正文不能为空")
    private String content;

    private Integer status;      // 可空，默认1（发布）。传0存草稿
}
