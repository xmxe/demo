package com.xmxe.study_demo.designpattern.eventlisten;

public class EventListenerTest {
    public static void main(String[] args) {
        EventSourceObject object = new EventSourceObject();
        // 注册监听器
        object.addCusListener(new CusEventListener() {
            @Override
            public void fireCusEvent(CusEvent e) {
                super.fireCusEvent(e);
            }
        });
        // 触发事件
        object.setName("AiLu");
    
    }
}
/**
 * 1. 事件
 * 事件一般继承自java.util.EventObject类，封装了事件源对象及跟事件相关的信息。
 * 2. 事件源
 * 事件源是事件发生的地方，由于事件源的某项属性或状态发生了改变(比如BUTTON被单击、TEXTBOX的值发生改变等等)导致某项事件发生。换句话说就是生成了相应的事件对象。因为事件监听器要注册在事件源上，所以事件源类中应该要有盛装监听器的容器(List、Set等等)。
 * 3. 事件监听器
 * 事件监听器实现java.util.EventListener接口，注册在事件源上，当事件源的属性或状态改变时，取得相应的监听器调用其内部的回调方法。
 * 事件、事件源、监听器三者之间的联系
 * 事件源-----产生----->事件------>被事件监听器发现------>进入事件处理代码
 */
