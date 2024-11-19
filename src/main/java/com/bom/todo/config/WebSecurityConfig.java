package com.bom.todo.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.filter.CorsFilter;

import com.bom.todo.security.JwtAuthenticationFilter;

@Configuration
public class WebSecurityConfig {
	
	@Autowired
	private JwtAuthenticationFilter jwtAuthenticationFilter;
	
	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		// http 시큐리티 빌더
		http.cors() // WebMvcConfig에서 이미 설정했으므로 기본 cors 설정
		.and()
		.csrf() // csrf는 현재 사용하지 않으므로 disable
			.disable()
		.httpBasic() // token을 사용하므로 basic 인증 disable
			.disable()
		.sessionManagement() // session 기반이 아님을 선언
			.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
		.and()
		.authorizeRequests() // /와 /auth/** 경로는 인증 안 해도 됨
			.antMatchers("/", "/auth/**").permitAll()
		.anyRequest() // /와 /auth/** 이외의 모든 경로는 인증해야 됨
			.authenticated();
		
		// filter 등록
		// 매 요청마다
		// CorsFilter 실행한 후에
		// jwtAuthenticationFilter 실행
		http.addFilterAfter(jwtAuthenticationFilter, CorsFilter.class);
		
		return http.build();
	}
}
