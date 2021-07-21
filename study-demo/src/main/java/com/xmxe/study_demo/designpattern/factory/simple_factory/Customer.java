package com.xmxe.study_demo.designpattern.factory.simple_factory;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 客户
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public abstract class Customer {

    /**
     * 客户名称
     */
    private String name;

    /**
     * 客户类型
     */
    private String type;

}
