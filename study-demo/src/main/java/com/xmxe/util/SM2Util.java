package com.xmxe.util;

import org.bouncycastle.crypto.engines.SM2Engine;
import org.bouncycastle.crypto.params.ECPrivateKeyParameters;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.crypto.util.PrivateKeyFactory;
import org.bouncycastle.util.encoders.Hex;

import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Arrays;
import java.util.Base64;

public class SM2Util {

	static {
		Security.addProvider(new BouncyCastleProvider());
	}

	public static KeyPair generateSM2KeyPair() throws Exception {
		KeyPairGenerator keyGen = KeyPairGenerator.getInstance("EC", "BC");
		keyGen.initialize(new java.security.spec.ECGenParameterSpec("sm2p256v1"));
		return keyGen.generateKeyPair();
	}

	private static String getUncompressedPubKeyHex(PublicKey pubKey) {
		// System.out.println("公钥类型: " + pubKey.getClass().getName());
		java.security.interfaces.ECPublicKey ecPub = (java.security.interfaces.ECPublicKey) pubKey;
		java.security.spec.ECPoint w = ecPub.getW();
		return "04"
				+ paddedHex(w.getAffineX().toByteArray())
				+ paddedHex(w.getAffineY().toByteArray());
	}

	private static String paddedHex(byte[] bytes) {
		String s = new java.math.BigInteger(1, bytes).toString(16);
		while (s.length() < 64) s = "0" + s;
		return s;
	}

	/** 获取私钥的PKCS#8 DER编码（Base64）—— 可直接硬编码复用 */
	public static String getPrivateKeyPkcs8Base64(PrivateKey privateKey) {
		return java.util.Base64.getEncoder().encodeToString(privateKey.getEncoded());
	}

	public static String decryptFromFrontend(String encryptedHex, PrivateKey privateKey) throws Exception {
		byte[] cipherBytes = Hex.decode(encryptedHex);
		// 根据第一个字节判断格式
		int firstByte = cipherBytes[0] & 0xFF;

		if (firstByte == 0x04) {
			System.out.println("检测到未压缩格式 (04开头) - 使用C1C3C2转C1C2C3");
			return decryptC1C3C2(cipherBytes, privateKey);
		} else if (firstByte == 0x02 || firstByte == 0x03) {
			System.out.println("检测到压缩格式 (" + String.format("%02x", firstByte) + "开头) - 直接解密");
			return decryptCompressed(cipherBytes, privateKey);
		} else {
			System.out.println("检测到未知格式，尝试自动处理");
			return tryAllFormats(cipherBytes, privateKey);
		}
	}

	// C1C3C2 → C1C2C3 转换
	public static byte[] convertC1C3C2ToC1C2C3(byte[] c1c3c2) {
		int c1Len = 65; // 未压缩点：0x04 + 32x + 32y
		int c3Len = 32; // SM3 digest (32字节)
		int totalLen = c1c3c2.length;
		int c2Len = totalLen - c1Len - c3Len;

		// 验证长度
		if (totalLen < c1Len + c3Len) {
			throw new IllegalArgumentException("密文长度无效: " + totalLen + "，至少需要 " + (c1Len + c3Len) + " 字节");
		}

		System.out.println("C1C3C2转换详情:");
		System.out.println("  - 总长度: " + totalLen);
		System.out.println("  - C1长度: " + c1Len);
		System.out.println("  - C3长度: " + c3Len);
		System.out.println("  - C2长度: " + c2Len);

		// 分割各部分
		byte[] c1 = Arrays.copyOfRange(c1c3c2, 0, c1Len);           // 公钥点
		byte[] c3 = Arrays.copyOfRange(c1c3c2, c1Len, c1Len + c3Len); // SM3哈希
		byte[] c2 = Arrays.copyOfRange(c1c3c2, c1Len + c3Len, totalLen); // 实际加密数据

		// 重组为 C1C2C3
		byte[] c1c2c3 = new byte[totalLen];
		System.arraycopy(c1, 0, c1c2c3, 0, c1Len);           // C1
		System.arraycopy(c2, 0, c1c2c3, c1Len, c2Len);       // C2
		System.arraycopy(c3, 0, c1c2c3, c1Len + c2Len, c3Len); // C3

		System.out.println("C1C3C2 → C1C2C3 转换完成");
		return c1c2c3;
	}

	// 反向转换：C1C2C3 → C1C3C2
	public static byte[] convertC1C2C3ToC1C3C2(byte[] c1c2c3) {
		int c1Len = 65; // 未压缩点
		int c3Len = 32; // SM3 digest
		int totalLen = c1c2c3.length;
		int c2Len = totalLen - c1Len - c3Len;

		if (totalLen < c1Len + c3Len) {
			throw new IllegalArgumentException("密文长度无效: " + totalLen);
		}

		byte[] c1 = Arrays.copyOfRange(c1c2c3, 0, c1Len);
		byte[] c2 = Arrays.copyOfRange(c1c2c3, c1Len, c1Len + c2Len);
		byte[] c3 = Arrays.copyOfRange(c1c2c3, c1Len + c2Len, totalLen);

		byte[] c1c3c2 = new byte[totalLen];
		System.arraycopy(c1, 0, c1c3c2, 0, c1Len);
		System.arraycopy(c3, 0, c1c3c2, c1Len, c3Len);
		System.arraycopy(c2, 0, c1c3c2, c1Len + c3Len, c2Len);

		return c1c3c2;
	}

