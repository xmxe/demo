package com.xmxe.study_demo;

import java.util.HashMap;
import java.util.concurrent.TimeUnit;

import org.apache.cxf.endpoint.Client;
import org.apache.cxf.jaxws.endpoint.dynamic.JaxWsDynamicClientFactory;
import org.apache.log4j.Logger;
import org.junit.Test;

public class MyTest {

    protected Logger logger = Logger.getLogger(MyTest.class);

    @Test
    public void webServiceTestSend() {

        // 创建动态客户端
        JaxWsDynamicClientFactory dcf = JaxWsDynamicClientFactory.newInstance();
        Client client = dcf.createClient("http://localhost:8080/jn/ws/user?wsdl");

        // 需要密码的情况需要加上用户名和密码
        // client.getOutInterceptors().add(new
        // ClientLoginInterceptor(USER_NAME,PASS_WORD));
        Object[] objects = new Object[0];
        try {

            // invoke("方法名",参数1,参数2,参数3....);
            objects = client.invoke("add1", "1", "小明");
            System.out.println("返回数据:" + objects[0]);
        } catch (java.lang.Exception e) {
            e.printStackTrace();
        }
    }

    // 定义一个整数数组，如何找出一个整数是否在这个数组里并获得整数的下标
    @Test
    public void 二分查找() {
        int[] array = new int[1000];
        for (int i = 0; i < 1000; i++) {
            array[i] = i * 14;
        }
        System.out.println(binarySearch(array, 2422));
    }

    public int binarySearch(int[] array, int target) {
        // 查找范围起点
        int start = 0;
        // 查找范围终点
        int end = array.length - 1;
        // 查找范围中位数
        int mid;
        // 迭代进行二分查找
        while (start <= end) {
            /*
             * 我从c需要方向说明溢出原因，如果你定义一个假如是16位的mid，
             * 那么如果start和end很大，二者求和超过16位那就溢出，高位那个进位其实被计算机舍弃了，那么得到的mid也就是错误的下标。
             */
            // mid=(start+end)/2 有可能溢出
            mid = start + (end - start) / 2;
            if (array[mid] == target) {
                return mid;
            } else if (array[mid] < target) {
                start = mid + 1;
            } else {
                end = mid - 1;
            }
        }
        return -1;
    }

    @Test
    public void try_catch_finally_return() {
        // try中的return语句先执行了但并没有立即返回，等到finally执行结束后再return
        // System.out.println(trycatchReturnTest1());// try block -> finally block ->
        // b>25, b = 100 -> 100
        // System.out.println(trycatchReturnTest2());// try block -> return statement ->
        // finally block -> after return

        // finally块中的return语句会覆盖try块中的return返回
        // System.out.println(trycatchReturnTest4());// try block -> finally block ->
        // b>25, b = 100 -> 200

        // 如果finally语句中没有return语句覆盖返回值，那么原来的返回值可能因为finally里的修改而改变也可能不变
        // System.out.println(trycatchReturnTest5());// try block -> finally block ->
        // b>25, b = 100 -> 100 无改变
        // System.out.println(trycatchReturnTest6().get("KEY").toString());// FINALLY
        // 原因Java中只有传值没有传址

        // try块里的return语句在异常的情况下不会被执行，这样具体返回哪个看情况
        // System.out.println(trycatchReturnTest7());// try block -> catch block ->
        // finally block -> b>25, b = 35 -> 204

        // 当发生异常后，catch中的return执行情况与未发生异常时try中return的执行情况完全一样。
        System.out.println(trycatchReturnTest8());// try block -> catch block -> finally block -> b>25, b = 35 -> 35

    }

    public int trycatchReturnTest1() {
        int b = 20;
        try {
            System.out.println("try block");
            return b += 80;
        } catch (Exception e) {
            System.out.println("catch block");
        } finally {
            System.out.println("finally block");
            if (b > 25) {
                System.out.println("b>25, b = " + b);
            }
        }
        return b;
    }

    public String trycatchReturnTest2() {
        try {
            System.out.println("try block");
            return trycatchReturnTest3();
        } finally {
            System.out.println("finally block");
        }
    }

    public String trycatchReturnTest3() {
        System.out.println("return statement");
        return "after return";
    }

    public int trycatchReturnTest4() {
        int b = 20;
        try {
            System.out.println("try block");
            return b += 80;
        } catch (Exception e) {
            System.out.println("catch block");
        } finally {
            System.out.println("finally block");
            if (b > 25) {
                System.out.println("b>25, b = " + b);
            }
            return 200;
        }

        // return b;
    }

    public int trycatchReturnTest5() {
        int b = 20;
        try {
            System.out.println("try block");
            return b += 80;
        } catch (Exception e) {
            System.out.println("catch block");
        } finally {
            System.out.println("finally block");
            if (b > 25) {
                System.out.println("b>25, b = " + b);
            }
            b = 150;
        }
        return 2000;
    }

    public HashMap<String, String> trycatchReturnTest6() {
        HashMap<String, String> map = new HashMap<String, String>();
        map.put("KEY", "INIT");
        try {
            map.put("KEY", "TRY");
            return map;
        } catch (Exception e) {
            map.put("KEY", "CATCH");
        } finally {
            map.put("KEY", "FINALLY");
            map = null;
        }
        return map;
    }

    public int trycatchReturnTest7() {
        int b = 20;
        try {
            System.out.println("try block");
            b = b / 0;
            return b += 80;
        } catch (Exception e) {
            b += 15;
            System.out.println("catch block");
        } finally {
            System.out.println("finally block");
            if (b > 25) {
                System.out.println("b>25, b = " + b);
            }
            b += 50;
        }
        return 204;
    }

    public int trycatchReturnTest8() {
        int b = 20;
        try {
            System.out.println("try block");
            b = b / 0;
            return b += 80;
        } catch (Exception e) {
            System.out.println("catch block");
            return b += 15;
        } finally {
            System.out.println("finally block");
            if (b > 25) {
                System.out.println("b>25, b = " + b);
            }
            b += 50;
        }
        // return b;
    }

    @Test
    public void threadInterrupt() {
        try {
            Thread myThread = new Thread(() -> {
                try {
                    TimeUnit.SECONDS.sleep(3);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            System.out.println("这是一个线程");
            },"线程1");
            myThread.start();
            // 当设置中断状态时遇到线程阻塞会抛出InterruptedException异常并清除中断状态
            myThread.interrupt();
            // 调用Thread.interrupted()后会清除中断状态
            System.out.println("当前线程" + Thread.currentThread().getName() + "---->是否中断" + Thread.interrupted());
            TimeUnit.SECONDS.sleep(3);
            // 调用isInterrupted()不会清除中断状态
            System.out.println("调用线程是否中断 " + myThread.getName() + "---->是否中断" + myThread.isInterrupted());    
            
        } catch (Exception e) {
            System.out.println("main catch");
            e.printStackTrace();
        }
    }
}




