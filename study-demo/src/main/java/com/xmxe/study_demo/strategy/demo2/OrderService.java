package com.xmxe.study_demo.strategy.demo2;

import java.util.HashMap;
import java.util.Map;

public class OrderService {
    private static Map<String,OrderHandler> handMap = new HashMap<>();
    static {
        // 实例化com.xmxe.study_demo.strategy.demo2下的所有@OrderHandlerType标记的类
        try {
            BeanFactory.init("com.xmxe.study_demo.strategy.demo2");
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

        MobileOrderHandler mobile = BeanFactory.getBean("mobile", MobileOrderHandler.class);
        PCOrderHandler pc = BeanFactory.getBean("pc", PCOrderHandler.class);
        handMap.put("mobile",mobile);
        handMap.put("pc",pc);
      
    }
    

    public static void orderService(Order order) {
        // ...一些前置处理
        
        // 通过订单来源确定对应的handler
        OrderHandler orderHandler = handMap.get(order.getSource());
        orderHandler.handle(order);

        // ...一些后置处理
    }
    public static void main(String[] args) {
        Order order = new Order();
        order.setSource("mobile");
        orderService(order);
    }
}