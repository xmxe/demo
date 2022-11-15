package com.xmxe.study_demo.thread.juc;

import java.util.concurrent.atomic.AtomicMarkableReference;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.atomic.AtomicStampedReference;

/**
 * 保证原子性
 * 使用自旋+cas无锁保证线程安全
 */
public class AtomicReferenceTest {
    public static void main(String[] args) {
        
        /**
         * AtomicReference:通过volatile和Unsafe提供的CAS函数实现原子操作。 自旋+CAS的无锁操作保证共享变量的线程安全
         * value是volatile类型，这保证了当某线程修改value的值时，其他线程看到的value的值都是最新的值，即修改之后的volatile的值
         * 但是CAS操作可能存在ABA问题(A值被B值替换后又被A值替换)。AtomicStampedReference的出现就是为了解决这问题
         */
        AtomicReference<String> atomicReference = new AtomicReference<String>("initialValue");
        atomicReference.set("newValue");
        atomicReference.get();
        atomicReference.compareAndSet("expectedValue", "newValue");


        /**
         * AtomicStampedReference
         * 构造方法中initialStamp（时间戳）用来唯一标识引用变量，在构造器内部，实例化了一个Pair对象，Pair对象记录了对象引用
         * 和时间戳信息，采用int作为时间戳，实际使用的时候，要保证时间戳唯一（一般做成自增的），如果时间戳如果重复，还会出现ABA的问题。
         * AtomicStampedReference中的每一个引用变量都带上了pair.stamp这个时间戳，这样就可以解决CAS中的ABA的问题。
         * 
         * 构造函数,传入引用和戳
         * public AtomicStampedReference(V initialRef, int initialStamp) {
         *   pair = Pair.of(initialRef, initialStamp);
         * }
         * 
         * attemptStamp()：如果expectReference和目前值一致，设置当前对象的版本号戳为newStamp
         * compareAndSet(V expectedReference,V newReference,int expectedStamp,int newStamp)：expectedReference指的是变量预期的旧值， newReference指的是变量需要更改成的新值， expectedStamp指的是版本号的旧值， newStamp指的是版本号新值。
         * get() ：该方法返回当前对象值和当前对象的版本号戳
         * getReference() ：该方法返回当前对象值
         * getStamp() ：该方法返回当前对象的版本号戳
         * set() ：直接设置当前对象值和对象的版本号戳
         */
        AtomicStampedReference<String> atomicStampedReference = new AtomicStampedReference<>("initialValue",1);
        atomicStampedReference.attemptStamp("expectedReference", 2);
        atomicStampedReference.compareAndSet("expectedReference", "newReference", 2, 3);
 

        /**
         * AtomicMarkableReference
         * AtomicStampedReference可以知道，引用变量中途被更改了几次。有时候，我们并不关心引用变量更改了几次，只是单纯的关心
         * 是否更改过，所以就有了AtomicMarkableReference。AtomicMarkableReference的唯一区别就是不再用int标识引用，而是使用boolean变量——表示引用变量是否被更改过。
         * 
         * 构造函数
         * public AtomicMarkableReference(V initialRef, boolean initialMark) {
         *   pair = Pair.of(initialRef, initialMark);
         * }
         * 
         * 通过AtomicStampedReference、AtomicMarkableReference构造函数我们知道，AtomicStampedReference记录中间变化过的
         * 次数，而AtomicMarkableReference只是知道中间是否变化过，具体次数就不知道了，但都能解决ABA的问题
         * 
         */
        AtomicMarkableReference<String> atomicMarkableReference = new AtomicMarkableReference<String>("initialRef", true);
        atomicMarkableReference.attemptMark("expectedReference", false);
    }
}