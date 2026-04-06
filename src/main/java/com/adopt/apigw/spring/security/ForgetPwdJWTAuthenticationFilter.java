package com.adopt.apigw.spring.security;

import java.io.IOException;
import java.security.Key;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import javax.crypto.spec.SecretKeySpec;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;

import com.adopt.apigw.model.common.StaffUser;
import com.adopt.apigw.modules.acl.constants.AclConstants;
import com.adopt.apigw.modules.auditLog.service.AuditLogService;
import com.adopt.apigw.modules.role.domain.Role;
import com.adopt.apigw.service.common.StaffUserService;
import com.adopt.apigw.spring.LoggedInUser;
import com.adopt.apigw.spring.SpringContext;
import com.adopt.apigw.utils.CommonConstants;
import com.fasterxml.jackson.core.JsonParser.Feature;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

public class ForgetPwdJWTAuthenticationFilter extends UsernamePasswordAuthenticationFilter{

	private AuthenticationManager authenticationManager;

    public ForgetPwdJWTAuthenticationFilter(AuthenticationManager authenticationManager) {
        this.authenticationManager = authenticationManager;
        this.setFilterProcessesUrl("/staff/getStaffContactByUserName");
    }
    
    @Override
    public Authentication attemptAuthentication(HttpServletRequest req,

                                                HttpServletResponse resp) throws AuthenticationException {
        try {

        	ObjectMapper objectMapper = new ObjectMapper();
        	objectMapper.configure(Feature.AUTO_CLOSE_SOURCE, true);
        	String request = req.getInputStream().toString();
        	StaffUser user = new ObjectMapper().readValue(req.getInputStream(), StaffUser.class);
            StaffUserService staffUserService = SpringContext.getBean(StaffUserService.class);
    		StaffUser staffUser = null;
    		List<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>();
            StringBuilder roleList = new StringBuilder();
    		try {
    			staffUser = staffUserService.getByUserName(user.getUsername());
    			user.setPassword(staffUser.getPassword());
    			int i = 0;
                for (Role role : staffUser.getRoles()) {
                    authorities.add(new SimpleGrantedAuthority(role.getRolename()));
                    if (i != 0)
                        roleList.append(",");
                    roleList.append(role.getId());
                    i++;
                }
    		} catch (Exception e1) {
    			e1.printStackTrace();
    		}
			
    		Authentication authentication = new UsernamePasswordAuthenticationToken(staffUser, null, 
					  authorities);
    		//authentication = authenticationManager.authenticate(authentication);
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
    	try {
        Key hmacKey = new SecretKeySpec(Base64.getDecoder().decode(CommonConstants.SECRET),
                SignatureAlgorithm.HS256.getJcaName());

        StaffUser user = (StaffUser) auth.getPrincipal();
        String subString = user.toString();
        subString = subString + user.getPassword();
        StaffUserService staffUserService = SpringContext.getBean(StaffUserService.class);
		StaffUser staffUser;
		String phone = null;
		String email = null;
		try {
			staffUser = staffUserService.getByUserName(user.getUsername());
			phone = staffUser.getPhone();
			email = staffUser.getEmail();
		} catch (Exception e1) {
			e1.printStackTrace();
		}
        //Update sign with with new method
        String token = Jwts.builder()
                .setSubject(subString)
                .setExpiration(new Date(System.currentTimeMillis() + CommonConstants.FORGOT_PWD_EXPIRATION_TIME))
                .signWith(hmacKey)
                .compact();

        resp.addHeader(CommonConstants.AUTHORIZATION_HEADER_STRING, CommonConstants.AUTHORIZATION_TOKEN_PREFIX + token);
        resp.getWriter().println("{\"status\": 200," +
                " \"message\": \"Login Success\"," +
                " \"timestamp\": " + " \"" + LocalDateTime.now() + "\"," +
                " \"mvnoId\": " + user.getMvnoId() + "," +
                " \"phone\": " + phone + "," +
                " \"email\": " + email + "," +
                " \"accessToken\": \"" + CommonConstants.AUTHORIZATION_TOKEN_PREFIX + token + "\"}");
        
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
    	}else if (!failed.getMessage().equalsIgnoreCase("Bad credentials")) {
    		response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().println("{\"status\": 401," +
                    " \"message\": \"Username not found\"," +
                    " \"timestamp\": " + " \"" + LocalDateTime.now() + "\"}");
    	}else {
    		response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().println("{\"status\": 401," +
                    " \"message\": \"Login Failed\"," +
                    " \"timestamp\": " + " \"" + LocalDateTime.now() + "\"}");
    	}      
    }
 
}
