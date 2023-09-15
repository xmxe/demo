package com.xmxe.jdkfeature;

import java.io.BufferedReader;
import java.io.FileReader;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.AbstractMap;
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
import java.util.Optional;
import java.util.Random;
import java.util.Set;
import java.util.Stack;
import java.util.function.Supplier;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import com.xmxe.entity.Student;

public class StreamTest {

    public void stream() {
        /*
         * java.util.Stream表示了某一种元素的序列,在这些元素上可以进行各种操作。Stream操作可以是中间操作,也可以是完结操作。
         * 完结操作会返回一个某种类型的值,而中间操作会返回流对象本身,并且你可以通过多次调用同一个流操作方法来将操作结果串起来。
         * Stream是在一个源的基础上创建出来的,例如java.util.Collection中的list或者set（map不能作为Stream的源）。
         * Stream操作往往可以通过顺序或者并行两种方式来执行。
         */
        Student s1 = new Student(1L, "a", 15, "浙江");
        List<Student> students = List.of(s1);
        List<String> list = List.of("a", "b", "c");
        List<Integer> number = List.of(1, 2, 3, 4);
        String[] strArray = new String[] { "a", "b", "c" };

        /*
         * 构造Stream流的方式
         */
        Stream<String> stream = Stream.of("a", "b", "c");
        Stream<String> stream1 = Stream.of(strArray);
        Stream<String> stream2 = Arrays.stream(strArray);
        Stream<String> stream3 = list.stream();
        // 获取一个并行流
        Stream<String> parallelStream = list.parallelStream();
        // iterate与reduce很像,使用时管道必须有limit这样的操作来限制Stream大小。 从2开始生成一个等差数列
        Stream<Integer> stream5 = Stream.iterate(0, x -> x + 2).limit(6);
        stream5.forEach(System.out::println); // 0 2 4 6 8 10
        // 注意:一个Stream流只可以使用一次,重复使用了多次会抛出stream has already been operated upon or closed异常。

        /*
         * Stream流的之间的转换
         */
        try {
            // stream转换成Array
            String[] strArray1 = stream2.toArray(String[]::new);
            // stream转换成Collection
            List<String> list1 = stream1.collect(Collectors.toList());
            List<String> list2 = stream2.collect(Collectors.toCollection(ArrayList::new));
            Set<String> set = stream3.collect(Collectors.toSet());
            Stack<String> stack = stream3.collect(Collectors.toCollection(Stack::new));
            // 转换成String
            String str = stream.collect(Collectors.joining()).toString();
        } catch (Exception e) {
            e.printStackTrace();
        }

        /*
         * list转map
         * 使用Collectors.toMap的时候,如果有可以重复会报错,所以需要加(k1,k2) -> k1
         * (oldValue,newValue) -> oldValue表示,如果有重复的key,则保留旧值
         * 类似的,还有Collectors.toList()、Collectors.toSet(),表示把对应的流转化为list或者Set。
         */
        Map<Long, Student> list2map = students.stream()
                .collect(Collectors.toMap(Student::getId, student -> student, (oldValue, newValue) -> oldValue));

        /*
         * stream和parallelStream的简单区分:
         * stream是顺序流,由主线程按顺序对流执行操作,而parallelStream是并行流,内部以多线程并行执行的方式对流进行操作,但前提是流中的数据处理没有顺序要求
         * 如果流中的数据量足够大,并行流可以加快处速度。除了直接创建并行流,还可以通过parallel()把顺序流转换成并行流
         * parallelStream是流并行处理程序的代替方法。
         */
        // 获取空字符串的数量
        long count = parallelStream.filter(string -> string.isEmpty()).count();

        // ----常用方法----

        // filter(筛选)
        List<Student> streamStudents = students.stream().filter(s -> "浙江".equals(s.getAddress())).collect(Collectors.toList());

        // distinct(去重)
        list.stream().distinct().forEach(System.out::println);
        List<String> wnums = list.stream().flatMap(line -> Stream.of(line.split(""))).filter(word -> word.length() >= 0)
                .map(String::toLowerCase).distinct().sorted().collect(Collectors.toList());

        // limit(限制返回个数)
        list.stream().limit(2).forEach(System.out::println);
        // skip(跳过指定个数的元素后返回新的stream) limit和skip函数结合使用,可以方便地实现分页功能
        list.stream().skip(2).forEach(System.out::println);
        int pageSize = 3;
        int pageNum = 1;
        int start = (pageNum - 1) * pageSize;
        int end = pageNum * pageSize;
        List<Integer> result = number.stream().skip(start).limit(end - start).collect(Collectors.toList());

        // reduce(聚合)
        // 过滤拼接字符串
        Optional<String> concat1 = stream.filter(x -> x.compareTo("Z") > 0).reduce(String::concat);
        Optional<Integer> reduce = number.stream().reduce((x, y) -> x + y);
        // 两个参数的
        List<String> list_reduce = Arrays.asList("欢", "迎", "你");
        String appendStr = list_reduce.stream().reduce("北京", (a, b) -> a + b);// a="北京" b=“欢” -- a="北京欢" b="迎" ...以此类推
        // 拼接字符串->abc
        String concat = list_reduce.stream().reduce("", String::concat);
        // 三个参数的
        // 在串行流(stream)中,第三个参数combiner不会起作用.在并行流(parallelStream)中,流被fork join出多个线程进行执行,此时每个线程的执行流程就跟第二个方法reduce(identity, accumulator)一样,而第三个参数combiner函数,则是将每个线程的执行结果当成一个新的流,然后使用第一个方法reduce(accumulator)流程进行规约
        Integer v2 = number.stream().reduce(0,
                (x1, x2) -> {
                    System.out.println("stream accumulator: x1:" + x1 + "  x2:" + x2);
                    return x1 - x2;
                },
                // 并行的时候才会执行;x1和x2是每个线程的返回的容器x
                (x1, x2) -> {
                    System.out.println("stream combiner: x1:" + x1 + "  x2:" + x2);
                    return x1 * x2;
                });
        
        // stream求集合最大值最小值
        int max = number.stream().reduce(Integer::max).get();
        int min = number.stream().reduce(Integer::min).get();
        // 求和
        Integer sum1 = number.stream().reduce(Integer::sum).get();
        // 求和,有起始值
        int sumValue1 = number.stream().reduce(1, Integer::sum);
        // 得到最小值
        double minValue = Stream.of(-4.0, 1.0, 3.0, -2.0).reduce(Double.MAX_VALUE, Double::min);

        // summaryStatistics使用
        // 得到最大、最小、之和以及平均数。
        IntSummaryStatistics stats = number.stream().mapToInt((x) -> x).summaryStatistics();
        System.out.println("列表中最大的数 : " + stats.getMax() + "列表中最小的数 : " + stats.getMin()
                + "所有数之和 : " + stats.getSum() + "平均数 : " + stats.getAverage());

        // min() max()
        students.stream().max(Comparator.comparing(Student::getAge))
                .ifPresent(stu -> System.out.println("max age user:" + stu));
        students.stream().min(Comparator.comparing(Student::getAge))
                .ifPresent(stu -> System.out.println("min age user:" + stu));
        Student minS = students.stream().min((stu1, stu2) -> Integer.compare(stu1.getAge(), stu2.getAge())).get();
        Student maxS = students.stream().max((stu1, stu2) -> Integer.compare(stu1.getAge(), stu2.getAge())).get();
        int maxLines = list.stream().mapToInt(String::length).max().getAsInt();
        int minLines = list.stream().mapToInt(String::length).min().getAsInt();

        // generate()该方法主要用于生成一个无限连续的无序流，流中的元素由用户定义的supplier函数生成。该方法一般配合limit方法使用
        // 通过实现Supplier类的方法可以自定义流计算规则。随机获取两条学生信息
        class UserSupplier implements Supplier<Student> {
            private int index = 10;
            private Random random = new Random();

            @Override
            public Student get() {
                return new Student(index++, "pancm" + random.nextInt(10));
            }
        }
        Stream.generate(new UserSupplier()).limit(2).forEach(u -> System.out.println(u.getId() + ", " + u.getName()));
        // 获取随机数
        Stream<Double> stream6 = Stream.generate(Math::random).limit(2);
        stream6.forEach(System.out::println);
        Stream<Integer> ran = Stream.generate(new Random()::nextInt).limit(5);

        // map(转换)用于映射每个元素到对应的结果,一对一。
        List<String> addresses = students.stream().map(s -> "住址:" + s.getAddress()).collect(Collectors.toList());
        // 将集合中的每一个字符串,全部转换成大写
        List<String> collect = list.stream().map(String::toUpperCase).collect(Collectors.toList());
        // 上面使用了方法引用,和下面的lambda表达式语法效果是一样的
        List<String> collectLambda = list.stream().map(s -> s.toUpperCase()).collect(Collectors.toList());
        // 处理非字符串类型集合元素
        List<Integer> lengths = list.stream().map(String::length).collect(Collectors.toList());
        int sum = students.stream().mapToInt(Student::getAge).sum();

        // flatMap用于映射每个元素到对应的结果,一对多。
        List<String> words = Arrays.asList("hello", "word");
        words.stream().map(w -> Arrays.stream(w.split(""))).forEach(System.out::println);// [[h,e,l,l,o],[w,o,r,l,d]]
        words.stream().flatMap(w -> Arrays.stream(w.split(""))).forEach(System.out::println); // [h,e,l,l,o,w,o,r,l,d]
        
        // map:用于对流中的每个元素进行映射处理,然后再形成新的流 peek:用于debug调试流中间结果,不能形成新的流,但能修改引用类型字段的值
        // foreach:用于遍历,会中断流操作
        // 同map操作,但是使用peek操作流,流中的元素不会改变,能得到流中的每一个元素。map会改变流中的元素,map接收的是一个Function表达式,有返回值,而peek接收的是Consumer表达式,没有返回值
        list.stream().peek(e -> System.out.println("转换之前: " + e))
                .map(String::toUpperCase)
                .peek(e -> System.out.println("转换之后: " + e)).collect(Collectors.toList());

        /*
         * anyMatch/allMatch/noneMatch（匹配）
         * allMatch:Stream中全部元素符合则返回true;
         * anyMatch:Stream中只要有一个元素符合则返回true;
         * noneMatch:Stream中没有一个元素符合则返回true。
         */
        Boolean anyMatch = students.stream().anyMatch(s -> "湖北".equals(s.getAddress()));// if true-- System.out.println("有湖北人");
        Boolean allMatch = students.stream().allMatch(s -> s.getAge() >= 15);// if true-- System.out.println("所有学生都满15周岁");
        Boolean noneMatch = students.stream().noneMatch(s -> "杨洋".equals(s.getName()));// if true-- System.out.println("没有叫杨洋的同学");
        if (number.stream().anyMatch(n -> n % 3 == 0)) {
            System.out.println("集合中有元素是3的整数倍");
        }
        /*
         * findAny()方法返回当前流中的任意元素,可以和其他流操作结合使用,这里需要注意findAny()返回的结果被Optional所包裹,
         * 这里需要说明的就是Optional.ifPresent(Consumer<? super T> consumer)表示当Optional包裹的元素不为空时,执行consumer
         */
        number.stream().filter(n -> n > 2).findAny().ifPresent(System.out::println);
        Student stuOrelse = students.stream().findAny().orElse(s1);
        
        // 返回集合的第一个元素
        list.stream().findFirst().ifPresent(System.out::println);

        // 使用BufferedReader.lines()方法,将每行内容转成流
        try (BufferedReader reader = new BufferedReader(new FileReader("F:\\test_stream.txt"));) {
            Stream<String> lineStream = reader.lines();
            lineStream.forEach(System.out::println);
        } catch (Exception e1) {
            e1.printStackTrace();
        }

        // 使用Pattern.splitAsStream()方法,将字符串分隔成流
        Pattern pattern = Pattern.compile(",");
        Stream<String> stringStream = pattern.splitAsStream("a,b,c,d");
        stringStream.forEach(System.out::println);
    }

