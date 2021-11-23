package com.xmxe.study_demo.thread.juc;

import java.util.concurrent.ConcurrentLinkedQueue;

public class ConcurrentLinkedQueueTest {
    public static void main(String[] args) {
        // offer(E e) 将指定元素插入此队列的尾部。
        // poll() 获取并移除此队列的头，如果此队列为空，则返回 null。
        // peek() 获取但不移除此队列的头；如果此队列为空，则返回 null
        // remove(Object o) 从队列中移除指定元素的单个实例（如果存在）
        ConcurrentLinkedQueue<String> queue = new ConcurrentLinkedQueue<>();
        queue.offer("哈哈哈");
        System.out.println("offer后，队列是否空？" + queue.isEmpty());//offer后，队列是否空？false
        System.out.println("从队列中poll：" + queue.poll());//从队列中poll：哈哈哈
        System.out.println("从队列中peek：" + queue.peek());// 从队列中peek：哈哈哈
        System.out.println("从队列中peek：" + queue.peek());// 从队列中peek：哈哈哈
        System.out.println("pool后，队列是否空？" + queue.isEmpty());//pool后，队列是否空？true
        System.out.println("从队列中remove已存在元素 ：" + queue.remove("哈哈哈"));// 从队列中remove已存在元素 ：true
        System.out.println("从队列中remove不存在元素：" + queue.remove("123"));// 从队列中remove不存在元素：false
        System.out.println("remove后，队列是否空？" + queue.isEmpty());// remove后，队列是否空？true
        


    }
}

/**
 * 一个基于链接节点的无界线程安全队列。此队列按照 FIFO（先进先出）原则对元素进行排序。队列的头部 是队列中时间最长的元素。
 * 队列的尾部 是队列中时间最短的元素。新的元素插入到队列的尾部，队列获取操作从队列头部获得元素。当多个线程共享访问一个
 * 公共 collection 时，ConcurrentLinkedQueue 是一个恰当的选择。此队列不允许使用 null 元素
 * ConcurrentLinkedQueue使用约定：
 * 1:不允许null入列
 * 2:在入队的最后一个元素的next为null
 * 3:队列中所有未删除的节点的item都不能为null且都能从head节点遍历到
 * 4:删除节点是将item设置为null, 队列迭代时跳过item为null节点
 * 5:head节点跟tail不一定指向头节点或尾节点，可能存在滞后性
 *
 * 
 * 
 * boolean add(E e)// 将指定元素插入此队列的尾部。
 * boolean contains(Object o)// 如果此队列包含指定元素，则返回 true。
 * boolean isEmpty()// 如果此队列不包含任何元素，则返回 true。
 * Iterator<E> iterator()// 返回在此队列元素上以恰当顺序进行迭代的迭代器。
 * boolean offer(E e)// 将指定元素插入此队列的尾部。
 * E peek()// 获取但不移除此队列的头；如果此队列为空，则返回 null。
 * E poll()// 获取并移除此队列的头，如果此队列为空，则返回 null。
 * boolean remove(Object o)// 从队列中移除指定元素的单个实例（如果存在）。
 * int size()// 返回此队列中的元素数量。
 * Object[] toArray()// 返回以恰当顺序包含此队列所有元素的数组。
 * <T> T[] toArray(T[] a)// 返回以恰当顺序包含此队列所有元素的数组；返回数组的运行时类型是指定数组的运行时类型。
 * 
 **/