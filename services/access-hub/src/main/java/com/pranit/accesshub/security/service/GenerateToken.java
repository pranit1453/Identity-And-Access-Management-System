package com.pranit.accesshub.security.service;

import com.pranit.accesshub.security.model.UserDetail;

public interface GenerateToken {

    String generateAccessToken(UserDetail userDetail, String sessionId);

    String generateRefreshToken(UserDetail userDetail, String jti, String sessionId);
}
