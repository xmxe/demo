package com.xmxe.study_demo.stream;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.Test;

public class StreamTest {

    @Test
    public void lambda() {
        Consumer<String> methodParam = System.out::println;

        LambdaService lambdaservice = (a, b) -> {
            return a + b;
        };// 相当于LambdaService的实现类
        int c = lambdaservice.lambdaTest(3, 4);
        System.out.println("①---" + c);
        /*
         * java.util.Stream表示了某一种元素的序列，在这些元素上可以进行各种操作。Stream操作可以是中间操作，也可以是完结操作。
         * 完结操作会返回一个某种类型的值，而中间操作会返回流对象本身，并且你可以通过多次调用同一个流操作方法来将操作结果串起来。
         * Stream是在一个源的基础上创建出来的，例如java.util.Collection中的list或者set（map不能作为Stream的源）。
         * Stream操作往往可以通过顺序或者并行两种方式来执行。
         */
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

        List<Integer> list = new ArrayList<>();
        Collections.addAll(list, 5, 16, 41, 10);
        /* stream().map()可以看作对象转换 */
        list.stream().sorted((x, y) -> (x - y)).map(String::valueOf).filter(x -> x.startsWith("1"))
                .forEach(x -> methodParam.accept("④---" + x));
    }

    @Test
    public void stream() {

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
        List<Student> streamStudents = students.stream().filter(s -> "浙江".equals(s.getAddress()))
                .collect(Collectors.toList());
        streamStudents.forEach(System.out::println);


        // map(转换)
        List<String> addresses = students.stream().map(s -> "住址:" + s.getAddress()).collect(Collectors.toList());
        addresses.forEach(a -> System.out.println(a));

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
        if (anyMatch) {
            System.out.println("有湖北人");
        }
        Boolean allMatch = students.stream().allMatch(s -> s.getAge() >= 15);
        if (allMatch) {
            System.out.println("所有学生都满15周岁");
        }
        Boolean noneMatch = students.stream().noneMatch(s -> "杨洋".equals(s.getName()));
        if (noneMatch) {
            System.out.println("没有叫杨洋的同学");
        }
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
        

    }



    

    @Test
    public void map(){
        //将集合中的每一个字符串，全部转换成大写
        List<String> alpha = Arrays.asList("Monkey", "Lion", "Giraffe", "Lemur");
        // 使用Stream管道流
        List<String> collect = alpha.stream().map(String::toUpperCase).collect(Collectors.toList());
        //上面使用了方法引用，和下面的lambda表达式语法效果是一样的
        List<String> collectLambda = alpha.stream().map(s -> s.toUpperCase()).collect(Collectors.toList());

        // 处理非字符串类型集合元素
        List<Integer> lengths = alpha.stream()
        .map(String::length)
        .collect(Collectors.toList());

        System.out.println(lengths); //[6, 4, 7, 5]
        Stream.of("Monkey", "Lion", "Giraffe", "Lemur")
                .mapToInt(String::length)
                .forEach(System.out::println);

        //flatMap
        List<String> words = Arrays.asList("hello", "word");
        words.stream().map(w -> Arrays.stream(w.split(""))).forEach(System.out::println);//[[h,e,l,l,o],[w,o,r,l,d]]
        words.stream().flatMap(w -> Arrays.stream(w.split(""))).forEach(System.out::println); // [h,e,l,l,o,w,o,r,l,d]





    }
}