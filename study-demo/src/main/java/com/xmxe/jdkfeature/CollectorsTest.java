package com.xmxe.jdkfeature;

import java.util.Collections;
import java.util.Comparator;
import java.util.DoubleSummaryStatistics;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.function.BinaryOperator;
import java.util.stream.Collectors;

import com.xmxe.entity.Student;

/**
 * https://docs.oracle.com/javase/8/docs/api/java/util/stream/Collectors.html
 * java.util.stream.Collectors它会根据不同的策略将元素收集归纳起来,例如将元素累积到集合中,根据各种标准汇总元素等。
 */
public class CollectorsTest {
    public static void main(String[] args) {
        Student s1 = new Student(1L, "a", 15, "浙江");
        List<Student> students = List.of(s1);

        // counting方法返回一个Collector收集器接受T类型的元素,用于计算输入元素的数量。如果没有元素,则结果为0。
        Long count = students.stream().collect(Collectors.counting());
        
        // maxBy方法(最小的minBy同理)返回一个Collector收集器,它根据给定的Comparator比较器生成最大元素,描述为Optional。
        Integer maxAge = students.stream().map(Student::getAge).collect(Collectors.maxBy(Integer::compare)).get();
        Optional<Student> maxAge2 = students.stream().collect(Collectors.maxBy(Comparator.comparingInt(Student::getAge)));

        // summingDouble(summingInt,summingLong)返回一个Collector收集器,它生成应用于输入元素的double值函数的总和。如果没有元素,则结果为0。
        // 返回的总和可能会因记录值的顺序而变化,这是由于除了不同大小的值之外,还存在累积舍入误差。通过增加绝对量排序的值(即总量,样本越大,结果越准确)往往会产生更准确的结果。如果任何记录的值是NaN或者总和在任何点NaN,那么总和将是NaN。
        Double sd = students.stream().collect(Collectors.summingDouble(Student::getAge));
        Integer sumAge = students.stream().collect(Collectors.summingInt(Student::getAge));
        
        // averagingDouble(averagingInt,averagingLong)方法返回一个Collector收集器,它生成应用于输入元素的double值函数的算术平均值。如果没有元素,则结果为0。
        // 返回的平均值可能会因记录值的顺序而变化,这是由于除了不同大小的值之外,还存在累积舍入误差。通过增加绝对量排序的值(即总量,样本越大,结果越准确)往往会产生更准确的结果。如果任何记录的值是NaN或者总和在任何点NaN,那么平均值将是NaN。注意:double格式可以表示-253到253范围内的所有连续整数。如果管道有超过253的值,则平均计算中的除数将在253处饱和,从而导致额外的数值误差。
        Double averageAge = students.stream().collect(Collectors.averagingDouble(Student::getAge));
        
        // summarizingDouble(summarizingInt,summarizingLong)方法返回一个Collector收集器,它将double生成映射函数应用于每个输入元素,并返回结果值的摘要统计信息。
        DoubleSummaryStatistics statistics = students.stream().collect(Collectors.summarizingDouble(Student::getAge));
        System.out.println("count:" + statistics.getCount() + ",max:" + statistics.getMax() + ",sum:"
                + statistics.getSum() + ",average:" + statistics.getAverage());
        
        // joining()方法返回一个Collector收集器,它按遇见顺序将输入元素连接成String。
        String stu = students.stream().map(Student::getName).collect(Collectors.joining());
        // joining(delimiter)方法返回一个Collector收集器,它以遇见顺序连接由指定分隔符分隔的输入元素。
        String stu1 = students.stream().map(Student::getName).collect(Collectors.joining(","));
        // joining(delimiter, prefix, suffix)方法返回一个Collector收集器,它以遇见顺序将由指定分隔符分隔的输入元素与指定的前缀和后缀连接起来。
        String stu2 = students.stream().map(Student::getName).collect(Collectors.joining(",", "Names[", "]"));// // Names[刘一,陈二,张三,李四,王五,赵六,孙七]

        // mapping方法通过在累积之前将映射函数应用于每个输入元素,将Collector收集器接受U类型的元素调整为一个接受T类型的元素。
        // 将所有学生的姓名以","分隔连接成字符串
        String stu3 = students.stream().collect(Collectors.mapping(Student::getName, Collectors.joining(",")));// 刘一,陈二,张三,李四,王五,赵六,孙七

        // groupingBy分组 groupingBy(Function)方法返回一个Collector收集器对T类型的输入元素执行"group by"操作,根据分类函数对元素进行分组,并将结果返回到Map。分类函数将元素映射到某些键类型K。收集器生成一个<Map>,其键是将分类函数应用于输入元素得到的值,其对应值为List,其中包含映射到分类函数下关联键的输入元素。
        // 无法保证返回的Map或List对象的类型,可变性,可序列化或线程安全性。注意:返回的Collector收集器不是并发的。对于并行流管道,combiner函数通过将键从一个映射合并到另一个映射来操作,这可能是一个昂贵的操作。如果不需要保留元素出现在生成的Map收集器中的顺序,则使用groupingByConcurrent(Function)可以提供更好的并行性能。
        Map<Integer, List<Student>> ageMap = students.stream().collect(Collectors.groupingBy(Student::getAge));
        // groupingBy(Function, Collector)方法返回一个Collector收集器,对T类型的输入元素执行级联"group by"操作,根据分类函数对元素进行分组,然后使用指定的下游Collector收集器对与给定键关联的值执行缩减操作。
        // 分类函数将元素映射到某些键类型K。下游收集器对T类型的元素进行操作,并生成D类型的结果。产生收集器生成Map<K, D>。返回的Map的类型,可变性,可序列化或线程安全性无法保证。注意:返回的Collector收集器不是并发的。对于并行流管道,combiner函数通过将键从一个映射合并到另一个映射来操作,这可能是一个昂贵的操作。如果不需要保留向下游收集器提供元素的顺序,则使用groupingByConcurrent(Function, Collector)可以提供更好的并行性能。
        Map<Integer,Long> am = students.stream().collect(Collectors.groupingBy(Student::getAge, Collectors.counting()));
        // 多重分组,先根据年龄分再根据地址分
        Map<Integer, Map<String, List<Student>>> typeAgeMap = students.stream().collect(Collectors.groupingBy(Student::getAge, Collectors.groupingBy(Student::getAddress)));
        // groupingBy(Function, Supplier, Collector)方法返回一个Collector收集器,对T类型的输入元素执行级联"group by"操作,根据分类函数对元素进行分组,然后使用指定的下游Collector收集器对与给定键关联的值执行缩减操作。收集器生成的Map是使用提供的工厂函数创建的。
        // 分类函数将元素映射到某些键类型K。下游收集器对T类型的元素进行操作,并生成D类型的结果。产生收集器生成Map<K, D>。注意:返回的Collector收集器不是并发的。对于并行流管道,combiner函数通过将键从一个映射合并到另一个映射来操作,这可能是一个昂贵的操作。如果不需要保留向下游收集器提供元素的顺序,则使用groupingByConcurrent(Function, Supplier, Collector)可以提供更好的并行性能。
        TreeMap<Integer, Double> map = students.stream().collect(Collectors.groupingBy(Student::getAge,TreeMap::new,Collectors.averagingInt(Student::getAge)));
        
        // groupingByConcurrent返回一个并发Collector收集器对T类型的输入元素执行"group by"操作,根据分类函数对元素进行分组
        // 重载方法同groupingBy。这是一个Collector.Characteristics#CONCURRENT并发和Collector.Characteristics#UNORDERED无序收集器。分类函数将元素映射到某些键类型K。收集器生成一个ConcurrentMap<K, List>,其键是将分类函数应用于输入元素得到的值,其对应值为List,其中包含映射到分类函数下关联键的输入元素。无法保证返回的Map或List对象的类型,可变性或可序列化,或者返回的List对象的线程安全性。

        // 分区 partitioningBy(Predicate)方法返回一个Collector收集器,它根据Predicate对输入元素进行分区,并将它们组织成Map。返回的Map的类型,可变性,可序列化或线程安全性无法保证。
        // 分成两部分,一部分大于10岁,一部分小于等于10岁
        Map<Boolean, List<Student>> partMap = students.stream().collect(Collectors.partitioningBy(v -> v.getAge() > 10));
        // partitioningBy(Predicate, Collector)方法返回一个Collector收集器,它根据Predicate对输入元素进行分区,根据另一个Collector收集器减少每个分区中的值,并将它们组织成Map,其值是下游减少的结果。
        Map<Boolean, Double> collect = students.stream().collect(Collectors.partitioningBy(v -> v.getAge() > 10, Collectors.averagingInt(Student::getAge)));

        // reducing(BinaryOperator)返回一个Collector收集器,它在指定的BinaryOperator下执行其输入元素的缩减。结果被描述为Optional。
        // reducing()相关收集器在groupingBy或partitioningBy下游的多级缩减中使用时非常有用。要对流执行简单缩减,请使用Stream#reduce(BinaryOperator)。
        Integer allAge = students.stream().map(Student::getAge).collect(Collectors.reducing(Integer::sum)).get();
        // 列出所有学生中年龄最大的学生信息
        students.stream().collect(Collectors.reducing(BinaryOperator.maxBy(Comparator.comparingInt(Student::getAge)))).ifPresent(System.out::println);
        // 上面也可直接使用reduce
        students.stream().reduce(BinaryOperator.maxBy(Comparator.comparingInt(Student::getAge))).ifPresent(System.out::println);
        // reducing(Object, BinaryOperator)返回一个Collector收集器,它使用提供的标识在指定的BinaryOperator下执行其输入元素的缩减。
        Integer result = students.stream().map(Student::getAge).collect(Collectors.reducing(0, (d1, d2) -> d1 + d2));
        // 上面也可直接使用reduce
        Integer result1 = students.stream().map(Student::getAge).reduce(0, (d1, d2) -> d1 + d2);
        // reducing(Object, Function, BinaryOperator)返回一个Collector收集器,它在指定的映射函数和BinaryOperator下执行其输入元素的缩减。这是对reducing(Object, BinaryOperator)的概括,它允许在缩减之前转换元素。
        Integer result2 = students.stream().collect(Collectors.reducing(0, Student::getAge, (d1, d2) -> d1 + d2));
        // 上面也可直接使用reduce
        Integer result3 = students.stream().map(Student::getAge).reduce(0, (d1, d2) -> d1 + d2);

        // collectingAndThen方法调整Collector收集器以执行其它的结束转换。例如,可以调整toList()收集器,以始终生成一个不可变的列表
        List<Student> studentList = students.stream().collect(Collectors.collectingAndThen(Collectors.toList(), Collections::unmodifiableList));
        // 所有学生的平均年龄
        String str = students.stream().collect(Collectors.collectingAndThen(Collectors.averagingInt(Student::getAge), a -> "The Average is->" + a));

        // toCollection返回一个Collector收集器,它按遇见顺序将输入元素累积到一个新的Collection收集器中。Collection收集器由提供的工厂创建。
        LinkedList<Student> linkedList = students.stream().filter(d -> d.getAge() > 10).collect(Collectors.toCollection(LinkedList::new));

        // toConcurrentMap(Function, Function)返回一个并发的Collector收集器,它将元素累积到ConcurrentMap中,其键和值是将提供的映射函数应用于输入元素的结果。
        // 如果映射的键包含重复项(根据Object#equals(Object)),则在执行收集操作时会抛出IllegalStateException。如果映射的键可能有重复,请使用toConcurrentMap(Function, Function, BinaryOperator)。注意:键或值作为输入元素是常见的。在这种情况下,实用方法java.util.function.Function#identity()可能会有所帮助。这是一个Collector.Characteristics#CONCURRENT并发和Collector.Characteristics#UNORDERED无序收集器。
        ConcurrentMap<String,Integer> concurrentHashMap = students.stream().collect(Collectors.toConcurrentMap(Student::getName, Student::getAge));
        // toConcurrentMap(Function, Function, BinaryOperator)返回一个并发的Collector收集器,它将元素累积到ConcurrentMap中,其键和值是将提供的映射函数应用于输入元素的结果。
        // 如果映射的键包含重复项(根据Object#equals(Object)),则将值映射函数应用于每个相等的元素,并使用提供的合并函数合并结果。注意:有多种方法可以处理映射到同一个键的多个元素之间的冲突。toConcurrentMap的其它形式只是使用无条件抛出的合并函数,但你可以轻松编写更灵活的合并策略。例如,如果你有一个Person流,并且你希望生成一个“电话簿”映射名称到地址,但可能有两个人具有相同的名称,你可以按照以下方式进行优雅的处理这些冲突,并生成一个Map将名称映射到连接的地址列表中:Map<String, String> phoneBook = people.stream().collect(toConcurrentMap(Person::getName,Person::getAddress, (s, a) -> s + ", " + a));
        ConcurrentMap<Integer,Integer> concurrentHashMap1 = students.stream().collect(Collectors.toConcurrentMap(Student::getAge, v -> 1, (a, b) -> a + b));
        // toConcurrentMap(Function, Function, BinaryOperator, Supplier)返回一个并发的Collector收集器,它将元素累积到ConcurrentMap中,其键和值是将提供的映射函数应用于输入元素的结果。
        // 如果映射的键包含重复项(根据Object#equals(Object)),则将值映射函数应用于每个相等的元素,并使用提供的合并函数合并结果。ConcurrentMap由提供的供应商函数创建。这是一个Collector.Characteristics#CONCURRENT并发和Collector.Characteristics#UNORDERED无序收集器。
        ConcurrentSkipListMap<String,Integer> concurrentSkipListMap = students.stream().collect(Collectors.toConcurrentMap(Student::getName, v -> 1, (a, b) -> a + b, ConcurrentSkipListMap::new));
    
        // toMap(Function, Function)返回一个Collector收集器,它将元素累积到Map中,其键和值是将提供的映射函数应用于输入元素的结果。如果映射的键包含重复项(根据Object#equals(Object)),则在执行收集操作时会抛出IllegalStateException。如果映射的键可能有重复,请使用toMap(Function, Function, BinaryOperator)。注意:键或值作为输入元素是常见的。在这种情况下,实用方法java.util.function.Function#identity()可能会有所帮助。返回的Collector收集器不是并发的。对于并行流管道,combiner函数通过将键从一个映射合并到另一个映射来操作,这可能是一个昂贵的操作。如果不需要将结果以遇见的顺序插入Map,则使用toConcurrentMap(Function, Function)可以提供更好的并行性能。
        // toMap(Function, Function, BinaryOperator)返回一个并发的Collector收集器,它将元素累积到Map中,其键和值是将提供的映射函数应用于输入元素的结果。如果映射的键包含重复项(根据Object#equals(Object)),则将值映射函数应用于每个相等的元素,并使用提供的合并函数合并结果。注意:有多种方法可以处理映射到同一个键的多个元素之间的冲突。toMap的其它形式只是使用无条件抛出的合并函数,但你可以轻松编写更灵活的合并策略。例如,如果你有一个Person流,并且你希望生成一个“电话簿”映射名称到地址,但可能有两个人具有相同的名称,你可以按照以下方式进行优雅的处理这些冲突,并生成一个Map将名称映射到连接的地址列表中:Map<String, String> phoneBook = people.stream().collect(toConcurrentMap(Person::getName,Person::getAddress,(s, a) -> s + ", " + a));返回的Collector收集器不是并发的。对于并行流管道,combiner函数通过将键从一个映射合并到另一个映射来操作,这可能是一个昂贵的操作。如果不需要将结果以遇见的顺序插入Map,则使用toConcurrentMap(Function, Function, BinaryOperator)可以提供更好的并行性能。
        // toMap(Function, Function, BinaryOperator, Supplier)返回一个并发的Collector收集器,它将元素累积到Map中,其键和值是将提供的映射函数应用于输入元素的结果。如果映射的键包含重复项(根据Object#equals(Object)),则将值映射函数应用于每个相等的元素,并使用提供的合并函数合并结果。Map由提供的供应商函数创建。注意:返回的Collector收集器不是并发的。对于并行流管道,combiner函数通过将键从一个映射合并到另一个映射来操作,这可能是一个昂贵的操作。如果不需要将结果以遇见的顺序插入Map,则使用toConcurrentMap(Function, Function, BinaryOperator, Supplier)可以提供更好的并行性能。
    
        // toSet()返回一个Collector收集器,它将输入元素累积到一个新的Set中。返回的Set的类型,可变性,可序列化或线程安全性无法保证;如果需要更多地控制返回的Set,请使用toCollection(Supplier)。这是一个Collector.Characteristics#UNORDERED无序收集器。
        Set<Student> set = students.stream().collect(Collectors.toSet());
    }
}
