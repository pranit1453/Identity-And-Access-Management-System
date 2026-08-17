package com.pranit.accesshub.authentication.oauth2.model;

import com.pranit.accesshub.entities.constant.Provider;
import lombok.Builder;

@Builder
public record OAuth2UserInfo(
        String oauth2Id,
        String email,
        String username,
        String firstName,
        String lastName,
        Provider provider
) {
}

