package com.xmxe.study_demo.thread.juc;

import java.util.concurrent.DelayQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.Delayed;

// 生成订单30分钟未支付，则自动取消，该怎么实现? https://mp.weixin.qq.com/s/ijv_4_qWTrG-kA9jSbz1iw
// 一口气说出6种实现延时消息的方案 https://mp.weixin.qq.com/s/VcbZTsoD5-ioc4x7s6mj7Q
// 面试官：怎么不用定时任务实现关闭订单？ https://mp.weixin.qq.com/s/Oc188nkq4-s9ivt7Ki_M1A
// 再有人问你如何实现订单到期关闭，就把这篇文章发给他 https://mp.weixin.qq.com/s/BG1PqUWX0XwJX6aMCXCgvw
/**
 * DelayQueue是一个无界的BlockingQueue，用于放置实现了Delayed接口的对象，其中的对象只能在其到期时才能从队列中取走。
 * 这种队列是有序的，即队头对象的延迟到期时间最长。注意：不能将null元素放置到这种队列中。
 *
 * Poll():获取并移除队列的超时元素，没有则返回空
 * take():获取并移除队列的超时元素，如果没有则wait当前线程，直到有元素满足超时条件，返回结果。
 */
public class DelayQueueTest {
    public static void main(String[] args) {
        // 模拟5个订单 超时时间为5s 超过5s如果订单没有支付则判定为超时订单
        OrderDelay oider1 = new OrderDelay("000001",TimeUnit.MILLISECONDS.convert(20,TimeUnit.SECONDS));
        OrderDelay oider2 = new OrderDelay("000002",TimeUnit.MILLISECONDS.convert(17,TimeUnit.SECONDS));
        OrderDelay oider3 = new OrderDelay("000003",TimeUnit.MILLISECONDS.convert(32,TimeUnit.SECONDS));
        OrderDelay oider4 = new OrderDelay("000004",TimeUnit.MILLISECONDS.convert(31,TimeUnit.SECONDS));
        OrderDelay oider5 = new OrderDelay("000005",TimeUnit.MILLISECONDS.convert(28,TimeUnit.SECONDS));
    
        DelayQueue<OrderDelay> queue = new DelayQueue<OrderDelay>();  

        queue.put(oider1);
        queue.put(oider2);
        queue.put(oider3);
        queue.put(oider4);
        queue.put(oider5);
        // 模拟订单支付
        oider1.setType(1);oider4.setType(1);
        long start = System.currentTimeMillis();
        while(true){
            try {
                OrderDelay o = queue.take();
                System.out.println("订单 " +o+"的超时时间到了~~~"+(System.currentTimeMillis()-start) + " MilliSeconds");
                // 根据订单状态执行操作
                if(o.getType() == 0){

                }else{
                    // System.out.println("订单有效的是"+o.getOrderId());
                }
                if(queue.isEmpty()) break;
            } catch (InterruptedException e) {
                e.printStackTrace();  
            }  
        }
        
    }  
// 3600分钟转换成小时是多少
// System.out.println(TimeUnit.HOURS.convert(3600, TimeUnit.MINUTES));
// 3600分钟转换成天是多少
// System.out.println(TimeUnit.DAYS.convert(3600, TimeUnit.MINUTES));
}

 
class OrderDelay implements Delayed {
    // 订单id
    private String orderId;
    // 超时时间(纳秒)
    private long timeout;
    // 订单状态 超时取出对象后判定订单是否下单，否则将超时订单删除 type=1订单有效 type=0订单无效
    private int type;
 
    OrderDelay(String orderId, long timeout) {
        this.orderId = orderId;
        this.timeout = timeout + System.currentTimeMillis();
    }
 
    // compareTo 方法必须提供与 getDelay 方法一致的排序
    // 当生产者线程调用put之类的方法加入元素时，会触发Delayed接口中的compareTo方法进行排序，
    // 也就是说队列中元素的顺序是按到期时间排序的，而非它们进入队列的顺序。排在队列头部的元素是最早到期的，越往后到期时间赿晚。
    // 添加第一个元素不会触发排序
    public int compareTo(Delayed other) {
        if (other == this)  return 0;
        OrderDelay t = (OrderDelay) other;
        System.out.println("调用compareTo！比较的对象是"+this.getOrderId()+"和"+t.getOrderId());
        // 排序 排序的目的是将快要超时的对象放到队头;    
        long d = (this.getDelay(TimeUnit.MILLISECONDS) - t.getDelay(TimeUnit.MILLISECONDS));
        return (d == 0) ? 0 : ((d < 0) ? -1 : 1);
    }
 

