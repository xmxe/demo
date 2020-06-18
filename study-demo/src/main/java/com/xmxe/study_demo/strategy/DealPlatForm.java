package com.xmxe.study_demo.strategy;

/**
*策略类 
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