package com.xmxe.study_demo.jdkfeature;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;


public class FeatureFromJDK {

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

        // distinct(去重)
        List<String> list = Arrays.asList("111", "222", "333", "111", "222");
        list.stream().distinct().forEach(System.out::println);
        students.stream().distinct().forEach(System.out::println);

        // sorted(排序)
        List<String> list_sort = Arrays.asList("333", "222", "111");
        list_sort.stream().sorted().forEach(System.out::println);
        students.stream().sorted((stu1, stu2) -> Long.compare(stu2.getId(), stu1.getId()))
                .sorted((stu1, stu2) -> Integer.compare(stu2.getAge(), stu1.getAge())).forEach(System.out::println);

        // limit（限制返回个数）
        List<String> list_limit = Arrays.asList("333", "222", "111");
        list_limit.stream().limit(2).forEach(System.out::println);

        // skip(删除元素)
        List<String> list_skip = Arrays.asList("333", "222", "111");
        list_skip.stream().skip(2).forEach(System.out::println);

        // reduce(聚合)
        List<String> list_reduce = Arrays.asList("欢", "迎", "你");
        String appendStr = list_reduce.stream().reduce("北京", (a, b) -> a + b);
        System.out.println(appendStr);

        // min(求最小值)
        Student minS = students.stream().min((stu1, stu2) -> Integer.compare(stu1.getAge(), stu2.getAge())).get();
        System.out.println(minS.toString());

        // anyMatch/allMatch/noneMatch（匹配）
        Boolean anyMatch = students.stream().anyMatch(s -> "湖北".equals(s.getAddress()));
        if (anyMatch) {System.out.println("有湖北人");}

        Boolean allMatch = students.stream().allMatch(s -> s.getAge() >= 15);
        if (allMatch) {System.out.println("所有学生都满15周岁");}

        Boolean noneMatch = students.stream().noneMatch(s -> "杨洋".equals(s.getName()));
        if (noneMatch) {System.out.println("没有叫杨洋的同学");}

        /**
         * anyMatch：Stream 中任意一个元素符合传入的 predicate，返回 true
         * allMatch：Stream 中全部元素符合传入的 predicate，返回 true
         * noneMatch：Stream 中没有一个元素符合传入的 predicate，返回 true
         */
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

        // map(转换)
        List<String> addresses = students.stream().map(s -> "住址:" + s.getAddress()).collect(Collectors.toList());
        addresses.forEach(a -> System.out.println(a));

        //将集合中的每一个字符串，全部转换成大写
        List<String> alpha = Arrays.asList("Monkey", "Lion", "Giraffe", "Lemur");
        // 使用Stream管道流
        List<String> collect = alpha.stream().map(String::toUpperCase).collect(Collectors.toList());
        //上面使用了方法引用，和下面的lambda表达式语法效果是一样的
        List<String> collectLambda = alpha.stream().map(s -> s.toUpperCase()).collect(Collectors.toList());

        // 处理非字符串类型集合元素
        List<Integer> lengths = alpha.stream().map(String::length).collect(Collectors.toList());

        System.out.println(lengths); //[6, 4, 7, 5]
        Stream.of("Monkey", "Lion", "Giraffe", "Lemur").mapToInt(String::length).forEach(System.out::println);

        //flatMap
        List<String> words = Arrays.asList("hello", "word");
        words.stream().map(w -> Arrays.stream(w.split(""))).forEach(System.out::println);//[[h,e,l,l,o],[w,o,r,l,d]]
        words.stream().flatMap(w -> Arrays.stream(w.split(""))).forEach(System.out::println); // [h,e,l,l,o,w,o,r,l,d]
 
        
    }
  

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
        BiFunction<String, Integer, Student> biFunction = Student::new;
        Student stu = biFunction.apply("mengday", 28);
        System.out.println(stu.toString());

    }

    public static void main(String[] args) {
        FeatureFromJDK t = new FeatureFromJDK();
        t.newMapMethod();
    }
}