package com.xmxe.study_demo.proxy.staticproxy.impl;

import com.xmxe.study_demo.proxy.staticproxy.BuyHouse;

public class BuyHouseProxy implements BuyHouse {
	
	private BuyHouse bh = new BuyHouseImpl();
	
	@Override
	public void buyHouse() {
		
		System.out.println("BuyHouseProxy1:");
		bh.buyHouse();
		System.out.println("BuyHouseProxy2:");
	}

}
