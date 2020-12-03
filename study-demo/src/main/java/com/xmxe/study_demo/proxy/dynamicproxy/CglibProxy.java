package com.xmxe.study_demo.proxy.dynamicproxy;

import java.lang.reflect.Method;

import org.springframework.cglib.proxy.MethodInterceptor;
import org.springframework.cglib.proxy.MethodProxy;
import org.springframework.cglib.proxy.Enhancer;

public class CglibProxy implements MethodInterceptor {
	/** CGLib需要代理的目标对象 */
	private Object targetObject;
	
	public Object createProxyObject(Object obj) {
        this.targetObject = obj;
        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(obj.getClass());
        enhancer.setCallback(this);
        Object proxyObj = enhancer.create();
        // 返回代理对象
        return proxyObj;
    }


	@Override
	public Object intercept(Object proxyObject, Method method, Object[] objects, MethodProxy methodProxy) throws Throwable {
		Object obj = null;
		if("buyHouse".equals(method.getName())){
			//业务逻辑 ...
			System.out.println("do something...");
		}
		System.out.println("Before Method Invoke");
		obj = method.invoke(targetObject, objects);
		System.out.println("After Method Invoke");
		return obj;
	}

}

