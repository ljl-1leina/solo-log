package com.github.ljl1leina.sololog.service;

import com.github.ljl1leina.sololog.vo.LikeVO;

public interface LikeService {
    LikeVO isLike(Long postId);

    LikeVO like(Long postId);
}