    /**
     * 排序
     */
    public void sort() {
        /*
         * jdk7写法
         * Set<Integer> set1 = new TreeSet<>(new Comparator<Integer>() {
         *     @Override
         *     public int compare(Integer i,Integer o) {
         *        return i - o;
         *     }
         * });
         */

        List<String> sList = List.of("t", "a", "4", "yu");
        List<Integer> integer = List.of(3, 4, 1);
        List<Student> studentList = List.of(new Student(1, "name1"), new Student(2, "name2"));
        // 创建比较器
        Comparator<Map.Entry<Integer, String>> com = Comparator.comparing(Map.Entry::getValue);
        com = com.reversed();

        /*
         * 1.使用Stream sorted()完成自然排序、比较器和反向排序
         * sorted(Comparator<? super T> comparator):这里我们使用lambda表达式创建一个Comparator实例。
         * 我们可以按升序和降序对流元素进行排序。
         */

        // sorted():它使用自然顺序对流中的元素进行排序。元素类必须实现Comparable接口。
        // 同sList.stream(Comparator.naturalOrder()).sorted()
        // 要反转自然顺序,Comparator提供reverseOrder()方法。
        sList.stream().sorted(Comparator.reverseOrder()).forEach(System.out::println);
        sList.stream().sorted().forEach(System.out::println);
        integer.stream().sorted((x, y) -> (y - x)).forEach(x -> System.out.println(x));
        // 对象多个属性排序
        studentList.stream()
                .sorted((stu1, stu2) -> Long.compare(stu2.getId(), stu1.getId()))
                .sorted((stu1, stu2) -> Integer.compare(stu2.getAge(), stu1.getAge()))
                .forEach(System.out::println);
        // student对象name属性自然排序
        studentList.stream().sorted((x,y) -> x.getName().compareTo(y.getName())).forEach(System.out::println);
        // 上面的也可以这样写
        studentList.stream().sorted(Comparator.comparing(Student::getName)).forEach(System.out::println);
        // 反转
        studentList.stream().sorted(Comparator.comparing(Student::getAge).reversed()).forEach(System.out::println);
        // 同上 但是需要将s强转成Student 否则使用reversed()报错
        studentList.stream().sorted(Comparator.comparing((Student s) -> s.getAge()).reversed()).forEach(System.out::println);
        // 并行排序
        studentList.parallelStream().sorted(Comparator.comparing(Student::getAge)).collect(Collectors.toList());

        // Map排序
        Map<String, String> map = Map.of("k1", "v1", "k2", "v2");
        map.entrySet().stream().sorted(Comparator.comparing(Map.Entry::getValue))
                .forEach(e -> System.out.println("Key: " + e.getKey() + ", Value: " + e.getValue()));
        map.entrySet().stream().sorted(Comparator.comparing(Map.Entry::getKey))
                .forEach(e -> System.out.println("Key: " + e.getKey() + ", Value: " + e.getValue()));

        /*
         * 2.不使用stream直接使用list.sort()方法排序
         */
        studentList.sort((u1, u2) -> u1.getAge() - u2.getAge());
        studentList.sort(Comparator.comparing(Student::getAge));
        studentList.sort(Comparator.comparing(Student::getAge).reversed());
        // 多次排序
        studentList.sort(Comparator.comparing(Student::getAge).thenComparing(Student::getName));

        /*
         * 3.Collections排序
         */
        Collections.sort(studentList, Comparator.comparing(Student::getAge));
        Collections.sort(studentList, (u1, u2) -> u1.getAge() - u2.getAge());

    }

