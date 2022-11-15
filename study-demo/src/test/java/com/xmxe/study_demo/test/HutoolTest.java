package com.xmxe.study_demo.test;

import java.awt.Font;
import java.awt.Rectangle;
import java.awt.Color;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Date;
import java.util.HashMap;

import org.junit.Test;

import cn.hutool.cache.Cache;
import cn.hutool.cache.CacheUtil;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.date.DateUnit;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.date.Month;
import cn.hutool.core.img.ImgUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.IoUtil;
import cn.hutool.core.lang.Console;
import cn.hutool.core.lang.Validator;
import cn.hutool.core.map.BiMap;
import cn.hutool.core.util.IdcardUtil;
import cn.hutool.core.util.ReflectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.core.util.ZipUtil;
import cn.hutool.crypto.SecureUtil;
import cn.hutool.crypto.symmetric.AES;

// 不要再重复造轮子了，这款开源工具类库贼好使！(https://mp.weixin.qq.com/s/ZQV_hx_sbxCA8QXoC05wQQ)
public class HutoolTest {

    @Test
    public void convert(){
        String param = "10";
        int paramInt = Convert.toInt(param);
        int paramIntDefault = Convert.toInt(param, 0);

        // 把字符串转换成日期：

        String dateStr = "2020年09月29日";
        Date date = Convert.toDate(dateStr);

        // 把字符串转成Unicode：
        String unicodeStr = "沉默王二";
        String unicode = Convert.strToUnicode(unicodeStr);
    }

    @Test
    public void datetime(){
        // 获取当前日期：
        Date date = DateUtil.date();
        //DateUtil.date()返回的其实是DateTime，它继承自Date对象，重写了toString()方法，返回yyyy-MM-dd HH:mm:ss格式的字符串。
        System.out.println(date);// 2020-09-29 04:28:02

        // 字符串转日期：
        String dateStr = "2020-09-29";
        Date date0 = DateUtil.parse(dateStr);
        // DateUtil.parse()会自动识别一些常用的格式，比如说：
        // yyyy-MM-dd HH:mm:ss
        // yyyy-MM-dd
        // HH:mm:ss
        // yyyy-MM-dd HH:mm
        // yyyy-MM-dd HH:mm:ss.SSS
        // 还可以识别带中文的：
        // 年月日时分秒
        // 格式化时间差：

        String dateStr1 = "2020-09-29 22:33:23";
        Date date1 = DateUtil.parse(dateStr1);

        String dateStr2 = "2020-10-01 23:34:27";
        Date date2 = DateUtil.parse(dateStr2);

        long betweenDay = DateUtil.between(date1, date2, DateUnit.MS);

        // 输出：2天1小时1分4秒
        // String formatBetween = DateUtil.formatBetween(betweenDay, BetweenFormater.Level.SECOND);

        // 星座和属相：

        // 射手座
        String zodiac = DateUtil.getZodiac(Month.DECEMBER.getValue(), 10);
        // 蛇
        String chineseZodiac = DateUtil.getChineseZodiac(1989);
    }

    @Test
    public void io(){
        BufferedInputStream in = FileUtil.getInputStream("hutool/origin.txt");
        BufferedOutputStream out = FileUtil.getOutputStream("hutool/to.txt");
        long copySize = IoUtil.copy(in, out, IoUtil.DEFAULT_BUFFER_SIZE);
    }

    @Test
    public void string(){
        String template = "{}，一枚沉默但有趣的程序员，喜欢他的文章的话，请微信搜索{}";
        String str = StrUtil.format(template, "沉默王二", "沉默王二");
    }

    @Test
    public void reflect(){
        class ReflectDemo {
            private int id;

            public ReflectDemo() {
                System.out.println("构造方法");
            }

            public void print() {
                System.out.println("我是沉默王二");
            }

            public void main(String[] args) throws IllegalAccessException {
                // 构建对象
                ReflectDemo reflectDemo = ReflectUtil.newInstance(ReflectDemo.class);

                // 获取构造方法
                Constructor[] constructors = ReflectUtil.getConstructors(ReflectDemo.class);
                for (Constructor constructor : constructors) {
                    System.out.println(constructor.getName());
                }

                // 获取字段
                Field field = ReflectUtil.getField(ReflectDemo.class, "id");
                field.setInt(reflectDemo, 10);
                // 获取字段值
                System.out.println(ReflectUtil.getFieldValue(reflectDemo, field));

                // 获取所有方法
                Method[] methods = ReflectUtil.getMethods(ReflectDemo.class);
                for (Method m : methods) {
                    System.out.println(m.getName());
                }

                // 获取指定方法
                Method method = ReflectUtil.getMethod(ReflectDemo.class, "print");
                System.out.println(method.getName());

                // 执行方法
                ReflectUtil.invoke(reflectDemo, "print");
            }
        }
    }

    @Test
    public void compress(){
        ZipUtil.zip("hutool", "hutool.zip");
        File unzip = ZipUtil.unzip("hutool.zip", "hutoolzip");
    }


    @Test
    public void idCode(){
        // 身份证工具
        // Hutool封装的IdcardUtil可以用来对身份证进行验证，支持大陆15位、18位身份证，港澳台10位身份证。

        String ID_18 = "321083197812162119";
        String ID_15 = "150102880730303";

        boolean valid = IdcardUtil.isValidCard(ID_18);
        boolean valid15 = IdcardUtil.isValidCard(ID_15);
    }

