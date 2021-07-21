package com.xmxe.study_demo.designpattern.factory.factory_method;

import com.xmxe.study_demo.designpattern.factory.simple_factory.Agent;
import com.xmxe.study_demo.designpattern.factory.simple_factory.Customer;

/**
 * 代理商工厂
 */
public class AgentFactory implements CustomerFactory {

    @Override
    public Customer create(String type, String name) {
        return new Agent(type, name);
    }

}