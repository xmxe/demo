package com.xmxe.designpattern.proxy.static_;

import com.xmxe.designpattern.proxy.BuyHouse;
import com.xmxe.designpattern.proxy.BuyHouseImpl;

public class BuyHouseProxy implements BuyHouse {

    private BuyHouse bh = new BuyHouseImpl();

    @Override
    public void buyHouse() {
        System.out.println("before staticproxy");
        bh.buyHouse();
        System.out.println("after staticproxy");
    }

}
