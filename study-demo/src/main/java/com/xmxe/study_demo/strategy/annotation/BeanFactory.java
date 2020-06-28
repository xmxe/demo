package com.xmxe.study_demo.strategy.annotation;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.reflections.Reflections;

public class BeanFactory {
    
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