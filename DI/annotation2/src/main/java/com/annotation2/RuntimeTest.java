package com.annotation2;

public class RuntimeTest {
	public static void main(String[] args) throws ClassNotFoundException{
		Class<?> c = Class.forName("com.annotation.Cat");
		RuntimeAnnotation r = c.getAnnotation(RuntimeAnnotation.class);
		System.out.println(r);
		CustomAnnotation ca = c.getAnnotation(CustomAnnotation.class);
		System.out.println(ca);
	}
}
