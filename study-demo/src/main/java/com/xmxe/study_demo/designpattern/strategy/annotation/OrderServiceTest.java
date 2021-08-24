package com.xmxe.study_demo.designpattern.strategy.annotation;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import java.math.BigDecimal;

import org.reflections.Reflections;
import org.reflections.scanners.SubTypesScanner;
import org.reflections.scanners.TypeAnnotationsScanner;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;

public class OrderServiceTest {

    public static void main(String[] args){
        OrderEntity order = new OrderEntity();
        // order.setSource("default");
        // order.setSource("pc");
        order.setSource("mobile");
        orderService(order);
    }
    private static Map<String,Object> handMap = new HashMap<>();
    
    static {       
        try {
            // 实例化com.xmxe.study_demo.strategy.annotation下的所有@OrderHandlerTypeAnnotation标记的类
            // 注解标记的source的值作为map中key,作用在注解的类作为map中的value
            ClassFactory.init("com.xmxe.study_demo.designpattern.strategy.annotation");
            handMap = ClassFactory.container;
        } catch (Exception e) {
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
    public static void orderService(OrderEntity order) {
        // ...一些前置处理
        
        // 通过订单来源确定对应的handler
        OrderHandler orderHandler = (OrderHandler) handMap.get(order.getSource());
        orderHandler.handle(order);

        // ...一些后置处理
    }

}

class ClassFactory {
    
    /**
     * 获取注解标记的实现类，将实现类放到map里面 key为注解source中的值 value为注解标记的实现类
     */
    protected static final Map<String, Object> container = new HashMap<String, Object>();

    /**
     * 初始化指定包下的所有@OrderHandlerTypeAnnotation注解标记的类
     * 
     * @param packageName 初始化包路径
     * @throws Exception
     */
    public static void init(String packageName) throws Exception {

        ConfigurationBuilder config = new ConfigurationBuilder();
        config.addUrls(ClasspathHelper.forPackage(packageName));
        config.setScanners(new TypeAnnotationsScanner(),new SubTypesScanner());

        // Reflections f = new Reflections(config);
        Reflections f = new Reflections(packageName);
        Set<Class<?>> set = f.getTypesAnnotatedWith(OrderHandlerTypeAnnotation.class);
        for (Class<?> c : set) {
            Object clazzInstance = c.getDeclaredConstructor().newInstance();
            OrderHandlerTypeAnnotation annotation = c.getAnnotation(OrderHandlerTypeAnnotation.class);
            container.put(annotation.source(), clazzInstance);
        }
    }

    /**
     * 根据注解source获取实例
     * 
     * @param annoName 注解的source值
     * @return 注解标记的对应的实现类
     */
    public static Object getClass(String annoName) {
        return container.get(annoName);
    }

    /**
     * 根据注解source获取指定类型的实例
     * 
     * @param annoName 注解source值
     * @param clazz bean类型
     * @return 指定类型的实例
     */
    public static <T> T getClass(String annoName, Class<T> clazz) {
        return clazz.cast(getClass(annoName));
    }
}

/**
 * 业务总接口
 */
interface OrderHandler {
    void handle(OrderEntity order);
}
/**
 * 策略模式：业务实现类
 */
@OrderHandlerTypeAnnotation(source = "default")
class OrderHandlerImplClass implements OrderHandler{

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
 * 策略模式：实体封装类
 */
class OrderEntity {
    /**
     * 订单来源
     */
    private String source;
    /**
     * 支付方式
     */
    private String payMethod;
    /**
     * 订单编号
     */
    private String code;
    /**
     * 订单金额
     */
    private BigDecimal amount;
    // ...其他的一些字段
    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getPayMethod() {
        return payMethod;
    }

    public void setPayMethod(String payMethod) {
        this.payMethod = payMethod;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }
    
}