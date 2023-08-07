package com.xmxe.jdkfeature.thread.juc;

import java.util.concurrent.ArrayBlockingQueue;

public class BlockingQueueTest {
    public static void main(String[] args) throws InterruptedException {
        test();
    }

    public static void test() throws InterruptedException{
         // ArrayBlockingQueue的边界大小
        int capacity = 5; 
  
        ArrayBlockingQueue<String>  queue = new ArrayBlockingQueue<String>(capacity); 
  
        // 使用put()方法添加元素 
        queue.put("StarWars"); 
        queue.put("SuperMan"); 
        queue.put("Flash"); 
        queue.put("BatMan"); 
        queue.put("Avengers"); 
  
        // 打印队列
        System.out.println("queue contains "+ queue); 
  
        // 移除一些元素
        queue.remove(); 
        queue.remove(); 
		queue.put("CaptainAmerica"); 
        queue.put("Thor"); 
  
        System.out.println("queue contains " + queue); 
    }
}

/**
 * 方法类型    抛出异常    特殊值(通常返回true/false)    阻塞     超时
 * 插入        add(e)	 offer(e)                    put(e)  offer(e,time,unit)
 * 移除        remove()	 poll()	                     take()  poll(time,unit)
 * 检查        element() peek()	                     无	     无
 */