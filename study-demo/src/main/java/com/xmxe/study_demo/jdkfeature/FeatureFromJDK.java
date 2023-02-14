package com.xmxe.study_demo.jdkfeature;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Base64;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

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
     * jdk8新增的map方法
     */
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
     * jdk8自带Base64 无需再使用第三方Base64
     */
    public void jdk8Base64(){
        String str = "str";
        String encoded = Base64.getEncoder().encodeToString(str.getBytes( StandardCharsets.UTF_8));
        String decoded = new String(Base64.getDecoder().decode(encoded), StandardCharsets.UTF_8);
    }

    /**
     * 从JDK 1.8开始，Nashorn取代Rhino(JDK 1.6, JDK1.7)成为Java的嵌入式JavaScript引擎。
     * 它使用基于JSR 292的新语言特性，将JavaScript编译成Java字节码
     */
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
