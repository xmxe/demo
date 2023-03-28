package com.xmxe.study_demo.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class PropertiesUtil {
	private static Properties config = null;

	static {
		InputStream in = PropertiesUtil.class.getClassLoader()
				.getResourceAsStream("db.properties");
		config = new Properties();
		try {
			config.load(in);
			in.close();
		} catch (IOException e) {
			System.out.println("No db.properties defined error");
		}
	}

	// 根据key读取value
	public static String readValue(String key) {
		// Properties props = new Properties();
		try {
			String value = config.getProperty(key, "");
			return value;
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("ConfigInfoError" + e.toString());
			return null;
		}
	}

	public static String readValue(String key, String defaultVal) {
		String value = readValue(key);
		if (value == null || value.length() == 0) {
			value = defaultVal;
		}
		return value;
	}

	// 获取prop实例
	public static Properties getInstance() {
		return config;
	}

}
