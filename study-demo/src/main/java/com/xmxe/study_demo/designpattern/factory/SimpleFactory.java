package com.xmxe.study_demo.designpattern.factory;

public class SimpleFactory {
    public static void main(String[] args) {
        Customer2 merchant = CustomerFactory2.create("M", "Java技术栈商户");
        System.out.println(merchant);

        Customer2 bankPartner = CustomerFactory2.create("B", "Java技术栈银行客户");
        System.out.println(bankPartner);

        Customer2 agent = CustomerFactory2.create("A", "Java技术栈代理商");
        System.out.println(agent);
    }
}

/**
 * 客户
 */
abstract class Customer2 {

    /**
     * 客户名称
     */
    private String name;

    /**
     * 客户类型
     */
    private String type;

    public Customer2(){}
    public Customer2(String name,String type){
        this.name = name;
        this.type = type;
    }

    public void setName(String name){
        this.name = name;
    }
    public String getName(){
        return name;
    }
    
    public void setType(String type){
        this.type = type;
    }
    public String getType(){
        return type;
    }

    @Override
    public String toString(){
        return "name="+name+"type="+type;
    }

}

/**
 * 客户简单工厂
 */
class CustomerFactory2 {

    private static Merchant2 createMerchant(String type, String name) {
        return new Merchant2(type, name);
    }

    private static BankPartner2 createBankPartner(String type, String name) {
        return new BankPartner2(type, name);
    }

    private static Agent2 createAgent(String type, String name) {
        return new Agent2(type, name);
    }

    public static Customer2 create(String type, String name) {
        if ("M".equals(type)) {
            return createMerchant(type, name);
        } else if ("B".equals(type)) {
            return createBankPartner(type, name);
        } else if ("A".equals(type)) {
            return createAgent(type, name);
        }
        return null;
    }

}

/**
 * 代理商
 */
class Agent2 extends Customer2 {

    /**
     * 代理周期
     */
    private int period;

    /**
     * 代理产品
     */
    private int[] products;

    public Agent2(String name, String type) {
        super(name, type);
    }

    public void setPeriod(int period){
        this.period = period;
    }
    public int getPeriod(){
        return period;
    }

    public void setProducts(int[] products){
        this.products = products;
    }
    public int[] getProducts(){
        return products;
    }
}

class Merchant2 extends Customer2 {

    /**
     * 合同类型
     */
    private int contractType;

    /**
     * 结算周期（天）
     */
    private int settmentDays;

    public Merchant2(String name, String type) {
        super(name, type);
    }

    public void setContractType(int contractType){
        this.contractType = contractType;
    }
    public int getContractType(){
        return contractType;
    }

    public void setSettmentDays(int settmentDays){
        this.settmentDays = settmentDays;
    }
    public int getSettmentDays(){
        return settmentDays;
    }
}

class BankPartner2 extends Customer2 {

    /**
     * 银行编码
     */
    private String code;

    /**
     * 银行地址
     */
    private String address;

    public BankPartner2(String name, String type) {
        super(name, type);
    }

    public void setCode(String code){
        this.code = code;
    }
    public String getCode(){
        return code;
    }

    public void setAddress(String address){
        this.address = address;
    }
    public String getAddress(){
        return address;
    }
}