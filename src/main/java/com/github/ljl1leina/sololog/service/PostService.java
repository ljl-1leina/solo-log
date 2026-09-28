package com.github.ljl1leina.sololog.service;

import com.github.ljl1leina.sololog.dto.PostSaveDTO;

public interface PostService {
    Long create( PostSaveDTO dto);
}
