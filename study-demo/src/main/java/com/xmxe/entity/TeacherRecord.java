package com.xmxe.entity;

public record TeacherRecord(String name,int age) {
    
    /**
     * record修饰的本质上也是类,当然也可以在里面定义方法
     * 然后,就可以这样调用它:
     * TeacherRecord r = new TeacherRecord("name", 20);
     * r.distance();
     */
    int distance(){
        System.out.println(name);
        return age;
    }
    
}

/**
 * 单独文件申明：
 * public record TeacherRecord(String name, int age){}
 * 在类内部申明：
 * public class DidispaceTest {
 *     public record TeacherRecord(String name, int age){}
 * }
 * 函数内申明：
 * public class DidispaceTest {
 *     public void test() {
 *         public record TeacherRecord(String name, int age){}
 *     }
 * }
 * record修饰的是一个final类,自动实现equals、hashCode、toString函数,成员变量均为public属性
 */
