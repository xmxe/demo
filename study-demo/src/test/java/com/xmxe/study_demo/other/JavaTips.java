package com.xmxe.study_demo.other;

import java.io.File;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import com.xmxe.study_demo.jdkfeature.Student;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.ImmutableTriple;

/**
 * 写java时的一点小技巧
 */
public class JavaTips{

    public static void main(String[] args) throws Exception {
        // 如何把list集合拼接成以逗号分隔的字符串 a,b,c
        List<String> list = Arrays.asList("a", "b", "c");
        // 第一种方法，可以用stream流
        String join = list.stream().collect(Collectors.joining(","));
        System.out.println(join); // 输出 a,b,c
        // 第二种方法，其实String也有join方法可以实现这个功能
        String join2 = String.join(",", list);
        System.out.println(join2); // 输出 a,b,c

        // 比较两个对象是否相等
        boolean eqa = Objects.equals("strA", "strB");
        System.out.println(eqa);

        // 两个List集合取交集
        List<String> list1 = new ArrayList<>();
        list1.add("a");
        list1.add("b");
        list1.add("c");
        List<String> list2 = new ArrayList<>();
        list2.add("a");
        list2.add("b");
        list2.add("d");
        list1.retainAll(list2);
        System.out.println(list1); // 输出[a, b]

        // commons-lang3
        // 首字母转成大写
        String str = "yideng";
        String capitalize = StringUtils.capitalize(str);
        System.out.println(capitalize); // 输出Yideng

        // 重复拼接字符串
        String strRepeat = StringUtils.repeat("ab", 2);
        System.out.println(strRepeat); // 输出abab

        // 格式化日期
        // Date类型转String类型
        String date = DateFormatUtils.format(new Date(), "yyyy-MM-dd HH:mm:ss");
        System.out.println(date); // 输出 2021-05-01 01:01:01
        // String类型转Date类型
        Date date2 = DateUtils.parseDate("2021-05-01 01:01:01", "yyyy-MM-dd HH:mm:ss");
        // 计算一个小时后的日期
        Date date3 = DateUtils.addHours(new Date(), 1);

        // 包装临时对象
        // 当一个方法需要返回两个及以上字段时，我们一般会封装成一个临时对象返回，现在有了Pair和Triple就不需要了
        // 返回两个字段
        ImmutablePair<Integer, String> pair = ImmutablePair.of(1, "yideng");
        System.out.println(pair.getLeft() + "," + pair.getRight()); // 输出 1,yideng
        // 返回三个字段
        ImmutableTriple<Integer, String, Date> triple = ImmutableTriple.of(1, "yideng", new Date());
        System.out.println(triple.getLeft() + "," + triple.getMiddle() + "," + triple.getRight()); // 输出 1,yideng,Wed Apr 07 23:30:00 CST 2021


        // commons-collections 集合工具类
        // 集合判空 封装了集合判空的方法，以下是源码：
        // public static boolean isEmpty(final Collection<?> coll) {
        //     return coll == null || coll.isEmpty();
        // }

        // public static boolean isNotEmpty(final Collection<?> coll) {
        //     return !isEmpty(coll);
        // }
        
        
        // 两个集合取交集
        List<String> listA = List.of("1","2","3");
        List<String> listB = List.of("1","2","3","4");
        Collection<String> collection = CollectionUtils.retainAll(listA, listB);
        // 两个集合取并集
        Collection<String> collection2 = CollectionUtils.union(listA, listB);
        // 两个集合取差集
        Collection<String> collection3 = CollectionUtils.subtract(listA, listB);

        // common-beanutils 操作对象
        Student student = new Student();
        BeanUtils.setProperty(student, "id", 1);
        BeanUtils.setProperty(student, "name", "yideng");
        System.out.println(BeanUtils.getProperty(student, "name")); // 输出 yideng
        System.out.println(student); // 输出 {"id":1,"name":"yideng"}

        // 对象转map
        Map<String, String> map = BeanUtils.describe(student);
        System.out.println(map); // 输出 {"id":"1","name":"yideng"}
        // map转对象
        Student newStudent = new Student();
        BeanUtils.populate(newStudent, map);
        System.out.println(newStudent); // 输出 {"id":1,"name":"yideng"}


        // commons-io 文件流处理
        File file = new File("demo1.txt");
        // 读取文件
        List<String> lines = FileUtils.readLines(file, Charset.defaultCharset());
        // 写入文件
        FileUtils.writeLines(new File("demo2.txt"), lines);
        File srcFile = new File("");
        File destFile = new File("");
        // 复制文件
        FileUtils.copyFile(srcFile, destFile);
    }
}