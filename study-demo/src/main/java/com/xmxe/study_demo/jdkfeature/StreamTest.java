package com.xmxe.study_demo.jdkfeature;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.IntSummaryStatistics;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.Stack;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.xmxe.study_demo.entity.Student;


public class StreamTest {

    /**
     * 
     * Java8 Stream：2万字20个实例，玩转集合的筛选、归约、分组、聚合(https://mp.weixin.qq.com/s/tcU3kFLF8GIvqXOFG3EgLQ)
     * Java8中Stream详细用法大全(https://mp.weixin.qq.com/s/0F_CBDlav8X4-CfE0hzjWQ)
     * Java8 Stream常用方法大合集(https://mp.weixin.qq.com/s/owDtfi-UUI7uL1dIjPOtMw)
     * Stream中的map、peek、foreach方法的区别？https://mp.weixin.qq.com/s/iJDyjdXDkJx95MpqZmUT3g
     * 聊聊工作中常用的Lambda表达式 https://mp.weixin.qq.com/s/sEiz3u3UDNkSNkEQXW3o6Q
     */
    public void stream() {
        /*
         * java.util.Stream表示了某一种元素的序列，在这些元素上可以进行各种操作。Stream操作可以是中间操作，也可以是完结操作。
         * 完结操作会返回一个某种类型的值，而中间操作会返回流对象本身，并且你可以通过多次调用同一个流操作方法来将操作结果串起来。
         * Stream是在一个源的基础上创建出来的，例如java.util.Collection中的list或者set（map不能作为Stream的源）。
         * Stream操作往往可以通过顺序或者并行两种方式来执行。
         */
        Student s1 = new Student(1L, "a", 15, "浙江");
        List<Student> students = List.of(s1);
        List<String> list = List.of("a","b","c");
        List<Integer> number = List.of(1,2,3,4);
        String[] strArray = new String[] { "a", "b", "c" };

        // 构造Stream流的方式
        Stream<String> stream = Stream.of("a", "b", "c");
        Stream<String> stream1 = Stream.of(strArray);
        Stream<String> stream2 = Arrays.stream(strArray);
        Stream<String> stream3 = list.stream();
        // 注意:一个Stream流只可以使用一次，重复使用了多次会抛出stream has already been operated upon or closed异常。

        // Stream流的之间的转换
        try {
            // stream转换成Array
            String[] strArray1 = stream2.toArray(String[]::new);
            // stream转换成Collection
            List<String> list1 = stream3.collect(Collectors.toList());
            List<String> list2 = stream3.collect(Collectors.toCollection(ArrayList::new));
            Set<String> set = stream3.collect(Collectors.toSet());
            Stack<String> stack = stream3.collect(Collectors.toCollection(Stack::new));
            // 转换成String
            String str = stream.collect(Collectors.joining()).toString();
        } catch (Exception e) {
            e.printStackTrace();
        }

        /**
         * stream和parallelStream的简单区分：
         * stream是顺序流，由主线程按顺序对流执行操作，而parallelStream是并行流，内部以多线程并行执行的方式对流进行操作，但前提是流中的数据处理没有顺序要求
         * 如果流中的数据量足够大，并行流可以加快处速度。除了直接创建并行流，还可以通过parallel()把顺序流转换成并行流
         */
        // parallelStream是流并行处理程序的代替方法。
        // 获取空字符串的数量
        long count = list.parallelStream().filter(string -> string.isEmpty()).count();

        // filter(筛选)
        List<Student> streamStudents = students.stream().filter(s -> "浙江".equals(s.getAddress())).collect(Collectors.toList());
        Student stuOrelse = students.stream().filter(s -> "李四".equals(s.getName())).findAny().orElse(s1);
        int sum = students.stream().filter(u -> "张三".equals(u.getName())).mapToInt(u -> u.getAge()).sum();

        // distinct(去重)
        list.stream().distinct().forEach(System.out::println);
        List<String> wnums = list.stream().flatMap(line -> Stream.of(line.split(""))).filter(word -> word.length() >= 0).map(String::toLowerCase).distinct().sorted().collect(Collectors.toList());

        // limit(限制返回个数)
        list.stream().limit(2).forEach(System.out::println);

        // skip(删除元素)
        list.stream().skip(2).forEach(System.out::println);

        // reduce(聚合)
        List<String> list_reduce = Arrays.asList("欢", "迎", "你");
        String appendStr = list_reduce.stream().reduce("北京", (a, b) -> a + b);
        String concat = stream.reduce("", String::concat);//拼接字符串->abc
        double minValue = Stream.of(-4.0, 1.0, 3.0, -2.0).reduce(Double.MAX_VALUE, Double::min);//得到最小值
        int sumValue = number.stream().reduce(Integer::sum).get();//求和, 无起始值
        int sumValue1 = number.stream().reduce(1, Integer::sum);//求和, 有起始值
        String concat1 = stream.filter(x -> x.compareTo("Z") > 0).reduce("", String::concat);//过滤拼接字符串   
        // stream求集合最大值最小值
        Integer max = number.stream().reduce(Integer::max).get();
        Integer min = number.stream().reduce(Integer::min).get();
        
        // iterate与reduce很像 使用时管道必须有limit这样的操作来限制Stream大小。
        Stream.iterate(2, n -> n + 2).limit(5).forEach(x -> System.out.print(x + " "));//从2开始生成一个等差数列

        // 通过实现Supplier类的方法可以自定义流计算规则。
        // 随机获取两条学生信息
        class UserSupplier implements Supplier<Student> {
            private int index = 10;
            private Random random = new Random();

            @Override
            public Student get() {
                return new Student(index++,"pancm" + random.nextInt(10));
            }
        }
        Stream.generate(new UserSupplier()).limit(2).forEach(u -> System.out.println(u.getId() + ", " + u.getName()));

        // summaryStatistics使用
        // 得到最大、最小、之和以及平均数。
        IntSummaryStatistics stats = number.stream().mapToInt((x) -> x).summaryStatistics();
        System.out.println("列表中最大的数 : " + stats.getMax() + "列表中最小的数 : " + stats.getMin()
        + "所有数之和 : " + stats.getSum() + "平均数 : " + stats.getAverage());

        // min() max()
        Student minS = students.stream().min((stu1, stu2) -> Integer.compare(stu1.getAge(), stu2.getAge())).get();
        Student maxS = students.stream().max((stu1, stu2) -> Integer.compare(stu1.getAge(), stu2.getAge())).get();
        int maxLines = list.stream().mapToInt(String::length).max().getAsInt();
        int minLines = list.stream().mapToInt(String::length).min().getAsInt();

        // 同map操作,但是使用peek操作流,流中的元素不会改变,能得到流中的每一个元素。map会改变流中的元素,map接收的是一个Function表达式,有返回值,而peek接收的是Consumer表达式,没有返回值
        list.stream().filter(e -> e.length() > 3)
        .peek(e -> System.out.println("转换之前: " + e)).map(String::toUpperCase).peek(e -> System.out.println("转换之后: " + e)).collect(Collectors.toList());

        /**
         * anyMatch/allMatch/noneMatch（匹配）
         * allMatch：Stream 中全部元素符合则返回 true ;
         * anyMatch：Stream 中只要有一个元素符合则返回 true;
         * noneMatch：Stream 中没有一个元素符合则返回 true。
         */
        Boolean anyMatch = students.stream().anyMatch(s -> "湖北".equals(s.getAddress()));
        if (anyMatch) {System.out.println("有湖北人");}

        Boolean allMatch = students.stream().allMatch(s -> s.getAge() >= 15);
        if (allMatch) {System.out.println("所有学生都满15周岁");}

        Boolean noneMatch = students.stream().noneMatch(s -> "杨洋".equals(s.getName()));
        if (noneMatch) {System.out.println("没有叫杨洋的同学");}

        List<Integer> num = Arrays.asList(1, 2, 3, 4, 5, 6);
        if (num.stream().anyMatch(n -> n % 3 == 0)) {
            System.out.println("集合中有元素是3的整数倍");
        }
        /**
         * findAny()方法返回当前流中的任意元素,可以和其他流操作结合使用,这里需要注意findAny()返回的结果被Optional所包裹,这里需要说明的就是
         * Optional.ifPresent(Consumer<? super T> consumer) 表示当Optional包裹的元素不为空时，执行consumer
         */
        num.stream().filter(n -> n > 2).findAny().ifPresent(System.out::println);
        

        // map(转换)用于映射每个元素到对应的结果，一对一。
        List<String> addresses = students.stream().map(s -> "住址:" + s.getAddress()).collect(Collectors.toList());
        List<String> alpha = Arrays.asList("Monkey", "Lion", "Giraffe", "Lemur"); //将集合中的每一个字符串，全部转换成大写
        List<String> collect = alpha.stream().map(String::toUpperCase).collect(Collectors.toList());
        //上面使用了方法引用，和下面的lambda表达式语法效果是一样的
        List<String> collectLambda = alpha.stream().map(s -> s.toUpperCase()).collect(Collectors.toList());
        List<Integer> lengths = alpha.stream().map(String::length).collect(Collectors.toList());// 处理非字符串类型集合元素

        //flatMap用于映射每个元素到对应的结果，一对多。
        List<String> words = Arrays.asList("hello", "word");
        words.stream().map(w -> Arrays.stream(w.split(""))).forEach(System.out::println);//[[h,e,l,l,o],[w,o,r,l,d]]
        words.stream().flatMap(w -> Arrays.stream(w.split(""))).forEach(System.out::println); // [h,e,l,l,o,w,o,r,l,d]
     }


