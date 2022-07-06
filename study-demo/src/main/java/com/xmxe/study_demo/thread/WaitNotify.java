package com.xmxe.study_demo.thread;

/**
 * wait 和 notify 有坑(https://mp.weixin.qq.com/s/KCu3iEG1xbqQVw1GviXOjA)
 * 
 * 超强图文｜并发编程【等待/通知机制】就是这个feel～
 * https://mp.weixin.qq.com/s?__biz=Mzg2MDYzODI5Nw==&mid=2247493990&idx=2&sn=9e2479dde357b4041978571610df696e&source=41#wechat_redirect
 * 
 */
public class WaitNotify {
    public static void main(String[] args) {
        WaitNotify w = new WaitNotify();

        Thread t1 = new Thread(() -> {
            synchronized (w) {
                for (int i = 0; i < 25; i++) {
                    System.out.println(i);
                    try {
                        w.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    w.notify();
                }
            }

        }, "线程1");
        t1.start();

        Thread t2 = new Thread(() -> {
            synchronized (w) {
                for (char a = 'a'; a < 'z'; a++) {
                    System.out.println(a);
                    w.notify();
                    try {
                        w.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }

        },"线程2");
        t2.start();
    }

}