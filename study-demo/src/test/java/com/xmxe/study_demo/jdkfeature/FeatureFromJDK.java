package com.xmxe.study_demo.jdkfeature;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.IntSummaryStatistics;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.Set;
import java.util.Stack;
import java.util.TreeSet;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

import org.junit.Test;

import com.xmxe.study_demo.entity.Student;
import com.xmxe.study_demo.enums.WeekEnum;

/**
 * JDK 5-15都有哪些经典新特性(https://mp.weixin.qq.com/s/1_pbYNskgTxjecZmpvVG0g)
 * 
 */
public class FeatureFromJDK {

    /**
     * lambda表达式
     * 函数式接口原理(https://mp.weixin.qq.com/s/DGR4kbwnY7LIJoYoGfKcRg)
     */
    @Test
    public void lambda() {
        /**
         * Function<T,R> 接受一个输入参数 返回一个结果
         * Consumer<T> 代表了接收一个输入参数并且无返回的操作
         * Predicate<T> 接收一个输入参数并且返回布尔值结果
         * Supplier<T> 无参数返回一个结果
         */
        Consumer<String> methodParam = System.out::println;

        LambdaService lambdaservice = (a, b) -> {
            return a + b;
        };// 相当于LambdaService的实现类

        int c = lambdaservice.lambdaTest(3, 4);
        methodParam.accept("①---" + c);

         /*
         * jdk7写法
         * Set<Integer> set1 = new TreeSet<>(new Comparator<Integer>() {
         *      @Override public int compare(Integer i,Integer o) {
         *          return i - o;
         *      }
         * });
         */
        // jdk8写法
        Set<Integer> set = new TreeSet<>((x, y) -> (x - y));
        Collections.addAll(set, 22, 3, 51, 44, 20, 6);
        set.forEach(x -> methodParam.accept("③---" + x));
        set.stream().filter(x -> x > 30).sorted((x, y) -> (y - x)).forEach(x -> methodParam.accept("②---" + x));

        /* stream().map()可以看作对象转换 */
        List.of(12,21,3).stream().sorted((x, y) -> (x - y)).map(String::valueOf).filter(x -> x.startsWith("1"))
                .forEach(x -> methodParam.accept("④---" + x));
    }

