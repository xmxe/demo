package com.xmxe.algorithm.sort;

import java.util.Arrays;

/**
 * 鸡尾酒排序 https://mp.weixin.qq.com/s?__biz=MzIxMjE5MTE1Nw==&mid=2653194919&idx=2&sn=f473bc9e0da124a303312a11902e2f52&chksm=8c99f87dbbee716b531df3fcf5882998f28794aad5609f225883d6c2dc71ba51b8a5126b32be&scene=21#wechat_redirect
 */
public class CockTailSort {
    private static void sort(int array[]) {
        int tmp = 0;
        for (int i = 0; i < array.length / 2; i++) {
            // 有序标记,每一轮的初始是true
            boolean isSorted = true;
            // 奇数轮,从左向右比较和交换
            for (int j = i; j < array.length - i - 1; j++) {
                if (array[j] > array[j + 1]) {
                    tmp = array[j];
                    array[j] = array[j + 1];
                    array[j + 1] = tmp;
                    // 有元素交换,所以不是有序,标记变为false
                    isSorted = false;
                }

            }
            if (isSorted) {
                break;
            }
            // 偶数轮之前,重新标记为true
            isSorted = true;
            // 偶数轮,从右向左比较和交换
            for (int j = array.length - i - 1; j > i; j--) {
                if (array[j] < array[j - 1]) {
                    tmp = array[j];
                    array[j] = array[j - 1];
                    array[j - 1] = tmp;
                    // 有元素交换,所以不是有序,标记变为false
                    isSorted = false;
                }
            }

            if (isSorted) {
                break;
            }
        }
    }

    public static void main(String[] args) {
        int[] array = new
        int[] {2,3,4,5,6,7,8,1};
        sort(array);
        System.out.println(Arrays.toString(array));

    }
}
