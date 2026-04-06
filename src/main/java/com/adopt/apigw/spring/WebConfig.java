package com.adopt.apigw.spring;

import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Configuration
//@EnableWebMvc
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
    	//registry.addMapping("/**");
		registry.addMapping("/**").allowedMethods("GET", "POST","PUT", "DELETE");

    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new HandlerInterceptorAdapter() {
            @Override
            public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
                String remoteAddr = request.getHeader("X-Forwarded-For");
                if (StringUtils.hasText(remoteAddr)) {
                    remoteAddr = remoteAddr.split(",")[0].trim();
                } else {
                    remoteAddr = request.getRemoteAddr();
                }
                request.setAttribute("clientIp", remoteAddr);
                return true;
            }
        });
    }
}