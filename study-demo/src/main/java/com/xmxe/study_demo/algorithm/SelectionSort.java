package com.xmxe.study_demo.algorithm;

import java.util.Arrays;

/**
 * 选择排序
 * https://mp.weixin.qq.com/s?__biz=MzIxMjE5MTE1Nw==&mid=2653198991&idx=1&sn=7f98d59898a911e1425baa6cc180c598&chksm=8c99e855bbee61439086680ceefef33c56038c5d552ae64c1d6135abe467b617aa62f4934f36&scene=21#wechat_redirect
 */
public class SelectionSort {
    public static void selectionSort(int[] array) {
        for (int i = 0; i < array.length - 1; i++) {
            int minIndex = i;
            for (int j = i + 1; j < array.length; j++) {
                minIndex = array[minIndex] < array[j] ? minIndex : j;
            }

            int temp = array[i];
            array[i] = array[minIndex];
            array[minIndex] = temp;

        }

    }

    public static void main(String[] args) {
        int[] array = new int[] { 3, 4, 2, 1, 5, 6, 7, 8, 30, 50, 1, 33, 24, 5, -4, 7, 0 };
        selectionSort(array);
        System.out.println(Arrays.toString(array));

    }
}
