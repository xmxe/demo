package com.xmxe.jdkfeature.thread.juc;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import com.google.common.util.concurrent.ThreadFactoryBuilder;

/**
 * 通过ExecutorService我们可以提交一个个的task,并且返回Future,然后通过调用Future.get方法来返回任务的执行结果
 * 
 * FutureTask使用场景:任务需要中断,获取返回结果,将原来十个接口的活用一个接口搞定
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
 * 
 * 这种方式虽然有效,但是需要保存每个返回的Future值,而且每个future.get是会阻塞还是比较麻烦的,幸好ExecutorService提供了一个invokeAll的方法,来保存所有的Future值,
 * executor.invokeAll(callableTasks)返回了一个List<Future>
 * 或者使用CompletionService,通过take()方法拿到最新的结果
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

        // future测试
        Future<String> future = executor.submit(callable);
        // 保证线程里面的任务已经开始执行
        TimeUnit.SECONDS.sleep(2);
        System.out.println("准备尝试取消任务--取消结果为⬇");
        /**
         * mayInterruptIfRunning:true-中断任务,false-不中断任务
         * calcel(boolean mayInterruptIfRunning)为中断任务,但是为什么设计的时候需要加入mayInterruptIfRunning参数来使正在进行的任务中断或者不中断继续执行呢,为什么不直接设计成一个无参的cancel()方法直接定义成中断正在执行的任务的方法？为什么还要设计一个不中断的方法参数
         * 经过测试cancel(false)得出自己的结论,不一定正确,当使用cancel(false)的时候,Callable里面的方法会正常执行,但是不会返回结果了,而使用cancel(true)的时候,Callable里面的方法就不会正常执行了,更不会返回结果了,当调用get()的时候就会抛出CancellationException异常,而isCancelled()方法只要是调用了cancel(boolean)就会返回true.featuretask使用线程池提交的话调用cancel(false),Callable里面的方法同调用cancel(true)时一样不会执行Callable里面的代码,不知什么原因,有可能是线程池里面的线程不允许中断,调用featuretask.run()是调用Callable里面的任务执行完后回调done()方法然后在继续往下执行,所以任务无法设置为中断,isDone()一直返回true(自己的结论未必正确)
         */
        System.out.println(future.cancel(false));
        // get会阻塞直到返回
        String call = future.get();
        System.out.println("返回结果--->" + call);

        TimeUnit.SECONDS.sleep(4);

        /**
         * NEW:初始状态0
         * COMPLETING:任务已经执行完(正常或者异常)完成进行时,准备赋值结果1
         * NORMAL:任务已经正常执行完,并已将任务返回值赋值到结果2
         * EXCEPTIONAL:任务执行失败抛出异常,并将异常赋值到结果3
         * CANCELLED:任务已取消4
         * INTERRUPTING:正在中断执行任务的线程5
         * INTERRUPTED:任务被中断6
         */
        
        // futuretask测试
        FutureTask<String> task = new FutureTask<String>(callable) {
            // 异步任务执行完成,回调
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

        // 1.普通线程启动(Futuretask实现了Runnable)
        // new Thread(task).start();
        // 2.线程池启动
        // executor.execute(task);

        // 3.FutureTask.run直接执行,会阻塞使下面的代码等待执行
        // run方法重点做了以下几件事：将runner属性设置成当前正在执行run方法的线程,调用callable成员变量的call方法来执行任务,设置执行结果outcome,如果执行成功,则outcome保存的就是执行结果；
        // 如果执行过程中发生了异常,则outcome中保存的就是异常,设置结果之前,先将state状态设为中间态,对outcome的赋值完成后,设置state状态为终止态(NORMAL或者EXCEPTIONAL),唤醒Treiber栈中所有等待的线程,善后清理(waiters,callable,runner设为null),检查是否有遗漏的中断,如果有,等待中断状态完成
        task.run();
        /*
         * 根据FutureTask.run()方法被执行的时机,FutureTask可以处于下面3种状态
         * 未启动：当创建一个FutureTask,且没有执行FutureTask.run()方法之前,这个FutureTask处于未启动状态。
         * 已启动：FutureTask.run()方法被执行的过程中,FutureTask处于已启动状态。
         * 已完成：FutureTask.run()方法执行完后正常结束,或被取消-FutureTask.cancel(...)或执行FutureTask.run()方法时抛出异常而异常结束,FutureTask处于已完成状态。
         */
        
        // 确保任务正在执行
        TimeUnit.SECONDS.sleep(1);

        System.out.println("任务是否结束--->" + task.isDone());
        System.out.println("尝试中断--->" + task.cancel(false));
        System.out.println("task.isCancelled()" + task.isCancelled());
        /*
         * 当FutureTask处于未启动或已启动状态时,执行FutureTask.get()方法将导致调用线程阻塞；
         * 当FutureTask处于已完成状态时,执行FutureTask.get()方法将导致调用线程立即返回结果或抛出异常。
         * 当FutureTask处于未启动状态时,执行FutureTask.cancel()方法将导致此任务永远不会被执行；
         * 当FutureTask处于已启动状态时,执行FutureTask.cancel(true)方法将以中断执行此任务线程的方式来试图停止任务；
         * 当FutureTask处于已启动状态时,执行FutureTask.cancel(false)方法将不会对正在执行此任务的线程产生影响(让正在执行的任务运行完成)；
         * 当FutureTask处于已完成状态时,执行FutureTask.cancel(…)方法将返回false。
         */
        String calltask = task.get();
        System.out.println("返回结果calltask--->" + calltask);

    }

}

