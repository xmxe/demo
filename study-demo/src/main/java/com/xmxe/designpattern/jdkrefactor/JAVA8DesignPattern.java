package com.xmxe.designpattern.jdkrefactor;

/*
 * 利用Java8新特征,重构传统设计模式,你学会了吗？https://mp.weixin.qq.com/s/xAxX9UBaSeoMTSGLbCPg7A
 */
public class JAVA8DesignPattern {

    class 策略模式 {
        interface OrderService {
            void saveOrder(String orderNo);
        }

        static class OrderServiceExecutor {

            private final OrderService service;

            public OrderServiceExecutor(OrderService service) {
                this.service = service;
            }

            public void save(String orderNo) {
                this.service.saveOrder(orderNo);
            }

        }

        public static void main(String[] args) {
            OrderServiceExecutor executor1 = new OrderServiceExecutor(
                    (String orderNo) -> System.out.println("order:" + orderNo + " save to mysql"));
            executor1.save("001");

            OrderServiceExecutor executor2 = new OrderServiceExecutor(
                    (String orderNo) -> System.out.println("order:" + orderNo + " save to nosql"));
            executor2.save("002");
        }
    }

}
