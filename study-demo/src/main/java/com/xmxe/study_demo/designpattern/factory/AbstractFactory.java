package com.xmxe.study_demo.designpattern.factory;


/**
 * 最全工厂设计模式案例详解，不服来辩！(https://mp.weixin.qq.com/s/uPl3MpA38ZwOwk6VWtdipA)
 */
public class AbstractFactory {
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

/**
 * 抽象工厂客户接口
 */
interface CustomerFactory {
    // 创建客户
    Customer createCustomer(String type, String name);
    // 创建扩展客户
    CustomerExt createCustomerExt();
}

/**
 * 客户
 */
abstract class Customer {

    /**
     * 客户名称
     */
    private String name;

    /**
     * 客户类型
     */
    private String type;

    public Customer(){}
    public Customer(String name,String type){
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
 * 扩展客户抽象类
 */
abstract class CustomerExt {

    /**
     * 客户曾用名
     */
    private String formerName;

    /**
     * 客户扩展说明
     */
    private String note;

    public CustomerExt(){}
    public CustomerExt(String formerName){
        this.formerName = formerName;
    }

    public void setFormerName(String formerName){
        this.formerName = formerName;
    }
    public String getFormerName(){
        return formerName;
    }
    public void setNote(String note){
        this.note = note;
    }
    public String getNote(){
        return note;
    }

    
    @Override
    public String toString(){
        return "formerName="+formerName+"note="+note;
    }

}
/**
 * 代理商
 */
class AgentExt extends CustomerExt {

    /**
     * 来源
     */
    private String source;

    /**
     * 资质
     */
    private String certification;

    public void setSource(String source){
        this.source = source;
    }
    public String getSource(){
        return source;
    }

    public void setCertification(String certification){
        this.certification = certification;
    }
    public String getCertification(){
        return certification;
    }

    
    @Override
    public String toString(){
        return super.toString();
    }

}
/**
 * 代理商
 */
class Agent extends Customer {

    /**
     * 代理周期
     */
    private int period;

    /**
     * 代理产品
     */
    private int[] products;

    public Agent(String name, String type) {
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

    
    @Override
    public String toString(){
        return super.toString();
    }
}

/**
 * 代理商工厂
 */

class AgentFactory implements CustomerFactory {

    @Override
    public Customer createCustomer(String type, String name) {
        return new Agent(type, name);
    }

    @Override
    public CustomerExt createCustomerExt() {
        return new AgentExt();
    }

}
/**
 * 银行客户扩展
 */
class BankPartnerExt extends CustomerExt {

    /**
     * 分行个数
     */
    private int branchCount;

    /**
     * ATM个数
     */
    private int atmCount;

    public void setbranchCount(int branchCount){
        this.branchCount = branchCount;
    }
    public int getBranchCount(){
        return branchCount;
    }

    public void setAtmCount(int atmCount){
        this.atmCount = atmCount;
    }
    public int getCertification(){
        return atmCount;
    }

    
    @Override
    public String toString(){
        return super.toString();
    }

}
class BankPartner extends Customer {

    /**
     * 银行编码
     */
    private String code;

    /**
     * 银行地址
     */
    private String address;

    public BankPartner(String name, String type) {
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

    
    @Override
    public String toString(){
        return super.toString();
    }
}

/**
 * 银行客户工厂
 */
class BankPartnerFactory implements CustomerFactory {

    @Override
    public Customer createCustomer(String type, String name) {
        return new BankPartner(type, name);
    }

    @Override
    public CustomerExt createCustomerExt() {
        return new BankPartnerExt();
    }

}
/**
 * 商户
 */
class MerchantExt extends CustomerExt {

    /**
     * 介绍人
     */
    private int introduceName;

    /**
     * 介绍人电话
     */
    private String introduceTel;

    public void setIntroduceName(int introduceName){
        this.introduceName = introduceName;
    }
    public int getIntroduceName(){
        return introduceName;
    }

    public void setIntroduceTel(String introduceTel){
        this.introduceTel = introduceTel;
    }
    public String getIntroduceTel(){
        return introduceTel;
    }

    
    @Override
    public String toString(){
        return super.toString();
    }

}

class Merchant extends Customer {

    /**
     * 合同类型
     */
    private int contractType;

    /**
     * 结算周期（天）
     */
    private int settmentDays;

    public Merchant(String name, String type) {
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

    @Override
    public String toString(){
        return super.toString();
    }
}

/**
 * 商户工厂
 */
class MerchantFactory implements CustomerFactory {

    @Override
    public Customer createCustomer(String type, String name) {
        return new Merchant(type, name);
    }

    @Override
    public CustomerExt createCustomerExt() {
        return new MerchantExt();
    }

}