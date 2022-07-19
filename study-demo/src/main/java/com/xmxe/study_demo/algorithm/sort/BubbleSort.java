package com.xmxe.study_demo.algorithm.sort;

/**
 * 冒泡排序
 * https://mp.weixin.qq.com/s?__biz=MzIxMjE5MTE1Nw==&mid=2653194666&idx=1&sn=69ce32870c0b981c40b1e124fbb6bba8&chksm=8c99fb70bbee72668cad223892ad362525d215e7f936458f99dd289eb82981099359310e9e54&scene=21#wechat_redirect
 */
public class BubbleSort{
    public static void main(String[] args) {
        int[] arr = {2,5,4,1};
        for(int i = 0;i < arr.length - 1;i++) {
            for(int j = 0;j < arr.length - 1 - i;j++) {
                if(arr[j] > arr[j+1]) {
                    int temp = arr[j];
                    arr[j] = arr[j+1];
                    arr[j+1] = temp;
                }
            }
        }
    }
}