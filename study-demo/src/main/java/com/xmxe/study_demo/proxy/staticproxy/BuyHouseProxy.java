package com.xmxe.study_demo.proxy.staticproxy;

public class BuyHouseProxy implements BuyHouse {
	
	private BuyHouse bh = new BuyHouseImpl();
	
	@Override
	public void buyHouse() {
		
		System.out.println("BuyHouseProxy1:");
		bh.buyHouse();
		System.out.println("BuyHouseProxy2:");
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

