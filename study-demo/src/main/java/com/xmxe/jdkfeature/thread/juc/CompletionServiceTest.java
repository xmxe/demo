package com.xmxe.jdkfeature.thread.juc;

import java.util.concurrent.Callable;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * 接口CompletionService的功能是以异步的方式一边生产新的任务,一边处理已完成任务的结果,将Executor和BlockingQueue功能融合在一起,多个线程执行任务,先执行完的进阻塞队列,然后可以按执行顺序获取结果
 * CompletionService仅有一个实现类ExecutorCompletionService,注意:当只有调用了ExecutorCompletionService的take(),poll()方法时,阻塞队列中的task执行结果才会从队列中移除掉,释放堆内存,如果业务不需要使用任务的返回值,则没进行调用take,poll方法,会导致没有释放堆内存,堆内存会随着调用量的增加一直增长,最终OOM
 */
public class CompletionServiceTest {
    public static void main(String[] args) {
        take();
        poll();
    }

    /**
     * 方法take()取得最先完成任务的Future对象,谁执行时间最短谁最先返回,即方法take()是按任务执行的速度,从快到慢的顺序获得Future对象。
     */
    public static void take(){
        ExecutorService executorService = Executors.newFixedThreadPool(3);
        CompletionService<String> service = new ExecutorCompletionService<>(executorService);
        for(int i = 0; i < 10; i++){
            service.submit(new Callable<String>() {
                @Override
                public String call() throws Exception {
                    long sleepValue = (int) (Math.random() * 1000);
                    System.out.println(Thread.currentThread().getName()+" sleep value: " + sleepValue);
                    Thread.sleep(sleepValue);
                    return "HelloWorld--" + Thread.currentThread().getName();
                }
            });
        }
        
        try {
            for(int i = 0; i < 10; i++){
                // 用于取出最新的线程执行结果,注意这里是阻塞的,与Future.get()相比,Future.get()只能按照顺序一个一个的等待线程执行结果,即便后面的线程已经有了计算结果也需等待
                // 而通过take()拿到的Future是最新的执行结果,原因是先执行完的进阻塞队列,与线程放入顺序无关
                String result = service.take().get();
                System.out.println(result);
            }
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
    }


    /**
     * poll和take的区别在于:take如果获取不到值则会等待,而poll则会返回null。poll还有一个重载方法poll(long timeout, TimeUnit unit)
     */
    public static void poll() {
        // poll方法：获取并移除表示下一个已完成任务的Future,如果不存在这样的任务,则返回null
		ExecutorService executorService = Executors.newCachedThreadPool();
		CompletionService<String> csRef = new ExecutorCompletionService<>(executorService);
		for (int i = 0; i < 1; i++) {
			csRef.submit(new Callable<String>() {
				public String call() throws Exception {
					Thread.sleep(3000);
					System.out.println("3秒过后了");
					return "haige3s";
				}
			});
		}
		for (int i = 0; i < 1; i++) {
            Future<String> future = csRef.poll();
            // dosomething ...
            if (future.isDone()) {
                try {
                    System.out.println(future.get());
                } catch (InterruptedException | ExecutionException e) {
                    e.printStackTrace();
                }
            }
		}
	
    }
}

/**
 * 总结
 * 自己创建一个集合来保存Future存根并循环调用其返回结果的时候,主线程并不能保证首先获得的是最先完成任务的线程返回值。它只是按加入线程池的顺序返回。因为take方法是阻塞方法,后面的任务完成了,前面的任务却没有完成,主程序就那样等待在那儿,只到前面的完成了,它才知道原来后面的也完成了。
 * 使用CompletionService来维护处理线程的返回结果时,主线程总是能够拿到最先完成的任务的返回值,而不管它们加入线程池的顺序。
 * CompletionService的实现是维护了一个保存Future的BlockingQueque。只有当这个Future的任务状态是结束的时候,才会加入到这个Queque中,take()方法其实就是Producer-Consumer中的Consumer。它会从Queue中取出Future对象,如果Queue是空的,就会阻塞在那里,直到有完成的Future对象加入到Queue中。也就是先完成的必定先被取出,这样就减少了不必要的等待时间。
 */