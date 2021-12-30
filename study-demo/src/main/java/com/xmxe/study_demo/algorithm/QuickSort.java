package com.xmxe.study_demo.algorithm;

import java.util.Arrays;

/**
 * 快速排序
 * https://mp.weixin.qq.com/s?__biz=MzIxMjE5MTE1Nw==&mid=2653195042&idx=1&sn=2b0915cd2298be9f2163cc90a3d464da&chksm=8c99f9f8bbee70eef627d0f5e5b80a604221abb3a1b5617b397fa178582dcb063c9fb6f904b3&scene=21#wechat_redirect
 */
public class QuickSort {
    //四个步骤：
    //1.比较startIndex和endIndex，更喜欢理解为校验
    //2.找出基准
    //3.左边部分排序
    //4.右边排序
    public static void quickSort(int[] arr, int startIndex, int endIndex) {
        if (startIndex < endIndex) {
            //找出基准
            int partition = partition(arr, startIndex, endIndex);
            //分成两边递归进行
            quickSort(arr, startIndex, partition - 1);
            quickSort(arr, partition + 1, endIndex);
        }
    }

    //找基准
    private static int partition(int[] arr, int startIndex, int endIndex) {
        int pivot = arr[startIndex];
        
        int left = startIndex;
        int right = endIndex;
        
        //等于就没有必要排序
        while (left != right) {
            
            while (left < right && arr[right] > pivot) {
                right--;
            }
          
            while (left < right && arr[left] <= pivot) {
                left++;
            }
            //找到left比基准大，right比基准小，进行交换
            if (left < right) {
                swap(arr, left, right);
            }
        }
        //第一轮完成，让left和right重合的位置和基准交换，返回基准的位置
        swap(arr, startIndex, left);
        return left;
    }

    //两数交换
    public static void swap(int[] arr, int i, int j) {
        int temp = arr[i];
        arr[i] = arr[j];
        arr[j] = temp;
    }

    public static void main(String[] args) {
        int[] a = {3, 1, 2, 4, 9, 6};
        quickSort(a, 0, a.length - 1);
        //输出结果
        System.out.println(Arrays.toString(a));
    }
}
