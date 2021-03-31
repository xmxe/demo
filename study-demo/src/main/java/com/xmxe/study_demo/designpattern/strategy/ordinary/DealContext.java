package com.xmxe.study_demo.designpattern.strategy.ordinary;

/**
 * 封装实体类 
 */
public class DealContext {
    private String type;// type为分享的app类型 如qq sina 
    private DealStrategy deal;// DealStrategy为统一业务封装接口
    
    public  DealContext(String type,DealStrategy deal){
       this.type = type;
       this.deal = deal;
   }
    public DealStrategy getDeal(){
       return deal;
   }
    public boolean options(String type){
       return this.type.equals(type);
   }
}