    /**
     * 
     * Java8 Stream：2万字20个实例，玩转集合的筛选、归约、分组、聚合(https://mp.weixin.qq.com/s/tcU3kFLF8GIvqXOFG3EgLQ)
     * Java8中Stream详细用法大全(https://mp.weixin.qq.com/s/0F_CBDlav8X4-CfE0hzjWQ)
     * Java8 Stream常用方法大合集(https://mp.weixin.qq.com/s/owDtfi-UUI7uL1dIjPOtMw)
     * Stream中的map、peek、foreach方法的区别？https://mp.weixin.qq.com/s/iJDyjdXDkJx95MpqZmUT3g
     * 聊聊工作中常用的Lambda表达式 https://mp.weixin.qq.com/s/sEiz3u3UDNkSNkEQXW3o6Q
     */
    @Test
    public void stream() {
       /*
         * java.util.Stream表示了某一种元素的序列，在这些元素上可以进行各种操作。Stream操作可以是中间操作，也可以是完结操作。
         * 完结操作会返回一个某种类型的值，而中间操作会返回流对象本身，并且你可以通过多次调用同一个流操作方法来将操作结果串起来。
         * Stream是在一个源的基础上创建出来的，例如java.util.Collection中的list或者set（map不能作为Stream的源）。
         * Stream操作往往可以通过顺序或者并行两种方式来执行。
         */
        Student s1 = new Student(1L, "a", 15, "浙江");
        Student s2 = new Student(2L, "b", 15, "湖北");
        Student s3 = new Student(3L, "c", 17, "北京");
        Student s4 = new Student(4L, "d", 17, "浙江");
        List<Student> students = List.of(s1,s2,s3,s4);

        //构造Stream流的方式
        Stream.of("Monkey", "Lion", "Giraffe", "Lemur").mapToInt(String::length).forEach(System.out::println);
        Stream<String> stream = Stream.of("a", "b", "c");
        String[] strArray = new String[] { "a", "b", "c" };
        stream = Stream.of(strArray);
        stream = Arrays.stream(strArray);
        List<String> list = Arrays.asList(strArray);
        stream = list.stream();
        //注意:一个Stream流只可以使用一次，上面代码为了简洁而重复使用了数次，因此会抛出 stream has already been operated upon or closed 异常。

        //Stream流的之间的转换
        try {
            // stream转换成 Array
            String[] strArray1 = stream.toArray(String[]::new);

            // stream转换成 Collection
            List<String> list1 = stream.collect(Collectors.toList());
            List<String> list2 = stream.collect(Collectors.toCollection(ArrayList::new));
            Set<String> set = stream.collect(Collectors.toSet());
            Stack<String> stack = stream.collect(Collectors.toCollection(Stack::new));

            // 转换成 String
            String str = stream.collect(Collectors.joining()).toString();
        } catch (Exception e) {
            e.printStackTrace();
        }

        /**
         * stream和parallelStream的简单区分：
         * stream是顺序流，由主线程按顺序对流执行操作，而parallelStream是并行流，内部以多线程并行执行的方式对流进行操作，但前提是流中的数据处理没有顺序要求
         * 如果流中的数据量足够大，并行流可以加快处速度。除了直接创建并行流，还可以通过parallel()把顺序流转换成并行流
         */
        //parallelStream 是流并行处理程序的代替方法。
        List<String> strings = Arrays.asList("a", "", "c", "", "e","", " ");
        // 获取空字符串的数量
        long count = strings.parallelStream().filter(string -> string.isEmpty()).count();

        // filter（筛选）
        List<Student> streamStudents = students.stream().filter(s -> "浙江".equals(s.getAddress())).collect(Collectors.toList());
        streamStudents.forEach(System.out::println);
        Student stuOrelse = students.stream().filter(s -> "李四".equals(s.getName())).findAny().orElse(s1);
        int sum = students.stream().filter(u -> "张三".equals(u.getName())).mapToInt(u -> u.getAge()).sum();

        // distinct(去重)
        strings.stream().distinct().forEach(System.out::println);
        students.stream().distinct().forEach(System.out::println);
        List<String> wnums = strings.stream().flatMap(line -> Stream.of(line.split(""))).filter(word -> word.length() >= 0)
            .map(String::toLowerCase).distinct().sorted().collect(Collectors.toList());

        // sorted(排序)
        list.stream().sorted().forEach(System.out::println);
        students.stream().sorted((stu1, stu2) -> Long.compare(stu2.getId(), stu1.getId()))
                .sorted((stu1, stu2) -> Integer.compare(stu2.getAge(), stu1.getAge())).forEach(System.out::println);

        // limit（限制返回个数）
        list.stream().limit(2).forEach(System.out::println);

        // skip(删除元素)
        list.stream().skip(2).forEach(System.out::println);

        // reduce(聚合)
        List<String> list_reduce = Arrays.asList("欢", "迎", "你");
        String appendStr = list_reduce.stream().reduce("北京", (a, b) -> a + b);
        String concat = Stream.of("A", "B", "C", "D").reduce("", String::concat);//拼接字符串
        double minValue = Stream.of(-4.0, 1.0, 3.0, -2.0).reduce(Double.MAX_VALUE, Double::min);//得到最小值
        int sumValue = Stream.of(1, 2, 3, 4).reduce(Integer::sum).get();//求和, 无起始值
        sumValue = Stream.of(1, 2, 3, 4).reduce(1, Integer::sum);//求和, 有起始值
        concat = Stream.of("a", "B", "c", "D", "e", "F").filter(x -> x.compareTo("Z") > 0).reduce("", String::concat);//过滤拼接字符串

        //iterate与reduce很像 使用时管道必须有limit这样的操作来限制Stream大小。
        Stream.iterate(2, n -> n + 2).limit(5).forEach(x -> System.out.print(x + " "));//从2开始生成一个等差数列

        //通过实现Supplier类的方法可以自定义流计算规则。
        //随机获取两条学生信息
        class UserSupplier implements Supplier<Student> {
            private int index = 10;
            private Random random = new Random();

            @Override
            public Student get() {
             return new Student(index++,"pancm" + random.nextInt(10));
            }
        }
        Stream.generate(new UserSupplier()).limit(2).forEach(u -> System.out.println(u.getId() + ", " + u.getName()));

        //summaryStatistics使用
        //得到最大、最小、之和以及平均数。
        List<Integer> numbers = Arrays.asList(1, 5, 7, 3, 9);
        IntSummaryStatistics stats = numbers.stream().mapToInt((x) -> x).summaryStatistics();

        System.out.println("列表中最大的数 : " + stats.getMax());
        System.out.println("列表中最小的数 : " + stats.getMin());
        System.out.println("所有数之和 : " + stats.getSum());
        System.out.println("平均数 : " + stats.getAverage());

        // min() max()
        Student minS = students.stream().min((stu1, stu2) -> Integer.compare(stu1.getAge(), stu2.getAge())).get();
        Student maxS = students.stream().max((stu1, stu2) -> Integer.compare(stu1.getAge(), stu2.getAge())).get();
        int maxLines = list.stream().mapToInt(String::length).max().getAsInt();
        int minLines = list.stream().mapToInt(String::length).min().getAsInt();

        //peek对每个元素执行操作并返回一个新的Stream,如同于map，能得到流中的每一个元素。但map接收的是一个Function表达式，有返回值；而peek接收的是Consumer表达式，没有返回值
        Stream.of("one", "two", "three", "four").filter(e -> e.length() > 3).peek(e -> System.out.println("转换之前: " + e))
            .map(String::toUpperCase).peek(e -> System.out.println("转换之后: " + e)).collect(Collectors.toList());

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
        //stream求集合最大值最小值
        num.stream().reduce(Integer::max);
        num.stream().reduce(Integer::min);

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
    @Test
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
        studentList.stream().sorted(Comparator.comparing(Student::getName)).forEach(System.out::println);
        // 反转
        studentList.stream().sorted(Comparator.comparing(Student::getAge).reversed()).forEach(System.out::println);

        Map<String, String> map = Map.of("k1", "v1","k2","v2");
        map.entrySet().stream().sorted(Comparator.comparing(Map.Entry::getValue))
            .forEach(e -> System.out.println("Key: "+ e.getKey() +", Value: "+ e.getValue()));
        map.entrySet().stream().sorted(Comparator.comparing(Map.Entry::getKey))
            .forEach(e -> System.out.println("Key: "+ e.getKey() +", Value: "+ e.getValue()));

    }

