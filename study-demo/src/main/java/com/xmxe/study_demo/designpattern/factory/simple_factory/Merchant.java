package com.xmxe.study_demo.designpattern.factory.simple_factory;

import lombok.Data;
import lombok.ToString;

/**
 * 商户
 */
@Data
@ToString(callSuper = true)
public class Merchant extends Customer {

    /**
     * 合同类型
     */
    private int contractType;

    /**
     * 结算周期（天）
     */
    private int settmentDays;

    public Merchant(String name, String type) {
        super(name, type);
    }
}
