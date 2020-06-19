package com.xmxe.study_demo.strategy.demo2;

import java.util.HashMap;
import java.util.Map;

public class OrderService {
    private static Map<String,Object> handMap = new HashMap<>();
    static {       
        try {
            // 实例化com.xmxe.study_demo.strategy.demo2下的所有@OrderHandlerType标记的类
            //注解标记的source的值作为map中key,作用在注解的类作为map中的value
            BeanFactory.init("com.xmxe.study_demo.strategy.demo2");
            handMap = BeanFactory.beanContainer;
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

        // MobileOrderHandler mobile = BeanFactory.getBean("mobile", MobileOrderHandler.class);
        // PCOrderHandler pc = BeanFactory.getBean("pc", PCOrderHandler.class);
        // handMap.put("mobile",mobile);
        // handMap.put("pc",pc);

      
    }
    

    public static void orderService(Order order) {
        // ...一些前置处理
        
        // 通过订单来源确定对应的handler
        OrderHandler orderHandler = (OrderHandler) handMap.get(order.getSource());
        orderHandler.handle(order);

        // ...一些后置处理
    }
    public static void main(String[] args) {
        Order order = new Order();
        // order.setSource("default");
        // order.setSource("pc");
        order.setSource("mobile");
        orderService(order);
    }
}