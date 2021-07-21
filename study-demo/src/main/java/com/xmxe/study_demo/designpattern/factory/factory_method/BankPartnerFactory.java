package com.xmxe.study_demo.designpattern.factory.factory_method;

import com.xmxe.study_demo.designpattern.factory.simple_factory.BankPartner;
import com.xmxe.study_demo.designpattern.factory.simple_factory.Customer;

/**
 * 银行客户工厂
 */
public class BankPartnerFactory implements CustomerFactory {

    @Override
    public Customer create(String type, String name) {
        return new BankPartner(type, name);
    }

}
