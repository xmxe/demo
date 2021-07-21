
package com.xmxe.study_demo.designpattern.factory.simple_factory;

import lombok.Data;
import lombok.ToString;

/**
 * 银行客户
 */
@Data
@ToString(callSuper = true)
public class BankPartner extends Customer {

    /**
     * 银行编码
     */
    private String code;

    /**
     * 银行地址
     */
    private String address;

    public BankPartner(String name, String type) {
        super(name, type);
    }
}