package com.ssafy.hello.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Controller
public class HelloController {
	
	@GetMapping("/hello")
	public String hello(HttpServletRequest request, HttpServletResponse response) {
		
		//새로운 message 값을 넘기겠다
		request.setAttribute("message", "Hello SpringBoot!");
		
		//우리가 가게될 view의 논리 이름 반환
		return "hello";
	}
}
