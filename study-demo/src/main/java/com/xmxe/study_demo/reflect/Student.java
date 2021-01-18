package com.xmxe.study_demo.reflect;

import com.xmxe.study_demo.strategy.annotation.OrderHandlerTypeAnnotation;

public class Student {
    private Integer age;
    public String name;
    protected String gender;

    public Student(Integer age,String name){
        this(name, age);
    }

    private Student(String name,Integer age){
        this.name = name;
        this.age = age;
    }
    
    @OrderHandlerTypeAnnotation(source = "ddd")
    public void method1(String name,Integer age){
        System.out.println(1);
    }
}
