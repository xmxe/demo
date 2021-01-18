package com.xmxe.study_demo.enums;

import java.util.EnumMap;
import java.util.EnumSet;
import java.util.Iterator;
import java.util.Map.Entry;


public class EnumTest {
	public static void main(String[] args) {
		EnumTest t = new EnumTest();
		t.test1();
		// t.test2();
		// t.test3();
		// t.test4();
		// t.test5();
		// t.test6();
	}

	/**
	 * values()
	 */
	public void test1() {
		for (WeekEnum e : WeekEnum.values()) {
			System.out.println("枚举.values()返回数组遍历后的为" + e.toString());
		}

		WeekEnum test = WeekEnum.TUE;
		switch (test) {
			case MON:
				System.out.println("今天是星期一");
				break;
			case TUE:
				System.out.println("今天是星期二");
				break;
			default:
				System.out.println(test);
				break;
		}
	}

	/**
	 * compareTo()
	 */
	public void test2() {
		WeekEnum test = WeekEnum.TUE;
		// compareTo(E o) 比较此枚举与指定对象的顺序。
		switch (test.compareTo(WeekEnum.MON)) {
			case -1:
				System.out.println("TUE 在 MON 之前");
				break;
			case 1:
				System.out.println("TUE 在 MON 之后");
				break;
			default:
				System.out.println("TUE 与 MON 在同一位置");
				break;
		}
	}

	/**
	 * enum methods
	 */
	public void test3() {
		WeekEnum test = WeekEnum.TUE;
		// getDeclaringClass()返回与此枚举常量的枚举类型相对应的 Class 对象
		System.out.println("getDeclaringClass(): " + test.getDeclaringClass().getName());

		// name() 返回此枚举常量的名称，在其枚举声明中对其进行声明。
		System.out.println("name(): " + test.name());
		// toString()返回枚举常量的名称，它包含在声明中。
		System.out.println("toString(): " + test.toString());

		// ordinal()， 返回值是从 0 开始 返回枚举常量的序数（它在枚举声明中的位置，其中初始常量序数为零）
		System.out.println("ordinal(): " + test.ordinal());
		System.out.println("EnumTest.FRI getValue() = " + WeekEnum.FRI.getValue());
		System.out.println("EnumTest.FRI isResr() = " + WeekEnum.SUN.isRest());
		System.out.println("枚举valueOf()返回的是enum " + WeekEnum.valueOf("SUN").getValue());
	}

	/**
	 * EnumSet and EnumMap
	 */
	public void test4() {
		EnumSet<WeekEnum> weekSet = EnumSet.allOf(WeekEnum.class);
		for (WeekEnum day : weekSet) {
			System.out.println("枚举allOf" + day);
		}

		// EnumMap的使用
		EnumMap<WeekEnum, String> weekMap = new EnumMap<>(WeekEnum.class);
		weekMap.put(WeekEnum.MON, "星期一");
		weekMap.put(WeekEnum.TUE, "星期二");
		// ... ...
		for (Iterator<Entry<WeekEnum, String>> iter = weekMap.entrySet().iterator(); iter.hasNext();) {
			Entry<WeekEnum, String> entry = iter.next();
			System.out.println(entry.getKey().name() + ":" + entry.getValue());
		}
	}
	/**
	 * 使用枚举替代if else
	 */
	public void test5() {
		/**
		 * 以前写法
		 */
		String str = "SUN";
		if (str.equals("SAT")) {
			System.out.println(1);
		} else if (str.equals("SUN")) {
			System.out.println(2);
		} else {
			System.out.println(3);
		}

		/**
		 * 枚举替代if else if写法 一行代码搞定 如果需要再加判断只需要增加枚举类属性即可 valueOf()获取枚举类型
		 */
		WeekEnum.valueOf(str).eval(1, 2);
	}

	/**
	 * 提供一个方法 根据枚举属性提供不同的实现
	 */
	public void test6(){
		AnimalEnum.PEOPLE.count(5);
		AnimalEnum.CAT.count(5);
		AnimalEnum.DOG.count(5);
	}

}
