package com.xmxe.study_demo.other;

import java.util.stream.Stream;

/**
 * Java 代码实现一个标准输出的进度条
 */
public class Processing {
    public static void main(String[] args) {
        char incomplete = '░'; // U+2591 Unicode Character 表示还没有完成的部分
        char complete = '█'; // U+2588 Unicode Character 表示已经完成的部分

        int total = 100;
        StringBuilder stringBuilder = new StringBuilder();
        Stream.generate(() -> incomplete).limit(total).forEach(stringBuilder::append);
        for (int i = 0; i < total; i++) {
            stringBuilder.replace(i, i + 1, String.valueOf(complete));
            String progressBar = "\r" + stringBuilder;
            String percent = " " + (i + 1) + "%";
            System.out.print(progressBar + percent);
            try {
              Thread.sleep(i * 5L);
            } catch (InterruptedException ignored) {

            }
          }
    }
}
