package com.xmxe.study_demo.jdkfeature;

import java.lang.annotation.ElementType;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

// @Retention(RetentionPolicy.RUNTIME)
// @Target(ElementType.METHOD)
/**
 * 重复注解，即一个注解可以在一个类、属性或者方法上同时使用多次；用@Repeatable定义重复注解
 */
@Repeatable(ScheduleTimes.class)
public @interface ScheduleTime {
    String value();
}

@interface ScheduleTimes {
    ScheduleTime[] value();
}

class ScheduleTimeTask {
    @ScheduleTime("10")
    @ScheduleTime("12")
    public void doSomething() {}
}
