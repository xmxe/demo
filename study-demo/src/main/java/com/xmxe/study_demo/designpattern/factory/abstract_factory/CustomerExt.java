package com.xmxe.study_demo.designpattern.factory.abstract_factory;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 客户扩展
 */
@Data
@NoArgsConstructor
public abstract class CustomerExt {

    /**
     * 客户曾用名
     */
    private String formerName;

    /**
     * 客户扩展说明
     */
    private String note;

}
