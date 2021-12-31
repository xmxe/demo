package com.xmxe.study_demo.algorithm;

import java.util.Arrays;

/**
 * 堆排序
 * https://mp.weixin.qq.com/s?__biz=MzIxMjE5MTE1Nw==&mid=2653195208&idx=1&sn=e3d6559402148458f0a4993b47d8bc6f&chksm=8c99f912bbee7004625a0b204acc8484acbdf4f1b18953e7ff5acbea958ec002d8c8ea072792&scene=21#wechat_redirect
 */
public class HeapSort {
    /**
     * 下沉调整
     * @param array       待调整的堆
     * @param parentIndex 要下沉的父节点
     * @param parentIndex 堆的有效大小
     */

    public static void downAdjust(int[] array,int parentIndex,int length){
        // temp保存父节点值，用于最后的赋值
        int temp = array[parentIndex];
        int childIndex = 2 * parentIndex + 1;

        while(childIndex < length){
            // 如果有右孩子，且右孩子大于左孩子的值，则定位到右孩子
            if(childIndex + 1 < length && array[childIndex + 1] > array[childIndex]){
                childIndex++;
            }
            // 如果父节点小于任何一个孩子的值，直接跳出
            if(temp >= array[childIndex])
                break;
            // 无需真正交换，单向赋值即可
            array[parentIndex] = array[childIndex];
            parentIndex = childIndex;
            childIndex = 2 * childIndex + 1;
        }

        array[parentIndex] = temp;

    }

    /**
     * 堆排序
     * @param array 待调整的堆
     */
    public static void heapSort( int[] array){

        // 1.把无序数组构建成二叉堆。
        for(int i = (array.length - 2) / 2; i >= 0; i--){
            downAdjust(array,i,array.length);
        }

        System.out.println(Arrays.toString(array));

        // 2.循环删除堆顶元素，移到集合尾部，调节堆产生新的堆顶。
        for(int i = array.length - 1; i > 0; i--){
            // 最后一个元素和第一元素进行交换
            int temp = array[i];
            array[i] = array[0];
            array[0] = temp;
            // 下沉调整最大堆
            downAdjust(array,0,i);
        }

    }

    public static void main(String[] args){
        int[] arr = new int[]{1,3,2,6,5,7,8,9,10,0};
        heapSort(arr);
        System.out.println(Arrays.toString(arr));

    }
}
