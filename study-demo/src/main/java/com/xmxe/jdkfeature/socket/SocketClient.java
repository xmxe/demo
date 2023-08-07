package com.xmxe.jdkfeature.socket;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.Socket;

public class SocketClient {

	public static void main(String[] args) throws Exception {
		// 基础模式
		basicModeSocketClient();
		// 双向通讯
		twoWayCommunicationClient();
		// 如何告知对方已发送完命令
		symbolEndOfSocket();

		bufferSocketClient();
	}

	/**
	 * 基础模式
	 */
	public static void basicModeSocketClient() throws Exception {
		// 要连接的服务端IP地址和端口
		String host = "127.0.0.1";
		int port = 55533;
		// 与服务端建立连接
		Socket socket = new Socket(host, port);
		// 建立连接后获得输出流
		OutputStream outputStream = socket.getOutputStream();
		String message = "你好yiwangzhibujian";
		socket.getOutputStream().write(message.getBytes("UTF-8"));
		outputStream.close();
		socket.close();
	}


	/**
	 * 如何告知对方已发送完命令
	 */
	public static void symbolEndOfSocket() {
		// 要连接的服务端IP地址和端口
		String host = "127.0.0.1";
		int port = 55533;
		// 与服务端建立连接
		try (Socket socket = new Socket(host, port);) {
			// 建立连接后获得输出流
			OutputStream outputStream = socket.getOutputStream();
			String message = "你好  yiwangzhibujian";
			// 首先需要计算得知消息的长度
			byte[] sendBytes = message.getBytes("UTF-8");
			// 然后将消息的长度优先发送出去
			outputStream.write(sendBytes.length >> 8);
			outputStream.write(sendBytes.length);
			// 然后将消息再次发送出去
			outputStream.write(sendBytes);
			outputStream.flush();
			// ==========此处重复发送一次,实际项目中为多个命名,此处只为展示用法
			message = "第二条消息";
			sendBytes = message.getBytes("UTF-8");
			outputStream.write(sendBytes.length >> 8);
			outputStream.write(sendBytes.length);
			outputStream.write(sendBytes);
			outputStream.flush();
			// ==========此处重复发送一次,实际项目中为多个命名,此处只为展示用法
			message = "the third message!";
			sendBytes = message.getBytes("UTF-8");
			outputStream.write(sendBytes.length >> 8);
			outputStream.write(sendBytes.length);
			outputStream.write(sendBytes);

			outputStream.close();

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	/**
	 * 双向通信,发送消息并接受消息
	 * 与之前server的不同在于,当读取完客户端的消息后,打开输出流,将指定消息发送回客户端
	 */
	public static void twoWayCommunicationClient() throws Exception {
		// 要连接的服务端IP地址和端口
		String host = "127.0.0.1";
		int port = 55533;
		// 与服务端建立连接
		Socket socket = new Socket(host, port);
		// 建立连接后获得输出流
		OutputStream outputStream = socket.getOutputStream();
		String message = "你好  yiwangzhibujian";
		outputStream.write(message.getBytes("UTF-8"));
		// 通过shutdownOutput告诉服务器已经发送完数据,后续只能接受数据
		socket.shutdownOutput();

		InputStream inputStream = socket.getInputStream();
		byte[] bytes = new byte[1024];
		int len;
		StringBuilder sb = new StringBuilder();
		while ((len = inputStream.read(bytes)) != -1) {
			// 注意指定编码格式,发送方和接收方一定要统一,建议使用UTF-8
			sb.append(new String(bytes, 0, len, "UTF-8"));
		}
		System.out.println("get message from server: " + sb);

		inputStream.close();
		outputStream.close();
		socket.close();

	}

	/**
	 * buffer socket client
	 */
	public static void bufferSocketClient() {
		Socket socket = null;
		try {
			socket = new Socket("127.0.0.1", 8888);
			// 构建IO
			InputStream is = socket.getInputStream();
			OutputStream os = socket.getOutputStream();

			BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(os));
			// 向服务器端发送一条消息
			bw.write("测试客户端和服务器通信,服务器接收到消息返回到客户端\n");
			bw.flush();

			// 读取服务器返回的消息
			BufferedReader br = new BufferedReader(new InputStreamReader(is));
			String mess = br.readLine();
			System.out.println("服务器：" + mess);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (socket != null) {
				try {
					socket.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

	}

}
