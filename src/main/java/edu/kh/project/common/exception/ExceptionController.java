package edu.kh.project.common.exception;

import org.springframework.web.bind.annotation.ControllerAdvice;

import lombok.experimental.StandardException;

@ControllerAdvice
public class ExceptionController {

	public String exceptionHandler(Exception e) {
		
		e.printStackTrace(); // 에러 내용을 콘솔에 출력
		
		return "error/500"; // /teamplates/error/500.html
	}
	
	
	
}
