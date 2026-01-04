package com.xmxe.jdkfeature.enums;

import lombok.Getter;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Getter
public enum EnergyTypeEnum {
	HD("hd", "煤电"),
	FD("fd", "风电"),
	GF("gf", "光伏"),
	SD("sd", "水电"),
	WD("wd", "外电"),
	CLEAN("clean","清洁能源"),
	HD_TYPE("1", "煤电"),
	FD_TYPE("2", "风电"),
	GF_TYPE("3", "光伏"),
	SD_TYPE("4", "水电"),
	WD_TYPE("5", "外电");

	private final String code;
	private final String name;

	EnergyTypeEnum(String code, String name) {
		this.code = code;
		this.name = name;
	}

	/**
	 * 根据 code 获取对应的名称
	 */
	public static String getNameByCode(String code) {
		return Arrays.stream(values())
				.filter(type -> type.getCode().equals(code))
				.map(EnergyTypeEnum::getName)
				.findFirst()
				.orElse(code); // 如果找不到就返回原值
	}

	/**
	 * 获取所有支持的 code 列表
	 */
	public static List<String> getAllCodes() {
		return Arrays.stream(values())
				.map(EnergyTypeEnum::getCode)
				.collect(Collectors.toList());
	}
}