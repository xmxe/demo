package com.xmxe.study_demo.reflect;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

import com.xmxe.study_demo.designpattern.strategy.annotation.OrderHandlerTypeAnnotation;


public class ReflectTest {
    public static void main(String[] args) throws Exception{
        Class<?> clazz = Class.forName("com.xmxe.study_demo.entity.Student");

        // 获取所有pubic修饰的成员变量
        Field[] fields = clazz.getFields();
        for (Field field : fields) {
            System.out.println("获取所有pubic修饰的成员变量==="+field);
        }
        // 获取指定的pulic成员变量
        Field field = clazz.getField("name");
        System.out.println("获取指定的pulic成员变量name===" + field);
        // 获取所有成员变量
        Field[] fields2 =  clazz.getDeclaredFields();
        for (Field field1 : fields2) {
            System.out.println("获取所有成员变量==="+field1);
        }
        // 获取指定成员变量，不考虑修饰符
        Field field3 = clazz.getDeclaredField("age");
        System.out.println("获取指定成员变量，不考虑修饰符==="+field3);

        // 获取所有public 修饰的构造方法，返回一个含有所有public修饰的构造函数对象的数组。
        Constructor<?>[] constructors = clazz.getConstructors();
        for(Constructor<?> constructor : constructors){
            System.out.println("获取所有public 修饰的构造方法==="+constructor);
        }
        // 获取所有构造函数，不考虑修饰符，参数是构造器中参数类型对应的Class对象。
        Constructor<?> con =  clazz.getDeclaredConstructor(String.class, Integer.class);
        System.out.println("获取所有构造函数，不考虑修饰符，参数是构造器中参数类型对应的Class对象==="+con);

        // 获取所有方法，不考虑修饰符
        Method[] methods = clazz.getDeclaredMethods();
        for(Method method : methods){
            System.out.println("获取所有方法，不考虑修饰符==="+method);
        }
        // 根据方法名获取
        Method method = clazz.getMethod("method1", String.class,Integer.class);
        System.out.println("根据方法名获取==="+method);

        // 获取类的全路径名
        String getName = clazz.getName();
        System.out.println("获取类的全路径名==="+getName);

        // 获取方法注解
        Annotation[] annotations = method.getAnnotations();
        for(Annotation annotation : annotations){
            System.out.println("获取方法注解==="+annotation);
        }

        OrderHandlerTypeAnnotation orderHandlerType = method.getAnnotation(OrderHandlerTypeAnnotation.class);
        System.out.println(orderHandlerType.source()+"---"+orderHandlerType.annotationType());
    }

}
/**
 * Java反射机制你还不会？那怎么看Spring源码？(https://mp.weixin.qq.com/s/jV9kE2ajB40f3fOU_lT9ng)
 * Java反射是什么？看这篇绝对会了！(https://mp.weixin.qq.com/s/QbacsQwTyvBJi12LYPNKJw)
 * 学会这篇反射，我就可以去吹牛逼了。(https://mp.weixin.qq.com/s/Dyg4qSqiyjSJTne8yvUYpQ)
 * 深入理解Java：类加载机制及反射 (https://mp.weixin.qq.com/s/kTYLjg_FlKBdAAQQvSAF9g)
 * 
 */