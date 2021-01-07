package com.xmxe.study_demo.thread;

/**
 * ThreadLocal测试1
 */
public class ThreadLocalTest {
    private static ThreadLocal<Integer> threadLocal = new ThreadLocal<>();

    public static void main(String[] args) {
        new Thread(() -> {
            try {
                for (int i = 0; i < 10; i++) {
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
                for (int i = 0; i < 10; i++) {
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


/**
 * ThreadLocal测试2
 */
class ThreadLocalTest2 {
    private static ThreadLocal<Integer> threadLocal = new ThreadLocal<Integer>() {
        // 实现initialValue()
        public Integer initialValue() {
            return 0;
        }
    };

    public static void main(String[] args) {
        ThreadLocalTest2 threadLocalTest2 = new ThreadLocalTest2();
        new ThreadOne(threadLocalTest2,"线程1").start();
        new ThreadOne(threadLocalTest2,"线程2").start();
        new ThreadOne(threadLocalTest2,"线程3").start();
        new ThreadOne(threadLocalTest2,"线程4").start();
    }

    public int next() {
        threadLocal.set(threadLocal.get() + 1);
        return threadLocal.get();
    }
    public void remove() {
        threadLocal.remove();
    }

    static class ThreadOne extends Thread {
        private ThreadLocalTest2 count;
        private String threadName;

        ThreadOne(ThreadLocalTest2 count,String threadName) {
            this.count = count;
            this.threadName = threadName;
        }
        public void run() {
            for (int i = 0; i < 3; i++) {
                System.out.println(Thread.currentThread().getName() + threadName + " count :" + count.next());
            }
            count.remove();
        }
    }
}



/**
 * ThreadLocal测试3
 */
class ThreadLocalTest3{
    public static final ThreadLocal<Integer> threalLocal = new ThreadLocal<>(){
        @Override
        protected Integer initialValue(){
            return 222;
        }
    };
    public static void main(String[] args) {
        new Thread(()->{
            threalLocal.set(1111);
            Integer v = threalLocal.get();
            System.out.println(Thread.currentThread().getName()+"-------"+v);
        },"线程1").start();

        new Thread(()->{
             Integer v = threalLocal.get();
             System.out.println(Thread.currentThread().getName()+"-------"+v);
        },"线程2").start();
    }
       
}
/**
 * InheritableThreadLocal
 * 父线程传递本地变量到子线程的java类
 */
class InheritableThreadLocalTest {
    public static void main(String[] args) {
        InheritableThreadLocal<String> inheritableThreadLocal = new InheritableThreadLocal<>();
        inheritableThreadLocal.set("主线程变量");
        new Thread(()->{
            System.out.println(inheritableThreadLocal.get());
        }).start();
        
    }
}