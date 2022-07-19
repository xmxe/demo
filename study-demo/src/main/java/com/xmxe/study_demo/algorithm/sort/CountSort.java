package com.xmxe.study_demo.algorithm.sort;

import java.util.Arrays;

/**
 * 计数排序
 * https://mp.weixin.qq.com/s?__biz=MzIxMjE5MTE1Nw==&mid=2653195533&idx=1&sn=02918dc51b07837ce1119f00d7900dbc&chksm=8c99ffd7bbee76c1d2e2e9b198259795285ec2c305d3613a5e39622195fd1c32bb6dbe52fa08&scene=21#wechat_redirect
 */
public class CountSort {
    public static int[] countSort(int[] array){

        // 1.得到数列的最大值和最小值，并算出差值d
        int max = array[0];
        int min = array[0];

        for (int i = 1; i < array.length; i++){
            if (array[i] > max){
                max = array[i];
            }
            if (array[i] < min){
                min = array[i];
            }
        }

        int d = max - min;

        // 2.创建统计数组并统计对应元素个数
        int[] countArray = new int[d + 1];

        for (int i = 0; i < array.length; i++){
            countArray[array[i] - min]++;
        }

        // 3.统计数组做变形，后面的元素等于前面的元素之和
        int sum = 0;

        for (int i = 0; i < countArray.length; i++){
            sum += countArray[i];
            countArray[i] = sum;

        }

        // 4.倒序遍历原始数列，从统计数组找到正确位置，输出到结果数组
        int[] sortedArray = new int[array.length];
        for (int i = array.length - 1; i >= 0; i--){
            sortedArray[countArray[array[i] - min] - 1] = array[i];
            countArray[array[i] - min]--;

        }
        return sortedArray;
    }

    public static void main(String[] args){
        int[] array = new int[] {95,94,91,98,99,90,99,93,91,92};
        int[] sortedArray = countSort(array);
        System.out.println(Arrays.toString(sortedArray));
    }
}
