package com.adopt.apigw.spring.security;

import com.adopt.apigw.model.common.StaffUser;
import com.adopt.apigw.modules.Teams.domain.Teams;
import com.adopt.apigw.modules.Teams.repository.TeamsRepository;
import com.adopt.apigw.modules.acl.constants.AclConstants;
import com.adopt.apigw.modules.auditLog.service.AuditLogService;
import com.adopt.apigw.spring.LoggedInUser;
import com.adopt.apigw.spring.SpringContext;
import com.adopt.apigw.utils.CommonConstants;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.crypto.spec.SecretKeySpec;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.security.Key;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;
import java.util.List;

public class JWTAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private AuthenticationManager authenticationManager;

    public JWTAuthenticationFilter(AuthenticationManager authenticationManager) {
        this.authenticationManager = authenticationManager;
        this.setFilterProcessesUrl("/api/v1/login");
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest req,

                                                HttpServletResponse resp) throws AuthenticationException {
        try {
            StaffUser user = new ObjectMapper().readValue(req.getInputStream(), StaffUser.class);

            long startTime = System.nanoTime();

            Authentication authentication =  authenticationManager.authenticate(

                    new UsernamePasswordAuthenticationToken(
                            user.getUsername(),
                            user.getPassword(),
                            new ArrayList<>()
                    )
            );
            long endTime = System.nanoTime();
            long elapsedTime = endTime - startTime;
          //  System.out.println("Elapsed Time:::::::: attemptAuthentication" + elapsedTime + " nanoseconds");
            return authentication;
        } catch (IOException ex) {
//            throw new RuntimeException(ex);
            ex.printStackTrace();
            return null;
        }
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest req,
                                            HttpServletResponse resp,
                                            FilterChain filter,
                                            Authentication auth) throws IOException, ServletException {
        Key hmacKey = new SecretKeySpec(Base64.getDecoder().decode(CommonConstants.SECRET),
                SignatureAlgorithm.HS256.getJcaName());
        LoggedInUser user = (LoggedInUser) auth.getPrincipal();
        String subString = new ObjectMapper().writeValueAsString(user);
        //Update sign with with new method
        long startTime1 = System.nanoTime();
        String token = Jwts.builder()
                .setSubject(subString)
                .setExpiration(new Date(System.currentTimeMillis() + CommonConstants.EXPIRATION_TIME))
                .signWith(hmacKey)
                .compact();
        long endTime1 = System.nanoTime();
        long elapsedTime1 = endTime1 - startTime1;
        System.out.println("Elapsed Time::::::::  token " + elapsedTime1 + " nanoseconds");
        resp.addHeader(CommonConstants.AUTHORIZATION_HEADER_STRING, CommonConstants.AUTHORIZATION_TOKEN_PREFIX + token);
        resp.getWriter().println("{\"status\": 200," +
                " \"message\": \"Login Success\"," +
                " \"timestamp\": " + " \"" + LocalDateTime.now() + "\"," +
                " \"userId\": " + user.getUserId() + "," +
                " \"mvnoId\": " + user.getMvnoId() + "," +
                " \"userRoles\": \"" + user.getRolesList() + "\"," +
                " \"partnerId\": " + user.getPartnerId() + "," +
                " \"serviceAreaId\": " + user.getServiceAreaId() + "," +
                " \"serviceAreaIdList\": " + " \"" + user.getServiceAreaIdList() + "\"," +
                " \"fullName\": " + " \"" + user.getFullName() + "\"," +
                " \"partnerFlag\": " + (CommonConstants.DEFAULT_PARTNER_ID != user.getPartnerId()) + "," +
                " \"isLco\": " + user.getLco() + "," +
                " \"userName\": " + " \"" + user.getUsername() + "\"," +
                " \"teams\": " + " \"" + user.getTeams() + "\"," +
                " \"assignableRoleIds\": " + " \"" + user.getAssignableRoleIds() + "\"," +
                " \"assignableRoleNames\": " + " \"" + user.getAssignableRoleNames() + "\"," +
                " \"accessToken\": \"" + CommonConstants.AUTHORIZATION_TOKEN_PREFIX + token + "\"}");
        //Add log of auth token

        try {
            AuditLogService auditLogService = SpringContext.getBean(AuditLogService.class);
            auditLogService.addAuditLogin(AclConstants.ACL_CLASS_STAFF_USER,
                    AclConstants.OPERATION_LOGIN, req.getRemoteAddr(), null, Long.valueOf(user.getUserId()), user.getUsername(), user);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response,
                                              AuthenticationException failed) throws IOException, ServletException {
    	if(failed.getMessage().equalsIgnoreCase("Bad credentials")) {
    		response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().println("{\"status\": 401," +
                    " \"message\": \"Password is wrong\"," +
                    " \"timestamp\": " + " \"" + LocalDateTime.now() + "\"}");
            MDC.clear();
    	}else if (!failed.getMessage().equalsIgnoreCase("Bad credentials")) {
    		response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().println("{\"status\": 401," +
                    " \"message\": \"Username not found\"," +
                    " \"timestamp\": " + " \"" + LocalDateTime.now() + "\"}");
            MDC.clear();
    	}else {
    		response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().println("{\"status\": 401," +
                    " \"message\": \"Login Failed\"," +
                    " \"timestamp\": " + " \"" + LocalDateTime.now() + "\"}");
            MDC.clear();
    	}      
    }
}
