//package com.adopt.apigw.spring.security;
//
//import com.adopt.apigw.model.common.Customers;
//import com.adopt.apigw.modules.acl.constants.AclConstants;
//import com.adopt.apigw.modules.auditLog.service.AuditLogService;
//import com.adopt.apigw.spring.LoggedInUser;
//import com.adopt.apigw.spring.SpringContext;
//import com.adopt.apigw.utils.CommonConstants;
//import com.fasterxml.jackson.databind.ObjectMapper;
//
//import io.jsonwebtoken.Jwts;
//import io.jsonwebtoken.SignatureAlgorithm;
//import org.springframework.security.authentication.AuthenticationManager;
//import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
//import org.springframework.security.core.Authentication;
//import org.springframework.security.core.AuthenticationException;
//import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
//
//import javax.crypto.spec.SecretKeySpec;
//import javax.servlet.FilterChain;
//import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpServletResponse;
//import java.io.IOException;
//import java.security.Key;
//import java.time.LocalDateTime;
//import java.util.ArrayList;
//import java.util.Base64;
//import java.util.Date;
//
//public class SubscriberJwtAuthenticationFilter extends UsernamePasswordAuthenticationFilter {
//
//    private AuthenticationManager authenticationManager;
//
//    public SubscriberJwtAuthenticationFilter(AuthenticationManager authenticationManager) {
//        this.authenticationManager = authenticationManager;
////        this.setFilterProcessesUrl("/api/portal/v1/subscriber/login");
//    }
//
//    @Override
//    public Authentication attemptAuthentication(HttpServletRequest req,
//
//                                                HttpServletResponse resp) throws AuthenticationException {
//        try {
//
//            Customers user = new ObjectMapper().readValue(req.getInputStream(), Customers.class);
//            user.setSelfcarepwd(user.getPassword());
//
//            return authenticationManager.authenticate(
//
//                    new UsernamePasswordAuthenticationToken(
//                            user.getUsername(),
//                            user.getSelfcarepwd(),
//                            new ArrayList<>()
//                    )
//            );
//        } catch (IOException ex) {
//            throw new RuntimeException(ex);
//        }
//    }
//
//    @Override
//    protected void successfulAuthentication(HttpServletRequest req,
//                                            HttpServletResponse resp,
//                                            FilterChain filter,
//                                            Authentication auth) throws IOException {
//        Key hmacKey = new SecretKeySpec(Base64.getDecoder().decode(CommonConstants.SECRET),
//                SignatureAlgorithm.HS256.getJcaName());
//
//        LoggedInUser user = (LoggedInUser) auth.getPrincipal();
//        String subString = new ObjectMapper().writeValueAsString(user);
//        //Update sign with new method
//        String token = Jwts.builder()
//                .setSubject(subString)
//                .setExpiration(new Date(System.currentTimeMillis() + CommonConstants.EXPIRATION_TIME))
//                .signWith(hmacKey)
//                .compact();
//
//        resp.addHeader(CommonConstants.AUTHORIZATION_HEADER_STRING, CommonConstants.AUTHORIZATION_TOKEN_PREFIX + token);
//        resp.getWriter().println("{\"status\": 200," +
//                " \"message\": \"Login Success\"," +
//                " \"timestamp\": " + " \"" + LocalDateTime.now() + "\"," +
//                " \"userId\": " + user.getUserId() + "," +
//                " \"mvnoId\": " + user.getMvnoId() + "," +
//                " \"fistName\": " + " \"" + user.getFirstName() + "\"," +
//                " \"lastName\": " + " \"" + user.getLastName() + "\"," +
//                " \"lastLoginTime\": " + " \"" + user.getLastLoginTime() + "\"," +
//                " \"partnerId\": " + user.getPartnerId() + "," +
//                " \"accessToken\": \"" + CommonConstants.AUTHORIZATION_TOKEN_PREFIX + token + "\"}");
//        //Add log of auth token
//
//        try {
//            AuditLogService auditLogService = SpringContext.getBean(AuditLogService.class);
//            auditLogService.addAuditLogin(AclConstants.ACL_CLASS_STAFF_USER,
//                    AclConstants.OPERATION_LOGIN, req.getRemoteAddr(), null,
//                    (long) user.getUserId(), user.getUsername(), user);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//
//    @Override
//    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response,
//                                              AuthenticationException failed) throws IOException {
//    	if(failed.getMessage().equalsIgnoreCase("Bad credentials")) {
//    		response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
//            response.getWriter().println("{\"status\": 401," +
//                    " \"message\": \"Password is wrong\"," +
//                    " \"timestamp\": " + " \"" + LocalDateTime.now() + "\"}");
//    	}else if (!failed.getMessage().equalsIgnoreCase("Bad credentials")) {
//    		response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
//            response.getWriter().println("{\"status\": 401," +
//                    " \"message\": \"Username not found\"," +
//                    " \"timestamp\": " + " \"" + LocalDateTime.now() + "\"}");
//    	}else {
//    		response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
//            response.getWriter().println("{\"status\": 401," +
//                    " \"message\": \"Login Failed\"," +
//                    " \"timestamp\": " + " \"" + LocalDateTime.now() + "\"}");
//    	}
//    }
//}
