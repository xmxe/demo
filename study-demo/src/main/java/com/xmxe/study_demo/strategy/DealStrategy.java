package com.xmxe.study_demo.strategy;

/**
 * 业务场景 将文章分享到不同的app  不适用 if else 判断 使用策略模式
 */
public interface DealStrategy {
    
    void dealMythod(String option);
}