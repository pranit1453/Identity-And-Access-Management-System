package com.pranit.accesshub.authentication.service;

import com.pranit.accesshub.security.model.UserDetail;

import java.util.UUID;

public interface UserDetailService {
    UserDetail loadUserByUserId(UUID userId);
}
