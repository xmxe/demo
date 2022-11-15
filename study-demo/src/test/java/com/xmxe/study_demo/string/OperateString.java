package com.xmxe.study_demo.string;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import com.xmxe.study_demo.entity.Student;

import org.apache.commons.lang3.StringUtils;
import org.junit.Test;

public class OperateString {

    /**
     * 多行字符串
     */
    @Test
    public void multiline_string() throws IOException {
        // 每个操作系统对换行符的定义都不尽相同，所以在拼接多行字符串之前，需要先获取到操作系统的换行符
        String newLine = System.getProperty("line.separator");

        String mutiLine = "亲爱的".concat(newLine).concat("我想你了").concat(newLine).concat("你呢？").concat(newLine)
                .concat("有没有在想我呢？");
        System.out.println(mutiLine);

        String mutiLine1 = "亲爱的" + newLine + "你好幼稚啊" + newLine + "技术文章里" + newLine + "你写这些合适吗";
        System.out.println(mutiLine1);

        // Java 8的String类加入了一个新的方法join()，可以将换行符与字符串拼接起来，非常方便
        String mutiLine2 = String.join(newLine, "亲爱的", "合适啊", "这叫趣味", "哈哈");
        System.out.println(mutiLine2);

        String mutiLine3 = new StringBuilder().append("亲爱的").append(newLine).append("看不下去了").append(newLine)
                .append("肉麻").toString();
        System.err.println(mutiLine3);

        // Java还可以通过Files.readAllBytes()方法从源文件中直接读取多行文本，格式和源文件保持一致
        String mutiLine4 = new String(Files.readAllBytes(Paths.get("src/main/resource/cmower.txt")));
        System.err.println(mutiLine4);

    }

    /**
     * 检查字符串是否为空
     *
     * Java 1.6之后，String类新添加了一个empty()方法，用于判断字符串是否为empty为了确保不抛出NPE，最好在判断之前先判空，
     * 因为empty()方法只判断了字符串的长度是否为0所以我们来优化一下isEmpty()方法
     */

    public boolean isEmpty(String str) {
        System.out.println(StringUtils.isBlank(" ")); // true;
        System.out.println(StringUtils.isEmpty(" ")); // false；
        return str == null || str.isEmpty();

    }  

    /**
     * 删除最后一个字符串
     *
     * 删除字符串最后一个字符，最简单的方法就是使用substring()方法进行截取，0作为起始下标，length() - 1作为结束下标。
     * 不管怎么样，substring()方法不是null安全的，需要先判空
     */
    @Test
    public void removeLastChar(String s) {
        String s1 = (s == null || s.length() == 0) ? null : (s.substring(0, s.length() - 1));
        System.out.println(s1);
        /**
         * 如果不想在操作之前判空，那么就直接上Apache的Commons Lang包
         */
        System.out.println(StringUtils.substring(s, 0, s.length() - 1));

        /**
         * 当然了，如果目的非常明确——就是只删除字符串的最后一个字符，还可以使用StringUtils类的chop()方法
         */
        System.out.println(StringUtils.chop(s));

        // 如果你对正则表达式了解的话，也可以使用replaceAll()方法进行替换，把最后一个字符.$替换成空字符串就可以了。
        System.out.println(s.replaceAll(".$", ""));

        // replaceAll()方法也不是null安全的，所以要提前判空：
        System.out.println((s == null) ? null : s.replaceAll(".$", ""));

        // 如果对Java 8的Lambda表达式和Optional比较熟的话，还可以这样写：
        String result1 = Optional.ofNullable(s).map(str -> str.replaceAll(".$", "")).orElse(s);
        System.out.println(result1);
    }

    /**
     * 统计字符在字符串中出现的次数
     */
    @Test
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

