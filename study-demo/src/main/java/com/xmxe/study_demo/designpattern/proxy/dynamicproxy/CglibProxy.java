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
        // 设置目标类的字节码文件,即需要给哪个类创建代理类
        enhancer.setSuperclass(obj.getClass());
        // 设置回调函数 需实现org.springframework.cglib.proxy.Callback接口，
		// 此处我们使用的是org.springframework.cglib.proxy.MethodInterceptor，也是一个接口，实现了Callback接口，
		// 当调用代理对象的任何方法的时候，都会被MethodInterceptor接口的invoke方法处理
        enhancer.setCallback(this);
        // 创建代理类
        Object proxyObj = enhancer.create();
        // 返回代理对象
        return proxyObj;
    }


	/**
	 * 代理对象方法拦截器
	 * @param o 代理对象
	 * @param method 被代理的类的方法，即BuyHouseImpl中的方法
	 * @param objects 调用方法传递的参数
	 * @param methodProxy 方法代理对象
	 */
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

