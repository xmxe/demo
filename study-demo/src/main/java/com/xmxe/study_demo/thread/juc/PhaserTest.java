package com.xmxe.study_demo.thread.juc;

import java.util.Random;
import java.util.concurrent.Phaser;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

/**
 * Phaser表示“阶段器”，用来解决控制多个线程分阶段共同完成任务的情景问题。其作用相比CountDownLatch和CyclicBarrier更加灵活
 * Phaser的主要特点包括：
 * 阶段同步：Phaser将线程的同步操作划分为多个阶段（phase），线程可以分别参与每个阶段，只有在当前阶段的所有线程都完成后，才能继续执行到下一个阶段。
 * 动态注册：Phaser允许动态地注册和注销参与者（线程），这意味着线程可以根据实际需求随时加入或退出阶段同步。
 * 灵活的阶段控制：Phaser提供了一些方法（如arrive()、awaitAdvance()等）来控制线程的执行状态和进度，可以根据需要灵活地控制阶段的进程以及线程的同步行为。
 * 支持有界和无界阶段：Phaser可以基于计数器（counter）或者自定义的进入条件（advance条件）来定义阶段的结束条件，可以实现有界或无界的阶段同步。
 * 灵活的错误处理：Phaser可以捕获到运行时的错误，并相应地中断正在等待的线程。
 * 
 * 常用方法:
 * int register():动态添加一个parties
 * int bulkRegister(int parties):动态添加多个parties parties:需要添加的个数
 * int getRegisteredParties():获取当前注册的parties数
 * int arriveAndAwaitAdvance():到达并等待其他线程到达,会阻塞
 * int arriveAndDeregister():到达并注销该parties，这个方法不会使线程阻塞
 * int arrive():到达，但不会使线程阻塞
 * int awaitAdvance(int phase):等待前行，可阻塞也可不阻塞，判断条件为传入的phase是否为当前phaser的phase。如果相等则阻塞，反之不进行阻塞,phase:阶段数值
 * int awaitAdvanceInterruptibly(int phase):该方法与awaitAdvance类似，唯一不一样的就是它可以进行打断。phase:阶段数值
 * int awaitAdvanceInterruptibly(int phase, long timeout, TimeUnit unit):phase:阶段数值,timeout:超时时间,unit:时间单位
 * int getArrivedParties():获取当前到达的parties数
 * int getUnarrivedParties():获取当前未到达的parties数
 * int getPhase():获取当前属于第几阶段，默认从0开始，最大为integer的最大值
 * boolean isTerminated():判断当前phaser是否关闭
 * void forceTermination():强制关闭当前phaser
 * 
 */
public class PhaserTest {

    public static void main(String[] args) {
        // 学生考试，都做完第1题才能开始做第2题,依次类推
        demo1();
        // 使用Phaser动态注册parties
        demo2();
        // 使用Phaser设置多个阶段
        demo3();
        // 常用方法演示
        demo4();
        // 利用arrive只监听线程完成第一部分任务
        demo5();
        // awaitAdvance演示
        demo6();
        // demo
        Phaser phaser = new Phaser(3); // 创建一个有3个参与者的Phaser
        for (int i = 0; i < 3; i++) {
            new Thread(() -> {
                System.out.println(Thread.currentThread().getName() + " 开始任务...");
                phaser.arriveAndAwaitAdvance(); // 等待其他线程全部到达
                System.out.println(Thread.currentThread().getName() + " 继续执行任务...");
                // 执行任务...
                phaser.arriveAndDeregister(); // 注销当前线程
            }).start();
        }
    }

