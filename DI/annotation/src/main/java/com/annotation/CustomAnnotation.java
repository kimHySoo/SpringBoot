package com.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;

@Target(value = {ElementType.FIELD, ElementType.TYPE})
public @interface CustomAnnotation {
	String[] value();
}
