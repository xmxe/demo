package com.xmxe.study_demo.designpattern.strategy.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface OrderHandlerTypeAnnotation {
    
    String source() default "default";
}
/**
 * 你知道 Java中的注解是如何工作的？(https://mp.weixin.qq.com/s/kx_111lekaIzkYYYAwjwxg)
 * 深入理解Java：注解(https://mp.weixin.qq.com/s/jmnz25x1pBJAN-tXQWOMJQ)
 * JDK中注解的底层原来是这样实现的(https://mp.weixin.qq.com/s/fWeWHceRqNEEmi-O3GnV0A)
 */