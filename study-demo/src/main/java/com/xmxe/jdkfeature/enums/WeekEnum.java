package com.xmxe.jdkfeature.enums;

public enum WeekEnum {
	// MON, TUE, WED, THU, FRI, SAT, SUN;

	// 自定义属性和方法
	MON(1) {
		@Override
		public double eval(double x, double y) {
			return 1;
		}
	},
	TUE(2) {
		@Override
		public double eval(double x, double y) {
			return 2;
		}
	},
	WED(3) {
		@Override
		public double eval(double x, double y) {
			return 3;
		}
	},
	THU(4) {
		@Override
		public double eval(double x, double y) {
			return 4;
		}
	},
	FRI(5) {
		@Override
		public double eval(double x, double y) {
			return 5;
		}
	},
	SAT(6) {
		@Override
		public boolean isRest() {
			return true;
		}

		@Override
		public double eval(double x, double y) {
			System.out.println(1);
			return x * y;
		}
	},
	SUN(0) {
		@Override
		public boolean isRest() {
			return true;
		}

		@Override
		public double eval(double x, double y) {
			System.out.println(2);
			return x + y;
		}
	};

	private int value;

	private WeekEnum(int value) {
		this.value = value;
	}

	public int getValue() {
		return value;
	}

	public boolean isRest() {
		return false;
	}

	public static final WeekEnum[] _table = new WeekEnum[7];
	static {
		for (WeekEnum c : values()) {
			_table[c.getValue()] = c;
		}
	}

	/**
	 * 抽象方法,由不同的枚举值提供不同的实现。
	 * 
	 * @param x
	 * @param y
	 * @return
	 */
	public abstract double eval(double x, double y);
}
