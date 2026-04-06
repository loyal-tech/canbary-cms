package com.adopt.apigw.spring.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@EnableWebSecurity
@ComponentScan("com.adopt.apigw.spring.security")
public class SecurityConfig {

    @Autowired
    @Qualifier("limitLoginAuthenticationProvider")
    AuthenticationProvider authenticationProvider;

    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
        auth.authenticationProvider(authenticationProvider);
    }

    @Configuration
    public static class WebAuthenticator extends WebSecurityConfigurerAdapter {

        @Autowired
        private AuthSuccessHandler authHandler;

        //"/login","/dist/**","/bower_components/**","/plugins/**","/authenticateStaff","/customers/changepassword"

        @Override
        protected void configure(HttpSecurity http) throws Exception {
            http
                    .authorizeRequests()
                    .antMatchers("/customers/changepassword").permitAll()
                    .antMatchers("/customers/updatepassword").permitAll()
                    .antMatchers("/var/document/custdoc/**").permitAll()
                    .antMatchers("/Customer").permitAll()
                    .antMatchers("/provisionCustomer").permitAll()
                    .antMatchers("/deprovisionCustomer").permitAll()
                    .antMatchers("/getCustomer").permitAll()
                    .antMatchers("/updateCustomer").permitAll()
                    .antMatchers("/debitcompleted").permitAll()
                    .antMatchers("/SoapApi/**").permitAll()
                    .antMatchers(
                            "/v2/api-docs",
                            "/swagger-resources/**",
                            "/swagger-ui.html",
                            "/webjars/**"
                    ).permitAll()
                    .antMatchers("/paytm/**").permitAll()
                    .antMatchers("/open/**").permitAll()
                    .anyRequest().authenticated()
                    .and()
                    .formLogin()
                    .loginPage("/login")
                    .loginProcessingUrl("/authenticateStaff")
                    .successHandler(authHandler)
                    .permitAll()
                    .and()
                    .logout().permitAll().logoutUrl("/logout")
                    .and()
                    .csrf().disable()
            ;
        }


        @Override
        public void configure(WebSecurity web) throws Exception {
            web
                    .ignoring()
                    .antMatchers("/resources/**","/var/document/custdoc/**", "/dist/**", "/bower_components/**", "/plugins/**", "/images/**", "/payment-response");
        }
    }

    @Component
    public class AuthSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

        private RedirectStrategy redirectStrategy = new DefaultRedirectStrategy();

        @Override
        protected void handle(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {
            redirectStrategy.sendRedirect(request, response, "home");
        }
    }


    @Configuration
    @Order(1)
    public static class APIAuthenticator extends WebSecurityConfigurerAdapter {

        private final CustomUserDetailsService customUserService;

        public APIAuthenticator(CustomUserDetailsService customUserDetailService) {
            this.customUserService = customUserDetailService;
        }

        @Override
        protected void configure(HttpSecurity http) throws Exception {
            http.antMatcher("/api/v1/**")
                    .cors()
                    .and()
                    .csrf()
                    .disable()
                    .authorizeRequests()
//                    .antMatchers(
//                            "/v2/api-docs",
//                            "/swagger-resources/**",
//                            "/swagger-ui.html",
//                            "/webjars/**"
//                    ).permitAll()
                    .antMatchers("/api/v1/login").permitAll()
                    .antMatchers("/var/document/custdoc/**").permitAll()
                    .antMatchers("/api/v1/generatePaytmLinkAndSendToCustomer").permitAll()
                    .antMatchers("/api/v1/payment/**").permitAll()
                    .antMatchers("/api/v1/subscriber/payment/download/**").permitAll()
                    .antMatchers("/api/v1/subscriber/invoice/download/**").permitAll()
                    .antMatchers("/api/v1/subscriber/document/download/**/**").permitAll()
                    .antMatchers("/api/v1/order/process").permitAll()
                    .antMatchers("/api/v1/subscriber/forgotPassword/**").permitAll()
                    .antMatchers("/api/v1/subscriber/validateForgotPassword/**").permitAll()
                    .antMatchers("/api/v1/subscriber/customer/updatePassword/**").permitAll()
                    .antMatchers("/api/v1/subscriber/staff/updatePassword/**").permitAll()
                    .antMatchers("/paytm/**").permitAll()
                    .antMatchers("/open/**").permitAll()
                    .antMatchers("/SoapApi/**").permitAll()
                    .anyRequest()
                    .authenticated()
                    .and()
                    .addFilter(new JWTAuthorizationFilter(authenticationManager()))
                    .addFilter(new JWTAuthenticationFilter(authenticationManager()));
        }

        @Override
        protected void configure(AuthenticationManagerBuilder auth) throws Exception {
            auth.userDetailsService(customUserService)
                    .passwordEncoder(encoder());
        }

        @Bean
        public PasswordEncoder encoder() {
            return new BCryptPasswordEncoder();
        }
        @Configuration
        @Order(7)
        public static class ActuatorConfiguration extends WebSecurityConfigurerAdapter {
            @Override
            protected void configure(HttpSecurity http) throws Exception {
                http.antMatcher("/actuator/**")
                        .cors()
                        .and()
                        .authorizeRequests()
                        .antMatchers("/actuator/**").permitAll()
                        .antMatchers("/var/document/custdoc/**").permitAll()
                        .antMatchers("/SoapApi/**").permitAll()
                        .anyRequest()
                        .authenticated()
                        .and()
                        .csrf()
                        .disable();

            }
        }
    }
}
