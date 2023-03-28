package com.xmxe.study_demo.designpattern.factory;

public class FactoryMethod {
    public static void main(String[] args) {
        System.out.println("------工厂模式-工厂方法------");

        CustomerFactory1 merchantFactory = new MerchantFactory1();
        Customer1 merchant = merchantFactory.create("M", "Java技术栈商户");
        System.out.println(merchant);

        CustomerFactory1 bankPartnerFactory = new BankPartnerFactory1();
        Customer1 bankPartner = bankPartnerFactory.create("B", "Java技术栈银行客户");
        System.out.println(bankPartner);

        CustomerFactory1 agentFactory = new AgentFactory1();
        Customer1 agent = agentFactory.create("A", "Java技术栈代理商");
        System.out.println(agent);
    }
}

/**
 * 客户
 */
abstract class Customer1 {

    /**
     * 客户名称
     */
    private String name;

    /**
     * 客户类型
     */
    private String type;

    public Customer1() {
    }

    public Customer1(String name, String type) {
        this.name = name;
        this.type = type;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }

    @Override
    public String toString() {
        return "name=" + name + "type=" + type;
    }

}

/**
 * 工厂方法客户接口
 */
interface CustomerFactory1 {
    Customer1 create(String type, String name);
}

class Merchant1 extends Customer1 {

    /**
     * 合同类型
     */
    private int contractType;

    /**
     * 结算周期（天）
     */
    private int settmentDays;

    public Merchant1(String name, String type) {
        super(name, type);
    }

    public void setContractType(int contractType) {
        this.contractType = contractType;
    }

    public int getContractType() {
        return contractType;
    }

    public void setSettmentDays(int settmentDays) {
        this.settmentDays = settmentDays;
    }

    public int getSettmentDays() {
        return settmentDays;
    }
}

/**
 * 商户工厂
 */
class MerchantFactory1 implements CustomerFactory1 {

    @Override
    public Customer1 create(String type, String name) {
        return new Merchant1(type, name);
    }

}

/**
 * 代理商
 */
class Agent1 extends Customer1 {

    /**
     * 代理周期
     */
    private int period;

    /**
     * 代理产品
     */
    private int[] products;

    public Agent1(String name, String type) {
        super(name, type);
    }

    public void setPeriod(int period) {
        this.period = period;
    }

    public int getPeriod() {
        return period;
    }

    public void setProducts(int[] products) {
        this.products = products;
    }

    public int[] getProducts() {
        return products;
    }
}

/**
 * 代理商工厂
 */
class AgentFactory1 implements CustomerFactory1 {

    @Override
    public Customer1 create(String type, String name) {
        return new Agent1(type, name);
    }

}

class BankPartner1 extends Customer1 {

    /**
     * 银行编码
     */
    private String code;

    /**
     * 银行地址
     */
    private String address;

    public BankPartner1(String name, String type) {
        super(name, type);
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getAddress() {
        return address;
    }
}

/**
 * 银行客户工厂
 */
class BankPartnerFactory1 implements CustomerFactory1 {

    @Override
    public Customer1 create(String type, String name) {
        return new BankPartner1(type, name);
    }

}