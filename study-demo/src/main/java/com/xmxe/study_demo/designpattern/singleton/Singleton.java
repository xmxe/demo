package com.xmxe.study_demo.designpattern.singleton;

/**
 * 单例模式8种写法
 */

// 懒汉式:使用的时候才创建实例
class Singleton1 {
    private static Singleton1 instance;

    private Singleton1() {
    }

    public static Singleton1 getInstance() {
        if (instance == null) {
            instance = new Singleton1();
        }
        return instance;
    }
    // 线程不安全，不可用
}

class Singleton2 {

    private static Singleton2 instance;

    private Singleton2() {
    }

    public static synchronized Singleton2 getInstance() {
        if (instance == null) {
            instance = new Singleton2();
        }
        return instance;
    }
    // 同步方法，线程安全，效率低，不推荐。
}

class Singleton3 {

    private static Singleton3 singleton;

    private Singleton3() {
    }

    public static Singleton3 getInstance() {
        if (singleton == null) {
            synchronized (Singleton3.class) {
                singleton = new Singleton3();
            }
        }
        return singleton;
    }
    // 线程不安全，会产生多个实例，不可用。
}

// 饿汉式:类加载的时候就创建实例 无线程安全问题，不能延迟加载，影响系统性能。
class Singleton4 {

    private static Singleton4 instance = new Singleton4();

    private Singleton4() {
    }

    public static Singleton4 getInstance() {
        return instance;
    }

}

class Singleton5 {

    private static Singleton5 instance = null;

    static {
        instance = new Singleton5();
    }

    private Singleton5() {
    }

    public static Singleton5 getInstance() {
        return instance;
    }

}

// 双重校验锁
class Singleton6 {

    private static volatile Singleton6 singleton;

    private Singleton6() {
    }

    public static Singleton6 getInstance() {
        if (singleton == null) {
            synchronized (Singleton6.class) {
                if (singleton == null) {
                    singleton = new Singleton6();
                }
            }

        }
        return singleton;
    }
    // 双重校验锁，线程安全，推荐使用。
}

// 静态内部类

class Singleton7 {

    private static class SingletonHolder {
        private static final Singleton7 INSTANCE = new Singleton7();
    }

    private Singleton7() {
    }

    public static final Singleton7 getInstance() {
        return SingletonHolder.INSTANCE;
    }
    // 静态内部类，线程安全，主动调用时才实例化，延迟加载效率高，推荐使用。

}

// 枚举
enum Singleton8 {

    INSTANCE;

    public void whateverMethod() {

    }
    /**
     * 注意事项 
     * 1、考虑多线程问题
     * 2、单例类构造方法要设置为private类型禁止外界new创建 private Singleton() {}
     * 3、如果类可序列化，考虑反序列化生成多个实例问题，解决方案如下
     * private Object readResolve() throws ObjectStreamException {
     * // instead of the object we're on, return the class variable INSTANCE
     *    return INSTANCE;
     * }
     * 4.枚举类型，无线程安全问题，避免反序列华创建新的实例，很少使用。
     */

}