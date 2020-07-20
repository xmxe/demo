package com.xmxe.study_demo.string;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.SplittableRandom;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.IntStream;

import com.google.common.base.Joiner;
import com.google.common.base.Splitter;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.math3.random.RandomDataGenerator;

public class OperateString {

    /**
     * 多行字符串
     */
    public void multiline_string() throws IOException {
        // 每个操作系统对换行符的定义都不尽相同，所以在拼接多行字符串之前，需要先获取到操作系统的换行符
        String newLine = System.getProperty("line.separator");

        String mutiLine = "亲爱的".concat(newLine).concat("我想你了").concat(newLine).concat("你呢？").concat(newLine)
                .concat("有没有在想我呢？");
        System.out.println(mutiLine);

        String mutiLine1 = "亲爱的" + newLine + "你好幼稚啊" + newLine + "技术文章里" + newLine + "你写这些合适吗";
        System.out.println(mutiLine1);

        // Java 8 的 String 类加入了一个新的方法 join()，可以将换行符与字符串拼接起来，非常方便：
        String mutiLine2 = String.join(newLine, "亲爱的", "合适啊", "这叫趣味", "哈哈");
        System.out.println(mutiLine2);

        String mutiLine3 = new StringBuilder().append("亲爱的").append(newLine).append("看不下去了").append(newLine)
                .append("肉麻").toString();
        System.err.println(mutiLine3);

        // Java 还可以通过 Files.readAllBytes() 方法从源文件中直接读取多行文本，格式和源文件保持一致
        String mutiLine4 = new String(Files.readAllBytes(Paths.get("src/main/resource/cmower.txt")));
        System.err.println(mutiLine4);

    }

    /**
     * 检查字符串是否为空
     *
     * Java 1.6 之后，String 类新添加了一个 empty() 方法，用于判断字符串是否为 empty 为了确保不抛出
     * NPE，最好在判断之前先判空，因为 empty() 方法只判断了字符串的长度是否为 0 所以我们来优化一下 isEmpty() 方法
     */

    public boolean isEmpty(String str) {
        System.out.println(StringUtils.isBlank(" ")); // true;
        System.out.println(StringUtils.isEmpty(" ")); // false；
        return str == null || str.isEmpty();

    }

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

    /**
     * 删除最后一个字符串
     *
     * 删除字符串最后一个字符，最简单的方法就是使用 substring() 方法进行截取，0 作为起始下标，length() - 1 作为结束下标。
     * 不管怎么样，substring() 方法不是 null 安全的，需要先判空
     */
    public void removeLastChar(String s) {
        String s1 = (s == null || s.length() == 0) ? null : (s.substring(0, s.length() - 1));
        System.out.println(s1);
        /**
         * 如果不想在操作之前判空，那么就直接上 Apache 的 Commons Lang 包
         */
        System.out.println(StringUtils.substring(s, 0, s.length() - 1));

        /**
         * 当然了，如果目的非常明确——就是只删除字符串的最后一个字符，还可以使用 StringUtils 类的 chop() 方法
         */
        System.out.println(StringUtils.chop(s));

        // 如果你对正则表达式了解的话，也可以使用 replaceAll() 方法进行替换，把最后一个字符 .$ 替换成空字符串就可以了。
        System.out.println(s.replaceAll(".$", ""));

        // 当然了，replaceAll() 方法也不是 null 安全的，所以要提前判空：
        System.out.println((s == null) ? null : s.replaceAll(".$", ""));

        // 如果对 Java 8 的 Lambda 表达式和 Optional 比较熟的话，还可以这样写：
        String result1 = Optional.ofNullable(s).map(str -> str.replaceAll(".$", "")).orElse(s);
        System.out.println(result1);
    }

    /**
     * 统计字符在字符串中出现的次数
     */

