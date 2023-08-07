package com.xmxe.test;

import java.io.File;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.ehcache.shadow.org.terracotta.offheapstore.util.Retryer;

import java.nio.charset.Charset;
import java.time.LocalDateTime;

import com.google.common.base.Stopwatch;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.HashMultiset;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.common.collect.MapDifference;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multiset;
import com.google.common.collect.Range;
import com.google.common.collect.RangeMap;
import com.google.common.collect.Sets;
import com.google.common.collect.Table;
import com.google.common.collect.Tables;
import com.google.common.collect.TreeRangeMap;
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

        // 创建集合
        List<String> list = Lists.newArrayList();
        List<Integer> list2 = Lists.newArrayList(1, 2, 3);
        // 反转list
        List<Integer> reverse = Lists.reverse(list2);
        System.out.println(reverse); // 输出[3, 2, 1]
        // list集合元素太多,可以分成若干个集合,每个集合10个元素
        List<List<Integer>> partition = Lists.partition(list2, 10);

        Map<String, String> map = Maps.newHashMap();
        Set<String> set = Sets.newHashSet();
    }

    /**
     * Multimap的特点其实就是可以包含有几个重复Key的value,可以put进入多个不同value但是相同的key,但是又不会覆盖前面的内容
     */
    public static void doMultiMap() {
        // Multimap: key-value key可以重复,value也可重复
        Multimap<String, String> multimap = ArrayListMultimap.create();
        multimap.put("csc", "1");
        multimap.put("lwl", "1");
        multimap.put("csc", "1");
        multimap.put("lwl", "one");
        System.out.println(multimap.get("csc"));// [1, 1]
        System.out.println(multimap.get("lwl"));// [1, one]

        // Multimap一个key可以映射多个value的HashMap
        Multimap<String, Integer> map = ArrayListMultimap.create();
        map.put("key", 1);
        map.put("key", 2);
        Collection<Integer> values = map.get("key");
        System.out.println(map); // 输出 {"key":[1,2]}
        // 还能返回你以前使用的臃肿的Map
        Map<String, Collection<Integer>> collectionMap = map.asMap();
    }

    /**
     * MultiSet有一个相对有用的场景,就是跟踪每种对象的数量,所以可以用来进行数量统计
     */
    public static void doMultiSet() {
        // MultiSet:无序+可重复 count()方法获取单词的次数,增强了可读性+操作简单
        Multiset<String> set = HashMultiset.create();
        set.add("csc");
        set.add("lwl");
        set.add("csc");
        System.out.println(set.size());// 3
        System.out.println(set.count("csc"));// 2

        // Multiset一种用来计数的Set
        Multiset<String> multiset = HashMultiset.create();
        multiset.add("apple");
        multiset.add("apple");
        multiset.add("orange");
        System.out.println(multiset.count("apple")); // 输出2
        // 查看去重的元素
        Set<String> set2 = multiset.elementSet();
        System.out.println(set2); // 输出["orange","apple"]
        // 还能查看没有去重的元素
        Iterator<String> iterator = multiset.iterator();
        while (iterator.hasNext()) {
            System.out.println(iterator.next());
        }
        // 还能手动设置某个元素出现的次数
        multiset.setCount("apple", 5);
    }

    /**
     * BiMap的键必须唯一,值也必须唯一,可以实现value和key互转
     */
    public static void doBiMap() {
        BiMap<Integer, String> biMap = HashBiMap.create();
        biMap.put(1, "lwl");
        biMap.put(2, "csc");
        BiMap<String, Integer> map = biMap.inverse(); // value和key互转,反转后的BiMap并不是一个新的对象,它实现了一种视图的关联,所以对反转后的BiMap执行的所有操作会作用于原先的BiMap上。
        map.forEach((v, k) -> System.out.println(v + "-" + k)); // lwl-1 csc-2

        // BiMap一种连value也不能重复的HashMap
        BiMap<String, String> biMap2 = HashBiMap.create();
        // 如果value重复,put方法会抛异常,除非用forcePut方法
        biMap2.put("key", "value");
        System.out.println(biMap2); // 输出{"key":"value"}
        // 既然value不能重复,何不实现个翻转key/value的方法,已经有了
        BiMap<String, String> inverse = biMap2.inverse();
        System.out.println(inverse); // 输出{"value":"key"}
    }

    /**
     * Table<R,C,V> table = HashBasedTable.create();由泛型可以看出,table由双主键R（行）,C（列）共同决定,V是存储值
     *
     * 新增数据：table.put(R,C,V)
     * 获取数据：V v = table.get(R,C)
     * 遍历数据: Set<R> set = table.rowKeySet(); Set<C> set = table.columnKeySet();
     */
    public static void doTable() {
        // 双键的Map Map--> Table-->rowKey+columnKey+value
        Table<String, String, Integer> tables = HashBasedTable.create();
        tables.put("csc", "lwl", 1);
        // row+column对应的value
        System.out.println(tables.get("csc", "lwl"));// 1

        // 获得key或value的集合
        // rowKey或columnKey的集合
        Set<String> rowKeys = tables.rowKeySet();
        Set<String> columnKeys = tables.columnKeySet();

        // value集合
        Collection<Integer> values = tables.values();
        // 计算key对应的所有value的和
        for (String key : tables.rowKeySet()) {
            Set<Map.Entry<String, Integer>> rows = tables.row(key).entrySet();
            int total = 0;
            for (Map.Entry<String, Integer> row : rows) {
                total += row.getValue();
            }
            System.out.println(key + ": " + total);
        }

        // 转换rowKey和columnKey
        // 这一操作也可以理解为行和列的转置,直接调用Tables的静态方法transpose：

        Table<String, String, Integer> table2 = Tables.transpose(tables);
        Set<Table.Cell<String, String, Integer>> cells = table2.cellSet();
        cells.forEach(cell -> System.out.println(cell.getRowKey() + "," + cell.getColumnKey() + ":" + cell.getValue()));

        Map<String, Map<String, Integer>> rowMap = tables.rowMap();
        Map<String, Map<String, Integer>> columnMap = tables.columnMap();

        // Table:一种有两个key的HashMap
        // 一批用户,同时按年龄和性别分组
        Table<Integer, String, String> table = HashBasedTable.create();
        table.put(18, "男", "yideng");
        table.put(18, "女", "Lily");
        System.out.println(table.get(18, "男")); // 输出yideng
        // 这其实是一个二维的Map,可以查看行数据
        Map<String, String> row = table.row(18);
        System.out.println(row); // 输出{"男":"yideng","女":"Lily"}
        // 查看列数据
        Map<Integer, String> column = table.column("男");
        System.out.println(column); // 输出{18:"yideng"}
    }

    public static void doSetsAndMaps() {
        // 不可变集合的创建
        ImmutableList<String> iList = ImmutableList.of("csc", "lwl");
        ImmutableSet<String> iSet = ImmutableSet.of("csc", "lwl");
        ImmutableMap<String, String> iMap = ImmutableMap.of("csc", "hello", "lwl", "world");

        // set的交集,并集,差集
        HashSet<Integer> setA = Sets.newHashSet(1, 2, 3, 4, 5);
        HashSet<Integer> setB = Sets.newHashSet(4, 5, 6, 7, 8);
        // 并集
        SetView<Integer> union = Sets.union(setA, setB);
        // 差集 setA-setB
        SetView<Integer> difference = Sets.difference(setA, setB);
        // 交集
        SetView<Integer> intersection = Sets.intersection(setA, setB);

        // map的交集,并集,差集
        HashMap<String, Integer> mapA = Maps.newHashMap();
        mapA.put("a", 1);
        mapA.put("b", 2);
        mapA.put("c", 3);
        HashMap<String, Integer> mapB = Maps.newHashMap();
        mapB.put("b", 20);
        mapB.put("c", 3);
        mapB.put("d", 4);
        MapDifference<String, Integer> mapDifference = Maps.difference(mapA, mapB);
        // mapA和mapB相同的entry
        System.out.println(mapDifference.entriesInCommon());// {c=3}
        // mapA和mapB key相同的value不同的entry
        System.out.println(mapDifference.entriesDiffering());// {b=(2, 20)}
        // 只存在mapA的entry
        System.out.println(mapDifference.entriesOnlyOnLeft());// {a=1}
        // 只存在mapB的entry
        System.out.println(mapDifference.entriesOnlyOnRight());// {d=4}
    }

    /**
     * EventBus是Guava的事件处理机制,是设计模式中的观察者模式（生产/消费者编程模型）的优雅实现。对于事件监听和发布订阅模式
     * EventBus内部实现原理不复杂,EventBus内部会维护一个Multimap<Class<?>,Subscriber> map,key就代表消息对应的类(不同消息不同类,区分不同的消息)、value是一个Subscriber,
     * Subscriber其实就是对应消息处理者。如果有消息发布就去这个map里面找到这个消息对应的Subscriber去执行
     */
    public static void doEventsBus() {

        class OrderMessage {
            public OrderMessage() {
            };

            public OrderMessage(String message) {
                this.message = message;
            }

            String message;

            public void setMessage(String message) {
                this.message = message;
            }

            public String getMessage() {
                return message;
            }
        }

        // 使用@Subscribe注解,表明使用dealWithEvent方法处理OrderMessage类型对应的消息,可以注解多个方法,不同的方法处理不同的对象消息
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
    public static void doStopWatch() {
        Stopwatch stopwatch = Stopwatch.createStarted();
        for (int i = 0; i < 100000; i++) {
            // do some thing
        }
        long nanos = stopwatch.elapsed(TimeUnit.MILLISECONDS);
        System.out.println("逻辑代码运行耗时：" + nanos);
    }

    /**
     * 文件操作
     */
    public static void doFileOperation() {

        try {
            // 数据写入
            File newFile = new File("D:/text.txt");
            Files.write("this is a test".getBytes(), newFile);
            // 再次写入会把之前的内容冲掉
            Files.write("csc".getBytes(), newFile);
            // 追加写
            Files.asCharSink(newFile, Charset.defaultCharset(), FileWriteMode.APPEND).write("lwl");
            // deprecated过时的方法
            // Files.append("lwl", newFile, Charset.defaultCharset());

            // 简单读
            List<String> lines = Files.readLines(newFile, Charset.defaultCharset());
            lines.forEach(System.out::println);

            // 带有文本行处理器的“读”
            /* LineProcessor --> 数据行处理器 */
            Integer lines1 = Files.asCharSource(newFile, Charset.defaultCharset())
                    .readLines(new LineProcessor<Integer>() {
                        private int lineNum = 0;

                        /* This method will be called once for each line. */
                        @Override
                        public boolean processLine(String line) {
                            lineNum++;
                            return true;
                        }

                        /* Return the result of processing all the lines. NOT the result of EACH line */
                        @Override
                        public Integer getResult() {
                            return lineNum;
                        }
                    });
            System.out.println(lines);

            // 一次将文件中内容全部读出来
            String allContent = Files.asCharSource(newFile,Charset.defaultCharset()).read();
            System.out.println(allContent);

            // Files.copy(from, to);// 复制文件
            // Files.move(from, to);// 移动文件
            // Files.touch(file);// 更新文件时间戳
            // Files.equals(File file1,File file2);// 比较两个文件之间的内容是不是完全一致的
            // Files.createTempDir();// 方法创建临时目录
            // Files.createParentDirs(File);// 创建父级目录（只是递归创建父级记录,不会把这个文件也移动）
            // Files.hash(File);// 获得文件的hash
            // Files.map(file);// 获取内存映射buffer
            // Files.getFileExtension(fullname);// 获得文件的扩展名
            // Files.getNameWithoutExtension(file);// 获得不带扩展名的文件名
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static void doRangeMap() {
        RangeMap<Integer, String> rangeMap = TreeRangeMap.create();
        rangeMap.put(Range.closedOpen(0, 60), "fail");
        rangeMap.put(Range.closed(60, 90), "satisfactory");
        rangeMap.put(Range.openClosed(90, 100), "excellent");

        System.out.println(rangeMap.get(59));// fail
        System.out.println(rangeMap.get(60));// satisfactory
        System.out.println(rangeMap.get(90));// satisfactory
        System.out.println(rangeMap.get(91));// excellent

    }

    /**
     * guava retry
     * <dependency>
     *     <groupId>com.github.rholder</groupId>
     *     <artifactId>guava-retrying</artifactId>
     *     <version>2.0.0</version>
     * </dependency>
     */
    // public String guavaRetry(Integer num) {
    //     Retryer<String> retryer = RetryerBuilder.<String>newBuilder()
    //             //无论出现什么异常,都进行重试,还有retryIfRuntimeException(),retryIfExceptionOfType(NullPointException.class),retryIfException(e -> e.getMessage().contains("NullPointerException"))
    //             .retryIfException()
    //             //返回结果为error时,进行重试
    //             .retryIfResult(result -> Objects.equals(result, "error"))
    //             //重试等待策略：等待2s后再进行重试,其他的重试策略WaitStrategies.exponentialWait(100, 5, TimeUnit.MINUTES)指数等待策略,WaitStrategies.fibonacciWait(100, 2, TimeUnit.MINUTES)斐波那契等待策略,WaitStrategies.randomWait(10, TimeUnit.SECONDS)随机时长等待策略,WaitStrategies.incrementingWait(1, TimeUnit.SECONDS, 5, TimeUnit.SECONDS)递增等待策略,WaitStrategies.exceptionWait(ArithmeticException.class, e -> 1000L)异常等待策略,WaitStrategies.join(WaitStrategies.exceptionWait(ArithmeticException.class, e -> 1000L),WaitStrategies.fixedWait(5, TimeUnit.SECONDS))复合等待策略
    //             .withWaitStrategy(WaitStrategies.fixedWait(2, TimeUnit.SECONDS))
    //             //重试停止策略：重试达到3次,其他策略StopStrategies.neverStop()无限重试,StopStrategies.stopAfterDelay(3, TimeUnit.MINUTES)重试多长时间后停止
    //             .withStopStrategy(StopStrategies.stopAfterAttempt(3))
    //             .withRetryListener(new RetryListener() {
    //                 @Override
    //                 public <V> void onRetry(Attempt<V> attempt) {
    //                        // getAttemptNumber(),表示准备开始第几次重试；
    //                        // getDelaySinceFirstAttempt(),表示距离第一次重试的延迟,也就是与第一次重试的时间差,单位毫秒；
    //                        // hasException(),表示是异常导致的重试还是正常返回；
    //                        // hasResult(),表示是否返回了结果；因为有时候是因为返回了特定结果才进行重试；
    //                        // getExceptionCause(),如果是异常导致的重试,那么获取具体具体的异常类型；
    //                        // getResult(),返回重试的结果；
    //                        // get(),如果有的话,返回重试的结果；和getResult不同的在于对异常的处理；
    //                     System.out.println("RetryListener: 第" + attempt.getAttemptNumber() + "次调用");
    //                 }
    //             })
    //            // 自定义阻塞策略:自旋锁.默认只提供一种阻塞策略:ThreadSleepStrategy,实现方式是通过Thread.sleep(sleepTime)来实现
    //             .withBlockStrategy(new BlockStrategy(){
    //                  @Override
    //                    public void block(long sleepTime) throws InterruptedException {                       
    //                        LocalDateTime startTime = LocalDateTime.now();
    //                        long start = System.currentTimeMillis();
    //                        long end = start;
    //                        log.info("[SpinBlockStrategy]...begin wait.");
    //                        while (end - start <= sleepTime) {
    //                            end = System.currentTimeMillis();
    //                        }
    //                        //使用Java8新增的Duration计算时间间隔
    //                        Duration duration = Duration.between(startTime, LocalDateTime.now());
    //                        log.info("[SpinBlockStrategy]...end wait.duration={}", duration.toMillis());                       
    //                    }
    //                })
    //              // 任务执行时长限制,noTimeLimit无时长限制,其他的还有AttemptTimeLimiters.fixedTimeLimit(10, TimeUnit.SECONDS, Executors.newCachedThreadPool())指定任务的执行时长限制
    //              .withAttemptTimeLimiter(AttemptTimeLimiters.noTimeLimit())
    //             .build();
    //     try {
    //         String result = retryer.call(new Callable<String>(){
    //                            @Override
    //                            public String call() throws Exception {
    //                                return "error";
    //                            }
    //                        });
    //     } catch (Exception e) {
    //         e.printStackTrace();
    //     }
    //     return "test";
    // }
}
