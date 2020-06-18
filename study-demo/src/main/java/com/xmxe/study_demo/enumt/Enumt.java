package com.xmxe.study_demo.enumt;

import java.util.EnumMap;
import java.util.EnumSet;
import java.util.Iterator;
import java.util.Map.Entry;

import org.junit.Test;

public class Enumt {

	@Test
	public void test1() {
		for (MonEnum e : MonEnum.values()) {
			System.out.println("枚举.values()返回数组遍历后的为"+e.toString());
		}

		MonEnum test = MonEnum.TUE;
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

	@Test
	public void test2() {
		MonEnum test = MonEnum.TUE;
		// compareTo(E o) 比较此枚举与指定对象的顺序。
		switch (test.compareTo(MonEnum.MON)) {
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

		// getDeclaringClass()返回与此枚举常量的枚举类型相对应的 Class 对象
		System.out.println("getDeclaringClass(): " + test.getDeclaringClass().getName());

		// name() 返回此枚举常量的名称，在其枚举声明中对其进行声明。
		// toString()返回枚举常量的名称，它包含在声明中。
		System.out.println("name(): " + test.name());
		System.out.println("toString(): " + test.toString());

		// ordinal()， 返回值是从 0 开始 返回枚举常量的序数（它在枚举声明中的位置，其中初始常量序数为零）
		System.out.println("ordinal(): " + test.ordinal());
	}

	@Test
	public void test3() {
		System.out.println("EnumTest.FRI getValue() = " + MonEnum.FRI.getValue());
		System.out.println("EnumTest.FRI isResr() = " + MonEnum.SUN.isRest());
		System.out.println("枚举valueOf()返回的是enum "+MonEnum.valueOf("SUN").getValue());
	}

	@Test
	public void test4() {
		EnumSet<MonEnum> weekSet = EnumSet.allOf(MonEnum.class);
		for (MonEnum day : weekSet) {
			System.out.println("枚举allOf"+day);
		}

		// EnumMap的使用
		EnumMap<MonEnum, String> weekMap = new EnumMap<>(MonEnum.class);
		weekMap.put(MonEnum.MON, "星期一");
		weekMap.put(MonEnum.TUE, "星期二");
		// ... ...
		for (Iterator<Entry<MonEnum, String>> iter = weekMap.entrySet().iterator(); iter.hasNext();) {
			Entry<MonEnum, String> entry = iter.next();
			System.out.println(entry.getKey().name() + ":" + entry.getValue());
		}
	}
	@Test
	public void test5() {
		/**
		 * 以前写法
		 */
		String str = "SUN";
		if(str.equals("SAT")) {
			System.out.println(1);
		}else if(str.equals("SUN")) {
			System.out.println(2);
		}else {
			System.out.println(3);
		}
		
		/**
		 * 枚举替代if else if写法  一行代码搞定  如果需要再加判断只需要增加枚举类属性即可
		 * valueOf()获取枚举类型
		 * https://mp.weixin.qq.com/s/GJBaopEqPMOhi2pGtenL9g
		 */
		MonEnum.valueOf(str).eval(1, 2);
	}
}
