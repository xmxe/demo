package com.xmxe.study_demo.designpattern.observer;

import java.util.Observable;

/**
 * 观察者模式：被观察者 当此类发生变化进行通知
 */
public class TheObserved extends Observable{

    /**
     * 定义一个被观察者方法 当执行这个方法通知到观察者
     */
    public void eat(){
        System.out.println("开始执行eat方法,准备通知观察者...");
        // 改变状态
        this.setChanged();
        // 通知所有观察者
        this.notifyObservers("this is args");
    }
    /**
     * 当执行eat方法时 在观察者回调方法里面调用此方法
     *
     */
    public void noodles(Object args){
        System.out.println(args);
    }
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