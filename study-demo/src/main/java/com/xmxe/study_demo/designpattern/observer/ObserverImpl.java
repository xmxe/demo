package com.xmxe.study_demo.designpattern.observer;

import java.util.Observable;
import java.util.Observer;

/**
 * 观察者模式 ：观察者
 */
public class ObserverImpl implements Observer{

    private String name;

    public ObserverImpl(String name){this.name = name;}

    public String getName(){
        return name;
    }
    public void setName(String name){
        this.name = name;
    }

    /**
     * 当被观察者发生改变通知时执行此方法
     * @param o 被观察者实例
     * @param arg 调用notifyObservers方法时传递的参数
     */
    @Override
    public void update(Observable o, Object arg) {
        TheObserved theObserved = (TheObserved)o;
        theObserved.noodles(String.format("%s----%s",arg,name));
    }
    
}
