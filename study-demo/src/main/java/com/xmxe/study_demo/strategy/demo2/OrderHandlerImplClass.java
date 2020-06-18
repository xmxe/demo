package com.xmxe.study_demo.strategy.demo2;

/**
 * 业务实现类
 */
@OrderHandlerType(source = "default")
public class OrderHandlerImplClass implements OrderHandler{

    @Override
    public void handle(Order order) {
       System.out.println("默认");

    }

}
@OrderHandlerType(source = "mobile")
class MobileOrderHandler implements OrderHandler {
    @Override
    public void handle(Order order) {
        System.out.println("处理移动端订单");
    }
}

@OrderHandlerType(source = "pc")
class PCOrderHandler implements OrderHandler {
    @Override
    public void handle(Order order) {
        System.out.println("处理PC端订单");
    }
}