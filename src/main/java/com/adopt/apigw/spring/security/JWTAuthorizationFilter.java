package com.adopt.apigw.spring.security;

import com.adopt.apigw.core.utillity.log.ApplicationLogger;
import com.adopt.apigw.spring.LoggedInUser;
import com.adopt.apigw.utils.CommonConstants;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.slf4j.MDC;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

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
import java.util.UUID;

public class JWTAuthorizationFilter extends BasicAuthenticationFilter {

    public JWTAuthorizationFilter(AuthenticationManager authenticationManager) {
        super(authenticationManager);
    }

    @Override
    protected void doFilterInternal(HttpServletRequest req,
                                    HttpServletResponse resp,
                                    FilterChain filterChain) throws IOException, ServletException {
        try {
            String header = req.getHeader(CommonConstants.AUTHORIZATION_HEADER_STRING);

            if (header == null || !header.startsWith(CommonConstants.AUTHORIZATION_TOKEN_PREFIX)) {
                filterChain.doFilter(req, resp);

                return;
            }

            UsernamePasswordAuthenticationToken authentication = getAuthentication(req);

            SecurityContextHolder.getContext().setAuthentication(authentication);

            SecurityContext securityContext = SecurityContextHolder.getContext();
		    String userName=((LoggedInUser)securityContext.getAuthentication().getPrincipal()).getFullName();
		    MDC.put("userName", userName);
            if(userName == null && req.getHeaders("userName").hasMoreElements()) {
                org.slf4j.MDC.remove("userName");
                org.slf4j.MDC.put("userName", req.getHeaders("userName").nextElement());
            }
            if(req.getHeaders("traceId").hasMoreElements()) {
                org.slf4j.MDC.remove("traceId");
                org.slf4j.MDC.put("traceId", req.getHeaders("traceId").nextElement());
            } else {
                org.slf4j.MDC.put("traceId", UUID.randomUUID().toString().replaceAll("-", ""));
            }
            org.slf4j.MDC.put("spanId", UUID.randomUUID().toString().replaceAll("-", ""));





            filterChain.doFilter(req, resp);




        } catch (ExpiredJwtException e) {
            resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            resp.getWriter().println("{\"status\": 401," +
                    " \"message\": \"JWT Expired\"," +
                    " \"timestamp\": " + " \"" + LocalDateTime.now() + "\"}");
        }






    }

    private UsernamePasswordAuthenticationToken getAuthentication(HttpServletRequest req) {

        String token = req.getHeader(CommonConstants.AUTHORIZATION_HEADER_STRING);
        Key hmacKey = new SecretKeySpec(Base64.getDecoder().decode(CommonConstants.SECRET),
                SignatureAlgorithm.HS256.getJcaName());


        if (token != null) {
            String subject = Jwts.parserBuilder()
                    .setSigningKey(hmacKey)
                    .build()
                    .parseClaimsJws(token.replace(CommonConstants.AUTHORIZATION_TOKEN_PREFIX, ""))
                    .getBody()
                    .getSubject();

            if (subject != null) {
                LoggedInUser user = null;
                try {
                    user = new ObjectMapper().readValue(subject, LoggedInUser.class);
                } catch (Exception e) {
                    ApplicationLogger.logger.error(e.getMessage(), e);
                }
                MDC.put("jwtToken", token);
                return new UsernamePasswordAuthenticationToken(user, token, new ArrayList<>());
            }

            return null;
        }

        return null;
    }
}
