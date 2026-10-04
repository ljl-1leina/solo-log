package com.github.ljl1leina.sololog.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LikeVO {
    private boolean liked;
    private Integer likeCount;
}
