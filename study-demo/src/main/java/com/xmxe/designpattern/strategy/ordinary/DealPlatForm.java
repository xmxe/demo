package com.xmxe.designpattern.strategy.ordinary;

/**
 * 策略模式优化if else
 * 统一业务接口实现类（策略类）.业务逻辑越多(if else越多)策略类越多
 */
public class DealPlatForm implements DealStrategy {

    @Override
    public void dealMythod(String option) {
        System.out.println("默认~~~");
    }

}

class DealSina implements DealStrategy {

    @Override
    public void dealMythod(String option) {
        System.out.println("新浪分享~~~");
    }

}

class DealQQ implements DealStrategy {

    @Override
    public void dealMythod(String option) {
        System.out.println("QQ分享~~~");
    }

}

/**
 * 定义通用'分享'的业务接口
 * 
 */
interface DealStrategy {

    void dealMythod(String option);
}