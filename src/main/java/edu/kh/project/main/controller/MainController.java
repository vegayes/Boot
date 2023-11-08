package edu.kh.project.main.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;


@Controller
public class MainController {

	
	@RequestMapping("/")
	public String mainForward(Model model) {
		
//		model.addAttribute("message", "스프링 부트 테스트 중입니다.");
		model.addAttribute("name", "홍길동");
		
		
		// Spring MVC : /webapp/WEB-INF/views/common/main.jsp
		
		// Spring Boot (+ thymeleaf 템플릿 엔진)
		// src/main/resources/templates/common/main.html
		// html이 없어서 vs랑 연계해서 하기. 
		
		return "common/main";
	}
	
	
	
}
