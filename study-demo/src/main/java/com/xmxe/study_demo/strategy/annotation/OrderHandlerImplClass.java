package com.xmxe.study_demo.strategy.annotation;

/**
 * 业务实现类
 */
@OrderHandlerTypeAnnotation(source = "default")
public class OrderHandlerImplClass implements OrderHandler{

    @Override
    public void handle(OrderEntity order) {
       System.out.println("默认");

    }

}
@OrderHandlerTypeAnnotation(source = "mobile")
class MobileOrderHandler implements OrderHandler {
    @Override
    public void handle(OrderEntity order) {
        System.out.println("处理移动端订单");
    }
}

@OrderHandlerTypeAnnotation(source = "pc")
class PCOrderHandler implements OrderHandler {
    @Override
    public void handle(OrderEntity order) {
        System.out.println("处理PC端订单");
    }
}
/**
 * 业务总接口
 */
interface OrderHandler {
    void handle(OrderEntity order);
}