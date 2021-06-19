package com.xmxe.study_demo.designpattern.proxy.dynamicproxy;

import java.lang.reflect.Method;

import org.springframework.cglib.proxy.MethodInterceptor;
import org.springframework.cglib.proxy.MethodProxy;
import org.springframework.cglib.proxy.Enhancer;

public class CglibProxy implements MethodInterceptor {

	/** CGLib需要代理的目标对象 */
	private Object targetObject;
	
	public Object createProxyObject(Object obj) {
        this.targetObject = obj;
        // 创建Enhancer对象，类似于JDK动态代理的Proxy类
        Enhancer enhancer = new Enhancer();
        // 设置目标类的字节码文件
        enhancer.setSuperclass(obj.getClass());
        // 设置回调函数
        enhancer.setCallback(this);
        // 创建代理类
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
		System.out.println("Before CglibProxy Method Invoke");
		obj = method.invoke(targetObject, objects);
		System.out.println("After CglibProxy Method Invoke");
		return obj;
	}

}

