package com.xmxe.jdkfeature.enums;

import java.util.Arrays;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.Iterator;
import java.util.Map.Entry;

public class EnumTest {
	public static void main(String[] args) {
		EnumTest t = new EnumTest();
		t.values();
		t.compareTo();
		t.methods();
		t.EnumSet_EnumMap();
		t.insteadIFELSE();
		t.override();
	}

	/**
	 * values()
	 */
	public void values() {
		for (WeekEnum e : WeekEnum.values()) {
			System.out.println("枚举.values()返回数组遍历后的为" + e.toString());
			// 枚举.values()返回数组遍历后的为MON
			// 枚举.values()返回数组遍历后的为TUE
			// 枚举.values()返回数组遍历后的为WED
			// 枚举.values()返回数组遍历后的为THU
			// 枚举.values()返回数组遍历后的为FRI
			// 枚举.values()返回数组遍历后的为SAT
			// 枚举.values()返回数组遍历后的为SUN
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
	public void compareTo() {
		WeekEnum test = WeekEnum.TUE;
		// compareTo(E o)比较此枚举与指定对象的顺序。
		switch (test.compareTo(WeekEnum.MON)) {
			case -1:
				System.out.println("TUE在MON之前");
				break;
			case 1:
				System.out.println("TUE在MON之后");
				break;
			default:
				System.out.println("TUE与MON在同一位置");
				break;
		}
	}

	/**
	 * enum methods
	 */
	public void methods() {
		WeekEnum test = WeekEnum.TUE;
		// getDeclaringClass()返回与此枚举常量的枚举类型相对应的Class对象
		System.out.println("getDeclaringClass(): " + test.getDeclaringClass().getName());// getDeclaringClass(): com.xmxe.study_demo.enums.WeekEnum

		// name()返回此枚举常量的名称,在其枚举声明中对其进行声明。
		System.out.println("name(): " + test.name()); // name(): TUE
		// toString()返回枚举常量的名称,它包含在声明中。
		System.out.println("toString(): " + test.toString());// toString(): TUE

		// ordinal(),返回值是从0开始,返回枚举常量的序数（它在枚举声明中的位置,其中初始常量序数为零）
		System.out.println("ordinal(): " + test.ordinal());// oridinal(): 1

		System.out.println("EnumTest.FRI getValue() = " + WeekEnum.FRI.getValue());// EnumTest.FRI getValue() = 5

		System.out.println("EnumTest.FRI isResr() = " + WeekEnum.SUN.isRest());// EnumTest.FRI isResr() = true
		WeekEnum w1 = WeekEnum.valueOf(WeekEnum.class, "TUE");
		WeekEnum w2 = WeekEnum.valueOf("TUE");
		System.out.println("w1==" + w1 + "===w2" + w2 + "===" + w1.compareTo(test) + "---" + w2.equals(w1));// w1==TUE===w2TUE===0---true
		System.out.println("枚举valueOf()返回的是enum " + WeekEnum.valueOf("SUN").getValue());// 枚举valueOf()返回的是enum 1

		System.out.println(Arrays.toString(WeekEnum._table));// [SUN, MON, TUE, WED, THU, FRI, SAT]

		System.out.println(EnergyTypeEnum.getNameByCode("6"));
	}

	/**
	 * EnumSet and EnumMap
	 */
	public void EnumSet_EnumMap() {
		EnumSet<WeekEnum> weekSet = EnumSet.allOf(WeekEnum.class);
		for (WeekEnum day : weekSet) {
			System.out.println("枚举allOf" + day);
			// 枚举allOfMON
			// 枚举allOfTUE
			// 枚举allOfWED
			// 枚举allOfTHU
			// 枚举allOfFRI
			// 枚举allOfSAT
			// 枚举allOfSUN

		}

		// EnumMap的使用
		EnumMap<WeekEnum, String> weekMap = new EnumMap<>(WeekEnum.class);
		weekMap.put(WeekEnum.MON, "星期一");
		weekMap.put(WeekEnum.TUE, "星期二");
		// ... ...
		for (Iterator<Entry<WeekEnum, String>> iter = weekMap.entrySet().iterator(); iter.hasNext();) {
			Entry<WeekEnum, String> entry = iter.next();
			System.out.println(entry.getKey().name() + ":" + entry.getValue());
			// MON:星期一
			// TUE:星期二
		}
	}

	/**
	 * 使用枚举替代if else
	 */
	public void insteadIFELSE() {
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
		 * 枚举替代if else一行代码搞定,如果需要再加判断只需要增加枚举类属性即可,valueOf()获取枚举类型
		 */
		WeekEnum.valueOf(str).eval(1, 2);
	}

	/**
	 * 提供一个方法,根据枚举属性提供不同的实现
	 */
	public void override() {
		AnimalEnum.PEOPLE.count(5);
		AnimalEnum.CAT.count(5);
		AnimalEnum.DOG.count(5);
	}

}
