package com.xmxe.study_demo.designpattern.factory.abstract_factory;

import lombok.Data;
import lombok.ToString;

/**
 * 商户
 */
@Data
@ToString(callSuper = true)
public class MerchantExt extends CustomerExt {

    /**
     * 介绍人
     */
    private int introduceName;

    /**
     * 介绍人电话
     */
    private String introduceTel;

}
