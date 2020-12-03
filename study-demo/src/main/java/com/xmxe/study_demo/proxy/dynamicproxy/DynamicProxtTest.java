package com.xmxe.study_demo.proxy.dynamicproxy;


public class DynamicProxtTest {
    public static void main(String[] args) {
        jdkDynamicProxy();
        cglibDynamicProxy();
    }

	public static void jdkDynamicProxy() {
		JDKProxy jdkProxy = new JDKProxy();
		// 创建代理对象
		BuyHouse proxyBuyHouse = (BuyHouse)jdkProxy.newProxy(new BuyHouseImpl());
		proxyBuyHouse.buyHouse();
    }
    
    public static void cglibDynamicProxy() {
		CglibProxy cglibProxy = new CglibProxy();
		BuyHouse buyHouse = (BuyHouse) cglibProxy.createProxyObject(new BuyHouseImpl());
		buyHouse.buyHouse();
	}
}

class BuyHouseImpl implements BuyHouse {

	@Override
	public void buyHouse() {
		
		System.out.println("BuyHouseImpl:");
	}

}
interface BuyHouse {
	void buyHouse();
}
/**
 * 终于有人把 java代理 讲清楚了，万字详解！(https://mp.weixin.qq.com/s/C9Vpfcgl3NB_0rBpLh2yCA)
 * 【Spring基础】JDK和CGLIB动态代理区别(https://blog.csdn.net/yhl_jxy/article/details/80635012)
 * 
 * JDK代理是不需要第三方库支持，只需要JDK环境就可以进行代理，使用条件:
 * 1）实现InvocationHandler 
 * 2）使用Proxy.newProxyInstance产生代理对象
 * 3）被代理的对象必须要实现接口
 * CGLib必须依赖于CGLib的类库，但是它需要类来实现任何接口代理的是指定的类生成一个子类，
 * 覆盖其中的方法，是一种继承但是针对接口编程的环境下推荐使用JDK的代理；
 */