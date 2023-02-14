package com.xmxe.study_demo.jdkfeature;

import java.util.Optional;
import java.util.function.Supplier;

import com.xmxe.study_demo.entity.Student;

public class OptionalTest {
    /**
     * Java8 Optional最佳实践 https://mp.weixin.qq.com/s/QMY9H2iMQGGRRORKc1COdw
     * 告别丑陋判空，一个Optional类就能搞定！https://mp.weixin.qq.com/s/_yY2rwy5s2PnIVfliLRmBw
     */
    public void optional() throws Exception{
        // 1、创建包装对象值为空的Optional对象
        Optional<String> optEmpty = Optional.empty();
        // 2、创建包装对象值非空的Optional对象
        Optional<String> optOf = Optional.of("optional");
        // 3、创建包装对象值允许为空也可以不为空的Optional对象 传null就得到Optional.empty(), 非null就调用Optional.of(obj).
        Optional<String> optOfNullable1 = Optional.ofNullable(null);
        Optional<String> optOfNullable2 = Optional.ofNullable("optional");

        Student person = new Student();
        person.setAge(5);
        // 如果value不为空则做返回，如果为空则抛出异常"No value present"简单实例展示
        Optional.ofNullable(person).get();

        // isPresent()方法就是会返回一个boolean类型值，如果对象不为空则为true，如果为空则false
        if (Optional.ofNullable(person).isPresent()){
            //写不为空的逻辑
            System.out.println("不为空");
        } else{
            //写为空的逻辑
            System.out.println("为空");
        }

        // Optional.ifPresent()方法(判读是否为空并返回函数)如果对象非空，则运行函数体如果对象不为空，则会打印这个年龄，因为内部已经做了NPE（非空判断），所以就不用担心空指针异常了
        Optional.ofNullable(person).ifPresent(p -> System.out.println("年龄"+p.getAge()));
        Optional.ofNullable(person).ifPresentOrElse(p -> System.out.println("不为null"),()->{System.out.println("null");});

        // Optional.filter()方法(过滤对象)
        // filter()方法大致意思是，接受一个对象，然后对他进行条件过滤，如果条件符合则返回Optional对象本身，如果不符合则返回空Optional
        Optional.ofNullable(person).filter(p -> p.getAge()>50);

        // Optional.map()方法(对象进行二次包装)Optional.flatMap()
        // map()方法将对应Funcation函数式接口中的对象，进行二次运算，封装成新的对象然后返回在Optional中
        String optName = Optional.ofNullable(person).map(p -> person.getName()).orElse("name为空");
        Optional<Object> optName2 = Optional.ofNullable(person).map(p -> Optional.ofNullable(p.getName()).orElse("name为空"));

        // Optional.orElse()方法(为空返回对象)
        // 常用方法之一，这个方法意思是如果包装对象为空的话，就执行orElse方法里的value，如果非空，则返回写入对象
        // 注意：不管Optional是不是null, orElse都会执行里面的方法，orElseGet不会执行里面的方法，这也是orElse和orElseGet的区别
        optEmpty.orElse("100");//optEmpty为null返回100 不为null返回optEmpty的值

        // Optional.orElseGet()方法 (为空返回Supplier对象)
        // 这个与orElse很相似，入参不一样，入参为Supplier对象，为空返回传入对象的.get()方法，如果非空则返回当前对象
        Optional<Supplier<Student>> sup=Optional.ofNullable(Student::new);
        //调用get()方法，此时才会调用对象的构造方法，即获得到真正对象
        Optional.ofNullable(person).orElseGet(sup.get());

        // Optional.orElseThrow()方法(为空返回异常)
        // 方法作用的话就是如果为空，就抛出你定义的异常，如果不为空返回当前对象
        Optional.ofNullable(person).orElseThrow(() -> new RuntimeException("person为null"));

        /**
         * orElse()和orElseGet()和orElseThrow()的异同点
         * 方法效果类似，如果对象不为空，则返回对象，如果为空，则返回方法体中的对应参数，所以可以看出这三个方法体中参数是不一样的
         * orElse（T 对象）
         * orElseGet（Supplier <T> 对象）
         * orElseThrow（异常）
         *
         * map()和orElseGet的异同点
         * 方法效果类似，对方法参数进行二次包装，并返回, 入参不同
         * map（function 函数）
         * flatmap（Optional<function> 函数）
         */
    }
}
