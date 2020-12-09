package com.xmxe.study_demo.jdkfeature;

public class TryWithResources {
   public static void main(String[] args) {
       //实现try-with-resources必须实现AutoCloseable接口
        class TWR implements AutoCloseable{

            private String str;
            public TWR(String str){this.str = str;}
            @Override
            public void close() throws Exception {
                System.out.println(str.toUpperCase());
            }
            
        }
        try(TWR t = new TWR("a")){
            int a = 1 / 0;
        }catch(Exception e){
            e.printStackTrace();
        }
   }

    
}
