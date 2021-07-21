package com.xmxe.study_demo.designpattern.factory.abstract_factory;

import lombok.Data;
import lombok.ToString;

/**
 * 商户
 */
@Data
@ToString(callSuper = true)
public class AgentExt extends CustomerExt {

    /**
     * 来源
     */
    private String source;

    /**
     * 资质
     */
    private String certification;

}
