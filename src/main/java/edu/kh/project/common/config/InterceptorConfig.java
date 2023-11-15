package edu.kh.project.common.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import edu.kh.project.common.interceptor.BoardTypeInterceptor;

@Configuration
public class InterceptorConfig implements WebMvcConfigurer{

	@Bean // 방금 만든 interceptor를 bean으로 만듦. 
	public BoardTypeInterceptor boardTypeInterceptor() {
		return new BoardTypeInterceptor();
	}

	
	@Override
	public void addInterceptors(InterceptorRegistry registry) {

		// 우리가 만든 interceptor를 추가함
		registry.addInterceptor(boardTypeInterceptor())
		.addPathPatterns("/**") // 가로챌 경로 지정(여러개 작성 시 , 로 구분)
		.excludePathPatterns("/css/**", "/images/**", "/js/**"); // 가로 채지 않을 경로
		
		// 다른 interceptor 생성 시
		/*
		registry.addInterceptor(boardTypeInterceptor())
		.addPathPatterns("/**") // 가로챌 경로 지정(여러개 작성 시 , 로 구분)
		.excludePathPatterns("/css/**", "/images/**", "/js/**"); // 가로 채지 않을 경로
		*/
	}
	
	
	
}
