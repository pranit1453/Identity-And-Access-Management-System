package com.pranit.accesshub.authentication.oauth2.service;

import com.pranit.accesshub.authentication.oauth2.factory.OAuth2ProviderFactory;
import com.pranit.accesshub.authentication.oauth2.model.OAuth2UserInfo;
import com.pranit.accesshub.entities.constant.Provider;
import com.pranit.accesshub.entities.entity.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class OAuth2AuthenticationService {

    private final OAuth2ProviderFactory factory;
    private final OAuth2UserProvisioningService provisioningService;

    public User authenticate(final String registrationId, final OAuth2User user) {
        log.debug("OAuth2 authentication started. provider: {}", registrationId);
        try {
            final Provider provider = Provider.valueOf(registrationId.toUpperCase());
            final OAuth2UserInfo userInfo = factory.getStrategy(provider).extractUserInfo(user);
            final User userData = provisioningService.provisionUserCredential(userInfo);
            log.info("OAuth2 authentication successful. provider:{}, userId:{}",
                    provider, userData.getUserId());
            return userData;
        } catch (Exception ex) {
            log.warn("OAuth2 authentication failed. provider: {}, errorType: {}",
                    registrationId, ex.getClass().getSimpleName());
            throw ex;
        }
    }

}
