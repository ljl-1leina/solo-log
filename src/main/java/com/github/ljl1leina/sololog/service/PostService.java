package com.github.ljl1leina.sololog.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.github.ljl1leina.sololog.dto.PostSaveDTO;
import com.github.ljl1leina.sololog.vo.PostDetailVO;
import com.github.ljl1leina.sololog.vo.PostListVO;

public interface PostService {
    Long create( PostSaveDTO dto);

    IPage<PostListVO> listPublished(int page, int size);

    PostDetailVO getDetail(Long id);
}
