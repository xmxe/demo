package com.xmxe.study_demo.designpattern.adapter;

/**
 * 适配器模式,就是将一个类的接口转换成客户希望的另一个接口，使接口不兼容的类可以一起工作，也被称为包装器模式（Wrapper）。
 * 巧妙的运用适配器模式，让你的工作量至少减轻一半 https://mp.weixin.qq.com/s/IRCnLajfpHU5bQCo9HQxUg
 */
public class PlayerClient {
    public static void main(String[] args) {
        //对象适配
        ExpensiveAdapter adapter = new ExpensiveAdapter(new HuaweiPhone());
        adapter.action();
    }
}


interface Phone {
    void productPhone();
}

interface Player{
    void action();
}

class HuaweiPhone implements Phone{

    @Override
    public void productPhone() {
        System.out.println("生产一部华为手机");        
    }
    
}
class ExpensiveAdapter implements Player{

    private Phone phone;

    public ExpensiveAdapter(Phone phone) {
        this.phone = phone;
    }


    @Override
    public void action() {
        
        //调用HuaweiPhone中的productPhone方法
        phone.productPhone();
        System.out.println("用手机播放音乐");
    }
}