    // 返回距离你自定义的超时时间还有多少
    // 如果此方法返回的值小０或者等于０，则消费者线程会从队列中取出此元素，并进行处理
    @Override
    public long getDelay(TimeUnit unit) {
        long delay = unit.convert(timeout - System.currentTimeMillis(),TimeUnit.MILLISECONDS);
        System.out.println("调用getDelay,订单:"+getOrderId()+"超时时间="+delay);
        return delay;
    }
 
    @Override
    public String toString() {
        String str = String.format("对象~~~orderId:%s，type=%d,timeout=%d", orderId,type,timeout);
        return str;
    }

    public String getOrderId(){
        return orderId;
    }

    public void setOrderId(String orderId){
        this.orderId = orderId;
    }

    public int getType(){
        return type;
    }

    public void setType(int type){
        this.type = type;
    }

    
}

/**
 * boolean add(E e) 将指定的元素插入到此延迟队列中。如果可以立即执行此操作而不违反容量限制， 则在成功后返回true，如果当前没有可用空间，则抛出IllegalStateException。
 * boolean addAll(Collection<? extends E> c) 将指定集合中的所有元素添加到此队列中。
 * void	clear() 从此延迟队列中原子地删除所有元素。
 * int drainTo(Collection<? super E> c) 从该队列中删除所有可用的元素，并将它们添加到给定的集合中。
 * int drainTo(Collection<? super E> c, int maxElements) 最多从该队列中删除给定数量的可用元素，并将它们添加到给定的集合中。
 * Iterator<E> iterator() 返回此队列中所有元素（已过期和未过期）的迭代器。
 * boolean offer(E e) 将指定的元素插入到此队列中，如果可以立即执行此操作，而不会违反容量限制， true在成功时 false如果当前没有可用空间，则返回false。
 * boolean	offer(E e, long timeout, TimeUnit unit) 将指定的元素插入到此队列中，等待指定的等待时间（如有必要）才能使空间变得可用。
 * E peek() 检索但不删除此队列的头，如果此队列为空，则返回null 。
 * E poll() 检索并删除此队列的头，或者如果此队列没有已过期延迟的元素，则返回null 。
 * E poll(long timeout, TimeUnit unit) 检索并删除此队列的头部，如果需要，等待具有到期延迟的元素可用于此队列，或指定的等待时间到期。
 * void put(E e) 将指定的元素插入到此延迟队列中。
 * int remainingCapacity() 返回该队列最好可以（在没有存储器或资源约束）接受而不会阻塞，或附加的元素的数量Integer.MAX_VALUE如果没有固有的限制。总是返回Integer.MAX_VALUE ，因为DelayQueue没有容量限制。
 * boolean remove(Object o) 从该队列中删除指定元素的单个实例（如果存在），无论其是否已过期。
 * boolean removeAll(Collection<?> c) 删除指定集合中包含的所有此集合的元素（可选操作）。
 * int size() 返回此集合中的元素数。
 * E take() 检索并删除此队列的头部，如果需要，等待有一个延迟到期的元素在此队列上可用。
 * Object[] toArray() 返回一个包含此队列中所有元素的数组。
 * <T> T[] toArray(T[] a) 返回一个包含此队列中所有元素的数组; 返回的数组的运行时类型是指定数组的运行时类型。
 * E element() 检索，但不删除，这个队列的头。
 * boolean contains(Object o) 如果此集合包含指定的元素，则返回true 。
 * boolean containsAll(Collection<?> c) 如果此集合包含指定 集合中的所有元素，则返回true。
 * boolean isEmpty() 如果此集合不包含元素，则返回true 。
 * boolean retainAll(Collection<?> c) 仅保留此集合中包含在指定集合中的元素（可选操作）。
 * String toString() 返回此集合的字符串表示形式。
 * 
 */