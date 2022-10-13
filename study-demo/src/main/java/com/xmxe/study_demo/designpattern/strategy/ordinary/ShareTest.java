package com.xmxe.study_demo.designpattern.strategy.ordinary;

import java.util.ArrayList;
import java.util.List;

public class ShareTest {
    public static void main(String[] args) {
        shareOptions("Sina");
    }

    private static List<DealContext> algs = new ArrayList<>();
   //静态代码块,先加载所有的策略
    static {
       algs.add(new DealContext("Sina",new DealSina()));
       algs.add(new DealContext("QQ",new DealQQ()));
    }
    public static void shareOptions(String type){
       DealStrategy dealStrategy = null;
       for (DealContext deal : algs) {
           if (deal.options(type)) {
               dealStrategy = deal.getDeal();
               break;
            }  
        }
        if(dealStrategy != null)
            dealStrategy.dealMythod(type);
    }   
}

/**
 * 封装实体类 
 */
class DealContext {
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