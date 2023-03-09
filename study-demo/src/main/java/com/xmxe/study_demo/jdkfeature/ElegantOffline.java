package com.xmxe.study_demo.jdkfeature;

/**
 * 优雅上下线
 */
public class ElegantOffline {
    public static void main(String[] args) {
        boolean flag = true;
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("hook execute...");
        }));
        while (flag) {
            // app is runing
        }
        System.out.println("main thread execute end...");
        // 当执行kill 15 pid的时候打印hook execute...并给出一个提示Process finished with exit code 143 (interrupted by signal 15: SIGTERM)
    }

}