    /**
     * [Java8 stream排序以及自定义比较器，很实用！](https://mp.weixin.qq.com/s/NCJ0_sF0RVIeZdkQveNHmQ)
     * [Java 8排序的10个姿势](https://mp.weixin.qq.com/s/xsvIStVM36LAdEExNsyxbw)
     */
    public void sort(){
        List<String> sList = List.of("t","a","4","yu");
        // 1.使用Stream sorted()完成自然排序、比较器和反向排序
        // sorted(Comparator<? super T> comparator)：这里我们使用lambda表达式创建一个Comparator实例。
        // 我们可以按升序和降序对流元素进行排序。

        // sorted()：它使用自然顺序对流中的元素进行排序。元素类必须实现Comparable接口。
        sList.stream().sorted().forEach(System.out::println);// 同list.stream(Comparator.naturalOrder()).sorted()
        // 要反转自然顺序，Comparator提供reverseOrder()方法。
        sList.stream().sorted(Comparator.reverseOrder()).forEach(System.out::println);

        List<Student> studentList = List.of(new Student(1,"name1"),new Student(2,"name2"));
        // sorted(排序)
        studentList.stream().sorted((stu1, stu2) -> Long.compare(stu2.getId(), stu1.getId())).sorted((stu1, stu2) -> Integer.compare(stu2.getAge(), stu1.getAge())).forEach(System.out::println);
        studentList.stream().sorted(Comparator.comparing(Student::getName)).forEach(System.out::println);
        // 反转
        studentList.stream().sorted(Comparator.comparing(Student::getAge).reversed()).forEach(System.out::println);
        // 并行排序
        studentList.parallelStream().sorted(Comparator.comparing(Student::getAge)).collect(Collectors.toList());

        Map<String, String> map = Map.of("k1", "v1","k2","v2");
        map.entrySet().stream().sorted(Comparator.comparing(Map.Entry::getValue))
            .forEach(e -> System.out.println("Key: "+ e.getKey() +", Value: "+ e.getValue()));
        map.entrySet().stream().sorted(Comparator.comparing(Map.Entry::getKey))
            .forEach(e -> System.out.println("Key: "+ e.getKey() +", Value: "+ e.getValue()));

        // 2.不使用stream 直接使用list.sort()方法排序
        studentList.sort((u1, u2) -> u1.getAge() - u2.getAge());
        studentList.sort(Comparator.comparing(Student::getAge));
        studentList.sort(Comparator.comparing(Student::getAge).reversed());
        // 多次排序
        studentList.sort(Comparator.comparing(Student::getAge).thenComparing(Student::getName));
        // Collections排序
        Collections.sort(studentList, Comparator.comparing(Student::getAge));

    }


