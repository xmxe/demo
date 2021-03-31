package com.xmxe.study_demo.designpattern.eventlisten;

import java.util.EventObject;

/**
 * 事件类,用于封装事件源及一些与事件相关的参数.
 */
public class CusEvent extends EventObject{
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
