package com.xmxe.study_demo.strategy.ordinary;

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
       dealStrategy.dealMythod(type);
    }
    
}