    public void countString() {

        String someString = "chenmowanger";
        char someChar = 'e';
        int count = 0;
        for (int i = 0; i < someString.length(); i++) {
            if (someString.charAt(i) == someChar) {
                count++;
            }
        }
        System.out.println(count);

        long countjdk8 = someString.chars().filter(ch -> ch == 'e').count();
        System.out.println(countjdk8);

        // 如果想使用第三方类库的话，可以继续选择 Apache 的 Commons Lang 包
        int count2 = StringUtils.countMatches("chenmowanger", "e");
        System.out.println(count2);

    }

    /**
     * 拆分字符串
     */
    public void splitStr() {
        String[] splitted = "沉默王二，一枚有趣的程序员".split("，");
        // 当然了，该方法也不是 null 安全的
        // 之前反复提到的 StringUtils 类，来自 Apache 的 Commons Lang 包：
        String[] splitted2 = StringUtils.split("沉默王二，一枚有趣的程序员", "，");

        System.out.println(StringUtils.split("a..b.c", '.')); // ["a", "b", "c"]
        System.out.println(StringUtils.splitByWholeSeparatorPreserveAllTokens("a..b.c", ".")); // ["a","", "b", "c"]
        // ps:注意以上两个方法区别。

        // StringUtils 拆分之后得到是一个数组，我们可以使用 Guava 的
        Splitter splitter = Splitter.on(",");

        splitter.splitToList("ab,,b,c");// 返回是一个 List 集合，结果：[ab, , b, c]

        splitter.omitEmptyStrings().splitToList("ab,,b,c"); // 忽略空字符串，输出结果 [ab, b, c]

    }

    /**
     * 字符串拼接
     */
    public void appendStr() {

        // 第一个参数为字符串连接符，比如说：
        String message = String.join("-", "王二", "太特么", "有趣了");
        System.out.println(message);
        // 输出结果为：王二-太特么-有趣了

        String chenmo1 = "沉默";
        String wanger1 = "王二";

        System.out.println(StringUtils.join(chenmo1, wanger1));

        String chenmo2 = "沉默";
        String wanger2 = "王二";

        System.out.println(chenmo2.concat(wanger2));

        String[] array = new String[] { "test", "1234", "5678" };
        StringBuilder stringBuilder = new StringBuilder();

        for (String s : array) {
            stringBuilder.append(s).append(";");
        }
        // 防止最终拼接字符串为空
        if (stringBuilder.length() > 0) {
            stringBuilder.deleteCharAt(stringBuilder.length() - 1);
        }
        System.out.println(stringBuilder.toString());
        // 上面业务代码不太难，但是需要注意一下上面这段代码非常容易出错，容易抛出 StringIndexOutOfBoundsException。

        // 这里我们可以直接使用以下方法获取拼接之后字符串
        String[] arrayStr = { "a", "b", "c" };
        System.out.println(StringUtils.join(arrayStr, ",")); // "a,b,c"
        // StringUtils 只能传入数组拼接字符串，不过我比较喜欢集合拼接，所以再推荐下 Guava 的 Joiner。
        // 实例代码如下：

        String[] array1 = new String[] { "test", "1234", "5678" };
        List<String> list = new ArrayList<>();
        list.add("test");
        list.add("1234");
        list.add("5678");
        StringUtils.join(array1, ",");

        // 逗号分隔符，跳过 null
        Joiner joiner = Joiner.on(",").skipNulls();
        joiner.join(array);
        joiner.join(list);

    }

    /**
     * 字符串固定长度
     */
    public void fleng() {
        // 字符串固定长度 8位，若不足，往左补 0
        System.out.println(StringUtils.leftPad("test", 8, "0"));
        // 另外还有一个 StringUtils#rightPad,这个方法与上面方法正好相反。

    }

    /**
     * 字符串关键字替换
     */

    public void wordRep() {
        // 默认替换所有关键字
        System.out.println(StringUtils.replace("aba", "a", "z")); // "zbz";

        // 替换关键字，仅替换一次
        System.out.println(StringUtils.replaceOnce("aba", "a", "z")); // "zba";

        // 使用正则表达式替换
        // System.out.println(StringUtils.replacePattern("ABCabc123", "[^A-Z0-9]+",
        // "")); // "ABC123";
    }

}
