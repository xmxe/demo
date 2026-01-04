package com.xmxe.util;

import java.util.BitSet;

import com.google.common.base.Charsets;

/**
 * 布隆过滤器说某个元素存在，小概率会误判。布隆过滤器说某个元素不在，那么这个元素一定不在。
 */
public class BloomFilter {
    /**
     * 一个长度为10亿的比特位
     */
    private static final int DEFAULT_SIZE = 256 << 22;

    /**
     * 为了降低错误率,使用加法hash算法,所以定义一个8个元素的质数数组
     */
    private static final int[] seeds = { 3, 5, 7, 11, 13, 31, 37, 61 };

    /**
     * 相当于构建8个不同的hash算法
     */
    private static HashFunction[] functions = new HashFunction[seeds.length];

    /**
     * 初始化布隆过滤器的bitmap
     */
    private static BitSet bitset = new BitSet(DEFAULT_SIZE);

    /**
     * 添加数据
     *
     * @param value 需要加入的值
     */
    public static void add(String value) {
        if (value != null) {
            for (HashFunction f : functions) {
                // 计算 hash 值并修改 bitmap 中相应位置为 true
                bitset.set(f.hash(value), true);
            }
        }
    }

    /**
     * 判断相应元素是否存在
     * 
     * @param value 需要判断的元素
     * @return 结果
     */
    public static boolean contains(String value) {
        if (value == null) {
            return false;
        }
        boolean ret = true;
        for (HashFunction f : functions) {
            ret = bitset.get(f.hash(value));
            // 一个 hash 函数返回 false 则跳出循环
            if (!ret) {
                break;
            }
        }
        return ret;
    }

    static class HashFunction {

        private int size;
        private int seed;

        public HashFunction(int size, int seed) {
            this.size = size;
            this.seed = seed;
        }

        public int hash(String value) {
            int result = 0;
            int len = value.length();
            for (int i = 0; i < len; i++) {
                result = seed * result + value.charAt(i);
            }
            // int r = (size - 1) & result;
            return (size - 1) & result;
        }
    }

    /**
     * 测试。。。
     */
    public static void main(String[] args) {

        for (int i = 0; i < seeds.length; i++) {
            functions[i] = new HashFunction(DEFAULT_SIZE, seeds[i]);
        }

        // 添加1亿数据
        for (int i = 0; i < 100000000; i++) {
            add(String.valueOf(i));
        }
        String id = "123456789";
        add(id);

        System.out.println(contains(id)); // true
        System.out.println("" + contains("234567890")); // false

        // Guava版本的布隆过滤器实现
        // 初始化一个存储String类型的布隆过滤器,预计插入100个元素,误判率为0.01,默认值是0.03
        com.google.common.hash.BloomFilter<String> bloomFilter = com.google.common.hash.BloomFilter.create(com.google.common.hash.Funnels.stringFunnel(Charsets.UTF_8), 100, 0.01);
        // 插入元素
        bloomFilter.put("Lynn");
        bloomFilter.put("666");
        bloomFilter.put("八股文");
        // 判断元素是否存在
        System.out.println(bloomFilter.mightContain("Lynn")); // true
        System.out.println(bloomFilter.mightContain("张三"));  // false
        
        // redis布隆过滤器
    }
}
