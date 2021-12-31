package com.xmxe.study_demo.designpattern.proxy.staticproxy;

public class StaticProxyTest {
    public static void main(String[] args) {
        BuyHouseProxy bh = new BuyHouseProxy();
        bh.buyHouse();
    }
}
