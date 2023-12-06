package com.xmxe.jdkfeature.thread.juc;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.DelayQueue;
import java.util.concurrent.Delayed;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.TimeUnit;

public class BlockingQueueTest {
    public static void main(String[] args) throws InterruptedException {
        test();
    }

    public static void test() throws InterruptedException {
        // ArrayBlockingQueue的边界大小
        int capacity = 5;

        ArrayBlockingQueue<String> queue = new ArrayBlockingQueue<String>(capacity);

        // 使用put()方法添加元素
        queue.put("StarWars");
        queue.put("SuperMan");
        queue.put("Flash");
        queue.put("BatMan");
        queue.put("Avengers");

        // 打印队列
        System.out.println("queue contains " + queue);

        // 移除一些元素
        queue.remove();
        queue.remove();
        queue.put("CaptainAmerica");
        queue.put("Thor");

        System.out.println("queue contains " + queue);
    }
}

/**
 * 方法类型 抛出异常 特殊值(通常返回true/false) 阻塞    超时
 * 
 * 插入     add(e)         offer(e)         put(e) offer(e,time,unit)
 * 移除     remove()       poll()           take() poll(time,unit)
 * 检查     element()      peek()           无      无
 * 
 * drainTo(Collection c, int maxElements):一次性取走队列中的数据到c中，可以指定取的个数。该方法可以提升获取数据效率，不需要多次分批加锁或释放锁
 */

/**
 * ArrayBlockingQueue是一个基于数组的阻塞队列，初始化的时候必须指定队列大小，源码实现比较简单，采用的是ReentrantLock和Condition实现生产者和消费者模型
 */
class ArrayBlockingQueueTest {
    static class Container {

        /**
         * 初始化阻塞队列
         */
        private final BlockingQueue<Integer> queue = new ArrayBlockingQueue<>(10);