	// 未压缩格式解密（04开头）
	private static String decryptC1C3C2(byte[] cipherBytes, PrivateKey privateKey) throws Exception {
		// C1C3C2 → C1C2C3
		byte[] c1c2c3 = convertC1C3C2ToC1C2C3(cipherBytes);

		ECPrivateKeyParameters privKeyParam = (ECPrivateKeyParameters)
				PrivateKeyFactory.createKey(privateKey.getEncoded());

		// 使用C1C2C3模式解密
		SM2Engine engine = new SM2Engine(SM2Engine.Mode.C1C2C3);
		engine.init(false, privKeyParam);

		byte[] plainBytes = engine.processBlock(c1c2c3, 0, c1c2c3.length);
		return new String(plainBytes, StandardCharsets.UTF_8);
	}

	// 压缩格式解密（02或03开头）
	private static String decryptCompressed(byte[] cipherBytes, PrivateKey privateKey) throws Exception {
		ECPrivateKeyParameters privKeyParam = (ECPrivateKeyParameters) PrivateKeyFactory.createKey(privateKey.getEncoded());

		// 压缩格式通常直接使用C1C2C3模式
		SM2Engine engine = new SM2Engine(SM2Engine.Mode.C1C2C3);
		engine.init(false, privKeyParam);

		byte[] plainBytes = engine.processBlock(cipherBytes, 0, cipherBytes.length);
		return new String(plainBytes, StandardCharsets.UTF_8);
	}

	// 尝试所有可能的格式
	private static String tryAllFormats(byte[] cipherBytes, PrivateKey privateKey) {
		String[] methods = {
				"原始数据(C1C2C3)",
				"原始数据(C1C3C2)",
				"添加04前缀(C1C2C3)",
				"添加04前缀并转C1C2C3"
		};

		byte[][] testData = {
				cipherBytes, // 原始
				cipherBytes, // 原始（不同模式）
				addPrefix(cipherBytes, (byte) 0x04), // 加04
				convertC1C3C2ToC1C2C3(addPrefix(cipherBytes, (byte) 0x04)) // 加04并转换
		};

		SM2Engine.Mode[] modes = {
				SM2Engine.Mode.C1C2C3,
				SM2Engine.Mode.C1C3C2,
				SM2Engine.Mode.C1C2C3,
				SM2Engine.Mode.C1C2C3
		};

		for (int i = 0; i < methods.length; i++) {
			try {
				ECPrivateKeyParameters privKeyParam = (ECPrivateKeyParameters)
						PrivateKeyFactory.createKey(privateKey.getEncoded());

				SM2Engine engine = new SM2Engine(modes[i]);
				engine.init(false, privKeyParam);

				byte[] plainBytes = engine.processBlock(testData[i], 0, testData[i].length);
				String result = new String(plainBytes, StandardCharsets.UTF_8);

				System.out.println("✓ " + methods[i] + " 解密成功: " + result);
				return result;
			} catch (Exception e) {
				// e.printStackTrace();
				System.out.println("✗ " + methods[i] + " 失败: " + e.getMessage());
			}
		}

		throw new RuntimeException("所有解密尝试都失败");
	}

	private static byte[] addPrefix(byte[] data, byte prefix) {
		byte[] result = new byte[data.length + 1];
		result[0] = prefix;
		System.arraycopy(data, 0, result, 1, data.length);
		return result;
	}

	/** 从Base64加载私钥 */
	public static PrivateKey loadPrivateKeyFromBase64(String pkcs8Base64) throws Exception {
		byte[] pkcs8 = Base64.getDecoder().decode(pkcs8Base64);
		PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(pkcs8);
		KeyFactory keyFactory = KeyFactory.getInstance("EC", "BC");
		return keyFactory.generatePrivate(spec);
	}

	/**
	 * 测试方法
	 */
	public static void main(String[] args) throws Exception {
		// KeyPair keyPair = generateSM2KeyPair();
		// String frontPublicKey = getUncompressedPubKeyHex(keyPair.getPublic());
		// String privateKeyBase64 = getPrivateKeyPkcs8Base64(keyPair.getPrivate());
		// System.out.println("前端公钥: " + frontPublicKey);
		// System.out.println("后端私钥: " + privateKeyBase64);

		// 测试数据
		String encryptedHex = "d7d48e6497ee58636cdb83d43c54697079e668ffcddc23c94f9788e8247d4b1577ceec19dd80de587fdc23d4ed024f0acc676c6f449576eb5a95135c5bb7e8a44c7507b5385392529d02cf5a9892a586f4e6c251b74f36ba7bd1131abf1c29dae094e18d6c4f334bb719f2874627f242";
		String privateKeyBase64 = "MIGTAgEAMBMGByqGSM49AgEGCCqBHM9VAYItBHkwdwIBAQQgDSd+qEfDfdciXKEODn9fN5c7CfO6VsHlL765JEnePnmgCgYIKoEcz1UBgi2hRANCAAQFxSaO5eXZtgUKiRq383CDUvanY+7kmvOD/tMTajbOO7nmEqYOij2GWo9uN3mNgus7uy18isn39jQU/SeSBjbr";

		System.out.println("密文长度: " + encryptedHex.length());
		System.out.println("密文前20字符: " + encryptedHex.substring(0, 20));

		try {
			PrivateKey privateKey = loadPrivateKeyFromBase64(privateKeyBase64);
			String decryptedText = decryptFromFrontend(encryptedHex, privateKey);
			System.out.println("解密成功! 结果: " + decryptedText);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	// <script src="https://cdn.jsdelivr.net/npm/sm-crypto@0.3.2/dist/sm2.min.js"></script>
	// let currentPublicKey = "0405c5268ee5e5d9b6050a891ab7f3708352f6a763eee49af383fed3136a36ce3bb9e612a60e8a3d865a8f6e37798d82eb3bbb2d7c8ac9f7f63414fd27920636eb";
	// const encryptData = sm2.doEncrypt(plainText, currentPublicKey);
}