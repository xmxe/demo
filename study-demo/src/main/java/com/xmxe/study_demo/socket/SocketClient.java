package com.xmxe.study_demo.socket;

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

		bufferSocketClient();
	}

	/**
	 * 基础模式
	 */
	public static void basicModeSocketClient() throws Exception{
		// 要连接的服务端IP地址和端口
		String host = "127.0.0.1"; 
		int port = 55533;
		// 与服务端建立连接
		Socket socket = new Socket(host, port);
		// 建立连接后获得输出流
		OutputStream outputStream = socket.getOutputStream();
		String message="你好  yiwangzhibujian";
		socket.getOutputStream().write(message.getBytes("UTF-8"));
		outputStream.close();
		socket.close();
	}
	/**
	 * 双向通信，发送消息并接受消息
	 * 与之前server的不同在于，当读取完客户端的消息后，打开输出流，将指定消息发送回客户端
	 */
	public static void twoWayCommunicationClient() throws Exception{
		// 要连接的服务端IP地址和端口
		String host = "127.0.0.1";
		int port = 55533;
		// 与服务端建立连接
		Socket socket = new Socket(host, port);
		// 建立连接后获得输出流
		OutputStream outputStream = socket.getOutputStream();
		String message = "你好  yiwangzhibujian";
		outputStream.write(message.getBytes("UTF-8"));
		//通过shutdownOutput告诉服务器已经发送完数据，后续只能接受数据
		socket.shutdownOutput();
		
		InputStream inputStream = socket.getInputStream();
		byte[] bytes = new byte[1024];
		int len;
		StringBuilder sb = new StringBuilder();
		while ((len = inputStream.read(bytes)) != -1) {
		  //注意指定编码格式，发送方和接收方一定要统一，建议使用UTF-8
		  sb.append(new String(bytes, 0, len,"UTF-8"));
		}
		System.out.println("get message from server: " + sb);
		
		inputStream.close();
		outputStream.close();
		socket.close();
	
	}

	/**
	 * buffer socket client
	 */
	public static void bufferSocketClient(){
		Socket socket = null;
		try {
			socket = new Socket("127.0.0.1",8888);
			//构建IO
			InputStream is = socket.getInputStream();
			OutputStream os = socket.getOutputStream();
			
			BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(os));
			//向服务器端发送一条消息
			bw.write("测试客户端和服务器通信，服务器接收到消息返回到客户端\n");
			bw.flush();
			
			//读取服务器返回的消息
			BufferedReader br = new BufferedReader(new InputStreamReader(is));
			String mess = br.readLine();
			System.out.println("服务器："+mess);
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			if(socket != null){
				try {
					socket.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

	}

}
