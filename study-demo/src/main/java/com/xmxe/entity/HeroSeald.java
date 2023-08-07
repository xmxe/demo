package com.xmxe.entity;

/*
 * 详解Java 17中的新特性：“密封类”https://mp.weixin.qq.com/s/XRPI2WlaNGZ05n7Uw7FGGw
 */

// 通过sealed关键词和permitspermits关键来定义Hero是一个需要密封的类,并且它的子类只允许为TankHero,AttackHero,SupportHero这三个
public sealed class HeroSeald permits TankHeroSeald, AttackHeroSeald, SupportHeroSeald {

}

/**
 * 父类HeroSeald被sealed修饰之后,sealed的密封要求被传递过来,此时子类就必须在sealed、non-sealed、final之间选择一个定义,它们分别代表：
 * sealed：继续延续密封类特性,可以继续指定继承的类,并传递密封定义给子类
 * non-sealed：声明这个类为非密封类,可以被任意继承
 * final：不允许继承
 */
non-sealed class TankHeroSeald extends HeroSeald{

}
non-sealed class AttackHeroSeald extends HeroSeald {

}
final class SupportHeroSeald extends HeroSeald {

}