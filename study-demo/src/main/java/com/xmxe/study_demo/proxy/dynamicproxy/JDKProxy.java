package com.xmxe.study_demo.proxy.dynamicproxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

public class JDKProxy implements InvocationHandler{
	
	private Object object; 
	
	public JDKProxy(final Object object) {
		this.object = object;
	}

	@Override
	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
		
		System.out.println("买房前准备");
		Object result = method.invoke(object, args);
		System.out.println("买房后装修");
		return result;
	}

	
}
