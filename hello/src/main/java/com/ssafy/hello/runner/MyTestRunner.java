package com.ssafy.hello.runner;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;


// 빈으로 등록해주세요...!!
@Component
public class MyTestRunner implements CommandLineRunner {
	
	// 우리 설정에 있는 메시지와 숫자값을 가져오겠다...! (필드)
	
	@Value("${my.custom.msg}")
	private String message;
	
	@Value("${my-custom-number}")
	private int number;

	@Override
	public void run(String... args) throws Exception {
		// TODO Auto-generated method stub
		System.out.println("내가 가져온 값에 대한 출력");
		System.out.println("가져온 메시지: " + message);
		System.out.println("가져온 숫자: " + number);
	}

}
