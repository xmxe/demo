package com.xmxe.study_demo.algorithm;

import java.util.Arrays;

/**
 * 希尔排序
 * https://mp.weixin.qq.com/s?__biz=MzIxMjE5MTE1Nw==&mid=2653199674&idx=1&sn=9ab7bb7e465104c67a3d8590ebd0fe6c&chksm=8c99efe0bbee66f69c07e5f423d7751c9667fa82beb6dcaef4c0e96dac9545d2277c8179c765&scene=21#wechat_redirect
 * 
 */
public class ShellSort {
    public static void sort(int[] array) {
        // 希尔排序的增量
        int d = array.length;
        while (d > 1) {
            // 使用希尔增量的方式，即每次折半
            d = d / 2;
            for (int x = 0; x < d; x++) {
                for (int i = x + d; i < array.length; i = i + d) {
                    int temp = array[i];
                    int j;
                    for (j = i - d; j >= 0 && array[j] > temp; j = j - d) {
                        array[j + d] = array[j];
                    }
                    array[j + d] = temp;
                }
            }
        }
    }

    public static void main(String[] args){
        int[] array = {5,3,9,12,6,1,7,2,4,11,8,10};
        sort(array);
        System.out.println(Arrays.toString(array));

    }
}