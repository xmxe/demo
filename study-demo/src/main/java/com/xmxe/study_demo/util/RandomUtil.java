package com.xmxe.study_demo.util;

import java.security.SecureRandom;
import java.util.Random;
import java.util.SplittableRandom;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.IntStream;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.math3.random.RandomDataGenerator;


public class RandomUtil {
     /**
     * 生成随机字符串
     */
    public void random_one() {
        int leftLimit = 97; // 'a'
        int rightLimit = 122; // 'z'
        int targetStringLength = 6;
        Random random = new Random();
        StringBuilder buffer = new StringBuilder(targetStringLength);
        for (int i = 0; i < targetStringLength; i++) {
            int randomLimitedInt = leftLimit + (int) (random.nextFloat() * (rightLimit - leftLimit + 1));
            buffer.append((char) randomLimitedInt);
        }
        String generatedString = buffer.toString();

        System.out.println(generatedString);
    }

    public void random_two() {
        int length = 6;
        boolean useLetters = true;
        // 不使用数字
        boolean useNumbers = false;
        String generatedString = RandomStringUtils.random(length, useLetters, useNumbers);

        System.out.println(generatedString);
    }

    /**
     * 生成随机数
     */
    public void random_three() {
        // Math 类中的 random 方法返回一个 [0.0, 1.0) 区间的 double 值。下面这段代码能得到一个 min 和 max 之间的随机数
        int max = 108, min = 3;
        int randomWithMathRandom = (int) ((Math.random() * (max - min)) + min);

        // Java 1.7 之前，最流行的随机数生成方法是 nextInt。这个方法提供了带参数和无参数两个版本。不带参数调用时，nextInt
        // 会以近似相等概率返回任意 int 值，因此很可能会得到负数：
        Random random = new Random();
        int randomWithNextInt = random.nextInt();
        // 如果调用 netxInt 时带上 bound 参数，将得到预期区间内的随机数：
        int randomWintNextIntWithinARange = random.nextInt(max - min) + min;

        // Java 8 引入了新的 ints 方法，返回一个java.util.stream.IntStream，不带参数的 ints方法将返回一个无限 int
        // 流：
        IntStream unlimitedIntStream = random.ints();
        // 调用时还可以指定参数来限制流大小：
        int streamSize = 5;
        IntStream limitedIntStream = random.ints(streamSize);
        // 当然，也可以为生成数值设置最大值和最小值：
        IntStream limitedIntStreamWithinARange = random.ints(streamSize, min, max);

        /**
         * Java 1.7 中ThreadLocalRandom类提供了一种新的更高效的随机数生成方法。与 Random 类相比有三个重要区别： 无需显式初始化
         * ThreadLocalRandom 实例。这样可以避免创建大量无用的实例，浪费垃圾收集器回收时间。 不能为 ThreadLocalRandom
         * 设置随机种子（seed），这可能会导致问题。如果需要设置随机种子，应该避免采用这种方式生成随机数。 Random 类在多线程时表现不佳。
         */
        int randomWithThreadLocalRandomInARange = ThreadLocalRandom.current().nextInt(min, max);

        // Java 8 及更高版本提供了几种新方法。首先，nextInt 方法有两个变体：
        int randomWithThreadLocalRandom = ThreadLocalRandom.current().nextInt();
        int randomWithThreadLocalRandomFromZero = ThreadLocalRandom.current().nextInt(max);
        // 其次，还可以使用 ints 方法
        IntStream streamWithThreadLocalRandom = ThreadLocalRandom.current().ints();

        /**
         * Java 8 还带来了一个快速随机数生成器SplittableRandom类。
         * 正如 JavaDoc 中描述的那样，这是一个支持并行计算的随机数生成器。值得注意的是，这个实例非线程安全。使用该类时需要当心。
         * 现在有了 nextInt 和 ints 方法。调用 nextInt 时，可以通过参数指定 top 和 bottom 范围： 这样可以检查 max
         * 参数是否大于
         * min。检查失败会收到一个到IllegalArgumentException。但是，这里不会进行正数或负数检查。因此，参数可以填写负数。也可以在调用时使用一个参数或者不使用参数。工作方式与之前描述的相同
         */
        SplittableRandom splittableRandom = new SplittableRandom();
        int randomWithSplittableRandom = splittableRandom.nextInt(min, max);
        // 这里也提供了ints方法。这意味着可以轻松得到 int流。可以选择流数据个数有限或无限。对于有限流，可以为数字生成范围设置 top 和 bottom：

        IntStream limitedIntStreamWithinARangeWithSplittableRandom = splittableRandom.ints(streamSize, min, max);

        /**
         * 如果应用程序对安全敏感，则应考虑使用SecureRandom。这是一个强加密随机数生成器。实例默认构造函数不使用随机种子。因此，我们应该：
         * 设置随机种子：随机种子不可预测
         * 设置 java.util.secureRandomSeed 系统属性为 true。 
         * 该类继承自java.util.Random。现在，我们介绍了上面各种随机数生成方法。例如，如果需要获取任意 int 值，调用 nextInt
         * 时可以不带参数
         */
        SecureRandom secureRandom = new SecureRandom();
        int randomWithSecureRandom = secureRandom.nextInt();
        // 如果需要设置随机数生成范围，则可以在调用时带 bound 参数
        int randomWithSecureRandomWithinARange = secureRandom.nextInt(max - min) + min;

        RandomDataGenerator randomDataGenerator = new RandomDataGenerator();
        int randomWithRandomDataGenerator = randomDataGenerator.nextInt(min, max);

    }
}