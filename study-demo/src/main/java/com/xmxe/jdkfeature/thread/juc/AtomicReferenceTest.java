package com.xmxe.jdkfeature.thread.juc;

import java.util.concurrent.atomic.AtomicMarkableReference;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.atomic.AtomicStampedReference;

/**
 * 保证原子性,使用自旋+cas无锁保证线程安全
 * 
 * 通过AtomicStampedReference、AtomicMarkableReference构造函数我们知道,AtomicStampedReference记录中间变化过的次数,而AtomicMarkableReference只是知道中间是否变化过,具体次数就不知道了,但都能解决ABA的问题
 */
public class AtomicReferenceTest {
    public static void main(String[] args) {

        /**
         * AtomicReference:通过volatile和Unsafe提供的CAS函数实现原子操作。自旋+CAS的无锁操作保证共享变量的线程安全。
         * value是volatile类型,这保证了当某线程修改value的值时,其他线程看到的value的值都是最新的值,即修改之后的volatile的值
         * 但是CAS操作可能存在ABA问题(A值被B值替换后又被A值替换)。AtomicStampedReference的出现就是为了解决这问题
         *
         * 常用方法
         * 原子性地更新AtomicReference内部的value值,其中expect代表当前AtomicReference的value值,update则是需要设置的新引用值。该方法会返回一个boolean的结果,当expect和AtomicReference的当前值不相等时,修改会失败,返回值为false,若修改成功则会返回true。
         * compareAndSet(V expect, V update)
         * 
         * 原子性地更新AtomicReference内部的value值,并且返回AtomicReference的旧值。
         * getAndSet(V newValue)
         * 
         * 原子性地更新value值,并且返回AtomicReference的旧值,该方法需要传入一个Function接口。
         * getAndUpdate(UnaryOperator<V> updateFunction)
         * 
         * 原子性地更新value值,并且返回AtomicReference更新后的新值,该方法需要传入一个Function接口。
         * updateAndGet(UnaryOperator<V> updateFunction)
         * 
         * 原子性地更新value值,并且返回AtomicReference更新前的旧值。该方法需要传入两个参数,第一个是更新后的新值,第二个是BinaryOperator接口。
         * getAndAccumulate(V x, BinaryOperator<V> accumulatorFunction)
         * 
         * 原子性地更新value值,并且返回AtomicReference更新后的值。该方法需要传入两个参数,第一个是更新的新值,第二个是BinaryOperator接口。
         * accumulateAndGet(V x, BinaryOperator<V> accumulatorFunction)
         * 
         * 获取AtomicReference的当前对象引用值。
         * get()
         * 
         * 设置AtomicReference最新的对象引用值,该新值的更新对其他线程立即可见。
         * set(V newValue)
         * 
         * 设置AtomicReference的对象引用值。
         * lazySet(V newValue)
         * 
         */
        AtomicReference<String> atomicReference = new AtomicReference<String>("initialValue");
        atomicReference.set("newValue");
        atomicReference.get();
        atomicReference.compareAndSet("expectedValue", "newValue");

        /**
         * AtomicStampedReference
         * 构造方法中initialStamp（时间戳）用来唯一标识引用变量,在构造器内部,实例化了一个Pair对象,Pair对象记录了对象引用和时间戳信息,采用int作为时间戳,实际使用的时候,要保证时间戳唯一（一般做成自增的）,如果时间戳如果重复,还会出现ABA的问题。
         * AtomicStampedReference中的每一个引用变量都带上了pair.stamp这个时间戳,这样就可以解决CAS中的ABA的问题。
         * 
         * 构造函数,传入引用和戳
         * public AtomicStampedReference(V initialRef, int initialStamp) {
         *     pair = Pair.of(initialRef, initialStamp);
         * }
         * 
         * attemptStamp():如果expectReference和目前值一致,设置当前对象的版本号戳为newStamp
         * compareAndSet(V expectedReference,V newReference,int expectedStamp,int newStamp):expectedReference指的是变量预期的旧值,newReference指的是变量需要更改成的新值,expectedStamp指的是版本号的旧值,newStamp指的是版本号新值。
         * get():该方法返回当前对象值和当前对象的版本号戳
         * getReference():该方法返回当前对象值
         * getStamp():该方法返回当前对象的版本号戳
         * set():直接设置当前对象值和对象的版本号戳
         */
        AtomicStampedReference<String> atomicStampedReference = new AtomicStampedReference<>("initialValue", 1);
        atomicStampedReference.attemptStamp("expectedReference", 2);
        atomicStampedReference.compareAndSet("expectedReference", "newReference", 2, 3);

        /**
         * AtomicMarkableReference
         * AtomicStampedReference可以知道引用变量中途被更改了几次。有时候,我们并不关心引用变量更改了几次,只是单纯的关心是否更改过,所以就有了AtomicMarkableReference。
         * AtomicMarkableReference的唯一区别就是不再用int标识引用,而是使用boolean变量——表示引用变量是否被更改过。
         * 
         * 构造函数
         * public AtomicMarkableReference(V initialRef, boolean initialMark) {
         *     pair = Pair.of(initialRef, initialMark);
         * }
         * 
         */
        AtomicMarkableReference<String> atomicMarkableReference = new AtomicMarkableReference<String>("initialRef",
                true);
        atomicMarkableReference.attemptMark("expectedReference", false);
    }
}