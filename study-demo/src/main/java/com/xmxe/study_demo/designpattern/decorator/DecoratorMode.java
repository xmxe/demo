package com.xmxe.study_demo.designpattern.decorator;

/**
 * 巧妙的运用装饰器，让你的代码高出一个逼格！(https://mp.weixin.qq.com/s/8VM5PSbByoH5jgYg1bwVgA)
 * 如何优雅的使用装饰器模式(https://mp.weixin.qq.com/s/AeTusyoLesfowHFoaAOLRA)
 */
public class DecoratorMode {
    public static void main(String[] args) {
        Clothes clothes = new MakeClothes();
        clothes = new EmbroideryDecorator(clothes);//给衣服绣花
        clothes = new MickeyDecorator(clothes);//给衣服添加米老鼠图案
        clothes.makeClothes();
        System.out.println("成品已经完成！");
    }
}

/**
 * 建立基本骨架
 */
interface Clothes {
    void makeClothes();
}

/**
 * 被装饰类
 */
class MakeClothes implements Clothes {

    @Override
    public void makeClothes() {
        System.out.println("制作一件衣服");
    }
}

/**
 * 装饰类
 */
class Decorator implements Clothes {

    private Clothes clothes;

    public Decorator(Clothes clothes) {
        this.clothes = clothes;
    }

    @Override
    public void makeClothes() {
        clothes.makeClothes();
    }
}

/**
 * 具体的装饰类
 */
class EmbroideryDecorator extends Decorator {
    public EmbroideryDecorator(Clothes clothes) {
        super(clothes);
    }

    @Override
    public void makeClothes() {
        super.makeClothes();
        System.out.println("给衣服绣制花朵");
    }
}

/**
 * 具体的装饰类
 */
class MickeyDecorator extends Decorator {

    public MickeyDecorator(Clothes clothes) {
        super(clothes);
    }

    @Override
    public void makeClothes() {
        super.makeClothes();
        System.out.println("给衣服绘制米老鼠图案");
    }
}