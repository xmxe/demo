package com.xmxe.study_demo.designpattern.observer;

// import java.util.Observable;
// import java.util.Observable;
// import java.util.Observer;

/**
 * jdk9之后过时 原因1、不能序列化 2、不是线程安全 3、支持事件模型的功能简单
 * 弃用原因：
 * 他们没有为应用程序提供足够丰富的事件模型。例如，他们只能支持某些更改的概念，但不传达有关更改的任何信息。
 * 不可序列化-因为，Observable不实现可序列化。因此，您既不能序列化Observable也不能对其子类进行序列化。
 * 没有线程安全-方法可以被其子类覆盖，并且事件通知可以以不同的顺序发生，并且可能在不同的线程上发生，这足以破坏任何“线程安全”。
 * 并且大多数问题都具有修复的复杂性，但仍然“未解决”或没有积极的发展，这就是不推荐使用的原因。
 * 观察者模式的替代方案：对于更丰富的事件模型，请考虑使用该java.beans
 * 程序包。为了在线程之间进行可靠且有序的消息传递，请考虑使用java.util.concurrent程序包中的并发数据结构之一
 * 有关反应式流样式的编程，请参见Flow API。
 */

public class ObserverTest {
    public static void main(String[] args) {

        // TheObserved theObserved = new TheObserved();
        // ObserverImpl observerImpl1 = new ObserverImpl("1");
        // ObserverImpl observerImpl2 = new ObserverImpl("2");

        // theObserved.addObserver(observerImpl1);
        // theObserved.addObserver(observerImpl2);

        // theObserved.eat();
    }

    /**
     * 观察者模式：被观察者 当此类发生变化进行通知
     */
    // static class TheObserved extends Observable{

        /**
         * 定义一个被观察者方法 当执行这个方法通知到观察者
         */
        // public void eat(){
        //     System.out.println("开始执行eat方法,准备通知观察者...");
        //     // 改变状态
        //     this.setChanged();
        //     // 通知所有观察者
        //     this.notifyObservers("this is args");
        // }

        /**
         * 当执行eat方法时 在观察者回调方法里面调用此方法
         *
         */
    //     public void noodles(Object args){
    //         System.out.println(args);
    //     }
    // }

    /**
     * 观察者模式 ：观察者
     */
    // static class ObserverImpl implements Observer{

    //     private String name;

    //     public ObserverImpl(String name){this.name = name;}

    //     public String getName(){
    //         return name;
    //     }
    //     public void setName(String name){
    //         this.name = name;
    //     }

        /**
         * 当被观察者发生改变通知时执行此方法
         * @param o 被观察者实例
         * @param arg 调用notifyObservers方法时传递的参数
         */
        
    //     @Override
    //     public void update(Observable o, Object arg) {
    //         TheObserved theObserved = (TheObserved)o;
    //         theObserved.noodles(String.format("%s----%s",arg,name));
    //     }
        
    // }
}


/**
 * void addObserver(Observer o)  如果观察者与集合中已有的观察者不同，则向对象的观察者集中添加此观察者。 
 * protected void clearChanged()  指示对象不再改变，或者它已对其所有的观察者通知了最近的改变，所以 hasChanged 方法将返回 false。 
 * int countObservers()  返回 Observable 对象的观察者数目。 
 * void deleteObserver(Observer o) 从对象的观察者集合中删除某个观察者。 
 * void deleteObservers()  清除观察者列表，使此对象不再有任何观察者。 
 * boolean hasChanged()  测试对象是否改变。 
 * void notifyObservers()  如果 hasChanged 方法指示对象已改变，则通知其所有观察者，并调用 clearChanged 方法来指示此对象不再改变。 
 * void notifyObservers(Object arg)  如果 hasChanged 方法指示对象已改变，则通知其所有观察者，并调用 clearChanged 方法来指示此对象不再改变。 
 * protected void setChanged()  标记此 Observable 对象为已改变的对象；现在执行 hasChanged 方法将返回 true。hasChanged()为true才会通知观察者数据有变化，并且在通知完成之后调用clearChanged()修改hasChanged()为false，所以当主题数据改变时，需要先调用setChanged()方法使hasChanged为true
 */
