package com.xmxe.jdkfeature.thread.juc;

import java.util.concurrent.locks.StampedLock;

/**
 * StampedLock的主要特点如下：
 * 三种控制模式：StampedLock支持三种不同的控制模式,包括写锁、悲观读锁和乐观读锁。写锁用于独占写入,悲观读锁与传统的读锁相似,用于独占读取,而乐观读锁则提供了一种乐观的读取方式。
 * 乐观读锁：StampedLock引入了乐观读锁的概念。乐观读锁不会阻塞写锁或悲观读锁,当获取乐观读锁时,允许其他线程获取写锁或悲观读锁。乐观读锁无需加锁,仅作为一个戳记（stamp）返回给调用者。在读取数据后,需要通过检查戳记是否仍然有效来验证数据是否被修改。
 * 支持条件等待：StampedLock还支持使用Condition对象进行条件等待,类似于ReentrantLock。可以使用await()、signal()、signalAll()等方法来进行线程间的协调与通信。
 * 无法重入：与ReentrantLock不同,StampedLock不支持重入。同一个线程获取了一次锁之后,再次获取相同类型的锁会导致线程死锁。
 * 高性能：StampedLock的实现被设计为高度优化,尤其在低竞争和读写频率相差较大的情况下,性能较高。
 *
 * ReadWriteLock有个潜在的问题：如果有线程正在读,写线程需要等待读线程释放锁后才能获取写锁,即读的过程中不允许写,这是一种悲观的读锁。要进一步提升并发执行效率,Java8引入了新的读写锁：StampedLock。
 * StampedLock和ReadWriteLock相比,改进之处在于：读的过程中也允许获取写锁后写入！这样一来,我们读的数据就可能不一致,所以,需要一点额外的代码来判断读的过程中是否有写入,这种读锁是一种乐观锁。
 * 乐观锁的意思就是乐观地估计读的过程中大概率不会有写入,因此被称为乐观锁。反过来,悲观锁则是读的过程中拒绝有写入,也就是写入必须等待。显然乐观锁的并发效率更高,但一旦有小概率的写入导致读取的数据不一致,需要能检测出来,再读一遍就行。
 */
public class StampedLockTest {
    private final StampedLock stampedLock = new StampedLock();

    private double x;
    private double y;

    public void move(double deltaX, double deltaY) {
        long stamp = stampedLock.writeLock(); // 获取写锁
        try {
            x += deltaX;
            y += deltaY;
        } finally {
            stampedLock.unlockWrite(stamp); // 释放写锁
        }
    }

    public double distanceFromOrigin() {
        long stamp = stampedLock.tryOptimisticRead(); // 获得一个乐观读锁
        // 注意下面两行代码不是原子操作
        // 假设x,y = (100,200)
        double currentX = x;
        // 此处已读取到x=100,但x,y可能被写线程修改为(300,400)
        double currentY = y;
        // 此处已读取到y,如果没有写入,读取是正确的(100,200)
        // 如果有写入,读取是错误的(100,400)
        if (!stampedLock.validate(stamp)) { // 检查乐观读锁后是否有其他写锁发生
            stamp = stampedLock.readLock(); // 获取一个悲观读锁
            try {
                currentX = x;
                currentY = y;
            } finally {
                stampedLock.unlockRead(stamp); // 释放悲观读锁
            }
        }
        return Math.sqrt(currentX * currentX + currentY * currentY);
    }

    public static void main(String[] args) {
        StampedLockTest demo = new StampedLockTest();

        // 写线程
        new Thread(() -> {
            for (int i = 0; i < 10; i++) {
                demo.move(1, 1);
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();

        // 读线程
        new Thread(() -> {
            for (int i = 0; i < 10; i++) {
                double distance = demo.distanceFromOrigin();
                System.out.println("Distance: " + distance);
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    
    }
}
/**
 * 和ReadWriteLock相比,写入的加锁是完全一样的,不同的是读取。注意到首先我们通过tryOptimisticRead()获取一个乐观读锁,并返回版本号。接着进行读取,读取完成后,我们通过validate()去验证版本号,如果在读取过程中没有写入,版本号不变,验证成功,我们就可以放心地继续后续操作。
 * 如果在读取过程中有写入,版本号会发生变化,验证将失败。在失败的时候,我们再通过获取悲观读锁再次读取。由于写入的概率不高,程序在绝大部分情况下可以通过乐观读锁获取数据,极少数情况下使用悲观读锁获取数据。
 * 可见,StampedLock把读锁细分为乐观读和悲观读,能进一步提升并发效率。但这也是有代价的：一是代码更加复杂,二是StampedLock是不可重入锁,不能在一个线程中反复获取同一个锁。
 * StampedLock还提供了更复杂的将悲观读锁升级为写锁的功能,它主要使用在if-then-update的场景：即先读,如果读的数据满足条件,就返回,如果读的数据不满足条件,再尝试写。
 */