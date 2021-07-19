package com.xmxe.study_demo.designpattern.filter;

import java.util.ArrayList;
import java.util.List;

public class CustomFilter {
    public static void main(String[] args) {
        List<Person> personList = new ArrayList<>();
        personList.add(new Person("程咬金","男",36));
        personList.add(new Person("露娜","女",16));
        personList.add(new Person("伽啰","女",18));
        personList.add(new Person("苏烈","男",40));
        personList.add(new Person("武则天","女",24));
        personList.add(new Person("韩信","男",16));

        //过程出年龄已满18周岁的同学
        Filter ageFilter = new AgeFilter();
        personList = ageFilter.filterOperate(personList);
        System.out.println(personList);
        System.out.println("========分隔符=======");

        //过滤出性别为男的同学
        Filter genderFilter = new GenderFilter();
        personList = genderFilter.filterOperate(personList);
        System.out.println(personList);
    }
}

class Person{

    private String name;//姓名

    private String gender;//性别

    private int age;//年龄


    public String getName() {
        return name;
    }
    public void setName(String name){
        this.name = name;
    }

    public String getGender() {
        return gender;
    }
    public void setGender(String gender){
        this.gender = gender;
    }
    
    public int getAge(){
        return age;
    }
    public void setAge(int age){
        this.age = age;
    }

    public Person(String name, String gender, int age) {
        this.name = name;
        this.gender = gender;
        this.age = age;
    }

    @Override
    public String toString() {
        return "Person{" +
                "name='" + name + '\'' +
                ", gender='" + gender + '\'' +
                ", age=" + age +
                '}';
    }
}

interface Filter{
     /**
     * 过滤操作
     * @param personList
     * @return
     */
    List<Person> filterOperate(List<Person> personList);
}

/**
 * 过滤已满足18岁的同学
 */
class AgeFilter implements Filter {

    @Override
    public List<Person> filterOperate(List<Person> personList) {
        List<Person> targetList = new ArrayList<>();
        for (Person person : personList) {
            if(person.getAge() >= 18){
                targetList.add(person);
            }
        }
        return targetList;
    }
}

/**
 * 过滤出性别为男的同学
 */
class GenderFilter implements Filter {

    @Override
    public List<Person> filterOperate(List<Person> personList) {
        List<Person> targetList = new ArrayList<>();
        for (Person person : personList) {
            if("男".equals(person.getGender())){
                targetList.add(person);
            }
        }
        return targetList;
    }
}