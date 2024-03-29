package com.xmxe.jdkfeature.socket;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousChannelGroup;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class SocketServer {

	public static void main(String[] args) throws Exception {
		// 基础模式
		basicModeSocketServer();
		// 双向模式
		twoWayCommunicationServer();
		// 告知对方已发送完命令
		symbolEndOfSocket();
		// 多线程
		twoWayCommunicationServerUseThread();

		bufferSocketServer();

		// BIO
		bioServer();
		// NIO
		nioServer();
		// AIO
		aioServer();
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
	public static void basicModeSocketServer() throws Exception {
		// 监听指定的端口
		int port = 55533;
		ServerSocket server = new ServerSocket(port);

		// server将一直等待连接的到来
		System.out.println("server将一直等待连接的到来");
		Socket socket = server.accept();
		// 建立好连接后,从socket中获取输入流,并建立缓冲区进行读取
		InputStream inputStream = socket.getInputStream();
		byte[] bytes = new byte[1024];
		int len;
		StringBuilder sb = new StringBuilder();
		while ((len = inputStream.read(bytes)) != -1) {
			// 注意指定编码格式,发送方和接收方一定要统一,建议使用UTF-8
			sb.append(new String(bytes, 0, len, "UTF-8"));
		}
		System.out.println("get message from client: " + sb);
		inputStream.close();
		socket.close();
		server.close();
	}

	/**
	 * 双向通信,发送消息并接受消息
	 */
	public static void twoWayCommunicationServer() {
		// 监听指定的端口
		int port = 55533;
		try (ServerSocket server = new ServerSocket(port)) {
			// server将一直等待连接的到来
			System.out.println("server将一直等待连接的到来");

			while (true) {
				Socket socket = server.accept();
				// 建立好连接后,从socket中获取输入流,并建立缓冲区进行读取
				InputStream inputStream = socket.getInputStream();

				byte[] bytes = new byte[1024];
				int len;
				StringBuilder sb = new StringBuilder();
				while ((len = inputStream.read(bytes)) != -1) {
					// 注意指定编码格式,发送方和接收方一定要统一,建议使用UTF-8
					sb.append(new String(bytes, 0, len, "UTF-8"));
				}
				System.out.println("get message from client: " + sb);
				BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
				bw.write("mess" + "\n");
				bw.flush();
				inputStream.close();
				socket.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	/*
	 * 如何告知对方已发送完命令
	 * 1.通过Socket关闭
	 * 当Socket关闭的时候,服务端就会收到响应的关闭信号,那么服务端也就知道流已经关闭了,这个时候读取操作完成,就可以继续后续工作。但是这种方式有一些缺点,客户端Socket关闭后,将不能接受服务端发送的消息,也不能再次发送消息;如果客户端想再次发送消息,需要重现创建Socket连接
	 * 2.通过Socket关闭输出流的方式
	 * 这种方式调用的方法是：socket.shutdownOutput();而不是(outputStream为发送消息到服务端打开的输出流):outputStream.close();如果关闭了输出流,那么相应的Socket也将关闭,和直接关闭Socket一个性质。
	 * 调用Socket的shutdownOutput()方法,底层会告知服务端我这边已经写完了,那么服务端收到消息后,就能知道已经读取完消息,如果服务端有要返回给客户的消息那么就可以通过服务端的输出流发送给客户端,如果没有,直接关闭Socket。
	 * 这种方式通过关闭客户端的输出流,告知服务端已经写完了,虽然可以读到服务端发送的消息,但是还是有一点点缺点:不能再次发送消息给服务端,如果再次发送,需要重新建立Socket连接。这个缺点,在访问频率比较高的情况下将是一个需要优化的地方
	 * 3.通过约定符号.
	 * 这种方式的用法,就是双方约定一个字符或者一个短语,来当做消息发送完成的标识,通常这么做就需要改造读取方法。假如约定单端的一行为end,代表发送完成,例如下面的消息,end则代表消息发送完成:那么服务端响应的读取操作需要进行如下改造：
	 * //建立好连接后,从socket中获取输入流,并建立缓冲区进行读取
	 * BufferedReader read=new BufferedReader(new
	 * InputStreamReader(socket.getInputStream(),"UTF-8"));
	 * String line;
	 * StringBuilder sb = new StringBuilder();
	 * while ((line = read.readLine()) != null && "end".equals(line)) {
	 *     //注意指定编码格式,发送方和接收方一定要统一,建议使用UTF-8
	 *     sb.append(line);
	 * }
	 * 优点：不需要关闭流,当发送完一条命令（消息）后可以再次发送新的命令（消息）
	 * 缺点：需要额外的约定结束标志,太简单的容易出现在要发送的消息中,误被结束,太复杂的不好处理,还占带宽。
	 * 4.通过指定长度
	 */

	public static void symbolEndOfSocket() {
		// 监听指定的端口
		int port = 55533;
		try (ServerSocket server = new ServerSocket(port)) {
			// server将一直等待连接的到来
			System.out.println("server将一直等待连接的到来");
			Socket socket = server.accept();
			// 建立好连接后,从socket中获取输入流,并建立缓冲区进行读取
			InputStream inputStream = socket.getInputStream();
			byte[] bytes;
			// 因为可以复用Socket且能判断长度,所以可以一个Socket用到底
			while (true) {
				// 首先读取两个字节表示的长度
				int first = inputStream.read();
				// 如果读取的值为-1说明到了流的末尾,Socket已经被关闭了,此时将不能再去读取
				if (first == -1) {
					break;
				}
				int second = inputStream.read();
				int length = (first << 8) + second;
				// 然后构造一个指定长的byte数组
				bytes = new byte[length];
				// 然后读取指定长度的消息即可
				inputStream.read(bytes);
				System.out.println("get message from client: " + new String(bytes, "UTF-8"));
			}
			inputStream.close();
			socket.close();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 在上面的例子中,服务端仅仅只是接受了一个Socket请求,并处理了它,然后就结束了,但是在实际开发中,一个Socket服务往往需要服务大量的Socket请求,那么就不能再服务完一个Socket的时候就关闭了,这时候可以采用循环接受请求并处理的逻辑
	 */
	public static void optimizesymbolEndOfSocket() {
		// 监听指定的端口
		int port = 55533;
		try (ServerSocket server = new ServerSocket(port);) {
			// server将一直等待连接的到来
			System.out.println("server将一直等待连接的到来");

			while (true) {
				Socket socket = server.accept();
				// 建立好连接后,从socket中获取输入流,并建立缓冲区进行读取
				InputStream inputStream = socket.getInputStream();
				byte[] bytes = new byte[1024];
				int len;
				StringBuilder sb = new StringBuilder();
				while ((len = inputStream.read(bytes)) != -1) {
					// 注意指定编码格式,发送方和接收方一定要统一,建议使用UTF-8
					sb.append(new String(bytes, 0, len, "UTF-8"));
				}
				System.out.println("get message from client: " + sb);
				inputStream.close();
				socket.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	/**
	 * 使用多线程
	 */
	public static void twoWayCommunicationServerUseThread() {
		// 监听指定的端口
		int port = 55533;
		try (ServerSocket server = new ServerSocket(port)) {
			// server将一直等待连接的到来
			System.out.println("server将一直等待连接的到来");

			// 如果使用多线程,那就需要线程池,防止并发过高时创建过多线程耗尽资源
			ExecutorService threadPool = Executors.newFixedThreadPool(100);

			while (true) {
				Socket socket = server.accept();
				Runnable runnable = () -> {
					try {
						// 建立好连接后,从socket中获取输入流,并建立缓冲区进行读取
						InputStream inputStream = socket.getInputStream();
						byte[] bytes = new byte[1024];
						int len;
						StringBuilder sb = new StringBuilder();
						while ((len = inputStream.read(bytes)) != -1) {
							// 注意指定编码格式,发送方和接收方一定要统一,建议使用UTF-8
							sb.append(new String(bytes, 0, len, "UTF-8"));
						}
						System.out.println("get message from client: " + sb);
						BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
						bw.write("mess" + "\n");
						bw.flush();
						inputStream.close();
						socket.close();
					} catch (Exception e) {
						e.printStackTrace();
					}
				};
				threadPool.submit(runnable);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	/**
	 * buffer socket
	 */
	public static void bufferSocketServer() {
		try (ServerSocket ss = new ServerSocket(8888)) {
			System.out.println("启动服务器....");
			Socket s = ss.accept();
			// InetAddress in = s.getInetAddress();
			// System.out.println("客户端:"+in.getLocalHost()+"已连接到服务器");
			System.out.println("客户端:" + InetAddress.getLocalHost() + "已连接到服务器");

			BufferedReader br = new BufferedReader(new InputStreamReader(s.getInputStream()));
			// 读取客户端发送来的消息
			String mess = br.readLine();
			System.out.println("客户端：" + mess);
			BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(s.getOutputStream()));
			bw.write(mess + "\n");
			bw.flush();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}


	public static void bioServer() throws IOException{
		ServerSocket serverSocket = null;
		try {
			serverSocket = new ServerSocket(4444);
		} catch (IOException e) {
			System.err.println("Could not listen on port: 4444.");
			System.exit(1);
		}

		Socket clientSocket = null;
		try {
			clientSocket = serverSocket.accept();
		} catch (IOException e) {
			System.err.println("Accept failed.");
			System.exit(1);
		}

		PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
		BufferedReader in = new BufferedReader(
				new InputStreamReader(
						clientSocket.getInputStream()));
		String inputLine;

		while ((inputLine = in.readLine()) != null) {
			out.println(inputLine);
			out.flush();
			if (inputLine.equals("Bye.")) {
				break;
			}
		}

		out.close();
		in.close();
		clientSocket.close();
		serverSocket.close();
	}

	public static void nioServer() throws IOException{
		// 创建一个选择器selector
        Selector selector= Selector.open();
        // 创建serverSocketChannel
        ServerSocketChannel serverSocketChannel=ServerSocketChannel.open();
        // 绑定端口
        serverSocketChannel.socket().bind(new InetSocketAddress(8888));
        // 必须得设置成非阻塞模式
        serverSocketChannel.configureBlocking(false);
        // 将channel注册到selector并设置监听事件为ACCEPT
        serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
        System.out.println("===========NIO服务端启动============");
        while(true){
            // 超时等待
            if(selector.select(1000)==0){
                System.out.println("===========NIO服务端超时等待============");
                continue;
            }
            // 有客户端请求被轮询监听到，获取返回的SelectionKey集合
            Iterator<SelectionKey> iterator=selector.selectedKeys().iterator();
            // 迭代器遍历SelectionKey集合
            while (iterator.hasNext()){
                SelectionKey key=iterator.next();
                // 判断是否为ACCEPT事件
                if (key.isAcceptable()){
                    // 处理接收请求事件
                    SocketChannel socketChannel=((ServerSocketChannel) key.channel()).accept();
                    // 非阻塞模式
                    socketChannel.configureBlocking(false);
                    // 注册到Selector并设置监听事件为READ
                    socketChannel.register(selector,SelectionKey.OP_READ, ByteBuffer.allocate(1024));
                    System.out.println("成功连接客户端");
                }
                // 判断是否为READ事件
                if (key.isReadable()){
                    SocketChannel socketChannel = (SocketChannel) key.channel();

                    try {
                        // 获取以前设置的附件对象，如果没有则新建一个
                        ByteBuffer buffer = (ByteBuffer) key.attachment();
                        if (buffer == null) {
                            buffer = ByteBuffer.allocate(1024);
                            key.attach(buffer);
                        }
                        // 清空缓冲区
                        buffer.clear();
                        // 将通道中的数据读到缓冲区
                        int len = socketChannel.read(buffer);
                        if (len > 0) {
                            buffer.flip();
                            String message = new String(buffer.array(), 0, len);
                            System.out.println("收到客户端消息：" + message);
                        } else if (len < 0) {
                            // 接收到-1，表示连接已关闭
                            key.cancel();
                            socketChannel.close();
                            continue;
                        }
                        // 注册写事件，下次向客户端发送消息
                        socketChannel.register(selector, SelectionKey.OP_WRITE, buffer);
                    } catch (IOException e) {
                        // 取消SelectionKey并关闭对应的SocketChannel
                        key.cancel();
                        socketChannel.close();
                    }
                }
                // 判断是否为WRITE事件
                if (key.isWritable()){
                    SocketChannel socketChannel = (SocketChannel) key.channel();
                    // 获取buffer
                    ByteBuffer buffer = (ByteBuffer) key.attachment();
                    String hello = "你好，坤坤！";
                    // 清空buffer
                    buffer.clear();
                    // buffer中写入消息
                    buffer.put(hello.getBytes());
                    buffer.flip();
                    // 向channel中写入消息
                    socketChannel.write(buffer);
                    buffer.clear();
                    System.out.println("向客户端发送消息：" + hello);
                    // 设置下次读写操作，向 Selector 进行注册
                    socketChannel.register(selector, SelectionKey.OP_READ, buffer);
                }
                // 移除本次处理的SelectionKey,防止重复处理
                iterator.remove();
            }
        }
	}


	public static void aioServer() throws Exception {
		// 创建异步通道组，处理IO事件
        AsynchronousChannelGroup group = AsynchronousChannelGroup.withFixedThreadPool(10, Executors.defaultThreadFactory());
        // 创建异步服务器Socket通道，并绑定端口
        AsynchronousServerSocketChannel server = AsynchronousServerSocketChannel.open(group).bind(new InetSocketAddress(8888));
        System.out.println("=============AIO服务端启动=========");

        // 异步等待接收客户端连接
        server.accept(null, new CompletionHandler<AsynchronousSocketChannel, Object>() {
            // 创建ByteBuffer
            final ByteBuffer buffer = ByteBuffer.allocate(1024);

            @Override
            public void completed(AsynchronousSocketChannel channel, Object attachment) {
                System.out.println("客户端连接成功");
                try {
                    buffer.clear();
                    // 异步读取客户端发送的消息
                    channel.read(buffer, null, new CompletionHandler<Integer, Object>() {
                        @Override
                        public void completed(Integer len, Object attachment) {
                            buffer.flip();
                            String message = new String(buffer.array(), 0, len);
                            System.out.println("收到客户端消息：" + message);

                            // 异步发送消息给客户端
                            channel.write(ByteBuffer.wrap(("你好，阿坤！").getBytes()), null, new CompletionHandler<Integer, Object>() {
                                @Override
                                public void completed(Integer result, Object attachment) {
                                    // 关闭输出流
                                    try {
                                        channel.shutdownOutput();
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                }

                                @Override
                                public void failed(Throwable exc, Object attachment) {
                                    exc.printStackTrace();
                                    try {
                                        channel.close();
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                }
                            });
                        }

                        @Override
                        public void failed(Throwable exc, Object attachment) {
                            exc.printStackTrace();
                            try {
                                channel.close();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
                // 继续异步等待接收客户端连接
                server.accept(null, this);
            }

            @Override
            public void failed(Throwable exc, Object attachment) {
                exc.printStackTrace();
                // 继续异步等待接收客户端连接
                server.accept(null, this);
            }
        });
        // 等待所有连接都处理完毕
        group.awaitTermination(Long.MAX_VALUE, TimeUnit.SECONDS);
	}
}