    /**
     * jdk8新增的map方法
     */
    @Test
    public void newMapMethod(){

        Map<String,Object> newMap = new HashMap<>();
        newMap.put("a", 1);newMap.put("b", 2);

        //remove方法 接收2个参数，key和value，只有当Map中键值对同时等于参数Key和Value时才执行删除
        newMap.remove("a", 1);newMap.remove("b", 3);

        //replace(K key, V value) 方法
        newMap.replace("b", 3);
        //replace(K key, V oldValue, V newValue) 方法 如果key关联的值与指定的oldValue的值相等，则替换成新的newValue
        newMap.replace("b", 2, 4);
        //replaceAll方法 替换Map中所有Entry的value值，这个值由旧的key和value计算得出，接收参数 (K, V) -> V
        newMap.replaceAll((key, value) -> (key + "z") + value);

        //getOrDefault方法 根据key得到value 如果value存在，返回value，不存在，返回指定的值 不会往map里面put数据
        newMap.getOrDefault("c", 3);
        //forEach方法 遍历Map中的所有Entry, 对key, value进行处理
        newMap.forEach((key, value) -> System.out.println(key +"--->"+ value));//输出b--->3
        newMap.forEach((key, value) -> System.out.println(key +"--->"+ value));// 输出 b--->bz3

        //putIfAbsent方法 如果传入key对应的value已经存在，就返回存在的value，不进行替换。如果不存在，就添加key和value，返回null
        //与put区别在于put在放入数据时，如果放入数据的key已经存在与Map中，最后放入的数据会覆盖之前存在的数据，而putIfAbsent在放入数据时，如果存在重复的key，那么putIfAbsent不会放入值。
        newMap.putIfAbsent("a", "3");newMap.putIfAbsent("b",4);

        //computeIfAbsent方法 如果Key不存在，则Put这个Key和将Key带入函数运算后的结果为Value的键值对；如果Key存在，则忽略Put操作
        // 以前写法
        Object value = newMap.get("b");
        if(value == null){
            value = new Object();
            newMap.put("b", value);
        }
        // 使用computeIfAbsent
        newMap.computeIfAbsent("b", key -> new Object());

        // computeIfPresent方法 如果key存在，则将函数的运算结果作为这个Key对应的Value的新值Put进去 即根据旧的key和value计算新的值newValue, 如果newValue不为null，则设置key新的值为newValue，如果newValue为null,则删除该key的值
        // 如果key不存在 则不进行操作返回null
        newMap.computeIfPresent("b", (k, v) -> new Object());

        //不判断key存在不存在，直接按逻辑替换值
        newMap.compute("3", (k,v) -> String.valueOf(v)+"1" );
        newMap.compute("c", (k,v) -> String.valueOf(v)+"1" );

        // merge()适用于两种情况。如果给定的key不存在，它就变成了put(key, value)即newMap.get("a")==null的话merge操作就变成了传统的put操作。但是，如果key已经存在一些值，我们remappingFunction可以选择合并的方式
        // 以前写法
        // BiFunction第一个参数代表以前newMap.get("a")的值
        // BiFunction第二个参数代表输入的第二个参数
        // BiFunction第三个参数代表返回类型
        newMap.merge("a", 2, new BiFunction<Object, Object, Object>() {
            @Override
            public Object apply(Object mapValue, Object paramValue) {
                // return Integer.sum(oldValue,newValue);
                return String.valueOf(mapValue)+String.valueOf(paramValue);
            }
        });
        //下面的参数mapValue是newMap.get("a")以前的值 参数paramValue是merge输入的第二个参数2
        newMap.merge("a", 2, (mapValue,paramValue)->String.valueOf(paramValue)+String.valueOf(mapValue));

    }