/**
 * Future就是对于具体的Runnable或者Callable任务的执行结果进行取消、查询是否完成、获取结果。必要时可以通过get方法获取执行结果,该方法会阻塞直到任务返回结果。
 * public interface Future<T> {
 * 
 *     取消任务.取消任务成功返回true,取消任务失败返回false
 *     @param mayInterruptIfRunning:是否允许取消正在执行却没有执行完毕的任务,如果设置true,则表示可以取消正在执行过程中的任务,如果任务正在执行,若mayInterruptIfRunning为true,则会立即中断执行任务的线程并返回true,若mayInterruptIfRunning为false,则会返回true且不会中断任务执行线程,如果任务还没有执行,则无论mayInterruptIfRunning为true还是false,返回true,并且任务不会执行,如果任务已经完成或取消,则无论mayInterruptIfRunning为true还是false,返回false
 *     boolean cancel(boolean mayInterruptIfRunning);
 *
 *     任务是否被取消成功,如果在任务正常完成前被取消成功,则返回true
 *     boolean isCancelled();
 *
 *     任务是否完成
 *     boolean isDone();
 *
 *     通过阻塞获取执行结果
 *     T get() throws InterruptedException,ExecutionException;
 *
 *     通过阻塞获取执行结果。如果在指定的时间内没有返回,则返回null
 *     T get(long timeout, TimeUnit unit) throws InterruptedException,ExecutionException, TimeoutException;
 * 
 * }
 */
/**
 * Future和FutureTask都是在Java多线程编程中用于处理异步任务的接口和类,但它们之间有一些区别。
 * 
 * 接口vs类：
 * Future是一个接口,是用于表示一个可能还没有完成的异步任务的结果。
 * FutureTask是一个实现了Future接口的具体类,同时也实现了Runnable接口,可以被提交到线程池中执行。
 * 
 * 实现方式：
 * Future接口只提供了对结果的基本操作(如检查是否完成、获取结果),并没有提供任务的执行逻辑。
 * FutureTask是一个可以将任务逻辑封装起来的类,同时提供了对任务执行的管理和控制,并可以方便地取消任务的执行。
 * 
 * 取消任务：
 * Future接口提供了cancel()方法来尝试取消任务的执行,但它并不保证一定能取消任务的执行。
 * FutureTask类通过继承Future接口,并且实现了取消任务的逻辑,提供了更灵活的控制任务的取消。
 * 
 * 使用方式：
 * Future接口主要用于表示执行结果,并通过get()方法获取结果。通常配合使用ExecutorService来提交异步任务获取Future对象。
 * FutureTask类既可以表示任务的执行结果,也可以作为Runnable对象提交给线程池执行。可以通过get()方法获取任务执行结果,并提供了更多对任务的管理方法。
 * 
 * 总结来说,Future接口是用于表示异步任务结果的接口,而FutureTask是一个具体类,实现了Future接口,并提供了更多的任务管理和控制的功能。在实际使用中,Future接口适用于简单的任务结果获取,而FutureTask适用于更加复杂的任务管理和控制场景。
 */