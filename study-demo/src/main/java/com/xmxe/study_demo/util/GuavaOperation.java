package com.xmxe.study_demo.util;

import java.io.File;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.nio.charset.Charset;

import com.google.common.base.Stopwatch;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.HashMultiset;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.MapDifference;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multiset;
import com.google.common.collect.Sets;
import com.google.common.collect.Table;
import com.google.common.collect.Sets.SetView;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import com.google.common.io.FileWriteMode;
import com.google.common.io.Files;
import com.google.common.io.LineProcessor;

public class GuavaOperation {

    public static void main(String[] args) {
        // doMultiMap();
        // doMultiSet();
        // doBiMap();
        // doTable();
        // doSetsAndMaps();
        // doEventsBus();
        // doStopWatch();
        // doFileOperation();
    }
    

    /**
     * Multimap的特点其实就是可以包含有几个重复Key的value，可以put进入多个不同value但是相同的key，但是又不会覆盖前面的内容
     */
    public static void doMultiMap(){
        //Multimap: key-value  key可以重复，value也可重复
        Multimap<String, String> multimap = ArrayListMultimap.create();
        multimap.put("csc","1");
        multimap.put("lwl","1");
        multimap.put("csc","1");
        multimap.put("lwl","one");
        System.out.println(multimap.get("csc"));// [1, 1]
        System.out.println(multimap.get("lwl"));// [1, one]
    }

    /**
     * MultiSet 有一个相对有用的场景，就是跟踪每种对象的数量，所以可以用来进行数量统计
     */
    public static void doMultiSet(){
        //MultiSet: 无序+可重复   count()方法获取单词的次数  增强了可读性+操作简单
        Multiset<String> set = HashMultiset.create();
        set.add("csc");
        set.add("lwl");
        set.add("csc");
        System.out.println(set.size());// 3
        System.out.println(set.count("csc"));// 2
    }

    /**
     * BiMap的键必须唯一，值也必须唯一，可以实现value和key互转
     */
    public static void doBiMap(){
        BiMap<Integer,String> biMap = HashBiMap.create();
        biMap.put(1,"lwl");
        biMap.put(2,"csc");
        BiMap<String, Integer> map = biMap.inverse(); // value和key互转
        map.forEach((v, k) -> System.out.println(v + "-" + k)); // lwl-1  csc-2
    }


    /**
     * Table<R,C,V> table = HashBasedTable.create();，由泛型可以看出，table由双主键R（行）,C（列）共同决定，V是存储值
     * 
     * 新增数据：table.put(R,C,V)
     * 获取数据：V v = table.get(R,C)
     * 遍历数据: Set<R> set = table.rowKeySet(); Set<C> set = table.columnKeySet();
     */
    public static void doTable(){
        // 双键的Map Map--> Table-->rowKey+columnKey+value  
        Table<String, String, Integer> tables = HashBasedTable.create();
        tables.put("csc", "lwl", 1);
        //row+column对应的value
        System.out.println(tables.get("csc","lwl"));// 1
    }

    public static void doSetsAndMaps(){
        // 不可变集合的创建
        ImmutableList<String> iList = ImmutableList.of("csc", "lwl");
        ImmutableSet<String> iSet = ImmutableSet.of("csc", "lwl");
        ImmutableMap<String, String> iMap = ImmutableMap.of("csc", "hello", "lwl", "world");

        // set的交集, 并集, 差集
        HashSet<Integer> setA = Sets.newHashSet(1, 2, 3, 4, 5);  
        HashSet<Integer> setB = Sets.newHashSet(4, 5, 6, 7, 8); 
        //并集
        SetView<Integer> union = Sets.union(setA, setB);   
        //差集 setA-setB
        SetView<Integer> difference = Sets.difference(setA, setB);  
        //交集
        SetView<Integer> intersection = Sets.intersection(setA, setB);  


        // map的交集，并集，差集
        HashMap<String, Integer> mapA = Maps.newHashMap();
        mapA.put("a", 1);mapA.put("b", 2);mapA.put("c", 3);
        HashMap<String, Integer> mapB = Maps.newHashMap();
        mapB.put("b", 20);mapB.put("c", 3);mapB.put("d", 4);
        MapDifference<String, Integer> mapDifference = Maps.difference(mapA, mapB);
        //mapA 和 mapB 相同的 entry
        System.out.println(mapDifference.entriesInCommon());//  {c=3}
        //mapA 和 mapB key相同的value不同的 entry
        System.out.println(mapDifference.entriesDiffering());//  {b=(2, 20)}
        //只存在 mapA 的 entry
        System.out.println(mapDifference.entriesOnlyOnLeft());//  {a=1}
        //只存在 mapB 的 entry
        System.out.println(mapDifference.entriesOnlyOnRight());// {d=4}
    }

