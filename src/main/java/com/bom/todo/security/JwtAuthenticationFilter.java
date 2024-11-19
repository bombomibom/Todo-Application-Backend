package com.bom.todo.security;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter { // HttpFilter 대신
	
	@Autowired
	private TokenProvider tokenProvider;
	
	@Override
	protected void doFilterInternal(HttpServletRequest request, 
									HttpServletResponse response, 
									FilterChain filterChain) 
			throws ServletException, IOException { // doFilter 대신 
		try {
			// (1) 요청에서 토큰 가져오기
			String token = parseBearerToken(request);
			log.info("Filter is running...");
			if (token != null && !token.equalsIgnoreCase("null")) {
				// (2) TokenProvider를 이용해 토큰 인증 및 userId 가져오기. 위조된 경우 예외 처리
				String userId = tokenProvider.validateAndGetUserId(token);
				log.info("Authenticated user ID: " + userId);
				// (3) 인증 완료. UsernamePasswordAuthenticationToken 오브젝트에 인증 정보 저장. SecurityContextHolder에 등록해야 인증된 사용자라고 생각
				AbstractAuthenticationToken authentication = 
						new UsernamePasswordAuthenticationToken(userId, null, AuthorityUtils.NO_AUTHORITIES);
				authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
				SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
				securityContext.setAuthentication(authentication);
				SecurityContextHolder.setContext(securityContext);
				
				// TODO: 유효시간 검사 추가
			}
		} catch (Exception e) {
			logger.error("Could not set user authentication in security context", e);
		}
		
		filterChain.doFilter(request, response);
	}
	
	private String parseBearerToken(HttpServletRequest request) {
		// Http 요청의 헤더를 파싱해 Bearer 토큰을 리턴
		String bearerToken = request.getHeader("Authorization");
		
		if(StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
			return bearerToken.substring(7);
		}
		
		return null;
	}
}
