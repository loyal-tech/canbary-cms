package com.adopt.apigw.spring;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.adopt.apigw.service.common.StaffUserService;

@Component("limitLoginAuthenticationProvider")
public class LimitLoginAuthenticationProvider extends DaoAuthenticationProvider {

    @Autowired
    StaffUserService staffUserService;

    @Autowired
    @Override
    @Qualifier("customUserDetailService")
    public void setUserDetailsService(UserDetailsService customUserDetailService) {
        // TODO Auto-generated method stub
        super.setUserDetailsService(customUserDetailService);
        super.setPasswordEncoder(passwordEncoder());
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        // TODO Auto-generated method stub
        try {

            Authentication auth = super.authenticate(authentication);
            //if reach here, means login success, else an exception will be thrown
            //reset the user_attempts
            staffUserService.resetFailAttempts(authentication.getName());
            return auth;

        } catch (BadCredentialsException e) {
            //invalid login, update to user_attempts
            staffUserService.increaseFailAttempts(authentication.getName());
            throw e;
        }

    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