    /**
     * 分组排序
     */
    public void group_order(){

        List<Map<String,Object>> list = new ArrayList<>();
        Map<String,Object> map = new HashMap<>();
        // listmap分组 下面示例是根据map里的时间分组，返回一个map. key是((Date)o.get("time")).getTime()
        Map<Long, List<Map<String,Object>>> groupBy = list.stream().collect(Collectors.groupingBy(o->((Date)o.get("time")).getTime()));
        // 根据Map的key排序 创建一个linkedhashmap 遍历时有序
        Map<Long, List<Map<String, Object>>> sortedmap = new LinkedHashMap<>();
        groupBy.entrySet().stream().sorted(Map.Entry.<Long, List<Map<String,Object>>>comparingByKey()).forEachOrdered(e->sortedmap.put(e.getKey(),e.getValue()));
        // 降序
        // groupBy.entrySet().stream().sorted(Collections.reverseOrder(Map.Entry.comparingByKey()))...
        sortedmap.forEach((k,v)->{
            Map<String,Object> m = new HashMap<>();
            m.put("time",new Date(k));
            v.forEach (mm->{
                String tag_name = String.valueOf(mm.get("tag_name"));
                String rljl = String .valueOf(mm.get("rlil"));
                map.put(tag_name,rljl);
            });
            
        });

        // 单属性分组(根据用户的城市分组)
        // Map<String, List<User>> groupMap = userList.stream().collect(Collectors.groupingBy(User::getCity));
        // 多属性分组(根据用户的城市和性别分组)
        // Map<String, List<User>> groupMap = userList.stream().collect(Collectors.groupingBy(u -> u.getCity() + "|" + u.getSex()));
        class UserSupplier2 implements Supplier<Student> {
            private int index = 10;
            private Random random = new Random();

            @Override
            public Student get() {
                return new Student(index % 2 == 0 ? index++ : index, "pancm" + random.nextInt(10));
            }
        }
        System.out.println("通过id进行分组排序:");
        Map<Integer, List<Student>> personGroups = Stream.generate(new UserSupplier2()).limit(5).collect(Collectors.groupingBy(Student::getAge));
        Iterator<Map.Entry<Integer,List<Student>>> it = personGroups.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<Integer, List<Student>> persons =  it.next();
            System.out.println("id " + persons.getKey() + " = " + persons.getValue());
        }
        // 通过id进行分组排序:
        // id 10 = [{"id":10,"name":"pancm1"}]
        // id 11 = [{"id":11,"name":"pancm3"}, {"id":11,"name":"pancm6"}, {"id":11,"name":"pancm4"}, {"id":11,"name":"pancm7"}]
       
       
        /**
         * 分区排序
         */
        class UserSupplier3 implements Supplier<Student> {
            private int index = 16;
            private Random random = new Random();

            @Override
            public Student get() {
             return new Student( index++,"pancm" + random.nextInt(10));
            }
        }
        System.out.println("通过年龄进行分区排序:");
        Map<Boolean, List<Student>> children = Stream.generate(new UserSupplier3()).limit(5)
            .collect(Collectors.partitioningBy(p -> p.getId() < 18));
        System.out.println("小孩: " + children.get(true));
        System.out.println("成年人: " + children.get(false));

        // 通过年龄进行分区排序:
        // 小孩: [{"id":16,"name":"pancm7"}, {"id":17,"name":"pancm2"}]
        // 成年人: [{"id":18,"name":"pancm4"}, {"id":19,"name":"pancm9"}, {"id":20,"name":"pancm6"}]

    }

}
