package com.xmxe.study_demo.socket;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Java网络编程之socket的用法与实现 https://blog.csdn.net/a78270528/article/details/80318571
 */
public class SocketServer {

	public static void main(String[] args) throws Exception {
		// 基础模式
		basicModeSocketServer();
		// 双向模式
		twoWayCommunicationServer();
		// 多线程
		twoWayCommunicationServerUseThread();
		
		bufferSocketServer();
	}

	/**
     * 传统方式新建socket服务端
     */
    public void traditionIOReadSocketServer() {
        ServerSocket serverSocket = null;
        InputStream in = null;
        try {
            serverSocket = new ServerSocket(8080);
            int recvMsgSize = 0;
            byte[] recvBuf = new byte[1024];
            while (true) {
				Socket clntSocket = serverSocket.accept();
				// 获取服务端地址
                SocketAddress clientAddress = clntSocket.getRemoteSocketAddress();
                System.out.println("Handling client at " + clientAddress);
                in = clntSocket.getInputStream();
                while ((recvMsgSize = in.read(recvBuf)) != -1) {
                    byte[] temp = new byte[recvMsgSize];
                    System.arraycopy(recvBuf, 0, temp, 0, recvMsgSize);
                    System.out.println(new String(temp));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (serverSocket != null) {
                    serverSocket.close();
                }
                if (in != null) {
                    in.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
	}
	
	/**
	 * 基础模式
	 */
	public static void basicModeSocketServer() throws Exception{
		// 监听指定的端口
		int port = 55533;
		ServerSocket server = new ServerSocket(port);
		
		// server将一直等待连接的到来
		System.out.println("server将一直等待连接的到来");
		Socket socket = server.accept();
		// 建立好连接后，从socket中获取输入流，并建立缓冲区进行读取
		InputStream inputStream = socket.getInputStream();
		byte[] bytes = new byte[1024];
		int len;
		StringBuilder sb = new StringBuilder();
		while ((len = inputStream.read(bytes)) != -1) {
		  //注意指定编码格式，发送方和接收方一定要统一，建议使用UTF-8
		  sb.append(new String(bytes, 0, len,"UTF-8"));
		}
		System.out.println("get message from client: " + sb);
		inputStream.close();
		socket.close();
		server.close();
	}

	/**
	 * 双向通信，发送消息并接受消息
	 */
	public static void twoWayCommunicationServer(){
		// 监听指定的端口
		int port = 55533;
		try(ServerSocket server = new ServerSocket(port)){
			// server将一直等待连接的到来
			System.out.println("server将一直等待连接的到来");
			
			while(true){
				Socket socket = server.accept();
				// 建立好连接后，从socket中获取输入流，并建立缓冲区进行读取
				InputStream inputStream = socket.getInputStream();
				byte[] bytes = new byte[1024];
				int len;
				StringBuilder sb = new StringBuilder();
				while ((len = inputStream.read(bytes)) != -1) {
					// 注意指定编码格式，发送方和接收方一定要统一，建议使用UTF-8
					sb.append(new String(bytes, 0, len, "UTF-8"));
				}
				System.out.println("get message from client: " + sb);
				BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
				bw.write("mess"+"\n");
				bw.flush();
				inputStream.close();
				socket.close();
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		
	}

	/**
	 * 使用多线程
	 */
	public static void twoWayCommunicationServerUseThread(){
		// 监听指定的端口
		int port = 55533;
		try(ServerSocket server = new ServerSocket(port)){
			// server将一直等待连接的到来
			System.out.println("server将一直等待连接的到来");
		
			//如果使用多线程，那就需要线程池，防止并发过高时创建过多线程耗尽资源
			ExecutorService threadPool = Executors.newFixedThreadPool(100);
			
			while (true) {
				Socket socket = server.accept();
				Runnable runnable=()->{
					try {
						// 建立好连接后，从socket中获取输入流，并建立缓冲区进行读取
						InputStream inputStream = socket.getInputStream();
						byte[] bytes = new byte[1024];
						int len;
						StringBuilder sb = new StringBuilder();
						while ((len = inputStream.read(bytes)) != -1) {
							// 注意指定编码格式，发送方和接收方一定要统一，建议使用UTF-8
							sb.append(new String(bytes, 0, len, "UTF-8"));
						}
						System.out.println("get message from client: " + sb);
						BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
						bw.write("mess"+"\n");
						bw.flush();
						inputStream.close();
						socket.close();
					} catch (Exception e) {
						e.printStackTrace();
					}
				};
				threadPool.submit(runnable);
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		
	}

	/**
	 * buffer socket
	 */
	public static void bufferSocketServer(){
		try (ServerSocket ss = new ServerSocket(8888)){
			System.out.println("启动服务器....");
			Socket s = ss.accept();
			// InetAddress in =  s.getInetAddress();
			// System.out.println("客户端:"+in.getLocalHost()+"已连接到服务器");
			System.out.println("客户端:"+InetAddress.getLocalHost()+"已连接到服务器");
			
			BufferedReader br = new BufferedReader(new InputStreamReader(s.getInputStream()));
			//读取客户端发送来的消息
			String mess = br.readLine();
			System.out.println("客户端："+mess);
			BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(s.getOutputStream()));
			bw.write(mess+"\n");
			bw.flush();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
}
