package com.xmxe.study_demo.designpattern.factory.simple_factory;

public class Test {
    public static void main(String[] args) {
        Customer merchant = CustomerFactory.create("M", "Java技术栈商户");
        System.out.println(merchant);

        Customer bankPartner = CustomerFactory.create("B", "Java技术栈银行客户");
        System.out.println(bankPartner);

        Customer agent = CustomerFactory.create("A", "Java技术栈代理商");
        System.out.println(agent);
    }
}
