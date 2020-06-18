package com.xmxe.study_demo.proxy.dynamicproxy;

import java.lang.reflect.Method;

import org.springframework.cglib.proxy.MethodInterceptor;
import org.springframework.cglib.proxy.MethodProxy;

public class CglibProxy implements MethodInterceptor {

	@Override
	public Object intercept(Object object, Method method, Object[] objects, MethodProxy proxy) throws Throwable {
		System.out.println("Before Method Invoke");
		proxy.invokeSuper(object, objects);
		System.out.println("After Method Invoke");
		return object;
	}

}
