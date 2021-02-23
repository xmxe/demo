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
 * JDK和CGLIB动态代理区别(https://blog.csdn.net/yhl_jxy/article/details/80635012)
 * [设计模式---代理模式](https://www.cnblogs.com/daniels/p/8242592.html)
 * [一文读懂Java动态代理](https://mp.weixin.qq.com/s/1Jxxrbi8nk4pcD8I1ts5lQ)
 * 
 * JDK代理是不需要第三方库支持，只需要JDK环境就可以进行代理，使用条件:
 * 1）实现InvocationHandler 
 * 2）使用Proxy.newProxyInstance产生代理对象
 * 3）被代理的对象必须要实现接口
 * CGLib必须依赖于CGLib的类库，但是它需要类来实现任何接口代理的是指定的类生成一个子类，
 * 覆盖其中的方法，是一种继承但是针对接口编程的环境下推荐使用JDK的代理；
 */
/**
 * 为什么要用代理模式？
 * 中介隔离作用：
 * 在某些情况下，一个客户类不想或者不能直接引用一个委托对象，而代理类对象可以在客户类和委托对象之间起到中介的作用，
 * 其特征是代理类和委托类实现相同的接口。
 * 开闭原则，增加功能：代理类除了是客户类和委托类的中介之外，我们还可以通过给代理类增加额外的功能来扩展委托类的功能，
 * 这样做我们只需要修改代理类而不需要再修改委托类，符合代码设计的开闭原则。代理类主要负责为委托类预处理消息、过滤消息、
 * 把消息转发给委托类，以及事后对返回结果的处理等。代理类本身并不真正实现服务，而是同过调用委托类的相关方法，
 * 来提供特定的服务。真正的业务功能还是由委托类来实现，但是可以在业务功能执行的前后加入一些公共的服务。
 * 例如我们想给项目加入缓存、日志这些功能，我们就可以使用代理类来完成，而没必要打开已经封装好的委托类。
 */