package com.supplyfy.core.config.security;

import com.supplyfy.core.config.security.oauth2.OAuth2UserInfo;
import com.supplyfy.core.config.security.oauth2.OAuth2UserInfoFactory;
import com.supplyfy.core.domain.AuthProvider;
import com.supplyfy.core.domain.UserPrincipal;
import com.supplyfy.core.domain.WebAccountRepository;
import com.supplyfy.core.domain.model.WebAccount;
import com.supplyfy.core.exception.OAuth2AuthenticationProcessingException;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.Optional;

public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    @Autowired
    private WebAccountRepository webAccountRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest oAuth2UserRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(oAuth2UserRequest);

        try {
            return processOAuth2User(oAuth2UserRequest, oAuth2User);
        } catch (AuthenticationException ex) {
            throw ex;
        } catch (Exception ex) {
            // Throwing an instance of AuthenticationException will trigger the OAuth2AuthenticationFailureHandler
            throw new InternalAuthenticationServiceException(ex.getMessage(), ex.getCause());
        }
    }

    private OAuth2User processOAuth2User(OAuth2UserRequest oAuth2UserRequest, OAuth2User oAuth2User) {
        OAuth2UserInfo oAuth2UserInfo = OAuth2UserInfoFactory.getOAuth2UserInfo(oAuth2UserRequest.getClientRegistration().getRegistrationId(), oAuth2User.getAttributes());
        if(Objects.isNull(oAuth2UserInfo.getEmail()) || Strings.isEmpty(oAuth2UserInfo.getEmail())) {
            throw new OAuth2AuthenticationProcessingException("Email not found from OAuth2 provider");
        }

        Optional<WebAccount> userOptional = webAccountRepository.findByEmail(oAuth2UserInfo.getEmail());
        WebAccount user;
        if(userOptional.isPresent()) {
            user = userOptional.get();
            if(!user.getProvider().equals(AuthProvider.valueOf(oAuth2UserRequest.getClientRegistration().getRegistrationId()))) {
                throw new OAuth2AuthenticationProcessingException("Looks like you're signed up with " +
                    user.getProvider() + " account. Please use your " + user.getProvider() +
                    " account to login.");
            }
            user = updateExistingUser(user, oAuth2UserInfo);
        } else {
            user = registerNewUser(oAuth2UserRequest, oAuth2UserInfo);
        }

        return UserPrincipal.create(user, oAuth2User.getAttributes());
    }

    private WebAccount registerNewUser(OAuth2UserRequest oAuth2UserRequest, OAuth2UserInfo oAuth2UserInfo) {
        WebAccount webAccount = new WebAccount();

        webAccount.setProvider(AuthProvider.valueOf(oAuth2UserRequest.getClientRegistration().getRegistrationId()));
        webAccount.setProviderId(oAuth2UserInfo.getId());
        webAccount.setEmail(oAuth2UserInfo.getEmail());
        webAccount.setImageUrl(oAuth2UserInfo.getImageUrl());
        webAccount.setName(oAuth2UserInfo.getName());
        return webAccountRepository.save(webAccount);
    }

    private WebAccount updateExistingUser(WebAccount existingUser, OAuth2UserInfo oAuth2UserInfo) {
        existingUser.setImageUrl(oAuth2UserInfo.getImageUrl());
        return webAccountRepository.save(existingUser);
    }

}
