package com.xmxe.designpattern.proxy;

import com.xmxe.designpattern.proxy.dynamic.CglibProxy;
import com.xmxe.designpattern.proxy.dynamic.JDKProxy;
import com.xmxe.designpattern.proxy.static_.BuyHouseProxy;

public class ProxyTest {

    public static void main(String[] args) {
        // 静态代理测试
        staticProxy();
        // jdk动态代理
        jdkDynamicProxy();
        // cglib动态代理
        cglibDynamicProxy();
    }

    public static void staticProxy() {
        BuyHouseProxy bh = new BuyHouseProxy();
        bh.buyHouse();
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
