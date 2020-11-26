package com.xmxe.study_demo.proxy.dynamicproxy;

import java.lang.reflect.Proxy;

import org.springframework.cglib.proxy.Enhancer;

public class DynamicProxtTest {
    public static void main(String[] args) {
        jdkDynamicProxy();
        cglibDynamicProxy();
    }

	public static void jdkDynamicProxy() {
		BuyHouse buyHouse = new BuyHouseImpl();
		BuyHouse proxyBuyHouse = (BuyHouse) Proxy.newProxyInstance(BuyHouse.class.getClassLoader(),
				new Class[] { BuyHouse.class }, new JDKProxy(buyHouse));
		proxyBuyHouse.buyHouse();
    }
    
    public static void cglibDynamicProxy() {
		CglibProxy daoProxy = new CglibProxy();
		Enhancer enhancer = new Enhancer();
		enhancer.setSuperclass(Dao.class);
		enhancer.setCallback(daoProxy);
		Dao dao = (Dao) enhancer.create();
		dao.update();
		dao.select();
	}
}
class Dao {
	public void update() {
        System.out.println("PeopleDao.update()");
    }
    
    public void select() {
        System.out.println("PeopleDao.select()");
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
 */