    /**
     * 有这样的一个题目:5个学生一起参加考试，一共有三道题，要求所有学生到齐才能开始考试，全部同学都做完第一题，学生才能继续做第二题，全部学生做完了第二题，才能做第三题，所有学生都做完的第三题，考试才结束。
     * 分析这个题目:这是一个多线程（5个学生）分阶段问题（考试考试、第一题做完、第二题做完、第三题做完），所以很适合用Phaser解决这个问题。
     */
    public static void demo1() {

        /**
         * 下面说说Phaser的高级用法，在Phaser内有2个重要状态，分别是phase和party。
         * phase就是阶段，初值为0，当所有的线程执行完本轮任务，同时开始下一轮任务时，意味着当前阶段已结束，进入到下一阶段，phase的值自动加1。
         * party就是线程，party=4就意味着Phaser对象当前管理着4个线程。Phaser还有一个重要的方法经常需要被重载boolean onAdvance(int phase, int registeredParties)方法。此方法有2个作用:
         * 1、当每一个阶段执行完毕，此方法会被自动调用，因此，重载此方法写入的代码会在每个阶段执行完毕时执行，相当于CyclicBarrier的barrierAction。
         * 2、当此方法返回true时，意味着Phaser被终止，因此可以巧妙的设置此方法的返回值来终止所有线程。
         */
        class MyPhaser extends Phaser {
            /**
             * 在每个阶段执行完成后回调的方法 当所有线程都到达当前阶段时，Phaser会自动调用onAdvance()方法。
             * onAdvance()方法的默认实现是返回true，表示当前Phaser对象已经达到了终止状态（terminated），不再接受新的参与者注册。但是，你也可以自定义onAdvance()方法来改变这个行为。
             * @param phase 阶段
             * @param registeredParties 每个阶段的线程个数
             */
            @Override
            protected boolean onAdvance(int phase, int registeredParties) {

                switch (phase) {
                    case 0:
                        System.out.println("学生准备好了,学生人数:" + getRegisteredParties());
                        return false;
                    case 1:
                        System.out.println("第一题所有学生做完");
                        return false;
                    case 2:
                        System.out.println("第二题所有学生做完");
                        return false;
                    case 3:
                        System.out.println("第三题所有学生做完，结束考试");
                        return true;
                    default:
                        return true;
                }
            }
        }

        class StudentTask implements Runnable {

            private Phaser phaser;

            public StudentTask(Phaser phaser) {
                this.phaser = phaser;
            }

            @Override
            public void run() {
                System.out.println(Thread.currentThread().getName() + "到达考试");
                phaser.arriveAndAwaitAdvance();

                System.out.println(Thread.currentThread().getName() + "做第1题时间...");
                doExercise();
                System.out.println(Thread.currentThread().getName() + "做第1题完成...");
                phaser.arriveAndAwaitAdvance();

                System.out.println(Thread.currentThread().getName() + "做第2题时间...");
                doExercise();
                System.out.println(Thread.currentThread().getName() + "做第2题完成...");
                phaser.arriveAndAwaitAdvance();

                System.out.println(Thread.currentThread().getName() + "做第3题时间...");
                doExercise();
                System.out.println(Thread.currentThread().getName() + "做第3题完成...");
                phaser.arriveAndAwaitAdvance();
            }
            /**
             * 随机睡眠几秒钟
             */
            private void doExercise() {
                long duration = (long) (Math.random() * 10);
                try {
                    TimeUnit.SECONDS.sleep(duration);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

        /**
         * 题目:5个学生参加考试，一共有三道题，要求所有学生到齐才能开始考试,全部做完第一题，才能继续做第二题，后面类似。
         * Phaser有phase和party两个重要状态，phase表示阶段，party表示每个阶段的线程个数，只有每个线程都执行了phaser.arriveAndAwaitAdvance(),才会进入下一个阶段，否则阻塞等待。
         * 例如题目中5个学生(线程)都条用phaser.arriveAndAwaitAdvance();就进入下一题
         */
        MyPhaser phaser = new MyPhaser();
        StudentTask[] studentTask = new StudentTask[5];
        for (int i = 0; i < studentTask.length; i++) {
            studentTask[i] = new StudentTask(phaser);
            // 每个线程注册一次,共注册5次,表示phaser维护的线程个数
            phaser.register();
        }

        Thread[] threads = new Thread[studentTask.length];
        for (int i = 0; i < studentTask.length; i++) {
            threads[i] = new Thread(studentTask[i], "Student " + i);
            threads[i].start();
        }

        // 等待所有线程执行结束
        for (int i = 0; i < studentTask.length; i++) {
            try {
                threads[i].join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        System.out.println("Phaser has finished:" + phaser.isTerminated());

    }

    /**
     * 使用Phaser动态注册parties
     */
    public static void demo2() {
        Random random = new Random(System.currentTimeMillis());
        class Task extends Thread {
            private Phaser phaser;

            public Task(Phaser phaser) {
                this.phaser = phaser;
                // 动态注册任务
                this.phaser.register();
            }

            @Override
            public void run() {
                try {
                    System.out.println("The thread [" + getName() + "] is working---第几阶段>" + phaser.getPhase());
                    TimeUnit.SECONDS.sleep(random.nextInt(5));
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                // 等待其他线程完成工作
                phaser.arriveAndAwaitAdvance();
                System.out.println("The thread [" + getName() + "] work finished-----第几阶段>" + phaser.getPhase());
            }
        }

        Phaser phaser = new Phaser();
        // 创建5个任务
        for (int i = 0; i < 5; i++) {
            new Task(phaser).start();
        }
        System.out.println("之前的注册---" + phaser.getRegisteredParties());
        // 动态注册主线程
        phaser.register();
        System.out.println("之后的注册---" + phaser.getRegisteredParties());
        // 等待其他线程完成工作,由原来的等待5个线程完成变成等待6个线程(主线程)完成后执行
        phaser.arriveAndAwaitAdvance();
        System.out.println("All of worker finished the task---第几阶段>" + phaser.getPhase());
    }

    /**
     * 使用Phaser设置多个阶段
     */
    public static void demo3() {
        Random random = new Random(System.currentTimeMillis());
        class Athlete extends Thread {
            private Phaser phaser;
            private int no;// 运动员编号

            public Athlete(Phaser phaser, int no) {
                this.phaser = phaser;
                this.no = no;
            }

            @Override
            public void run() {
                try {
                    System.out.println(getName() + no + ": 当前处于第:" + phaser.getPhase() + "阶段: start running");
                    TimeUnit.SECONDS.sleep(random.nextInt(5));
                    System.out.println(getName() + no + ": end running 等待其他运动员完成跑步");
                    // 等待其他运动员完成跑步
                    phaser.arriveAndAwaitAdvance();

                    System.out.println(getName() + no + ": 当前处于第:" + phaser.getPhase() + "阶段 : start bicycle");
                    TimeUnit.SECONDS.sleep(random.nextInt(5));
                    System.out.println(getName() + no + ": end bicycle 等待其他运动员完成骑行");
                    // 等待其他运动员完成骑行
                    phaser.arriveAndAwaitAdvance();

                    System.out.println(getName() + no + ": 当前处于第:" + phaser.getPhase() + "阶段 : start long jump");
                    TimeUnit.SECONDS.sleep(random.nextInt(5));
                    System.out.println(getName() + no + ": end long jump 等待其他运动员完成跳远");
                    // 等待其他运动员完成跳远
                    phaser.arriveAndAwaitAdvance();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

        // 初始化5个parties
        Phaser phaser = new Phaser(5);
        for (int i = 1; i < 6; i++) {
            new Athlete(phaser, i).start();
        }
    }

    /**
     * 常用方法演示
     */
    public static void demo4() {

        // 初始化5个parties
        Phaser phaser = new Phaser(5);

        // 只有当全部线程通过时才会进入下一阶段，从0开始
        System.out.println("当前阶段数:" + phaser.getPhase());

        // 添加一个parties
        phaser.register();
        System.out.println("当前注册的Parties数:" + phaser.getRegisteredParties());
        // 添加多个parties
        phaser.bulkRegister(4);
        System.out.println("当前Parties数:" + phaser.getRegisteredParties());

        new Thread(new Runnable() {
            @Override
            public void run() {
                // 到达并等待其他线程到达
                phaser.arriveAndAwaitAdvance();
                // 下面代码永远不会执行到,因为只有一条线程到达,其他的都没有到达
                System.out.println("never execute");
            }
        }).start();
        new Thread(new Runnable() {
            @Override
            public void run() {
                // 到达后注销该parties，不等待其他线程
                phaser.arriveAndDeregister();
                System.out.println("go on");
            }
        }).start();
        try {
            TimeUnit.MILLISECONDS.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("当前Parties数:" + phaser.getRegisteredParties());
        System.out.println("当前到达数:" + phaser.getArrivedParties());
        System.out.println("当前未达数:" + phaser.getUnarrivedParties());

        // 何时会停止，只有当parties中的数量为0时或者调用forceTermination方法就会停止了，我们也可以重写phaser中的onAdvance，给他返回true就会使这个phaser停止了
        System.out.println("phaser是否结束:" + phaser.isTerminated());
        phaser.forceTermination();
        System.out.println("phaser是否结束:" + phaser.isTerminated());
    }

    /**
     * 利用arrive只监听线程完成第一部分任务
     */
    public static void demo5() {
        Random random = new Random(System.currentTimeMillis());
        class ArrayTask extends Thread {
            private Phaser phaser;

            public ArrayTask(int name, Phaser phaser) {
                super(String.valueOf(name));
                this.phaser = phaser;
            }

            @Override
            public void run() {
                try {
                    // 模拟第一部分工作
                    System.out.println(getName() + " start working");
                    TimeUnit.SECONDS.sleep(random.nextInt(3));
                    System.out.println(getName() + " end working");
                    // 该方法表示到达但不会使线程阻塞
                    phaser.arrive();
                    // 模拟第二部分工作
                    TimeUnit.SECONDS.sleep(random.nextInt(3));
                    System.out.println(getName() + " do other thing");
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

        // 初始化6个parties
        Phaser phaser = new Phaser(6);
        // 创建5个任务
        IntStream.rangeClosed(1, 5).forEach(i -> new ArrayTask(i, phaser).start());
        // 等待5个任务的第一部分完成
        phaser.arriveAndAwaitAdvance();
        // 所有线程第一阶段的任务都执行完才会打印
        System.out.println("all work finished");
    }

    /**
     * awaitAdvance演示
     */
    public static void demo6() {
        Random random = new Random(System.currentTimeMillis());
        class ArrayTask extends Thread {
            private Phaser phaser;

            public ArrayTask(int name, Phaser phaser) {
                super(String.valueOf(name));
                this.phaser = phaser;
            }

            @Override
            public void run() {
                try {
                    System.out.println(getName() + " start working one");
                    TimeUnit.SECONDS.sleep(random.nextInt(3));
                    System.out.println(getName() + " end working one");
                    phaser.arriveAndAwaitAdvance();

                    System.out.println(getName() + " start working two");
                    TimeUnit.SECONDS.sleep(random.nextInt(3));
                    System.out.println(getName() + " end working two");
                    phaser.arriveAndAwaitAdvance();

                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

        // 初始化6个parties
        Phaser phaser = new Phaser(5);
        // 创建5个任务
        IntStream.rangeClosed(1, 5).forEach(i -> new ArrayTask(i, phaser).start());
        // 当phaser中的当前阶段等于传入的阶段则该方法会阻塞，反之不会
        System.out.println("准备阻塞第几阶段>" + phaser.getPhase());
        // 阻塞当前阶段,当前阶段线程都执行完主线程就可以往下执行
        phaser.awaitAdvance(phaser.getPhase());
        System.out.println("all work finished");
    }
}