        // 如果想使用第三方类库的话，可以继续选择Apache的Commons Lang包
        int count2 = StringUtils.countMatches("chenmowanger", "e");
        System.out.println(count2);

    }

    /**
     * 拆分字符串
     */
    @Test
    public void splitStr() {
        String[] splitted = "沉默王二，一枚有趣的程序员".split("，");
        System.out.println(Arrays.toString(splitted));
        // 当然了，该方法也不是null安全的
        // 之前反复提到的StringUtils类，来自Apache的Commons Lang包：
        String[] splitted2 = StringUtils.split("沉默王二，一枚有趣的程序员", "，");
        System.out.println(Arrays.toString(splitted2));
        System.out.println(StringUtils.split("a..b.c", '.')); // ["a", "b", "c"]
        System.out.println(StringUtils.splitByWholeSeparatorPreserveAllTokens("a..b.c", ".")); // ["a","", "b", "c"]
        // ps:注意以上两个方法区别。

        // StringUtils拆分之后得到是一个数组，我们可以使用Guava的
        Splitter splitter = Splitter.on(",");

        splitter.splitToList("ab,,b,c");// 返回是一个List集合，结果：[ab, , b, c]

        splitter.omitEmptyStrings().splitToList("ab,,b,c"); // 忽略空字符串，输出结果[ab, b, c]

    }

    /**
     * 字符串拼接
     */
    @Test
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
        // 上面业务代码不太难，但是需要注意一下上面这段代码非常容易出错，容易抛出StringIndexOutOfBoundsException。

        // 这里我们可以直接使用以下方法获取拼接之后字符串
        String[] arrayStr = { "a", "b", "c" };
        System.out.println(StringUtils.join(arrayStr, ",")); // "a,b,c"
        // StringUtils只能传入数组拼接字符串，不过我比较喜欢集合拼接，所以再推荐下Guava的Joiner。
        // 实例代码如下：

        String[] array1 = new String[] { "test", "1234", "5678" };
        List<String> list = new ArrayList<>();
        list.add("test");
        list.add("1234");
        list.add("5678");
        StringUtils.join(array1, ",");

        // 逗号分隔符，跳过null
        Joiner joiner = Joiner.on(",").skipNulls();
        joiner.join(array);
        joiner.join(list);

    }

    /**
     * 字符串固定长度
     */
    @Test
    public void fleng() {
        // 字符串固定长度8位，若不足，往左补0
        System.out.println(StringUtils.leftPad("test", 8, "0"));
        // 另外还有一个StringUtils#rightPad,这个方法与上面方法正好相反。

    }

    /**
     * 字符串关键字替换
     */
    @Test
    public void wordRep() {
        // 默认替换所有关键字
        System.out.println(StringUtils.replace("aba", "a", "z")); // "zbz";

        // 替换关键字，仅替换一次
        System.out.println(StringUtils.replaceOnce("aba", "a", "z")); // "zba";

        // 使用正则表达式替换
        // System.out.println(StringUtils.replacePattern("ABCabc123", "[^A-Z0-9]+",
        // "")); // "ABC123";
    }

    @Test
    public void jdkNewStringOperation(){
        var str = "  woshidage   ";     
        var result1 = str.strip();    //首位空白
        var result2 = str.stripTrailing();  //去除尾部空白
        var result3 = str.stripLeading();  //去除首部空白
        var copyStr = str.repeat(2);  //复制几遍字符串
        var lineCount = str.lines().count(); //行数统计
        var str1 = " ";
        var isblank = str1.isBlank();  //判断字符串是空白
        var isempty = str1.isEmpty();  //判断字符串是否为空字符串

        System.out.println(isblank);
        System.out.println(isempty);
        System.out.println(result1);
        System.out.println(result2);
        System.out.println(result3);
        System.out.println(copyStr);
        System.out.println(lineCount);
    }

    /**
     * 内存地址
     */
    @Test
	public void memory() {
		Student a = new Student(1, "a");
		Student b = a;
		System.out.println(b.toString());// age=1 name=a
		a.setAge(2);
		System.out.println(b.toString());// age=2 name=a 内存地址引用的a a发生改变，b也改变
		a = new Student(3, "b");
		System.out.println(a.toString());// age=3 name=b
		System.out.println(b);// age=2 name=a a是一个新的内存地址 b还是原来的内存地址，b不变

		Student c = new Student(4, "c");
		Map<String, Student> map = new HashMap<>();
		map.put("a", a);
		map.put("c", c);
		Map<String, Student> m = map;
		System.out.println(m);// {a=age=3 name=b, c=age=4 name=c}
		map.remove("a");
		System.out.println(m);// {c=age=4 name=c} 内存地址引用的map map发生改变 m也跟着改变
		map = new HashMap<>();
		System.out.println(map);// {} map是一个新的内存地址
		System.out.println(m);// {c=age=4 name=c} 原来的内存地址引用没有变化
	}
}
