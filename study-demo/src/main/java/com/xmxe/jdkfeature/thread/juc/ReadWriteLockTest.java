package com.xmxe.jdkfeature.thread.juc;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * ReadWriteLock读写分离锁,可以大幅提升系统并行度.
 * 读-读 不互斥：读读之间不阻塞。
 * 读-写 互斥：读阻塞写,写也会阻塞读。
 * 写-写 互斥：写写阻塞。
 * 
 * ReadWriteLock的主要特点包括：
 * 分离读写：ReadWriteLock将锁分为读锁（ReadLock）和写锁（WriteLock）,允许多个线程同时获取读锁并进行读操作,但在写锁被获取时,其他线程无法读取或写入共享资源。
 * 读锁共享：多个线程可以同时持有读锁,读锁之间不会互斥。只有当没有线程持有写锁时,读锁才会被获取。
 * 写锁排他：写锁是独占的,一旦有线程获取写锁,其他线程无法获取读锁或写锁,只能等待写锁的释放。
 * 公平性：ReadWriteLock可以是公平的或非公平的。在公平模式下,锁按照线程请求的顺序分配；而在非公平模式下,线程可以与先前请求锁的线程竞争获取锁。
 * 避免写饥饿：ReadWriteLock采用优化策略,可以避免写线程长时间等待。即使有大量的读线程存在,写线程也能够及时获取写锁。
 * 
 * 示例:使用方法与ReentrantLock类似,只是读写锁分离.
 * private static ReentrantReadWriteLock readWriteLock = new ReentrantReadWriteLock();
 * private static Lock readLock = readWriteLock.readLock();
 * private static Lock writeLock = readWriteLock.writeLock();
 */
public class ReadWriteLockTest {
    public static void main(String[] args) {
        ReadWriteLockDemo readWriteLockDemo = new ReadWriteLockDemo();
        for (int i = 0; i < 6; i++) {
            int finalI = i;
            new Thread(new Runnable() {
                @Override
                public void run() {
                    readWriteLockDemo.setNum(finalI);
                }
            }, "写线程" + i).start();
        }
        for (int i = 0; i < 16; i++) {
            int finalI = i;
            new Thread(new Runnable() {
                @Override
                public void run() {
                    readWriteLockDemo.getNum(finalI);
                }
            }, "读线程" + i).start();
        }

        // demo2
        ReadWriteLockDemo2 demo = new ReadWriteLockDemo2();
        // 写操作示例
        new Thread(() -> {
            for (int i = 0; i < 5; i++) {
                demo.addData("Item " + i);
                System.out.println("Added: Item " + i);
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();

        // 读操作示例
        new Thread(() -> {
            while (true) {
                int dataSize = demo.getDataSize();
                for (int i = 0; i < dataSize; i++) {
                    String item = demo.getData(i);
                    System.out.println("Read: " + item);
                }
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    static class ReadWriteLockDemo {
        private volatile Map<String, Object> map = new HashMap<>();
        private ReadWriteLock readWriteLock = new ReentrantReadWriteLock();

        public void getNum(int i) {
            try {
                readWriteLock.readLock().lock();
                System.out.println(Thread.currentThread().getName() + "获取map");
                Object o = map.get(String.valueOf(i));
                System.out.println(Thread.currentThread().getName() + "获取成功" + o);
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

    static class ReadWriteLockDemo2 {
        private ReadWriteLock lock = new ReentrantReadWriteLock();
        private List<String> data = new ArrayList<>();

        public void addData(String item) {
            lock.writeLock().lock(); // 获取写锁
            try {
                data.add(item);
            } finally {
                lock.writeLock().unlock(); // 释放写锁
            }
        }

        public String getData(int index) {
            lock.readLock().lock(); // 获取读锁
            try {
                return data.get(index);
            } finally {
                lock.readLock().unlock(); // 释放读锁
            }
        }

        public int getDataSize() {
            lock.readLock().lock(); // 获取读锁
            try {
                return data.size();
            } finally {
                lock.readLock().unlock(); // 释放读锁
            }
        }

    }
}