    /**
     * EventBus是Guava的事件处理机制，是设计模式中的观察者模式（生产/消费者编程模型）的优雅实现。对于事件监听和发布订阅模式
     * EventBus内部实现原理不复杂，EventBus内部会维护一个Multimap<Class<?>, Subscriber> map，key就代表消息对应的
     * 类(不同消息不同类，区分不同的消息)、value是一个Subscriber，Subscriber其实就是对应消息处理者。如果有消息发布就
     * 去这个map里面找到这个消息对应的Subscriber去执行
     */
    public static void doEventsBus(){

        class OrderMessage {
            public OrderMessage(){};
            public OrderMessage(String message){
                this.message = message;
            }
            String message;
            public void setMessage(String message){
                this.message = message;
            }
            public String getMessage(){
                return message;
            }
        }
        
        //使用 @Subscribe 注解,表明使用dealWithEvent 方法处理 OrderMessage类型对应的消息
        //可以注解多个方法,不同的方法 处理不同的对象消息
        class OrderEventListener {
            @Subscribe
            public void dealWithEvent(OrderMessage event) {
                System.out.println("内容：" + event.getMessage());
            }
        }

        // new AsyncEventBus(String identifier, Executor executor);
        EventBus eventBus = new EventBus("lwl"); 
        eventBus.register(new OrderEventListener());
        // 发布消息
        eventBus.post(new OrderMessage("csc"));
    }

    /**
     * 统计耗时
     */
    public static void doStopWatch(){
        Stopwatch stopwatch = Stopwatch.createStarted();
        for(int i=0; i<100000; i++){
            // do some thing
        }
        long nanos = stopwatch.elapsed(TimeUnit.MILLISECONDS);
        System.out.println("逻辑代码运行耗时："+nanos);
    }

    /**
     * 文件操作
     */
    public static void doFileOperation(){

        try{
            // 数据写入
            File newFile = new File("D:/text.txt");
            Files.write("this is a test".getBytes(), newFile);
            //再次写入会把之前的内容冲掉
            Files.write("csc".getBytes(), newFile);
            //追加写
            Files.asCharSink(newFile, Charset.defaultCharset(), FileWriteMode.APPEND).write("lwl");
            // deprecated过时的方法
            // Files.append("lwl", newFile, Charset.defaultCharset());

            // 简单读
            List<String> lines = Files.readLines(newFile, Charset.defaultCharset());
            lines.forEach(System.out::println);

            // 带有文本行处理器的“读”
            /* LineProcessor --> 数据行处理器 */
            Integer lines1 = Files.asCharSource(newFile, Charset.defaultCharset()).readLines(new LineProcessor<Integer>() {
                private int lineNum = 0;

                /*This method will be called once for each line.*/
                @Override
                public boolean processLine(String line){
                    lineNum++;
                    return true;
                }

                /*Return the result of processing all the lines. NOT the result of EACH line */
                @Override
                public Integer getResult() {
                    return lineNum;
                }
            });
            System.out.println(lines);

            // 一次将文件中内容全部读出来
            String allContent = Files.asCharSource(newFile, Charset.defaultCharset()).read();
            System.out.println(allContent);
            
            // Files.copy(from, to);// 复制文件
            // Files.move(from, to);//移动文件
            // Files.touch(file);//更新文件时间戳
            // Files.equals(File file1,File file2);// 比较两个文件之间的内容是不是完全一致的 
            // Files.createTempDir();// 方法创建临时目录
            // Files.createParentDirs(File);// 创建父级目录（只是递归创建父级记录，不会把这个文件也移动）
            // Files.hash(File);//获得文件的hash
            // Files.map(file);//获取内存映射buffer
            // Files.getFileExtension(fullname);// 获得文件的扩展名
            // Files.getNameWithoutExtension(file);// 获得不带扩展名的文件名
        }catch(Exception e){
            e.printStackTrace();
        }
        
    }
}
