package com.xmxe.study_demo.designpattern.factory.simple_factory;

import lombok.Data;
import lombok.ToString;

/**
 * 代理商
 */
@Data
@ToString(callSuper = true)
public class Agent extends Customer {

    /**
     * 代理周期
     */
    private int period;

    /**
     * 代理产品
     */
    private int[] products;

    public Agent(String name, String type) {
        super(name, type);
    }
}
