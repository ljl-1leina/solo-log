package com.github.ljl1leina.sololog.service;

import com.github.ljl1leina.sololog.dto.LoginDTO;
import com.github.ljl1leina.sololog.dto.RegisterDTO;
import com.github.ljl1leina.sololog.vo.LoginVO;

public interface AuthService {
    void register(RegisterDTO dto);
    LoginVO login(LoginDTO dto);
}
