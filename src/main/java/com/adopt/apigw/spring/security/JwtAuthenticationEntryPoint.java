package com.adopt.apigw.spring.security;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
        final String expired = (String) request.getAttribute("expired");
        final String inValid = (String) request.getAttribute("inValid");

        if (expired != null) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, expired);
        } else if (inValid != null) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, inValid);
        } else {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid Login details");
        }
    }

}