    /**
     * 分组排序
     * @link CollectorsTest
     */
    public void group_order() {
        List<Student> students = new ArrayList<Student>();
        List<Map<String, Object>> list = new ArrayList<>();

        // listmap分组下面示例是根据map里的时间分组,返回一个map key是((Date)o.get("time")).getTime()
        Map<Long, List<Map<String, Object>>> groupBy = list.stream().collect(Collectors.groupingBy(o -> ((Date) o.get("time")).getTime()));
        // 根据Map的key排序创建一个LinkedHashMap,遍历时有序
        Map<Long, List<Map<String, Object>>> sortedmap = new LinkedHashMap<>();
        // Map.Entry.comparingByKey((o1,o2)->0);
        groupBy.entrySet().stream().sorted(Map.Entry.comparingByKey()) // Map.Entry.comparingByValue
                .forEachOrdered(e -> sortedmap.put(e.getKey(), e.getValue()));
        // 降序
        // groupBy.entrySet().stream().sorted(Collections.reverseOrder(Map.Entry.comparingByKey()))...
        sortedmap.forEach((k, v) -> {
            Map<String, Object> m = new HashMap<>();
            m.put("time", new Date(k));
            v.forEach(mm -> {
                // ...do something
            });

        });

        // 单属性分组(根据学生的住址分组)
        Map<String, List<Student>> groupMap = students.stream().collect(Collectors.groupingBy(Student::getAddress));
        // 多属性分组(根据用户的城市和性别分组)
        Map<String, List<Student>> groupMap2 = students.stream().collect(Collectors.groupingBy(u -> u.getAddress() + "|" + u.getAge()));

        class UserSupplier2 implements Supplier<Student> {
            private int index = 10;
            private Random random = new Random();

            @Override
            public Student get() {
                return new Student(index % 2 == 0 ? index++ : index, "pancm" + random.nextInt(10));
            }
        }
        // 通过id进行分组排序
        Map<Long, List<Student>> personGroups = Stream.generate(new UserSupplier2()).limit(5)
                .collect(Collectors.groupingBy(Student::getId));
        Iterator<Map.Entry<Long, List<Student>>> it = personGroups.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<Long, List<Student>> persons = it.next();
            System.out.println("id " + persons.getKey() + " = " + persons.getValue());
        }
        // 通过id进行分组排序:
        // id 10 = [{"id":10,"name":"pancm1"}]
        // id 11 = [{"id":11,"name":"pancm3"}, {"id":11,"name":"pancm6"},
        // {"id":11,"name":"pancm4"}, {"id":11,"name":"pancm7"}]

