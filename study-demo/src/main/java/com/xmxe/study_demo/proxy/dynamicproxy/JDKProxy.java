package com.xmxe.study_demo.proxy.dynamicproxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

public class JDKProxy implements InvocationHandler{
	
	// 需要代理的目标对象
	private Object object; 
	
	 /**
     * 将目标对象传入进行代理
     */
    public Object newProxy(Object targetObject) {
        this.object = targetObject;
        //返回代理对象
        return Proxy.newProxyInstance(targetObject.getClass().getClassLoader(),
                targetObject.getClass().getInterfaces(), this);
	}

	@Override
	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
		// 一般先写业务逻辑处理
		System.out.println("买房前准备");
		Object result = method.invoke(object, args);
		System.out.println("买房后装修");
		return result;
	}

	
}
