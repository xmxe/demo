package com.xmxe.jdkfeature.nio;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

/**
 * NIO之Selector选择器(https://www.cnblogs.com/snailclimb/p/9086334.html)
 */
public class NIO_Selector {
    /**
     * Selector的创建：Selector selector = Selector.open();
     * 为了将Channel和Selector配合使用,必须将Channel注册到Selector上,通过SelectableChannel.register()方法来实现,沿用nio创建socket
     * ServerSocketChannel channel = ServerSocketChannel.open();
     * channel.socket().bind(new InetSocketAddress(PORT));
     * channel.configureBlocking(false);
     * SelectionKey key = channel.register(selector,SelectionKey.OP_ACCEPT);
     * 
     * 与Selector一起使用时,Channel必须处于非阻塞模式下。这意味着不能将FileChannel与Selector一起使用,因为FileChannel不能切换到非阻塞模式。而套接字通道都可以。
     * 注意register()方法的第二个参数。这是一个“interest集合”,意思是在通过Selector监听Channel时对什么事件感兴趣。可以监听四种不同类型的事件：
     * 1.Connect连接 2.Accept接收 3.Read读 4.Write写这四种事件用SelectionKey的四个常量来表示：1.SelectionKey.OP_CONNECT 2.SelectionKey.OP_ACCEPT 3.SelectionKey.OP_READ 4.SelectionKey.OP_WRITE
     */

    /**
     * SelectionKey
     * 一个SelectionKey键表示了一个特定的通道对象和一个特定的选择器对象之间的注册关系。当向Selector注册Channel时,register()方法会返回一个SelectionKey对象。这个对象包含了一些你感兴趣的属性：
     * interest集合：就像向Selector注册通道一节中所描述的,interest集合是你所选择的感兴趣的事件集合。可以通过SelectionKey读写interest集合。
     * ready集合：通道已经准备就绪的操作的集合。在一次选择(Selection)之后,你会首先访问这个ready set。可以这样访问ready集合：
     * int readySet = selectionKey.readyOps();
     * 可以用像检测interest集合那样的方法,来检测channel中什么事件或操作已经就绪。但是,也可以使用以下四个方法,它们都会返回一个布尔类型：
     * selectionKey.isAcceptable();
     * selectionKey.isConnectable();
     * selectionKey.isReadable();
     * selectionKey.isWritable();
     * 从SelectionKey访问Channel和Selector很简单:
     * Channel channel = selectionKey.channel();
     * Selector selector = selectionKey.selector();
     * 可以将一个对象或者更多信息附着到SelectionKey上,这样就能方便的识别某个给定的通道。例如,可以附加与通道一起使用的Buffer,或是包含聚集数据的某个对象。使用方法：
     * selectionKey.attach(theObject);
     * Object attachedObj = selectionKey.attachment();
     * 还可以在用register()方法向Selector注册Channel的时候附加对象。如：
     * SelectionKey key = channel.register(selector,SelectionKey.OP_READ,theObject);
     * 
     * SelectionKey常用方法
     * key.attachment();返回SelectionKey的attachment,attachment可以在注册channel的时候指定。
     * key.channel();返回该SelectionKey对应的channel。
     * key.selector();返回该SelectionKey对应的Selector。
     * key.interestOps();返回代表需要Selector监控的IO操作的bit mask
     * key.readyOps();返回一个bit mask,代表在相应channel上可以进行的IO操作。
     */

