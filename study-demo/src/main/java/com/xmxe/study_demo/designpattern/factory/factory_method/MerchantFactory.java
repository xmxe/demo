
package com.xmxe.study_demo.designpattern.factory.factory_method;

import com.xmxe.study_demo.designpattern.factory.simple_factory.Customer;
import com.xmxe.study_demo.designpattern.factory.simple_factory.Merchant;

/**
 * 商户工厂
 */
public class MerchantFactory implements CustomerFactory {

    @Override
    public Customer create(String type, String name) {
        return new Merchant(type, name);
    }

}
