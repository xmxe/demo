package com.xmxe.util.excel.auto;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * MyCsvFileUtil的示例中导出的表头是属性名,如果正式的导出通常需要自定义表头名称,我们这里可以使用自定义注解来完成。
 */
@Target({ElementType.METHOD, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface JcExcelName {
 
    String name() default "";
 
}
/**
 * 然后在想导出的类里面,想加看得懂的名字就加,不加就拿属性名
 * class Student{
 *   @JcExcelName("年龄")
 *   private Integer age;
 *   @JcExcelName("姓名")
 *   public String name;
 * }
 */