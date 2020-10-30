package com.xmxe.study_demo.thread;

/**
 * ThreadLocal测试1
 */
class SeqCount {
    private static ThreadLocal<Integer> seqCount = new ThreadLocal<Integer>() {
        // 实现initialValue()
        public Integer initialValue() {
            return 0;
        }
    };

    public int nextSeq() {
        seqCount.set(seqCount.get() + 1);
        return seqCount.get();
    }

    public void removeSeq() {
        seqCount.remove();
    }

    public static void main(String[] args) {
        SeqCount seqCount = new SeqCount();
        SeqThread thread1 = new SeqThread(seqCount);
        SeqThread thread2 = new SeqThread(seqCount);
        SeqThread thread3 = new SeqThread(seqCount);
        SeqThread thread4 = new SeqThread(seqCount);
        thread1.start();
        thread2.start();
        thread3.start();
        thread4.start();
    }

    private static class SeqThread extends Thread {
        private SeqCount seqCount;

        SeqThread(SeqCount seqCount) {
            this.seqCount = seqCount;
        }

        public void run() {
            for (int i = 0; i < 3; i++) {
                System.out.println(Thread.currentThread().getName() + " seqCount :" + seqCount.nextSeq());
            }
            seqCount.removeSeq();
        }
    }
}

/**
 * ThreadLocal测试2
 */
public class ThreadLocalTest {
    private static ThreadLocal<Integer> threadLocal = new ThreadLocal<>();

    public static void main(String[] args) {

        new Thread(() -> {
            try {
                for (int i = 0; i < 100; i++) {
                    threadLocal.set(i);
                    System.out.println(Thread.currentThread().getName() + "====" + threadLocal.get());
                    try {
                        Thread.sleep(200);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            } finally {
                threadLocal.remove();
            }
        }, "threadLocal1").start();


        new Thread(() -> {
            try {
                for (int i = 0; i < 100; i++) {
                    System.out.println(Thread.currentThread().getName() + "====" + threadLocal.get());
                    try {
                        Thread.sleep(200);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            } finally {
                threadLocal.remove();
            }
        }, "threadLocal2").start();
    }
}

class ThreadLocalRemoveTest{
    public static final ThreadLocal<Integer> threalLocal = new ThreadLocal<>(){
        @Override
        protected Integer initialValue(){
            return 222;
        }
    };
    public static void main(String[] args) {
        new Thread(()->{
            threalLocal.set(1111);
            // 使用完 ThreadLocal 变量后，需要我们手动 remove 掉，防止 ThreadLocalMap 中 Entry 一直保持对 value 的强引用，导致 value 不能被回收导致内存泄漏
            threalLocal.remove();
            Integer v = threalLocal.get();
            System.out.println(Thread.currentThread().getName()+"-------"+v);
        },"线程1").start();

        new Thread(()->{
             Integer v = threalLocal.get();
             System.out.println(Thread.currentThread().getName()+"-------"+v);
        },"线程2").start();
    }
       
}