    /**
     * 通过Selector选择通道
     * 一旦向Selector注册了一或多个通道,就可以调用几个重载的select()方法。这些方法返回你所感兴趣的事件（如连接、接受、读或写）已经准备就绪的那些通道。下面是select()方法：
     * int select();阻塞到至少有一个通道在你注册的事件上就绪了
     * int select(long timeout);和select()一样,除了最长会阻塞timeout毫秒(参数)。
     * int selectNow();不会阻塞,不管什么通道就绪都立刻返回（译者注：此方法执行非阻塞的选择操作。如果自从前一次选择操作后,没有通道变成可选择的,则此方法直接返回零。
     *
     * select()方法返回的int值表示有多少通道已经就绪。亦即自上次调用select()方法后有多少通道变成就绪状态。
     * 如果调用select()方法,有一个通道变成就绪状态,返回了1,若再次调用select()方法,如果另一个通道就绪了,它会再次返回1。
     * 如果对第一个就绪的channel没有做任何操作,现在就有两个就绪的通道,但在每次select()方法调用之间,只有一个通道就绪了。
     * 一旦调用了select()方法,并且返回值表明有一个或更多个通道就绪了,然后可以通过调用selector的selectedKeys()方法:Set selectedKeys = selector.selectedKeys();
     * 当向Selector注册Channel时,Channel.register()方法会返回一个SelectionKey对象。这个对象代表了注册到该Selector的通道。
     * 注意每次迭代末尾的keyIterator.remove()调用。Selector不会自己从已选择键集中移除SelectionKey实例。必须在处理完通道时自己移除。下次该通道变成就绪时,Selector会再次将其放入已选择键集中。
     * SelectionKey.channel()方法返回的通道需要转型成你要处理的类型,如ServerSocketChannel或SocketChannel等。。
     * 
     */

    static class ServerSelector {
        private static final int BUF_SIZE = 1024;
        private static final int PORT = 8080;
        private static final int TIMEOUT = 3000;

        // public static void main(String[] args){
        // selector();
        // }

        /**
         * 接收就绪处理
         * 
         * @param key
         * @throws Exception
         */
        public void handleAccept(SelectionKey key) throws Exception {
            ServerSocketChannel serverSocketChannel = (ServerSocketChannel) key.channel();
            SocketChannel socketChannel = serverSocketChannel.accept();
            socketChannel.configureBlocking(false);
            socketChannel.register(key.selector(), SelectionKey.OP_READ, ByteBuffer.allocateDirect(BUF_SIZE));
        }

        /**
         * 读就绪处理
         * 
         * @param key
         * @throws Exception
         */
        public void handleRead(SelectionKey key) throws Exception {
            SocketChannel socketChannel = (SocketChannel) key.channel();
            ByteBuffer buf = (ByteBuffer) key.attachment();
            long bytesRead = socketChannel.read(buf);
            while (bytesRead > 0) {
                buf.flip();
                while (buf.hasRemaining()) {
                    System.out.print((char) buf.get());
                }
                System.out.println();
                buf.clear();
                bytesRead = socketChannel.read(buf);
            }
            if (bytesRead == -1) {
                socketChannel.close();
            }
        }

        /**
         * 写就绪处理
         * 
         * @param key
         * @throws Exception
         */
        public void handleWrite(SelectionKey key) throws Exception {
            ByteBuffer buf = (ByteBuffer) key.attachment();
            buf.flip();
            SocketChannel socketChannel = (SocketChannel) key.channel();
            while (buf.hasRemaining()) {
                socketChannel.write(buf);
            }
            buf.compact();
        }

