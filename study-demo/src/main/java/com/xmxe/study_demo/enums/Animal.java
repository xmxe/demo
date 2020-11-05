package com.xmxe.study_demo.enums;

/**
 * see jdk java.util.concurrent.TimeUnit details
 * imitate TineUnit sample
 */
public enum Animal {
    PEOPLE{
        public long hands(long h){return h+1;}
        public long foots(long f){return f+1;}
    },
    CAT{
        public long hands(long h){return h+2;}
        public long foots(long f){return f+2;}
    },
    DOG{
        public long hands(long h){return h+3;}
        // public long foots(long f){return f+3;}
    };
    public long hands(long h){
        throw new AbstractMethodError();
    }
    public long foots(long f){
        throw new AbstractMethodError();
    }

    public void count(long c){
        System.out.println(hands(c));
        System.out.println(foots(c));
    }
}