        // 分组
        Map<Integer, List<Student>> ageMap = students.stream().collect(Collectors.groupingBy(Student::getAge));
        // 多重分组,先根据年龄分再根据地址分
        Map<Integer, Map<String, List<Student>>> typeAgeMap = students.stream()
                .collect(Collectors.groupingBy(Student::getAge, Collectors.groupingBy(Student::getAddress)));
        // 分组聚合 统计学生相同年龄个数
        Map<Integer, Integer> ageCounts = students.stream()
                .collect(Collectors.groupingBy(Student::getAge, Collectors.summingInt(s -> 1)));

    }

    /**
     * 分区排序
     * @link CollectorsTest
     */
    public void partitioningBy(){
        List<Student> students = new ArrayList<Student>();

        class UserSupplier3 implements Supplier<Student> {
            private int index = 16;
            private Random random = new Random();

            @Override
            public Student get() {
                return new Student(index++, "pancm" + random.nextInt(10));
            }
        }
        // 通过年龄进行分区排序
        Map<Boolean, List<Student>> children = Stream.generate(new UserSupplier3()).limit(5)
                .collect(Collectors.partitioningBy(p -> p.getId() < 18));
        System.out.println("小孩: " + children.get(true) + "成年人: " + children.get(false));
        // 通过年龄进行分区排序:
        // 小孩: [{"id":16,"name":"pancm7"}, {"id":17,"name":"pancm2"}]
        // 成年人: [{"id":18,"name":"pancm4"}, {"id":19,"name":"pancm9"},
        // {"id":20,"name":"pancm6"}]
 
        // 分成两部分,一部分大于10岁,一部分小于等于10岁
        Map<Boolean, List<Student>> partMap = students.stream()
                .collect(Collectors.partitioningBy(v -> v.getAge() > 10));

    }

    /**
     * IntStream.range是Java 8中的一个方法，用于生成一个整数流（IntStream），该流包含指定范围内的连续整数。
     * IntStream.range(1, 5) // 生成整数流包含1, 2, 3, 4
     */
    public void intStream(){
        // 查找数组或list某个元素的下标

        String[] arr = {"apple", "banana", "orange", "grape"};
        // 使用IntStream.range方法创建一个从0开始到数组长度为止的整数流
        int index = IntStream.range(0, arr.length)
                .filter(i -> arr[i].equals("orange"))
                .findFirst()
                .orElse(-1);

        List<String> list = Arrays.asList("apple", "banana", "orange", "grape");
        int index2 = IntStream.range(0, list.size())
                .filter(i -> list.get(i).equals("orange"))
                .findFirst()
                .orElse(-1);

        if (index != -1) {
            System.out.println("The index of 'orange' in array is: " + index);
        } else {
            System.out.println("Element not found in array.");
        }

        // 两个相同大小List进行元素计算
        // 例如计算本月用电量与上月用电量的环比
        List<Double> ydl = List.of(3.1, 4.1, 5.2);
        List<Double> lastydl = List.of(3.3, 2.4, 4.5);
        List<Double> hb = IntStream.range(0, ydl.size())
				.mapToObj(i -> {
					BigDecimal ydlBigdecimal = new BigDecimal(String.valueOf(ydl.get(i)));
					BigDecimal lastydlBigdecimal = new BigDecimal(String.valueOf(lastydl.get(i)));
					return ydlBigdecimal.subtract(lastydlBigdecimal).divide(lastydlBigdecimal, 2, RoundingMode.HALF_UP).multiply(new BigDecimal("100")).doubleValue();
				}).collect(Collectors.toList());
    }

    /**
     * 将一个包含行数据的List转换为列数据并输出到控制台(List行转列)
     */
    public void row2column(){
        // 创建一个包含行数据的List
        List<List<Integer>> rows = new ArrayList<>();
        rows.add(Arrays.asList(1, 2, 3));
        rows.add(Arrays.asList(4, 5, 6));
        rows.add(Arrays.asList(7, 8, 9));

        // 将行数据转换为列数据核心操作
        int numCols = rows.get(0).size();
        List<List<Integer>> columns = rows.stream()
                .flatMap(row -> IntStream.range(0, numCols)
                .mapToObj(col -> new AbstractMap.SimpleEntry<>(col, row.get(col))))
                .collect(Collectors.groupingBy(Map.Entry::getKey,Collectors.mapping(Map.Entry::getValue, Collectors.toList())))
                .entrySet()
                .stream()
                .sorted(Comparator.comparingInt(Map.Entry::getKey))
                .map(Map.Entry::getValue)
                .collect(Collectors.toList());

        // 输出结果
        System.out.println("Original data:");
        // 输出数据到控制台.
        for (List<Integer> row : rows) {
            for (Integer cell : row) {
                System.out.print(cell.toString() + "\t");
            }
            System.out.println();
        }

        System.out.println("Transposed data:");
        // 输出数据到控制台.
        for (List<Integer> row : columns) {
            for (Integer cell : row) {
                System.out.print(cell.toString() + "\t");
            }
            System.out.println();
        }
    }

}
