package com.xmxe.study_demo.algorithm.sort;

import java.util.Arrays;

/**
 * 插入排序 https://mp.weixin.qq.com/s?__biz=MzIxMjE5MTE1Nw==&mid=2653199343&idx=1&sn=a5491fa908e45e6117423d9ba5062611&chksm=8c99e935bbee60232aacb7c2b74961a24e7b86d44bf98357c597ad277a8eb15639c1de7034d9&scene=21#wechat_redirect
 */
public class InsertSort {
    public static void sort( int[] array) {
        for (int i = 1; i < array.length; i++) {
            int insertValue = array[i];
            int j = i - 1;
            // 从右向左比较元素的同时，进行元素复制
            for (; j >= 0 && insertValue < array[j]; j--) {
                array[j + 1] = array[j];
            }
            // insertValue的值插入适当位置
            array[j + 1] = insertValue;
        }

    }

    public static void main(String[] args){
        int array[] = {12,1,3,46,5,0,-3,12,35,16};
        sort(array);
        System.out.println(Arrays.toString(array));
    }
}
