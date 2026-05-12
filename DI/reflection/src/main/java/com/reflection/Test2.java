package com.reflection;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.lang.reflect.Type;

public class Test2 {
	public static void main(String[] args) throws ClassNotFoundException{
		Class<?> c = Class.forName("com.reflection.Cat");
		Method[] methods = c.getDeclaredMethods();
		for(Method m:methods) {
			System.out.println("메서드 이름: "+m.getName());
			Class<?> returnType = m.getReturnType();
			System.out.println(" - 반환 타입: "+returnType.getName());
			
			Parameter[] params = m.getParameters();
			for(Parameter p:params) {
				Type t = p.getParameterizedType();
				System.out.println(" - 파라미터 이름: "+p.getName());
				System.out.println(" / 파라미터의 타입: "+t.getTypeName());
			}
		}
	}
}