        /**
         * 注册选择器
         */
        public void selector() {
            Selector selector = null;
            ServerSocketChannel serverSocketChannel = null;
            try {
                // 打开选择器
                selector = Selector.open();
                // 打开ServerSocketChannel通道
                serverSocketChannel = ServerSocketChannel.open();
                serverSocketChannel.socket().bind(new InetSocketAddress(PORT));
                // 设置非阻塞模式
                serverSocketChannel.configureBlocking(false);
                // 注册监听接收
                serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
                while (true) {
                    if (selector.select(TIMEOUT) == 0) {
                        System.out.println("未检测到有通道就绪");
                        continue;
                    }
                    Iterator<SelectionKey> iter = selector.selectedKeys().iterator();
                    while (iter.hasNext()) {
                        SelectionKey key = iter.next();
                        if (key.isAcceptable()) {
                            // 接收就绪
                            handleAccept(key);
                        }
                        if (key.isReadable()) {
                            // 读就绪
                            handleRead(key);
                        }
                        if (key.isWritable() && key.isValid()) {
                            // 写就绪
                            handleWrite(key);
                        }
                        if (key.isConnectable()) {
                            // 连接就绪
                            System.out.println("isConnectable = true");
                        }
                        iter.remove();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                try {
                    if (selector != null) {
                        selector.close();
                    }
                    if (serverSocketChannel != null) {
                        serverSocketChannel.close();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    static class ClientSelector {

        /* 标识数字 */
        private static int flag = 0;
        /* 缓冲区大小 */
        private static int BLOCK = 4096;
        /* 接受数据缓冲区 */
        private static ByteBuffer sendbuffer = ByteBuffer.allocate(BLOCK);
        /* 发送数据缓冲区 */
        private static ByteBuffer receivebuffer = ByteBuffer.allocate(BLOCK);
        /* 服务器端地址 */
        private final static InetSocketAddress SERVER_ADDRESS = new InetSocketAddress(
                "localhost", 8888);

        // public static void main(String[] args) {
        // selector();
        // }
        public void selector() throws Exception {
            // 打开socket通道
            SocketChannel socketChannel = SocketChannel.open();
            // 设置为非阻塞方式
            socketChannel.configureBlocking(false);
            // 打开选择器
            Selector selector = Selector.open();
            // 注册连接服务端socket动作
            socketChannel.register(selector, SelectionKey.OP_CONNECT);
            // 连接
            socketChannel.connect(SERVER_ADDRESS);

            int count = 0;
            while (true) {
                // 选择一组键,其相应的通道已为I/O操作准备就绪。
                // 此方法执行处于阻塞模式的选择操作。
                int selctorCount = selector.select();
                if (selctorCount <= 0)
                    continue;
                // 返回此选择器的已选择键集。
                Set<SelectionKey> selectionKeys = selector.selectedKeys();
                // System.out.println(selectionKeys.size());
                Iterator<SelectionKey> iterator = selectionKeys.iterator();
                while (iterator.hasNext()) {
                    SelectionKey selectionKey = iterator.next();
                    if (selectionKey.isConnectable()) {
                        System.out.println("client connect");
                        SocketChannel client = (SocketChannel) selectionKey.channel();
                        // 判断此通道上是否正在进行连接操作。
                        // 完成套接字通道的连接过程。
                        if (client.isConnectionPending()) {
                            client.finishConnect();
                            System.out.println("完成连接!");
                            sendbuffer.clear();
                            sendbuffer.put("Hello,Server".getBytes());
                            sendbuffer.flip();
                            client.write(sendbuffer);
                        }
                        client.register(selector, SelectionKey.OP_READ);
                    } else if (selectionKey.isReadable()) {
                        SocketChannel client = (SocketChannel) selectionKey.channel();
                        // 将缓冲区清空以备下次读取
                        receivebuffer.clear();
                        // 读取服务器发送来的数据到缓冲区中
                        count = client.read(receivebuffer);
                        if (count > 0) {
                            String receiveText = new String(receivebuffer.array(), 0, count);
                            System.out.println("客户端接受服务器端数据--:" + receiveText);
                            client.register(selector, SelectionKey.OP_WRITE);
                        }

                    } else if (selectionKey.isWritable()) {
                        sendbuffer.clear();
                        SocketChannel client = (SocketChannel) selectionKey.channel();
                        String sendText = "message from client--" + (flag++);
                        sendbuffer.put(sendText.getBytes());
                        // 将缓冲区各标志复位,因为向里面put了数据标志被改变要想从中读取数据发向服务器,就要复位
                        sendbuffer.flip();
                        client.write(sendbuffer);
                        System.out.println("客户端向服务器端发送数据--：" + sendText);
                        client.register(selector, SelectionKey.OP_READ);
                    }
                }
                selectionKeys.clear();
            }
        }
    }

}
