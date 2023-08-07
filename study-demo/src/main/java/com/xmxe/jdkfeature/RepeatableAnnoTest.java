package com.xmxe.jdkfeature;

import java.lang.annotation.ElementType;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Method;

/**
 * 重复注解
 */
public class RepeatableAnnoTest {

    public static void main(String[] args) throws Exception{
        Method method = RepeatableAnnoTest.class.getMethod("doSomething");
        ScheduleTime[] myAnnos = method.getAnnotationsByType(ScheduleTime.class);
        for(ScheduleTime anno:myAnnos) {
            System.out.println(anno.value());
        }
    }

    // jdk8写法,可以标注相同注解
    @ScheduleTime("10")
    @ScheduleTime("12")
    // jdk8之前的取巧写法
    // @ScheduleTimes({@ScheduleTime("10"),@ScheduleTime("12")})
    public void doSomething() {}
}

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
/**
 * 重复注解,即一个注解可以在一个类、属性或者方法上同时使用多次;用@Repeatable定义重复注解
 * 在使用@Repeatable注解时,我们需要同时提供一个容器注解(container annotation)来封装可重复注解的多个实例。
 */
@Repeatable(ScheduleTimes.class)
@interface ScheduleTime {
    String value();
}

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@interface ScheduleTimes {
    ScheduleTime[] value();
}

