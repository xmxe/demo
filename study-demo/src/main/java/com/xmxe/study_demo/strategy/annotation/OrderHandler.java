package com.xmxe.study_demo.strategy.annotation;

/**
 * 业务总接口
 */
public interface OrderHandler {
    void handle(Order order);
}