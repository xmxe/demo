package com.xmxe.study_demo.designpattern.factory.factory_method;

import com.xmxe.study_demo.designpattern.factory.simple_factory.Customer;

/**
 * 工厂方法客户接口
 */
public interface CustomerFactory {
    Customer create(String type, String name);
}
