package com.learningtest.spring.pointcut;

import org.junit.Test;
import org.springframework.aop.aspectj.AspectJExpressionPointcut;

import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;
import static org.mockito.Mockito.*;

import org.mockito.runners.MockitoJUnitRunner;
import org.junit.*;
import org.junit.runner.*;
import org.mockito.*;

import com.learningtest.spring.pointcut.Bean;
import com.learningtest.spring.pointcut.Target;

public class PointcutExpressionTest { 
	
	public void tagetClassPointcutMatches(String expression, boolean... expected) throws Exception {
		pointcutMatches(expression, expected[0], Target.class, "hello");
	}
	
	public void pointcutMatches(String expression, Boolean expected, Class<?> clazz, String methodName, Class<?>... args) throws Exception {
		AspectJExpressionPointcut pointcut = new AspectJExpressionPointcut();
		pointcut.setExpression(expression);
		
		assertThat(pointcut.getClassFilter().matches(clazz) 
				   && pointcut.getMethodMatcher().matches(clazz.getMethod(methodName, args), null),
				   is(expected));
	}

	@Test
	public void methodSignaturePointcut() throws SecurityException, NoSuchMethodException {
		
		AspectJExpressionPointcut pointcut = new AspectJExpressionPointcut();
//		pointcut.setExpression("execution(public int com.learningtest.spring.pointcut.Target.minus(int, int) throws java.lang.RuntimeException)");
		pointcut.setExpression("execution(* minus(int,int))");
		
		assertThat(pointcut.getClassFilter().matches(Target.class) &&
				   pointcut.getMethodMatcher().matches(
					  Target.class.getMethod("minus", int.class, int.class), null), is(true));
		
		assertThat(pointcut.getClassFilter().matches(Target.class) &&
				   pointcut.getMethodMatcher().matches(
					  Target.class.getMethod("plus", int.class, int.class), null), is(false));

		assertThat(pointcut.getClassFilter().matches(Bean.class) &&
				pointcut.getMethodMatcher().matches(
						Target.class.getMethod("method"), null), is(false));
	}
	
}
