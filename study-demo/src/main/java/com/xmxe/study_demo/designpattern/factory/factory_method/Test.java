package com.xmxe.study_demo.designpattern.factory.factory_method;

import com.xmxe.study_demo.designpattern.factory.simple_factory.Customer;

public class Test {
    public static void main(String[] args) {
        System.out.println("------工厂模式-工厂方法------");

        CustomerFactory merchantFactory = new MerchantFactory();
        Customer merchant = merchantFactory.create("M", "Java技术栈商户");
        System.out.println(merchant);

        CustomerFactory bankPartnerFactory = new BankPartnerFactory();
        Customer bankPartner = bankPartnerFactory.create("B", "Java技术栈银行客户");
        System.out.println(bankPartner);

        CustomerFactory agentFactory  = new AgentFactory();
        Customer agent = agentFactory.create("A", "Java技术栈代理商");
        System.out.println(agent);
    }
}
