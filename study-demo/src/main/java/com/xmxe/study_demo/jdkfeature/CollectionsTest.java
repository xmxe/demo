package com.xmxe.study_demo.jdkfeature;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Queue;
import java.util.Random;
import java.util.Set;

import com.xmxe.study_demo.entity.Student;

public class CollectionsTest {
    public static void main(String[] args) {
        List<Student> studentList = new ArrayList<>();
        studentList.add(new Student(1,"a"));
        studentList.add(new Student(2,"b"));
        List<Integer> listIn1 = new ArrayList<>();
        Collections.addAll(listIn1, 3,4,5);
        List<Integer> listIn2 = new ArrayList<>();
        Collections.addAll(listIn2, 4,6,8);
        List<Integer> listIn3 = new ArrayList<>();
        Collections.addAll(listIn3, 1,1,1);
        // Student要实现Comparable接口
        Collections.sort(studentList);
        Collections.sort(studentList, Comparator.comparing(Student::getAge));
        Collections.sort(studentList, (u1, u2) -> u1.getAge() - u2.getAge());

        // binarySearch()方法可以用于查找指定元素在一个已排序集合中的位置,如果集合中存在多个相同的元素，binarySearch()方法无法保证返回其中任何一个元素的下标。
        int index = Collections.binarySearch(listIn1, 3);

        // reverse()反转集合内的元素
        Collections.reverse(listIn2);

        // 打乱排序 通过Fisher–Yates算法实现的
        Collections.shuffle(listIn2);
        // 使用指定的随机性对列表元素进行随机重新排序
        Collections.shuffle(listIn2,new Random());

        // 交换List集合中任意两个元素的位置 如果要交换的两个位置相同，或者其中至少一个位置超过了List的大小范围，那么swap()方法将不做任何改变，直接返回
        Collections.swap(listIn2, 0, 1);

        // 用于用指定元素填充List中的所有元素。
        Collections.fill(listIn1, 5);

        // 将一个List中的元素复制到另一个List中 要求目标List的大小和源List的大小必须相等，否则将会抛出一个IndexOutOfBoundsException异常。
        Collections.copy(listIn3, listIn1);

        Student min = Collections.min(studentList);
        Student max = Collections.max(studentList);
        Student minbyCompare = Collections.min(studentList,(x,y)->x.getAge()-y.getAge());

        // 用于将List中指定范围内的元素向右旋转k个位置。等价于将List中后k个元素放到List的前面。如果k的值大于List的大小，会对k进行取模操作
        Collections.rotate(listIn2,2);
        // replaceAll()方法会将List中的所有元素都替换为指定的计算结果
        boolean replace = Collections.replaceAll(listIn2, 8, 13);
        // 在一个List中查找指定List的第一次出现的位置
        int i = Collections.indexOfSubList(listIn1, listIn2);
        // 在一个List中查找指定List的最后一次出现的位置
        i = Collections.lastIndexOfSubList(listIn1, listIn2);
        // 创建一个不可修改的集合 unmodifiableSet:不可修改的Set unmodifiableSortedSet:不可修改的SortedSet
        // unmodifiableNavigableSet/unmodifiableNavigableMap:建一个不可修改的NavigableSet集合。NavigableSet是一个继承自SortedSet的接口，它支持高效的区间遍历、查询和操作。它的常用实现类包括TreeSet和ConcurrentSkipListSet，如果需要在多线程环境中使用，可以考虑使用ConcurrentSkipListSet。与SortedSet相比，NavigableSet多了一些基于区间的操作方法
        // unmodifiableList:不可修改的List unmodifiableMap:不可修改的Map unmodifiableSortedMap:不可修改的SortedMap
        Collection<Student> cs = Collections.unmodifiableCollection(studentList);
        // 在多线程环境中提供同步访问的集合对象 synchronizedSet/synchronizedSortedSet/synchronizedNavigableSet/synchronizedList/SynchronizedRandomAccessList/synchronizedMap/synchronizedSortedMap/synchronizedNavigableMap
        Collection<Student> syn = Collections.synchronizedCollection(studentList);
        // 创建一个类型检查的集合对象,我们不指定集合对象的泛型类型，或者将不同类型的元素添加到集合中，可能会产生运行时类型转换异常。为了避免这种情况，可以使用checkedCollection()方法为集合对象提供类型检查功能。
        // checkedQueue/checkedSet/checkedSortedSet/checkedNavigableSet/checkedList/checkedMap/checkedSortedMap/checkedNavigableMap
        Collection<Student> checkedCollection = Collections.checkedCollection(studentList, Student.class);
        // 添加错误类型的元素到集合中会抛出ClassCastException异常
        checkedCollection.add(null);
        // 创建一个空的迭代器对象 emptyListIterator/emptyEnumeration/emptySet/emptySortedSet/emptyNavigableSet/emptyList/emptyMap/emptySortedMap/emptyNavigableMap
        Iterator<Object> ite = Collections.emptyIterator();
        // 创建只有一个元素的Set singletonList/singletonMap
        Set<Object> set = Collections.singleton(null);
        // 创建一个由同一个元素重复多次构成的集合对象。第一个参数表示集合中元素的个数，第二个参数表示集合中所有元素的值。该方法返回一个不可变的集合对象，其中所有元素都是指定的元素
        List<String> strings = Collections.nCopies(5, "");
        // 反转排序
        Collections.reverseOrder();
        Collections.reverse(listIn1);
        // 将某个集合对象转换为Enumeration对象 Collections.list(Enumeration<T> e)
        Enumeration<Student> es = Collections.enumeration(studentList);
        // 用于统计一个集合中某个元素出现的次数
        int f = Collections.frequency(listIn1,5);
        // 判断两个集合对象是否有相同的元素，没有的话返回true 方法接收两个参数：第一个参数是要判断的第一个集合对象，第二个参数是要判断的第二个集合对象。该方法返回一个boolean类型的结果，表示这两个集合是否互不相交，也就是没有相同的元素。
        boolean isDisjoint = Collections.disjoint(listIn1, listIn2);

        // 使用一个Map对象作为参数，并返回一个实现了Set接口的集合。返回的集合是基于指定的映射创建的，因此它具有Map的特性，例如键值对的唯一性，而且它所有的元素都是键，而值则都是固定的true
        Set<String> setFromMap = Collections.newSetFromMap(new HashMap<String, Boolean>());
        // 将一个双端队列（Deque）转换为一个后进先出的队列。
        Queue<Integer> lifoQueue = Collections.asLifoQueue(new ArrayDeque<Integer>());


    }
}
