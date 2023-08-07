package com.xmxe.jdkfeature.thread.juc;

import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveAction;
import java.util.concurrent.RecursiveTask;
import java.util.concurrent.TimeUnit;

/**
 * Java7任务并行执行神器：Fork&Join框架
 * https://mp.weixin.qq.com/s?__biz=MzI3ODcxMzQzMw==&mid=2247484997&idx=1&sn=51b297dddbbba40912f71236fb297453&scene=21#wechat_redirect
 * 
 * Fork/Join框架是Java7提供的并行执行任务框架,思想是将大任务分解成小任务,然后小任务又可以继续分解,然后每个小任务分别计算出结果再合并起来,
 * 最后将汇总的结果作为大任务结果。其思想和MapReduce的思想非常类似。对于任务的分割,要求各个子任务之间相互独立,能够并行独立地执行任务,互相之间不影响。
 * 
 * 对于ForkJoinTask,虽然有很多子类,但是我们在基本的使用中都是使用了带返回值的RecursiveTask和不带返回值的RecursiveAction类
 */
public class ForkJoinTest {
    public static void main(String[] args) {
        // 使用ForkJoinPool来执行任务
        ForkJoinPool forkJoinPool = new ForkJoinPool();

        // 生成一个计算资格,负责计算1+2+3+4 带返回值
        CountTaskTmp task = new CountTaskTmp(1, 4);
        // 不带返回值打印1-50
        PrintTask printTask = new PrintTask(1, 50);

        Integer r = forkJoinPool.invoke(task);
        System.out.println(r);

        forkJoinPool.submit(printTask);

        // forkpool执行任务第二种写法 使用submit
        // Future<Integer> result = forkJoinPool.submit(task);
        // try {
        // System.out.println(result.get());
        // } catch (Exception e) {
        // }

        try {
            // 在先前提交的任务（就是run中跑的东西）被执行的时候,开始有序的关闭。新的任务不会被执行。如果已关闭,则调用没有其他效果。该方法不会等待先前已提交的任务完全执行。
            forkJoinPool.awaitTermination(2, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        forkJoinPool.shutdown();
    }

    // 带返回值
    static class CountTaskTmp extends RecursiveTask<Integer> {
        // 设置一个阈值
        private static final int THRESHOLD = 2;
        private int start;
        private int end;

        public CountTaskTmp(int start, int end) {
            this.start = start;
            this.end = end;
        }

        // 实现compute方法来实现任务切分和计算
        @Override
        protected Integer compute() {
            int sum = 0;
            boolean canCompute = (end - start) <= THRESHOLD;
            if (canCompute) {
                for (int i = start; i <= end; i++)
                    sum += i;
            } else {
                // 如果任务大于阀值,就分裂成两个子任务计算
                int mid = (start + end) / 2;
                CountTaskTmp leftTask = new CountTaskTmp(start, mid);
                CountTaskTmp rightTask = new CountTaskTmp(mid + 1, end);

                // 执行子任务
                leftTask.fork();
                rightTask.fork();

                // 等待子任务执行完,并得到结果
                int leftResult = (int) leftTask.join();
                int rightResult = (int) rightTask.join();

                sum = leftResult + rightResult;
            }

            return sum;
        }
    }

    // 不带返回值
    static class PrintTask extends RecursiveAction {

        private static final long serialVersionUID = 1L;

        private static final int THRESHOLD = 9;

        private int start;

        private int end;

        public PrintTask(int start, int end) {
            super();
            this.start = start;
            this.end = end;
        }

        @Override
        protected void compute() {

            if (end - start < THRESHOLD) {
                for (int i = start; i <= end; i++) {
                    System.out.println(Thread.currentThread().getName() + ",i=" + i);
                }
            } else {
                int middle = (start + end) / 2;
                PrintTask firstTask = new PrintTask(start, middle);
                PrintTask secondTask = new PrintTask(middle + 1, end);
                invokeAll(firstTask, secondTask);
            }

        }
    }
}