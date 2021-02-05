package com.xmxe.study_demo.nio.netty;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

public class NettyClient {

    private final String host;
    private final int port;
    private final int firstMessageSize;

    public NettyClient(String host, int port, int firstMessageSize) {
        this.host = host;
        this.port = port;
        this.firstMessageSize = firstMessageSize;
    }

    /**
     * 客户端启动new NettyClient("127.0.0.1",9999,1024).run()
     */
    public void run() throws Exception {
        // Configure the client.
        EventLoopGroup group = new NioEventLoopGroup();
        try {

            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(group)
                        .channel(NioSocketChannel.class)
                        .option(ChannelOption.TCP_NODELAY, true)
                        .handler(new ChannelInitializer<SocketChannel>() {
                                    @Override
                                    public void initChannel(SocketChannel ch) throws Exception {
                                    ch.pipeline().addLast(
                                    // new LoggingHandler(LogLevel.INFO),
                                            new EchoClientHandler(firstMessageSize));
                                    }
                                });

            // Start the client.
            ChannelFuture channelFuture = bootstrap.connect(host, port).sync();
            // Wait until the connection is closed.
            channelFuture.channel().closeFuture().sync();
        } finally {
            // Shut down the event loop to terminate all threads.
            group.shutdownGracefully();
        }
    }

    static class EchoClientHandler extends ChannelInboundHandlerAdapter {
    
        private final ByteBuf firstMessage;
    
        /**
         * Creates a client-side handler.
         */
        public EchoClientHandler(int firstMessageSize) {
            if (firstMessageSize <= 0) {
                throw new IllegalArgumentException("firstMessageSize: " + firstMessageSize);
            }
            firstMessage = Unpooled.buffer(firstMessageSize);
            for (int i = 0; i < firstMessage.capacity(); i++) {
                firstMessage.writeByte((byte) i);
            }
        }
    
        @Override
        public void channelActive(ChannelHandlerContext ctx) {
            ctx.writeAndFlush(firstMessage);
            System.out.print("active");
        }
    
        @Override
        public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
            ctx.write(msg);
            System.out.print("read");
        }
    
        @Override
        public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
            ctx.flush();
            System.out.print("readok");
        }
    
        @Override
        public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
            // Close the connection when an exception is raised.
            cause.printStackTrace();
            ctx.close();
        }
    }
}
