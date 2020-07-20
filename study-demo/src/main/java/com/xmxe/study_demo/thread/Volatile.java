package com.xmxe.study_demo.thread;


public class Volatile {
    boolean stop = false;
	// volatile boolean stop = false;
    /**
     * 不加volatile关键字运行main方法会进入死循环 因为线程1是从栈内存中获取stop变量 
     * 加上volatile关键字则从主内存中获取stop变量
     * 
     */
    
	public static void main(String[] args) throws Exception{
		Volatile v = new Volatile();
		Thread ta = new Thread(()->v.execute(),"线程1");
		ta.start();
		// Thread.sleep(2000);
		Thread tb = new Thread(()->v.shutdown(),"线程2");
		tb.start();
	}
	
	public void execute(){
		while(!stop){
			String a = "a";
            //System.out.println(a); 
            /**
             * System.out.println()会影响volatile可见性,
             * 因为println()使用synchronized修饰的
             * JMM关于synchronized的两条规定:
             * 1）线程解锁前，必须把共享变量的最新值刷新到主内存中
             * 2）线程加锁时，将清空工作内存中共享变量的值，从而使用共享变量时需要从主内存中重新获取最新的值
             */
		}
	}
	public void shutdown(){
		System.out.println("do stop");
		stop = true;
	}
}