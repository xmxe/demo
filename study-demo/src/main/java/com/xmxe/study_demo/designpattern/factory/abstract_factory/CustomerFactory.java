package com.xmxe.study_demo.designpattern.factory.abstract_factory;

import com.xmxe.study_demo.designpattern.factory.simple_factory.Customer;

/**
 * 抽象工厂客户接口
 */
public interface CustomerFactory {
    
    Customer createCustomer(String type, String name);

    CustomerExt createCustomerExt();
}
