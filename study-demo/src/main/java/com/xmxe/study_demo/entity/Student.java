package com.xmxe.study_demo.entity;

import java.util.Objects;

import com.xmxe.study_demo.designpattern.strategy.annotation.OrderHandlerTypeAnnotation;

public class Student {
    private Long id;
    private String address;
    private Integer age;
    public String name;
    protected String gender;

    public Student() {
    }

    public Student(Integer age, String name) {
        this(name, age);
    }

    private Student(String name, Integer age) {
        this.name = name;
        this.age = age;
    }

    public Student(Long id, String name, int age, String address) {
        this.id = id;
        this.name = name;
        this.age = age;
        this.address = address;
    }

    @OrderHandlerTypeAnnotation(source = "ddd")
    public void method1(String name, Integer age) {
        System.out.println(1);
    }

    @Override
    public String toString() {
        return "Student{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", age=" + age +
                ", address='" + address + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        Student student = (Student) o;
        return age == student.age &&
                Objects.equals(id, student.id) &&
                Objects.equals(name, student.name) &&
                Objects.equals(address, student.address);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, age, address);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