    @Test
    public void console(){
        // 本地编码的过程中，经常需要使用System.out打印结果，但是往往一些复杂的对象不支持直接打印，
        // 比如说数组，需要调用Arrays.toString。Hutool封装的Console类借鉴了JavaScript中的console.log()，
        // 使得打印变成了一个非常便捷的方式。
        Console.log("沉默王二，一枚有趣的程序员");

        // 打印字符串模板
        Console.log("洛阳是{}朝古都",13);

        int[] ints = {1,2,3,4};
        // 打印数组
        Console.log(ints);
    }

    @Test
    public void validator(){
        Validator.isEmail("沉默王二");
        Validator.isMobile("itwanger.com");

    }

    @Test
    public void biMap(){
        // 双向查找 Map
        // Guava中提供了一种特殊的Map结构，叫做BiMap，实现了一种双向查找的功能，可以根据key查找value，也可以根据value查找key，Hutool也提供这种Map结构。

        BiMap<String, String> biMap = new BiMap<>(new HashMap<>());
        biMap.put("wanger", "沉默王二");
        biMap.put("wangsan", "沉默王三");

        // get value by key
        biMap.get("wanger");
        biMap.get("wangsan");

        // get key by value
        biMap.getKey("沉默王二");
        biMap.getKey("沉默王三");
    }



    @Test
    public void image(){
        // 缩放图片
        ImgUtil.scale(FileUtil.file("hutool/wangsan.jpg"),FileUtil.file("hutool/wangsan_small.jpg"),0.5f);

        // 裁剪图片：
        ImgUtil.cut(
                FileUtil.file("hutool/wangsan.jpg"),
                FileUtil.file("hutool/wangsan_cut.jpg"),
                new Rectangle(200, 200, 100, 100)
        );
        // 添加水印：
        ImgUtil.pressText(//
                FileUtil.file("hutool/wangsan.jpg"),
                FileUtil.file("hutool/wangsan_logo.jpg"),
                "沉默王二", Color.WHITE,
                new Font("黑体", Font.BOLD, 100),
                0,
                0,
                0.8f
        );

    }


    @Test
    public void cache(){
        // CacheUtil是Hutool封装的创建缓存的快捷工具类，可以创建不同的缓存对象：

        // FIFOCache：先入先出，元素不停的加入缓存直到缓存满为止，当缓存满时，清理过期缓存对象，清理后依旧满则删除先入的缓存。
        Cache<String, String> fifoCache = CacheUtil.newFIFOCache(3);
        fifoCache.put("key1", "沉默王一");
        fifoCache.put("key2", "沉默王二");
        fifoCache.put("key3", "沉默王三");
        fifoCache.put("key4", "沉默王四");

        // 大小为3，所以key3放入后key1被清除
        String value1 = fifoCache.get("key1");

        // LFUCache，最少使用，根据使用次数来判定对象是否被持续缓存，当缓存满时清理过期对象，清理后依旧满的情况下清除最少访问的对象并将其他对象的访问数减去这个最少访问数，以便新对象进入后可以公平计数。
        Cache<String, String> lfuCache = CacheUtil.newLFUCache(3);

        lfuCache.put("key1", "沉默王一");
        // 使用次数+1
        lfuCache.get("key1");
        lfuCache.put("key2", "沉默王二");
        lfuCache.put("key3", "沉默王三");
        lfuCache.put("key4", "沉默王四");

        // 由于缓存容量只有3，当加入第4个元素的时候，最少使用的将被移除（2,3被移除）
        String value2 = lfuCache.get("key2");
        String value3 = lfuCache.get("key3");

        // LRUCache，最近最久未使用，根据使用时间来判定对象是否被持续缓存，当对象被访问时放入缓存，当缓存满了，最久未被使用的对象将被移除。
        Cache<String, String> lruCache = CacheUtil.newLRUCache(3);

        lruCache.put("key1", "沉默王一");
        lruCache.put("key2", "沉默王二");
        lruCache.put("key3", "沉默王三");
        // 使用时间近了
        lruCache.get("key1");
        lruCache.put("key4", "沉默王四");

        // 由于缓存容量只有3，当加入第4个元素的时候，最久使用的将被移除（2）
        String value4 = lruCache.get("key2");
        System.out.println(value2);
    }

    public void a(){
        //  加密解密
        // 加密分为三种：

        // 对称加密（symmetric），例如：AES、DES 等
        // 非对称加密（asymmetric），例如：RSA、DSA 等
        // 摘要加密（digest），例如：MD5、SHA-1、SHA-256、HMAC等
        // Hutool 针对这三种情况都做了封装：

        // 对称加密 SymmetricCrypto
        // 非对称加密 AsymmetricCrypto
        // 摘要加密 Digester
        // 快速加密工具类 SecureUtil有以下这些方法：

        // 1）对称加密

        // SecureUtil.aes
        // SecureUtil.des
        // 2）非对称加密

        // SecureUtil.rsa
        // SecureUtil.dsa
        // 3）摘要加密

        // SecureUtil.md5
        // SecureUtil.sha1
        // SecureUtil.hmac
        // SecureUtil.hmacMd5
        // SecureUtil.hmacSha1

        AES aes = SecureUtil.aes();
        String encry = aes.encryptHex("沉默王二");
        System.out.println(encry);
        String oo = aes.decryptStr(encry);
        System.out.println(oo);
    }
}
