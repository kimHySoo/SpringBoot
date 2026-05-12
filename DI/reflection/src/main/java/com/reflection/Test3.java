package com.reflection;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class Test3 {
	public static void main(String[] args) throws ClassNotFoundException, NoSuchMethodException, SecurityException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		Class<?> c = Class.forName("com.ssafy.reflection.Cat");
		Constructor<?> constructor = c.getDeclaredConstructor(); 
		Object cat = constructor.newInstance();
		System.out.println(cat);
		
		Method m = c.getDeclaredMethod("setName", String.class);
		m.invoke(cat, "Dasy");
		
		Method m2 = c.getDeclaredMethod("setAge", int.class);
		m2.invoke(cat, 2);
		
		System.out.println(cat);
		
	}

}
