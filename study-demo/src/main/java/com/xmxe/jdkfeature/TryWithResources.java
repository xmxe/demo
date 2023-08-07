package com.xmxe.jdkfeature;

/**
 *  try语句如何更优雅的关闭资源？请看这里！ (https://mp.weixin.qq.com/s/mG1YCV7oOfdv5WMQIc9Ngw)
 */
public class TryWithResources {
   public static void main(String[] args) {
        try(
            TWR t = new TWR("a");
            // try语句中越是最后使用的资源,越是最早被关闭(t2的close()方法最先执行)
            TWR t2 = new TWR();
        ){
            int a = 1 / 0;
            System.out.println(a);
        }catch(Exception e){
            e.printStackTrace();

            // 对于try语句块内的异常,我们可以通过e.getMessage()获取,对于close()方法抛出的异常,其实编译器对这部分的异常进行特殊处理,将其放入到集合数组中了,因此我们需要通过e.getSuppressed()方法来获取
            Throwable[] suppressed = e.getSuppressed();
            for (int i = 0; i < suppressed.length; i++){
                System.out.println(suppressed[i].getMessage());
            }
        }
        
    }
    /**
     * 实现try-with-resources必须实现AutoCloseable接口
     */
    static class TWR implements AutoCloseable{

        private String str;

        public TWR(String str){this.str = str;}
        public TWR(){}

        public String getStr(){return str;}

        @Override
        public void close() throws Exception {
            System.out.println("execute auto close");
        }

        /*
        * 1.只要实现了AutoCloseable接口的类,并且在try里声明了对象变量,在try结束后,不管是否发生异常,close方法都会被调用
        * 2.其次,在try里越晚声明的对象,会越早被close掉
        * 3.try结束后自动调用的close方法,这个动作会早于finally里调用的方法
        */
        
    }
    
}
