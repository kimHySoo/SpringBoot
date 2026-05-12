package com.reflection;

public class Test {
	public static void main(String[] args) throws ClassNotFoundException{
		
		Class<Cat> c1 = Cat.class;
		Cat cat = new Cat("luna", 1);
		Class<?> c2 = cat.getClass();
		Class<?> c3 = Class.forName("com.reflection.Cat");
		
		System.out.println(c1==c2);
		System.out.println(c3==c2);
		System.out.println(c1.getName());
		System.out.println(c1.getSimpleName());
		
	}
}
