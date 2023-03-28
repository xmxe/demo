package com.xmxe.study_demo.thread.juc;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import com.google.common.util.concurrent.ThreadFactoryBuilder;

/**
 * Future、FutureTask、ExecutorService...
 * https://mp.weixin.qq.com/s/Pb2XF-DiUFMQAXxlJwerwg
 * 
 * 使用场景
 * 任务需要中断，获取返回结果，将原来十个接口的活用一个接口搞定
 * 例：
 * Future<Long> fansCountFT = executor.submit(() -> userService.countFansCountByUserId(userId));
 * Future<Long> msgCountFT = executor.submit(() -> userService.countMsgCountByUserId(userId));
 * Future<Long> collectCountFT = executor.submit(() -> userService.countCollectCountByUserId(userId));
 * Future<Long> followCountFT = executor.submit(() -> userService.countFollowCountByUserId(userId));
 * Future<Long> redBagCountFT = executor.submit(() -> userService.countRedBagCountByUserId(userId));
 * Future<Long> couponCountFT = executor.submit(() -> userService.countCouponCountByUserId(userId));
 * //get阻塞
 * fansCount = fansCountFT.get();
 * msgCount = msgCountFT.get();
 * collectCount = collectCountFT.get();
 * followCount = followCountFT.get();
 * redBagCount = redBagCountFT.get();
 * couponCount = couponCountFT.get();
 */
public class FutureTaskTest {
    /**
     * 核心线程8,最大线程20,保活时间30s,存储队列10,有守护线程.拒绝策略:将超负荷任务回退到调用者
     * 说明:默认使用核心线程(8)数执行任务,任务数量超过核心线程数就丢到队列,队列(10)满了就再开启新的线程,新的线程数最大为20,当任务执行完,新开启的线程将存活30s,若没有任务就消亡,线程池回到核心线程数量。
     */
    private static ExecutorService executor = new ThreadPoolExecutor(8, 20, 30L, TimeUnit.SECONDS,
            new LinkedBlockingQueue<Runnable>(10),
            new ThreadFactoryBuilder().setNameFormat("User_Async_FutureTask-%d").setDaemon(true).build(),
            new ThreadPoolExecutor.CallerRunsPolicy());

    public static void main(String[] args) throws Exception {
        Callable<String> callable = new Callable<String>() {
            @Override
            public String call() throws Exception {
                System.out.println("开始执行线程---------");
                TimeUnit.SECONDS.sleep(2);
                System.out.println("线程执行结束--------");
                return "hello world";
            }
        };

        /**
         * Future就是对于具体的Runnable或者Callable任务的执行结果进行取消、查询是否完成、获取结果。必要时可以通过get方法获取执行结果，该方法会阻塞直到任务返回结果。
         * public interface Future<T> {
         * 取消任务.取消任务成功返回true,取消任务失败返回false
         * @param mayInterruptIfRunning:是否允许取消正在执行却没有执行完毕的任务
         * 如果设置true，则表示可以取消正在执行过程中的任务,如果任务正在执行，若mayInterruptIfRunning为true，则会立即中断执行任务的线程并返回true，
         * 若mayInterruptIfRunning为false，则会返回true且不会中断任务执行线程，如果任务还没有执行，则无论mayInterruptIfRunning为true还是false，返回true,并且任务不会执行,如果任务已经完成或取消，则无论mayInterruptIfRunning为true还是false，返回false
         * boolean cancel(boolean mayInterruptIfRunning);
         *
         * 任务是否被取消成功，如果在任务正常完成前被取消成功，则返回true
         * boolean isCancelled();
         *
         * 任务是否完成
         * boolean isDone();
         *
         * 通过阻塞获取执行结果
         * T get() throws InterruptedException,ExecutionException;
         *
         * 通过阻塞获取执行结果。如果在指定的时间内没有返回，则返回null
         * T get(long timeout, TimeUnit unit) throws InterruptedException,
         * ExecutionException, TimeoutException;
         * }
         */
        // future测试
        Future<String> future = executor.submit(callable);
        // 保证线程里面的任务已经开始执行
        TimeUnit.SECONDS.sleep(2);
        System.out.println("准备尝试中断任务--中断结果为⬇");
        System.out.println(future.cancel(false));
        // get会阻塞直到返回
        String call = future.get();
        System.out.println("返回结果--->" + call);

        TimeUnit.SECONDS.sleep(4);

        /**
         * NEW:初始状态0
         * COMPLETING:任务已经执行完(正常或者异常)完成进行时，准备赋值结果1
         * NORMAL:任务已经正常执行完，并已将任务返回值赋值到结果2
         * EXCEPTIONAL:任务执行失败抛出异常，并将异常赋值到结果3
         * CANCELLED:任务已取消4
         * INTERRUPTING:正在中断执行任务的线程5
         * INTERRUPTED:任务被中断6
         */
        // futuretask测试
        FutureTask<String> task = new FutureTask<String>(callable) {
            // 异步任务执行完成，回调
            @Override
            protected void done() {
                try {
                    System.out.println("回调futuretask.done()");
                    // System.out.println(this.get());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };

        // 普通线程启动(Futuretask实现了Runnable)
        // new Thread(task).start();
        // 线程池启动
        // executor.execute(task);

        // FutureTask直接执行，会阻塞使下面的代码等待执行
        task.run();
        /**
         * run方法重点做了以下几件事：
         * 将runner属性设置成当前正在执行run方法的线程，调用callable成员变量的call方法来执行任务,设置执行结果outcome,如果执行成功,则outcome保存的就是执行结果；
         * 如果执行过程中发生了异常,则outcome中保存的就是异常，设置结果之前，先将state状态设为中间态,对outcome的赋值完成后，设置state状态为终止态(NORMAL或者EXCEPTIONAL),唤醒Treiber栈中所有等待的线程,善后清理(waiters,callable，runner设为null),检查是否有遗漏的中断，如果有，等待中断状态完成
         */
        // 确保任务正在执行
        TimeUnit.SECONDS.sleep(1);

        System.out.println("任务是否结束--->" + task.isDone());

        System.out.println("尝试中断--->" + task.cancel(false));

        System.out.println("task.isCancelled()" + task.isCancelled());
        String calltask = task.get();
        System.out.println("返回结果calltask--->" + calltask);

    }

}

/**
 * calcel(boolean mayInterruptIfRunning)为中断任务，但是为什么设计的时候需要加入mayInterruptIfRunning参数来使正在进行的任务中断或者不中断继续执行呢，为什么不直接设计成一个无参的cancel()方法直接定义成中断正在执行的任务的方法？为什么还要设计一个不中断的方法参数
 * 经过测试cancel(false)得出自己的结论，不一定正确，当使用cancel(false)的时候，Callable里面的方法会正常执行，但是不会返回结果了，而使用cancel(true)的时候，Callable里面的方法就不会正常执行了，更不会返回结果了,当调用get()的时候就会抛出CancellationException异常,而isCancelled()方法只要是调用了cancel(boolean)就会返回true
 * featuretask使用线程池提交的话调用cancel(false),Callable里面的方法同调用cancel(true)时一样不会执行Callable里面的代码,不知什么原因,有可能是线程池里面的线程不允许中断，调用featuretask.run()是调用Callable里面的任务执行完后回调done()方法然后在继续往下执行，所以任务无法设置为中断，isDone()一直返回true（自己的结论未必正确）
 */