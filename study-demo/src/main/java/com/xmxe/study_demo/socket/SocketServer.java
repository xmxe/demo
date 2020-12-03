package com.xmxe.study_demo.socket;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SocketServer {

	public static void main(String[] args) throws Exception {

		/*
		//---基础模式 begin---
	    int port = 55533;// 监听指定的端口
	    ServerSocket server = new ServerSocket(port);
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
	    //---基础模式 end---
	    */
		
		/*
		//双向通信，发送消息并接受消息  start
	    int port = 55533;//监听指定的端口
	    ServerSocket server = new ServerSocket(port);
	    System.out.println("server将一直等待连接的到来");
	    Socket socket = server.accept();
	    //建立好连接后，从socket中获取输入流，并建立缓冲区进行读取
	    InputStream inputStream = socket.getInputStream();
	    byte[] bytes = new byte[1024];
	    int len;
	    StringBuilder sb = new StringBuilder();
	    //只有当客户端关闭它的输出流的时候，服务端才能取得结尾的-1
	    while ((len = inputStream.read(bytes)) != -1) {
	      // 注意指定编码格式，发送方和接收方一定要统一，建议使用UTF-8
	      sb.append(new String(bytes, 0, len, "UTF-8"));
	    }
	    System.out.println("get message from client: " + sb);
	 
	    OutputStream outputStream = socket.getOutputStream();
	    outputStream.write("Hello Client,I get the message.".getBytes("UTF-8"));
	 
	    inputStream.close();
	    outputStream.close();
	    socket.close();
	    server.close();
	    //双向通信，发送消息并接受消息 end
	    */

		/*在上面的例子中，服务端仅仅只是接受了一个Socket请求，并处理了它，然后就结束了，但是在实际开发中，
		一个Socket服务往往需要服务大量的Socket请求，那么就不能再服务完一个Socket的时候就关闭了，这时候可以采用循环接受请求并处理的逻辑*/
		/*
		 这种一般新手写法，但是能够循环处理多个Socket请求，不过当一个请求的处理比较耗时的时候，
		 后面的请求将被阻塞，所以一般都是用多线程的方式来处理Socket，即每有一个Socket请求的时候，就创建一个线程来处理它。
		不过在实际生产中，创建的线程会交给线程池来处理，为了线程复用，创建线程耗时，回收线程慢。
		防止短时间内高并发，指定线程池大小，超过数量将等待，方式短时间创建大量线程导致资源耗尽，服务挂掉。 
		 
	    int port = 55533;// 监听指定的端口
	    ServerSocket server = new ServerSocket(port);
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
	      inputStream.close();
	      socket.close();
	    }*/
		
		//使用线程池		  
	    int port = 55533;// 监听指定的端口
	    ServerSocket server = new ServerSocket(port);
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
	          inputStream.close();
	          socket.close();
	        } catch (Exception e) {
	          e.printStackTrace();
	        }
	      };
	      threadPool.submit(runnable);
	    }
	}

}
/**
 * [Java 网络编程 之 socket 的用法与实现](https://blog.csdn.net/a78270528/article/details/80318571)
 */