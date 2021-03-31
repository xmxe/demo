package com.xmxe.study_demo.designpattern.observer;

public class ObserverTest {
    public static void main(String[] args) {
        TheObserved theObserved = new TheObserved();
        ObserverImpl observerImpl1 = new ObserverImpl("1");
        ObserverImpl observerImpl2 = new ObserverImpl("2");

        theObserved.addObserver(observerImpl1);
        theObserved.addObserver(observerImpl2);

        theObserved.eat();
    }
}
