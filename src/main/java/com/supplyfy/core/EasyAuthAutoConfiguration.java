package com.supplyfy.core;

import com.supplyfy.core.config.ApplicationProperties;
import com.supplyfy.core.config.TokenProvider;
import com.supplyfy.core.config.TokenProviderImpl;
import com.supplyfy.core.config.security.CustomOAuth2UserService;
import com.supplyfy.core.config.security.CustomUserDetailsService;
import com.supplyfy.core.config.security.oauth2.HttpCookieOAuth2AuthorizationRequestRepository;
import com.supplyfy.core.config.security.oauth2.OAuth2AuthenticationFailureHandler;
import com.supplyfy.core.config.security.oauth2.OAuth2AuthenticationSuccessHandler;
import com.supplyfy.core.config.security.oauth2.Oauth2ClientConfiguration;
import com.supplyfy.core.domain.model.WebAccount;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.registration.InMemoryClientRegistrationRepository;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import javax.sql.DataSource;

@Configuration
@ConfigurationProperties(prefix = "spring.easy.auth")
@EnableConfigurationProperties({ApplicationProperties.class, Oauth2ClientConfiguration.class})
@EntityScan(basePackageClasses = WebAccount.class)
@EnableJpaRepositories
public class EasyAuthAutoConfiguration {


    private String driverClassName;
    private String url;
    private String username;
    private String password;

    @Bean
    @ConditionalOnMissingBean
    public DataSource dataSource() {
        return DataSourceBuilder.create()
                .driverClassName(this.driverClassName)
                .url(this.url)
                .username(this.username)
                .password(this.password)
                .build();
    }

    @Bean
    @ConditionalOnMissingBean
    public TokenProvider tokenProvider() {
        return new TokenProviderImpl();
    }

    @Bean
    @ConditionalOnMissingBean
    public UserDetailsService userDetailsService() {
        return new CustomUserDetailsService();
    }

    @Bean
    @ConditionalOnMissingBean
    public DefaultOAuth2UserService defaultOAuth2UserService() {
        return new CustomOAuth2UserService();
    }

    @Bean
    @ConditionalOnMissingBean
    public ApplicationProperties applicationProperties() {
        return new ApplicationProperties();
    }

    @Bean
    @ConditionalOnMissingBean
    public HttpCookieOAuth2AuthorizationRequestRepository httpCookieOAuth2AuthorizationRequestRepository() {
        return new HttpCookieOAuth2AuthorizationRequestRepository();
    }

    @Bean
    @ConditionalOnMissingBean
    public AuthenticationSuccessHandler oAuth2AuthenticationSuccessHandler() {
        return new OAuth2AuthenticationSuccessHandler(tokenProvider(), applicationProperties(), httpCookieOAuth2AuthorizationRequestRepository());
    }

    @Bean
    @ConditionalOnMissingBean
    public AuthenticationFailureHandler oAuth2AuthenticationFailureHandler() {
        return new OAuth2AuthenticationFailureHandler();
    }



    public String getDriverClassName() {
        return driverClassName;
    }

    public void setDriverClassName(String driverClassName) {
        this.driverClassName = driverClassName;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }



}
