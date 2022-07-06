package com.xmxe.study_demo.designpattern.proxy.dynamicproxy;


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
		System.out.println("this is dynamicproxy BuyHouseImpl");
	}

}
interface BuyHouse {
	void buyHouse();
}
