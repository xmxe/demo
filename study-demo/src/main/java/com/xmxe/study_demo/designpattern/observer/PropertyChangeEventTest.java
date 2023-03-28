package com.xmxe.study_demo.designpattern.observer;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

/**
 * 替代过时的Observer实现观察者模式
 */
public class PropertyChangeEventTest {
    public static void main(String[] args) {
        Event event = new Event();
        // 添加事件
        event.addPropertyChangeListener(new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                System.out.println("获取发生改变的变量的旧值 OldValue:" + evt.getOldValue());
                System.out.println("获取发生改变的变量的新值 NewValue:" + evt.getNewValue());
                System.out.println("获取发生改变的变量名 PropertyName:" + evt.getPropertyName());
                System.out.println("获取改变的bean对象 bean:" + evt.getSource());
            }
        });

        // 触发事件
        event.setName("new Name");
    }

    static class Event {

        private String name;
        private PropertyChangeSupport listeners = new PropertyChangeSupport(this);

        public Event() {
            this.name = "my name";
        }

        public String getName() {
            return this.name;
        }

        public void setName(String name) {
            String oldValue = this.name;
            this.name = name;
            // 发布事件
            firePropertyChange("name", oldValue, name);
        }

        public void addPropertyChangeListener(PropertyChangeListener listener) {
            listeners.addPropertyChangeListener(listener);
        }

        public void removePropertyChangeListener(PropertyChangeListener listener) {
            listeners.removePropertyChangeListener(listener);
        }

        protected void firePropertyChange(String prop, Object oldValue, Object newValue) {
            listeners.firePropertyChange(prop, oldValue, newValue);
        }
    }

}
