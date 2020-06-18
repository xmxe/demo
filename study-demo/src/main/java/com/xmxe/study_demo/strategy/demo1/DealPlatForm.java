package com.xmxe.study_demo.strategy.demo1;

/**
*统一业务接口实现类（策略类） 业务逻辑越多（if else越多） 策略类越多 
*/
public class DealPlatForm implements DealStrategy{

    @Override
    public void dealMythod(String option) {

        System.out.println("默认~~~");
    }

}
class DealSina implements DealStrategy{

    @Override
    public void dealMythod(String option) {
        System.out.println("新浪~~~");
    }

}
class DealQQ implements DealStrategy{

    @Override
    public void dealMythod(String option) {
        System.out.println("QQ~~~");
    }

}