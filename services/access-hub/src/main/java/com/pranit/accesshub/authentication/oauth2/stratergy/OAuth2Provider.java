package com.pranit.accesshub.authentication.oauth2.stratergy;

import com.pranit.accesshub.authentication.oauth2.model.OAuth2UserInfo;
import com.pranit.accesshub.entities.constant.Provider;
import org.springframework.security.oauth2.core.user.OAuth2User;

public interface OAuth2Provider {

    Provider getProvider();

    OAuth2UserInfo extractUserInfo(OAuth2User user);
}
