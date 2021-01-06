package com.xmxe.study_demo.thread;

/**
 * 父线程传递本地变量到子线程的java类
 */
public class InheritableThreadLocalTest {
    public static void main(String[] args) {
        InheritableThreadLocal<String> inheritableThreadLocal = new InheritableThreadLocal<>();
        inheritableThreadLocal.set("主线程变量");
        new Thread(()->{
            System.out.println(inheritableThreadLocal.get());
        }).start();
        
    }
}
