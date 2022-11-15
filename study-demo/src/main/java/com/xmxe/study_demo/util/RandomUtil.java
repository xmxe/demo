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
        // Math 类中的random方法返回一个[0.0, 1.0)区间的double值。下面这段代码能得到一个min和max之间的随机数
        int max = 108, min = 3;
        int randomWithMathRandom = (int) ((Math.random() * (max - min)) + min);
        System.out.println(randomWithMathRandom);

        // Java 1.7之前，最流行的随机数生成方法是nextInt。这个方法提供了带参数和无参数两个版本。不带参数调用时，
        // nextInt会以近似相等概率返回任意int值，因此很可能会得到负数
        Random random = new Random();
        int randomWithNextInt = random.nextInt();
        System.out.println(randomWithNextInt);
        // 如果调用netxInt时带上bound参数，将得到预期区间内的随机数：
        int randomWintNextIntWithinARange = random.nextInt(max - min) + min;
        System.out.println(randomWintNextIntWithinARange);

        // Java 8引入了新的ints方法，返回一个java.util.stream.IntStream，不带参数的ints方法将返回一个无限int流：
        IntStream unlimitedIntStream = random.ints();
        unlimitedIntStream.forEach(x->System.out.println(x));
        // 调用时还可以指定参数来限制流大小：
        int streamSize = 5;
        IntStream limitedIntStream = random.ints(streamSize);
        limitedIntStream.forEach(x->System.out.println(x));
        // 当然，也可以为生成数值设置最大值和最小值：
        IntStream limitedIntStreamWithinARange = random.ints(streamSize, min, max);
        limitedIntStreamWithinARange.forEach(x->System.out.println(x));

        /**
         * Java 1.7中ThreadLocalRandom类提供了一种新的更高效的随机数生成方法。与Random类相比有三个重要区别：无需显式初始化
         * ThreadLocalRandom实例。这样可以避免创建大量无用的实例，浪费垃圾收集器回收时间。不能为ThreadLocalRandom
         * 设置随机种子（seed），这可能会导致问题。如果需要设置随机种子，应该避免采用这种方式生成随机数。Random类在多线程时表现不佳。
         */
        int randomWithThreadLocalRandomInARange = ThreadLocalRandom.current().nextInt(min, max);
        System.out.println(randomWithThreadLocalRandomInARange);

        // Java 8及更高版本提供了几种新方法。首先，nextInt方法有两个变体：
        int randomWithThreadLocalRandom = ThreadLocalRandom.current().nextInt();
        System.out.println(randomWithThreadLocalRandom);
        int randomWithThreadLocalRandomFromZero = ThreadLocalRandom.current().nextInt(max);
        System.out.println(randomWithThreadLocalRandomFromZero);
        // 其次，还可以使用ints方法
        IntStream streamWithThreadLocalRandom = ThreadLocalRandom.current().ints();
        System.out.println(streamWithThreadLocalRandom);

        /**
         * Java 8还带来了一个快速随机数生成器SplittableRandom类。
         * 正如JavaDoc中描述的那样，这是一个支持并行计算的随机数生成器。值得注意的是，这个实例非线程安全。使用该类时需要当心。
         * 现在有了nextInt和ints方法。调用nextInt时，可以通过参数指定top和bottom范围：这样可以检查max参数是否大于min。
         * 检查失败会收到一个到IllegalArgumentException。但是，这里不会进行正数或负数检查。因此，参数可以填写负数。也可以在调用时使用一个参数或者不使用参数。工作方式与之前描述的相同
         */
        SplittableRandom splittableRandom = new SplittableRandom();
        int randomWithSplittableRandom = splittableRandom.nextInt(min, max);
        System.out.println(randomWithSplittableRandom);

        // 这里也提供了ints方法。这意味着可以轻松得到int流。可以选择流数据个数有限或无限。对于有限流，可以为数字生成范围设置top和bottom：
        IntStream limitedIntStreamWithinARangeWithSplittableRandom = splittableRandom.ints(streamSize, min, max);
        limitedIntStreamWithinARangeWithSplittableRandom.forEach(x->System.out.println(x));

        /**
         * 如果应用程序对安全敏感，则应考虑使用SecureRandom。这是一个强加密随机数生成器。实例默认构造函数不使用随机种子。因此，我们应该：
         * 设置随机种子：随机种子不可预测,设置java.util.secureRandomSeed系统属性为true。 
         * 该类继承自java.util.Random。现在，我们介绍了上面各种随机数生成方法。例如，如果需要获取任意int值，调用nextInt时可以不带参数
         */
        SecureRandom secureRandom = new SecureRandom();
        int randomWithSecureRandom = secureRandom.nextInt();
        System.out.println(randomWithSecureRandom);
        // 如果需要设置随机数生成范围，则可以在调用时带bound参数
        int randomWithSecureRandomWithinARange = secureRandom.nextInt(max - min) + min;
        System.out.println(randomWithSecureRandomWithinARange);

        RandomDataGenerator randomDataGenerator = new RandomDataGenerator();
        int randomWithRandomDataGenerator = randomDataGenerator.nextInt(min, max);
        System.out.println(randomWithRandomDataGenerator);
    }
}