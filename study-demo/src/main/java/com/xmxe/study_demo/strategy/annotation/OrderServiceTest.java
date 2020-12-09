package com.xmxe.study_demo.strategy.annotation;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.reflections.Reflections;

public class OrderServiceTest {
    public static void main(String[] args) {
        Order order = new Order();
        // order.setSource("default");
        // order.setSource("pc");
        order.setSource("mobile");
        orderService(order);
    }

    private static Map<String,Object> handMap = new HashMap<>();
    static {       
        try {
            // 实例化com.xmxe.study_demo.strategy.annotation下的所有@OrderHandlerType标记的类
            // 注解标记的source的值作为map中key,作用在注解的类作为map中的value
            BeanFactory.init("com.xmxe.study_demo.strategy.annotation");
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
    

    /**
     * 真正处理业务逻辑的函数
     */
    public static void orderService(Order order) {
        // ...一些前置处理
        
        // 通过订单来源确定对应的handler
        OrderHandler orderHandler = (OrderHandler) handMap.get(order.getSource());
        orderHandler.handle(order);

        // ...一些后置处理
    }

}

class BeanFactory {
    
    /**
     *获取注解标记的实现类，将实现类放到map里面 key为source中的值 value为注解标记的实现类
     */
    protected static final Map<String, Object> beanContainer = new HashMap<String, Object>();

    /**
     * 初始化指定包下的所有@OrderHandlerType注解标记的类
     * 
     * @param packageName 初始化包路径
     * @throws InstantiationException
     * @throws IllegalAccessException
     */
    public static void init(String packageName) throws InstantiationException, IllegalAccessException {
        Reflections f = new Reflections(packageName);
        Set<Class<?>> set = f.getTypesAnnotatedWith(OrderHandlerType.class);
        for (Class<?> c : set) {
            Object bean = c.newInstance();
            OrderHandlerType annotation = c.getAnnotation(OrderHandlerType.class);

            beanContainer.put(annotation.source(), bean);
        }
    }

    /**
     * 根据注解名获取实例
     * 
     * @param beanName 注解的名称
     * @return 对应实例
     */
    public static Object getBean(String beanName) {
        return beanContainer.get(beanName);
    }

    /**
     * 根据注解名获取指定类型的实例
     * 
     * @param beanName bean名称，注解指定的value值
     * @param beanClass bean类型
     * @return 指定类型的实例
     */
    public static <T> T getBean(String beanName, Class<T> beanClass) {
        return beanClass.cast(getBean(beanName));
    }


}