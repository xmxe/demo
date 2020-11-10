package com.xmxe.study_demo.jdkfeature;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
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

import org.junit.Test;


public class FeatureFromJDK {

    /**
     * lambda表达式
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
 
        Set<Integer> set = new TreeSet<>();
        Collections.addAll(set, 22, 3, 51, 44, 20, 6);
        set.stream().filter(x -> x > 30).sorted((x, y) -> (y - x)).forEach(x -> methodParam.accept("②---" + x));

        /*
         * jdk7写法 Set<Integer> set1 = new TreeSet<>(new Comparator<Integer>() {
         * 
         * @Override public int compare(Integer i,Integer o) { return i - o; } });
         */
        // jdk8写法
        Set<Integer> set1 = new TreeSet<>((x, y) -> (x - y));
        Collections.addAll(set1, 22, 3, 51, 44, 20, 6);
        set1.forEach(x -> methodParam.accept("③---" + x));

        List<Integer> list = List.of(5,16,41,10);//JDK9新增of方法 只能用在list set map接口上不能用在实现类上，且长度固定 无法使用add put等方法
        /* stream().map()可以看作对象转换 */
        list.stream().sorted((x, y) -> (x - y)).map(String::valueOf).filter(x -> x.startsWith("1"))
                .forEach(x -> methodParam.accept("④---" + x));
    }

    /**
     * stream流
     */
    @Test
    public void stream() {
       /*
         * java.util.Stream表示了某一种元素的序列，在这些元素上可以进行各种操作。Stream操作可以是中间操作，也可以是完结操作。
         * 完结操作会返回一个某种类型的值，而中间操作会返回流对象本身，并且你可以通过多次调用同一个流操作方法来将操作结果串起来。
         * Stream是在一个源的基础上创建出来的，例如java.util.Collection中的list或者set（map不能作为Stream的源）。
         * Stream操作往往可以通过顺序或者并行两种方式来执行。
         */
        Student s1 = new Student(1L, "肖战", 15, "浙江");
        Student s2 = new Student(2L, "王一博", 15, "湖北");
        Student s3 = new Student(3L, "杨紫", 17, "北京");
        Student s4 = new Student(4L, "李现", 17, "浙江");
        List<Student> students = new ArrayList<>();
        students.add(s1);
        students.add(s2);
        students.add(s3);
        students.add(s4);

        // filter（筛选）
        List<Student> streamStudents = students.stream().filter(s -> "浙江".equals(s.getAddress())).collect(Collectors.toList());
        streamStudents.forEach(System.out::println);   
        Student stuOrelse = students.stream().filter(s -> "李四".equals(s.getName())).findAny().orElse(s1);
        int sum = students.stream().filter(u -> "张三".equals(u.getName())).mapToInt(u -> u.getAge()).sum();

        // distinct(去重)
        List<String> list = Arrays.asList("111", "222", "333", "111", "222");
        list.stream().distinct().forEach(System.out::println);
        students.stream().distinct().forEach(System.out::println);
        List<String> wnums = list.stream().flatMap(line -> Stream.of(line.split(""))).filter(word -> word.length() >= 0)
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

        //iterate 与reduce很像 使用时管道必须有 limit 这样的操作来限制 Stream 大小。
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

        // min(求最小值)
        Student minS = students.stream().min((stu1, stu2) -> Integer.compare(stu1.getAge(), stu2.getAge())).get();
        Student maxS = students.stream().max((stu1, stu2) -> Integer.compare(stu1.getAge(), stu2.getAge())).get();
        int maxLines = list.stream().mapToInt(String::length).max().getAsInt();
        int minLines = list.stream().mapToInt(String::length).min().getAsInt();

        //peek对每个元素执行操作并返回一个新的Stream
        Stream.of("one", "two", "three", "four").filter(e -> e.length() > 3).peek(e -> System.out.println("转换之前: " + e))
            .map(String::toUpperCase).peek(e -> System.out.println("转换之后: " + e)).collect(Collectors.toList());
 
        //parallelStream 是流并行处理程序的代替方法。
        List<String> strings = Arrays.asList("a", "", "c", "", "e","", " ");
        // 获取空字符串的数量
        long count =  strings.parallelStream().filter(string -> string.isEmpty()).count();

        /**anyMatch/allMatch/noneMatch（匹配）
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
         * findAny() 该方法返回当前流中的任意元素,可以和其他流操作结合使用,这里需要注意 findAny() 返回的结果
         * 被 Optional 所包裹，Optional 是 Java8 为优雅的避免 NPE 所采用的新 API，这里需要说明的就是 
         * Optional.ifPresent(Consumer<? super T> consumer) 表示当 Optional 包裹的元素不为空时，执行 consumer
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
 
        //构造Stream流的方式
        Stream.of("Monkey", "Lion", "Giraffe", "Lemur").mapToInt(String::length).forEach(System.out::println);
        Stream stream = Stream.of("a", "b", "c");
        String[] strArray = new String[] { "a", "b", "c" };
        stream = Stream.of(strArray);
        stream = Arrays.stream(strArray);
        List<String> list1 = Arrays.asList(strArray);
        stream = list1.stream();
        //Stream流的之间的转换
        //注意:一个Stream流只可以使用一次，这段代码为了简洁而重复使用了数次，因此会抛出 stream has already been operated upon or closed 异常。
        
        try {
            Stream<String> stream2 = Stream.of("a", "b", "c");
            // 转换成 Array
            String[] strArray1 = stream2.toArray(String[]::new);
          
            // 转换成 Collection
            List<String> list3 = stream2.collect(Collectors.toList());
            List<String> list2 = stream2.collect(Collectors.toCollection(ArrayList::new));   
            Set set1 = stream2.collect(Collectors.toSet());
            Stack stack1 = stream2.collect(Collectors.toCollection(Stack::new));
          
            // 转换成 String
            String str = stream.collect(Collectors.joining()).toString();
           } catch (Exception e) {
            e.printStackTrace();
           }
    }
  
    /**
     * jdk8新增的map方法
     */
    @Test
    public void newMapMethod(){
       
        //jdk8新增的map方法
        Map<String,Object> jdk8Map = new HashMap<>();
        jdk8Map.put("a", 1);jdk8Map.put("b", 2);
        //remove 方法 接收2个参数，key和value，只有当Map中键值对同时等于参数Key和Value时才执行删除
        jdk8Map.remove("a", 1);jdk8Map.remove("b", 3);
        //replace(K key, V value) 方法
        jdk8Map.replace("b", 3);
        //replace(K key, V oldValue, V newValue) 方法 如果key关联的值与指定的oldValue的值相等，则替换成新的newValue
        jdk8Map.replace("b", 2, 4);
        //getOrDefault方法 如果指定的key存在，返回value，不存在，返回指定的值 不会往map里面put数据
        System.out.println(jdk8Map.getOrDefault("c", 3));
        //forEach方法 遍历Map中的所有Entry, 对key, value进行处理
        jdk8Map.forEach((key, value) -> System.out.println(key +"--->"+ value));//输出b--->3
        //replaceAll方法 替换Map中所有Entry的value值，这个值由旧的key和value计算得出，接收参数 (K, V) -> V
        jdk8Map.replaceAll((key, value) -> (key + "z") + value);
        jdk8Map.forEach((key, value) -> System.out.println(key +"--->"+ value));// 输出 b--->bz3

        //putIfAbsent方法 如果传入key对应的value已经存在，就返回存在的value，不进行替换。如果不存在，就添加key和value，返回null
        //与put区别在于put在放入数据时，如果放入数据的key已经存在与Map中，最后放入的数据会覆盖之前存在的数据，而putIfAbsent在放入数据时，如果存在重复的key，那么putIfAbsent不会放入值。       
        jdk8Map.putIfAbsent("a", "3");jdk8Map.putIfAbsent("b",4);

        //computeIfAbsent方法 如果Key不存在，则Put这个Key和将Key带入函数运算后的结果为Value的键值对；如果Key存在，则忽略Put操作
        jdk8Map.computeIfAbsent("a", key -> key + " computed");
        jdk8Map.computeIfAbsent("c", key -> key + " computed");

        //computeIfPresent方法 如果Key存在，则将函数的运算结果作为这个Key对应的Value的新值Put进去 即根据旧的key和value计算新的值newValue, 如果newValue不为null，则设置key新的值为newValue，如果newValue为null,则删除该key的值
        jdk8Map.computeIfPresent("b", (k, v) -> k + v);

        //compute=computeIfAbsent+computeIfPresent 不判断存在不存在，直接按逻辑替换值
        jdk8Map.compute("3", (k,v) -> String.valueOf(v)+"1" );
        jdk8Map.compute("c", (k,v) -> String.valueOf(v)+"1" );
        //merge() 适用于两种情况。如果给定的key不存在，它就变成了put(key, value)。但是，如果key已经存在一些值，我们 remappingFunction 可以选择合并的方式
        //下面的参数2 = paramValue
        jdk8Map.merge("a", 2, (mapValue,paramValue)->String.valueOf(paramValue)+String.valueOf(mapValue));
        System.out.println(jdk8Map);
    }

    /**
     * 方法引用
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
     * optional处理NPE异常
     */
    @Test
    public void optional(){
        // 1、创建包装对象值为空的Optional对象
        Optional<String> optEmpty = Optional.empty();
        // 2、创建包装对象值非空的Optional对象
        Optional<String> optOf = Optional.of("optional");
        // 3、创建包装对象值允许为空也可以不为空的Optional对象
        Optional<String> optOfNullable1 = Optional.ofNullable(null);
        Optional<String> optOfNullable2 = Optional.ofNullable("optional");

        Student person = new Student();
        person.setAge(5);
        // 如果 value 不为空则做返回，如果为空则抛出异常 "No value present" 简单实例展示
        Optional.ofNullable(person).get();

        // isPresent() 方法就是会返回一个 boolean 类型值，如果对象不为空则为真，如果为空则 false
        if (Optional.ofNullable(person).isPresent()){
            //写不为空的逻辑
            System.out.println("不为空");
        } else{
            //写为空的逻辑
            System.out.println("为空");
        }

        // Optional.ifPresent() 方法 (判读是否为空并返回函数)如果对象非空，则运行函数体如果对象不为空，则会打印这个年龄，因为内部已经做了 NPE（非空判断），所以就不用担心空指针异常了
        Optional.ofNullable(person).ifPresent(p -> System.out.println("年龄"+p.getAge()));

        // Optional.filter() 方法 (过滤对象)
        // filter() 方法大致意思是，接受一个对象，然后对他进行条件过滤，如果条件符合则返回 Optional 对象本身，如果不符合则返回空 Optional
        Optional.ofNullable(person).filter(p -> p.getAge()>50);

        // Optional.map() 方法 (对象进行二次包装) Optional.flatMap()
        // map() 方法将对应 Funcation 函数式接口中的对象，进行二次运算，封装成新的对象然后返回在 Optional 中
        String optName = Optional.ofNullable(person).map(p -> person.getName()).orElse("name为空");
        Optional<Object> optName2 = Optional.ofNullable(person).map(p -> Optional.ofNullable(p.getName()).orElse("name为空"));

        // Optional.orElse() 方法 (为空返回对象)
        // 常用方法之一，这个方法意思是如果包装对象为空的话，就执行 orElse 方法里的 value，如果非空，则返回写入对象

        // Optional.orElseGet() 方法 (为空返回 Supplier 对象)
        // 这个与 orElse 很相似，入参不一样，入参为 Supplier 对象，为空返回传入对象的. get() 方法，如果非空则返回当前对象
        Optional<Supplier<Student>> sup=Optional.ofNullable(Student::new);
        //调用get()方法，此时才会调用对象的构造方法，即获得到真正对象
        Optional.ofNullable(person).orElseGet(sup.get());

        // Optional.orElseThrow() 方法 (为空返回异常)
        // 方法作用的话就是如果为空，就抛出你定义的异常，如果不为空返回当前对象
        //简单的一个查询
        //Member member = memberService.selectByPhone(request.getPhone());
        //Optional.ofNullable(member).orElseThrow(() -> new ServiceException("没有查询的相关数据"));

        /**
         * orElse() 和 orElseGet() 和 orElseThrow() 的异同点
         * 方法效果类似，如果对象不为空，则返回对象，如果为空，则返回方法体中的对应参数，所以可以看出这三个方法体中参数是不一样的
         * orElse（T 对象）
         * orElseGet（Supplier <T> 对象）
         * orElseThrow（异常）
         * map() 和 orElseGet 的异同点
         * 方法效果类似，对方法参数进行二次包装，并返回, 入参不同
         * map（function 函数）
         * flatmap（Optional<function> 函数）
         */

        // 实战场景
        // 场景 1：在 service 层中查询一个对象，返回之后判断是否为空并做处理
        //查询一个对象
        //Member member = memberService.selectByIdNo(request.getCertificateNo());
        //使用ofNullable加orElseThrow做判断和操作
        //Optional.ofNullable(member).orElseThrow(() -> new ServiceException("没有查询的相关数据"));
        //场景 2：我们可以在 dao 接口层中定义返回值时就加上 Optional 例如：我使用的是 jpa，其他也同理

        // public interface LocationRepository extends JpaRepository<Location, String> {
        //     Optional<Location> findLocationById(String id);
        // }
        // 然在是 Service 中

        // public TerminalVO findById(String id) {
        // //这个方法在dao层也是用了Optional包装了
        //         Optional<Terminal> terminalOptional = terminalRepository.findById(id);
        //         //直接使用isPresent()判断是否为空
        //         if (terminalOptional.isPresent()) {
        //         //使用get()方法获取对象值
        //             Terminal terminal = terminalOptional.get();
        //             //在实战中，我们已经免去了用set去赋值的繁琐，直接用BeanCopy去赋值
        //             TerminalVO terminalVO = BeanCopyUtils.copyBean(terminal, TerminalVO.class);
        //             //调用dao层方法返回包装后的对象
        //             Optional<Location> location = locationRepository.findLocationById(terminal.getLocationId());
        //             if (location.isPresent()) {
        //                 terminalVO.setFullName(location.get().getFullName());
        //             }
        //             return terminalVO;
        //         }
        //         //不要忘记抛出异常
        //         throw new ServiceException("该终端不存在");
        //     }




    }

}