package com.xmxe.designpattern.eventlisten;

import java.util.Set;
import java.util.HashSet;
import java.util.Iterator;
import java.util.EventListener;
import java.util.EventObject;

/**
 * 监听模式
 */
public class EventListenerTest {
    public static void main(String[] args) {
        /**
         * 事件:事件一般继承自java.util.EventObject类,封装了事件源对象及跟事件相关的信息。
         * 事件源:事件源是事件发生的地方,由于事件源的某项属性或状态发生了改变(比如BUTTON被单击、TEXTBOX的值发生改变等等)导致某项事件发生。换句话说就是生成了相应的事件对象。因为事件监听器要注册在事件源上,所以事件源类中应该要有盛装监听器的容器(List、Set等等)。
         * 事件监听器:事件监听器实现java.util.EventListener接口,注册在事件源上,当事件源的属性或状态改变时,取得相应的监听器调用其内部的回调方法。
         * 
         * 事件、事件源、监听器三者之间的联系
         * 事件源-----产生----->事件------>被事件监听器发现------>进入事件处理代码
         */

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

    /**
     * 事件源.
     */
    static class EventSourceObject {

        private String name;

        // 监听器容器
        private Set<CusEventListener> listener;

        public EventSourceObject() {
            this.listener = new HashSet<CusEventListener>();
            this.name = "defaultname";
        }

        // 给事件源注册监听器
        public void addCusListener(CusEventListener cel) {
            this.listener.add(cel);
        }

        // 当事件发生时,通知注册在该事件源上的所有监听器做出相应的反应（调用回调方法）
        protected void notifies() {
            CusEventListener cel = null;
            Iterator<CusEventListener> iterator = this.listener.iterator();
            while (iterator.hasNext()) {
                cel = iterator.next();
                cel.fireCusEvent(new CusEvent(this));
            }
        }

        public String getName() {
            return name;
        }

        /**
         * 模拟事件触发器,当成员变量name的值发生变化时,触发事件。
         */
        public void setName(String name) {
            if (!this.name.equals(name)) {
                this.name = name;
                notifies();
            }
        }
    }

    /**
     * 事件监听器,实现java.util.EventListener接口。定义回调方法,将你想要做的事放到这个方法下,因为事件源发生相应的事件时会调用这个方法。
     */
    static class CusEventListener implements EventListener {
        // 事件发生后的回调方法
        public void fireCusEvent(CusEvent e) {
            EventSourceObject eObject = (EventSourceObject) e.getSource();
            System.out.println("My name has been changed!");
            System.out.println("I got a new name,named \"" + eObject.getName() + "\"");
        }
    }

    /**
     * 事件类,用于封装事件源及一些与事件相关的参数.
     */
    static class CusEvent extends EventObject {
        private static final long serialVersionUID = 1L;
        private Object source;// 事件源

        public CusEvent(Object source) {
            super(source);
            this.source = source;
        }

        public Object getSource() {
            return source;
        }

        public void setSource(Object source) {
            this.source = source;
        }
    }

}