package com.adopt.apigw.filter;

import java.io.IOException;
import java.security.Key;
import java.util.ArrayList;
import java.util.Base64;

import javax.crypto.spec.SecretKeySpec;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import com.adopt.apigw.core.utillity.log.ApplicationLogger;
import com.adopt.apigw.utils.CommonConstants;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.GenericFilterBean;

import com.adopt.apigw.spring.LoggedInUser;
import com.netflix.zuul.context.RequestContext;

@Component
public class MyFilter extends GenericFilterBean {
	
	private static final String MVNO_ID_FROM_APIGW = "mvnoIdFromApigw";
	private static final String STAFF_ID_FROM_APIGW = "staffIdFromApigw";

	private static final String STAFF_USERNAME = "userName";

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {

		UsernamePasswordAuthenticationToken data = getAuthentication((HttpServletRequest) request);
		LoggedInUser loggedInUser = ((LoggedInUser) data.getPrincipal());
		RequestContext ctx = RequestContext.getCurrentContext();
		
		ctx.addZuulRequestHeader(MVNO_ID_FROM_APIGW, loggedInUser.getMvnoId().toString());
		ctx.addZuulRequestHeader(STAFF_ID_FROM_APIGW, loggedInUser.getStaffId().toString());
		ctx.addZuulRequestHeader(STAFF_USERNAME, loggedInUser.getUsername());

		request.setAttribute(MVNO_ID_FROM_APIGW, loggedInUser.getMvnoId());
		request.setAttribute(STAFF_ID_FROM_APIGW, loggedInUser.getStaffId());
		chain.doFilter(request, response);
	}

	private UsernamePasswordAuthenticationToken getAuthentication(HttpServletRequest req) {

		String token = req.getHeader(CommonConstants.AUTHORIZATION_HEADER_STRING);
//		String token = "Bearer eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ7XCJmaXJzdE5hbWVcIjpcImFkbWluXCIsXCJsYXN0TmFtZVwiOlwiYWRtaW5cIixcInVzZXJJZFwiOjIsXCJwYXJ0bmVySWRcIjoxLFwicm9sZXNMaXN0XCI6XCIxXCIsXCJzZXJ2aWNlQXJlYUlkXCI6bnVsbCxcIm12bm9JZFwiOjIsXCJzZXJ2aWNlQXJlYUlkTGlzdFwiOlsxLDIsNCw1LDYsNyw4LDksMTAsMTMsMTQsMTUsMTYsMTcsMTgsMTksMjIsMjMsMjQsMjUsMjcsMjgsMzAsMzEsMzIsMzMsNDcsNDgsNDksNTAsNTEsNTIsNTMsNTQsNTUsNTYsNTcsNTgsNTksNjAsNjEsNjIsNjUsNjYsNjcsNjgsNjksNzYsNzcsNzgsNzksODEsODIsODMsODQsOTBdLFwic3RhZmZJZFwiOjIsXCJidUlkc1wiOltdLFwibGNvXCI6ZmFsc2V9IiwiZXhwIjoxNjc2MDUyNzYzfQ.UdeQCUG6wQPA7tsdJDWie0GpcTgC6H5Mh1KIwiRdeB8";
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
				return new UsernamePasswordAuthenticationToken(user, null, new ArrayList<>());
			}

			return null;
		}

		return null;
	}
}
