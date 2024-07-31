package com.xmxe.jdkfeature;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

import com.xmxe.designpattern.strategy.annotation.OrderHandlerTypeAnnotation;
import com.xmxe.entity.Student;

public class ReflectTest {
    public static void main(String[] args) throws Exception{
        // 获取class的四种方式
        // 1.知道具体类的情况下可以使用
        // Class<ReflectTest> class1 = ReflectTest.class;

        // 2.通过Class.forName()传入类的全路径获取
        Class<?> clazz = Class.forName("com.xmxe.entity.Student");
        
        // 3.通过对象实例instance.getClass()获取
        // ReflectTest o = new ReflectTest();
        // Class<?> class3 = o.getClass();

        // 4.通过类加载器xxxClassLoader.loadClass()传入类路径获取,通过类加载器获取Class对象不会进行初始化,意味着不进行包括初始化等一系列步骤,静态代码块和静态对象不会得到执行
        // Class<?> class4 = ClassLoader.getSystemClassLoader().loadClass("com.xmxe.entity.Student");

        // 获取所有public修饰的构造方法,返回一个含有所有public修饰的构造函数对象的数组。
        Constructor<?>[] constructors = clazz.getConstructors();
        // 获取所有构造函数,不考虑修饰符,参数是构造器中参数类型对应的Class对象。
        Constructor<?> con =  clazz.getDeclaredConstructor(String.class, Integer.class);
        System.out.println("获取所有构造函数,不考虑修饰符,参数是构造器中参数类型对应的Class对象==="+con);
        for(Constructor<?> constructor : constructors){
            System.out.println("获取所有public修饰的构造方法==="+constructor);
            Student s = (Student)constructor.newInstance();
            System.out.println("通过构造方法获取实例来代替class.newInstance()过时的方法"+s);
        }

        // 获取所有pubic修饰的成员变量
        Field[] fields = clazz.getFields();
        for (Field field : fields) {
            System.out.println("获取所有pubic修饰的成员变量==="+field);
            // field.set(o, "");
        }
        // 获取所有成员变量,getDeclaredFields是获取类本身所有field,包括private
        Field[] fields2 =  clazz.getDeclaredFields();
        for (Field field1 : fields2) {
            System.out.println("获取所有成员变量==="+field1);
        }
        // 获取指定成员变量,不考虑修饰符
        Field field3 = clazz.getDeclaredField("age");
        System.out.println("获取指定成员变量,不考虑修饰符==="+field3);
        
        // 获取所有方法,不考虑修饰符
        Method[] methods = clazz.getDeclaredMethods();
        for(Method method : methods){
            System.out.println("获取所有方法,不考虑修饰符==="+method);
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
        // 根据类型获取注解,参数可以指定一个注解类,返回的是一个注解数组,可以通过此方法来获取重复注解@Repeatable
        OrderHandlerTypeAnnotation[] orderHandlerTypeAnnotations = method.getAnnotationsByType(OrderHandlerTypeAnnotation.class);
        for(OrderHandlerTypeAnnotation orderHandlerTypeAnnotation : orderHandlerTypeAnnotations){
            System.out.println(orderHandlerTypeAnnotation.source());
        }
        OrderHandlerTypeAnnotation orderHandlerType = method.getAnnotation(OrderHandlerTypeAnnotation.class);
        System.out.println(orderHandlerType.source()+"---"+orderHandlerType.annotationType());
    }

}