    /**
     * 方法引用(::)
     */
    @Test
    public void function(){
        /**
         * Function<T,R> 接受一个输入参数 返回一个结果
         * Consumer<T> 代表了接收一个输入参数并且无返回的操作
         * Predicate<T> 接收一个输入参数并且返回布尔值结果
         * Supplier<T> 无参数返回一个结果
         */

        // 使用双冒号::来构造静态函数引用
        Function<String, Integer> fun = Integer::parseInt;
        Integer value = fun.apply("123");
        System.out.println(value);

        // 使用双冒号::来构造非静态函数引用
        String content = "Hello JDK8";
        Function<Integer, String> func = content::substring;
        String result = func.apply(1);
        System.out.println(result);

        // 构造函数引用
        /**
         * Bifunction接收两个参数返回一个参数
         */
        BiFunction<Integer,String,  Student> biFunction = Student::new;
        Student stu = biFunction.apply(28,"mengday");
        System.out.println(stu.toString());

    }

    /**
     * 分组排序
     */
    @Test
    public void group_order(){

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

    /**
     * Java8 Optional最佳实践 https://mp.weixin.qq.com/s/QMY9H2iMQGGRRORKc1COdw
     * 不要再用if(obj!=null)判空了！！！ https://mp.weixin.qq.com/s/0AeETDJtKCiJV_rSPaNwTQ
     */
    @Test
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

    /**
     * jdk8自带Base64 无需再使用第三方Base64
     */
    @Test
    public void jdk8Base64(){
        String str = "str";
        String encoded = Base64.getEncoder().encodeToString(str.getBytes( StandardCharsets.UTF_8));
        String decoded = new String(Base64.getDecoder().decode(encoded), StandardCharsets.UTF_8);
    }

    /**
     * 从JDK 1.8开始，Nashorn取代Rhino(JDK 1.6, JDK1.7)成为Java的嵌入式JavaScript引擎。
     * 它使用基于JSR 292的新语言特性，将JavaScript编译成Java字节码
     */
    @Test
    public void jsEngine(){
        ScriptEngineManager scriptEngineManager = new ScriptEngineManager();
        ScriptEngine nashorn = scriptEngineManager.getEngineByName("nashorn");

        String name = "Hello World";
        try {
            nashorn.eval("print('" + name + "')");
        }catch(Exception e){
            System.out.println("执行脚本错误: "+ e.getMessage());
        }
    }

    /**
     * jdk11 新增的string方法
     */
    @Test
    public void jdk11StringMethod(){
        // isBank()
        System.out.println("abc".isBlank()); // false
        System.out.println("".isBlank()); // true
        System.out.println("\t \t".isBlank()); // true
        // 去除首尾空格
        System.out.println(" jay ".strip());  // "jay"
        // 去除首部空格
        System.out.println(" jay ".stripLeading());   // "jay "
        // 去除字符串尾部空格
        System.out.println(" jay ".stripLeading());   // " jay"
        // 行数统计
        System.out.println("a\nb\nc".lines().count());    // 3
        // 复制字符串
        System.out.println("jay".repeat(3));   // "jayjayjay"

    }

    /**
     * 其他功能预览
     */
    @Test
    public void otherFeature(Object param){
        // JDK9新增of方法 只能用在list set map接口上不能用在实现类上，且长度固定 无法使用add put等方法
        List<Integer> list = List.of(5,16,41,10);
        // jdk10新增静态方法copyof 返回不可修改的副本
        var copyList = List.copyOf(list);
        // list删除元素 删除成功返回true
        boolean removeSuccess = list.removeIf(x -> x.equals(5));
        
        // 1.7 a的值为1111，下划线不影响实际值，提升可读性
        int a = 11_11;

        // Java 12新增了mismatch方法，此方法返回第一个不匹配的位置，如果没有不匹配，则返回 -1L。
        Path file1 = Paths.get("c:\\jay.txt");
        Path file2 = Paths.get("c:\\aaa.txt");
        try {
            long fileMismatch = Files.mismatch(file1, file2);
            System.out.println(fileMismatch);
        } catch (IOException e) {
            e.printStackTrace();
        }

        if(param instanceof String paramStr){
            System.out.println(paramStr.length());
        }

        WeekEnum day = WeekEnum.FRI;
        switch (day) {
            case MON, FRI, SUN -> System.out.println(6);
            case TUE -> System.out.println(7);
            case THU, SAT -> System.out.println(8);
            case WED -> System.out.println(9);
        }
        String d = "m";
        int i = switch (d) {
            case "MONDAY" -> 1;
            case "TUESDAY" -> 2;
            case "WEDNESDAY" -> 3;
            case "THURSDAY" -> 4;
            case "FRIDAY" -> 5;
            case "SATURDAY" -> 6;
            case "SUNDAY" -> 7;
            default -> 0;
        };
        
        // 大多数时候，在switch表达式内部，我们会返回简单的值。一行代码的话可以用->返回，
        // 但是如果需要复杂的语句，放到{…}里，然后用yield返回一个值作为switch语句的返回值：
        // yield相比较于break，优化了返回值，不要要定义一个变量去接收，相较于return，还可以继续往下执行
        String fruit = "apple";
        String name = switch (fruit) {
            case "apple" -> "1";
            case "pear", "mango" -> "2";
            default -> {
                String code = "fruit.toString()";
                yield code;
            }
        };

        // java13文本块
        String html = """
                <html>
                    <body>
                        <p>Hello World</p>
                    </body>
                </html>
                """;
        // 改进NPE异常提示

    }

}
