package com.xmxe.study_demo.designpattern.factory.abstract_factory;

import com.xmxe.study_demo.designpattern.factory.simple_factory.Customer;

public class Test {
    public static void main(String[] args) {
        System.out.println("------工厂模式-抽象工厂------");

        CustomerFactory merchantFactory = new MerchantFactory();
        Customer merchant = merchantFactory.createCustomer("M", "Java技术栈商户");
        CustomerExt merchantExt = merchantFactory.createCustomerExt();
        System.out.println(merchant);
        System.out.println(merchantExt);

        CustomerFactory bankPartnerFactory = new BankPartnerFactory();
        Customer bankPartner = bankPartnerFactory.createCustomer("B", "Java技术栈银行客户");
        CustomerExt bankPartnerExt = bankPartnerFactory.createCustomerExt();
        System.out.println(bankPartner);
        System.out.println(bankPartnerExt);

        CustomerFactory agentFactory = new AgentFactory();
        Customer agent = agentFactory.createCustomer("A", "Java技术栈代理商");
        CustomerExt agentExt = agentFactory.createCustomerExt();
        System.out.println(agent);
        System.out.println(agentExt);
    }

}