        /**
         * 添加数据到阻塞队列
         * 
         * @param value
         */
        public void add(Integer value) {
            try {
                queue.put(value);
                System.out.println("生产者：" + Thread.currentThread().getName() + "，add：" + value);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        /**
         * 从阻塞队列获取数据
         */
        public void get() {
            try {
                Integer value = queue.take();
                System.out.println("消费者：" + Thread.currentThread().getName() + "，value：" + value);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }

    /**
     * 生产者
     */
    static class Producer extends Thread {

        private Container container;

        public Producer(Container container) {
            this.container = container;
        }

        @Override
        public void run() {
            for (int i = 0; i < 6; i++) {
                container.add(i);
            }
        }
    }

    /**
     * 消费者
     */
    static class Consumer extends Thread {

        private Container container;

        public Consumer(Container container) {
            this.container = container;
        }

        @Override
        public void run() {
            for (int i = 0; i < 6; i++) {
                container.get();
            }
        }
    }

    /**
     * 测试类
     */
    static class MyThreadTest {

        public static void main(String[] args) {
            Container container = new Container();

            Producer producer = new Producer(container);
            Consumer consumer = new Consumer(container);

            producer.start();
            consumer.start();
        }
    }
}

/**
 * LinkedBlockingQueue是一个基于链表的阻塞队列，初始化的时候无须指定队列大小，默认队列长度为Integer.MAX_VALUE，也就是int型最大值。
 * 同样的，采用的是ReentrantLock和Condition实现生产者和消费者模型，不同的是它使用了两个lock，这意味着生产者和消费者可以并行运行，程序执行效率进一步得到提升。
 */
class LinkedBlockingQueueTest {
    // 测试代码同上，将ArrayBlockingQueue换成LinkedBlockingQueue
}

/**
 * SynchronousQueue是一个没有缓冲的队列，生产者产生的数据直接会被消费者获取并且立刻消费，相当于传统的一个请求对应一个应答模式。
 * 相比ArrayBlockingQueue和LinkedBlockingQueue，SynchronousQueue实现机制也不同，它主要采用队列和栈来实现数据的传递，中间不存储任何数据，生产的数据必须得消费者处理，线程阻塞方式采用JDK提供的LockSupport park/unpark函数来完成，也支持公平和非公平两种模式。
 * 当采用公平模式时：使用一个FIFO队列来管理多余的生产者和消费者,当采用非公平模式时：使用一个LIFO栈来管理多余的生产者和消费者，这也是SynchronousQueue默认的模式
 */
class SynchronousQueueTest {
    // 测试代码同上
    static class Container {

        /**
         * 初始化阻塞队列
         */
        private final BlockingQueue<Integer> queue = new SynchronousQueue<>();

        /**
         * 添加数据到阻塞队列
         * 
         * @param value
         */
        public void add(Integer value) {
            try {
                queue.put(value);
                Thread.sleep(100);
                System.out.println("生产者：" + Thread.currentThread().getName() + "，add：" + value);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        /**
         * 从阻塞队列获取数据
         */
        public void get() {
            try {
                Integer value = queue.take();
                Thread.sleep(200);
                System.out.println("消费者：" + Thread.currentThread().getName() + "，value：" + value);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}

/**
 * PriorityBlockingQueue是一个基于优先级别的阻塞队列，底层基于数组实现，可以认为是一个无界队列。
 * PriorityBlockingQueue与ArrayBlockingQueue的实现逻辑，基本相似，也是采用ReentrantLock来实现加锁的操作。
 * 最大不同点在于：
 * 1.PriorityBlockingQueue内部基于数组实现的最小二叉堆算法，可以对队列中的元素进行排序，插入队列的元素需要实现Comparator或者Comparable接口，以便对元素进行排序
 * 2.其次，队列的长度是可扩展的，不需要显式指定长度，上限为Integer.MAX_VALUE-8
 */
class PriorityBlockingQueueTest {
    /**
     * 生产者
     */
    static class Producer extends Thread {

        private SynchronousQueueTest.Container container;

        public Producer(SynchronousQueueTest.Container container) {
            this.container = container;
        }

        @Override
        public void run() {
            container.add(5);
            container.add(3);
            container.add(1);
            container.add(2);
            container.add(0);
            container.add(4);
        }
    }
}

/**
 * DelayQueue是一个线程安全的延迟队列，存入队列的元素不会立刻被消费，只有到了其指定的延迟时间，才能够从队列中出队。
 * 底层采用的是PriorityQueue来存储元素，DelayQueue的特点在于:插入队列中的数据可以按照自定义的delay时间进行排序，快到期的元素会排列在前面，只有delay时间小于0的元素才能够被取出。
 */
class DelayQueueTest {
    static class Container {

        /**
         * 初始化阻塞队列
         */
        private final BlockingQueue<DelayedUser> queue = new DelayQueue<DelayedUser>();

        /**
         * 添加数据到阻塞队列
         * 
         * @param value
         */
        public void add(DelayedUser value) {
            try {
                queue.put(value);
                System.out.println("生产者：" + Thread.currentThread().getName() + "，add：" + value);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        /**
         * 从阻塞队列获取数据
         */
        public void get() {
            try {
                DelayedUser value = queue.take();
                String time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
                System.out.println(time + " 消费者：" + Thread.currentThread().getName() + "，value：" + value);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * DelayQueue队列中的元素需要显式实现Delayed接口，定义一个DelayedUser类
     */
    static class DelayedUser implements Delayed {

        /**
         * 当前时间戳
         */
        private long start;

        /**
         * 延迟时间(单位：毫秒)
         */
        private long delayedTime;

        /**
         * 名称
         */
        private String name;

        public DelayedUser(long delayedTime, String name) {
            this.start = System.currentTimeMillis();
            this.delayedTime = delayedTime;
            this.name = name;
        }

        @Override
        public long getDelay(TimeUnit unit) {
            // 获取当前延迟的时间
            long diffTime = (start + delayedTime) - System.currentTimeMillis();
            return unit.convert(diffTime, TimeUnit.MILLISECONDS);
        }

        @Override
        public int compareTo(Delayed o) {
            // 判断当前对象的延迟时间是否大于目标对象的延迟时间
            return (int) (this.getDelay(TimeUnit.MILLISECONDS) - o.getDelay(TimeUnit.MILLISECONDS));
        }

        @Override
        public String toString() {
            return "DelayedUser{" +
                    "delayedTime=" + delayedTime +
                    ", name='" + name + '\'' +
                    '}';
        }
    }

    /**
     * 生产者
     */
    static class Producer extends Thread {

        private Container container;

        public Producer(Container container) {
            this.container = container;
        }

        @Override
        public void run() {
            for (int i = 0; i < 6; i++) {
                container.add(new DelayedUser(1000 * i, "张三" + i));
            }
        }
    }
}