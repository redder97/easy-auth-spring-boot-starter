package com.supplyfy.core.config.security;


import com.supplyfy.core.domain.UserPrincipal;
import com.supplyfy.core.domain.WebAccountRepository;
import com.supplyfy.core.domain.model.WebAccount;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.transaction.annotation.Transactional;

public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    WebAccountRepository webAccountRepository;

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String email)
        throws UsernameNotFoundException {
        WebAccount user = webAccountRepository.findByEmail(email)
            .orElseThrow(() ->
                new UsernameNotFoundException("User not found with email : " + email)
            );

        return UserPrincipal.create(user);
    }

    @Transactional
    public UserDetails loadUserById(Long id) {
        WebAccount user = webAccountRepository.findById(id).orElseThrow(
            () -> new RuntimeException("Web Account not found for id: " + id)
        );

        return UserPrincipal.create(user);
    }
}
