package com.xmxe.study_demo.thread.juc;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * ReadWriteLock 读写分离锁, 可以大幅提升系统并行度. 
 * 读-读 不互斥：读读之间不阻塞。 
 * 读-写 互斥：读阻塞写，写也会阻塞读。
 * 写-写 互斥：写写阻塞。 
 * 示例 使用方法与ReentrantLock类似, 只是读写锁分离. 
 * private static ReentrantReadWriteLock readWriteLock=new ReentrantReadWriteLock(); 
 * private static Lock readLock = readWriteLock.readLock(); 
 * private static Lock writeLock = readWriteLock.writeLock();
 */
public class ReadWriteLockTest {
    public static void main(String[] args) {
        class ReadWriteLockDemo {
            private volatile Map<String, Object> map = new HashMap<>();
            private ReadWriteLock readWriteLock = new ReentrantReadWriteLock();
        
            public void getNum(int i) {
                try {
                    readWriteLock.readLock().lock();
                    System.out.println(Thread.currentThread().getName() + "获取map");
                    Object o = map.get(String.valueOf(i));
                    System.out.println(Thread.currentThread().getName() + "获取成功"+o);
                } finally {
                    readWriteLock.readLock().unlock();
                }
            }
            public void setNum(int num) {
                try {
                    readWriteLock.writeLock().lock();
                    System.out.println(Thread.currentThread().getName() + "写入map:" + num);
                    map.put(String.valueOf(num), num);
                    System.out.println(Thread.currentThread().getName() + "写入成功");
                } finally {
                    readWriteLock.writeLock().unlock();
                }
            }
        }


        ReadWriteLockDemo readWriteLockDemo = new ReadWriteLockDemo();
        for (int i = 0; i < 6; i++) {
            int finalI = i;
            new Thread(new Runnable() {
                @Override
                public void run() {
                    readWriteLockDemo.setNum(finalI);
                }
            }, "写线程"+i).start();
        }
        for (int i = 0; i < 16; i++) {
            int finalI = i;
            new Thread(new Runnable() {
                @Override
                public void run() {
                    readWriteLockDemo.getNum(finalI);
                }
            }, "读线程"+i).start();
        }
    }
}
