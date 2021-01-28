package com.xmxe.study_demo.nio;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;

/**
 * Selector运行单线程处理多个Channel，如果你的应用打开了多个通道，但每个连接的流量都很低，使用Selector就会很方便。
 * 例如在一个聊天服务器中。要使用Selector, 得向Selector注册Channel，然后调用它的select()方法。
 * 这个方法会一直阻塞到某个注册的通道有事件就绪。一旦这个方法返回，线程就可以处理这些事件，事件的例子有如新的连接进来、数据接收等。
 */
public class NIO_Selector {
    /**
     * Selector的创建：Selector selector = Selector.open();
     * 为了将Channel和Selector配合使用，必须将Channel注册到Selector上，通过SelectableChannel.register()方法来实现，沿用nio创建socket
     * server ssc= ServerSocketChannel.open(); 
     * ssc.socket().bind(new InetSocketAddress(PORT)); 
     * ssc.configureBlocking(false);
     * ssc.register(selector, SelectionKey.OP_ACCEPT);
     * 
     * 与Selector一起使用时，Channel必须处于非阻塞模式下。这意味着不能将FileChannel与Selector一起使用，因为FileChannel不能切换到非阻塞模式。而套接字通道都可以。
     * 注意register()方法的第二个参数。这是一个“interest集合”，意思是在通过Selector监听Channel时对什么事件感兴趣。可以监听四种不同类型的事件：
     * 1. Connect 2. Accept 3. Read 4. Write 这四种事件用SelectionKey的四个常量来表示：
     * 1.SelectionKey.OP_CONNECT 2. SelectionKey.OP_ACCEPT 3. SelectionKey.OP_READ 4.SelectionKey.OP_WRITE
     */

    /**
     * SelectionKey
     * 当向Selector注册Channel时，register()方法会返回一个SelectionKey对象。这个对象包含了一些你感兴趣的属性：
     * interest集合 ready集合 Channel Selector 附加的对象（可选）
     * interest集合：就像向Selector注册通道一节中所描述的，interest集合是你所选择的感兴趣的事件集合。可以通过SelectionKey读写interest集合。
     * ready 集合是通道已经准备就绪的操作的集合。在一次选择(Selection)之后，你会首先访问这个ready set。
     * 可以这样访问ready集合： int readySet = selectionKey.readyOps();
     * 可以用像检测interest集合那样的方法，来检测channel中什么事件或操作已经就绪。但是，也可以使用以下四个方法，它们都会返回一个布尔类型：
     * selectionKey.isAcceptable(); selectionKey.isConnectable();
     * selectionKey.isReadable(); selectionKey.isWritable();
     * 从SelectionKey访问Channel和Selector很简单:： Channel channel = selectionKey.channel(); Selector selector = selectionKey.selector();
     * 可以将一个对象或者更多信息附着到SelectionKey上，这样就能方便的识别某个给定的通道。例如，可以附加与通道一起使用的Buffer，或是包含聚集数据的某个对象。使用方法： selectionKey.attach(theObject); Object attachedObj = selectionKey.attachment();
     * 还可以在用register()方法向Selector注册Channel的时候附加对象。如： SelectionKey key = channel.register(selector, SelectionKey.OP_READ, theObject);
     */

    /**
     * 通过Selector选择通道
     * 一旦向Selector注册了一或多个通道，就可以调用几个重载的select()方法。这些方法返回你所感兴趣的事件（如连接、接受、读或写）已经准备就绪的那些通道。
     * 换句话说，如果你对“读就绪”的通道感兴趣，select()方法会返回读事件已经就绪的那些通道。
     * 
     * 下面是select()方法：
     * int select() 阻塞到至少有一个通道在你注册的事件上就绪了
     * int select(long timeout) 和select()一样，除了最长会阻塞timeout毫秒(参数)。
     * int selectNow()不会阻塞，不管什么通道就绪都立刻返回（译者注：此方法执行非阻塞的选择操作。如果自从前一次选择操作后，没有通道变成可选择的，则此方法直接返回零。
     *
     * select()方法返回的int值表示有多少通道已经就绪。亦即，自上次调用select()方法后有多少通道变成就绪状态。
     * 如果调用select()方法，因为有一个通道变成就绪状态，返回了1，若再次调用select()方法，如果另一个通道就绪了，
     * 它会再次返回1。如果对第一个就绪的channel没有做任何操作，现在就有两个就绪的通道，但在每次select()方法调用之间，
     * 只有一个通道就绪了。一旦调用了select()方法，并且返回值表明有一个或更多个通道就绪了，然后可以通过调用selector的selectedKeys()方法，
     * 访问“已选择键集（selected key set）”中的就绪通道。如下所示：
     * Set selectedKeys = selector.selectedKeys();
     * 当向Selector注册Channel时，Channel.register()方法会返回一个SelectionKey对象。这个对象代表了注册到该Selector的通道。
     * 注意每次迭代末尾的keyIterator.remove()调用。Selector不会自己从已选择键集中移除SelectionKey实例。必须在处理完通道时自己移除。
     * 下次该通道变成就绪时，Selector会再次将其放入已选择键集中。
     * SelectionKey.channel()方法返回的通道需要转型成你要处理的类型，如ServerSocketChannel或SocketChannel等。
     * 
     * 一个完整的使用Selector和ServerSocketChannel的案例可以参考的selector()方法。
     * 
     */
    /**
     * NIO新建socket server
     */
    
    static class ServerConnect {
        private static final int BUF_SIZE = 1024;
        private static final int PORT = 8080;
        private static final int TIMEOUT = 3000;

        // public static void main(String[] args){
        // selector();
        // }
        public void handleAccept(SelectionKey key) throws Exception {
            ServerSocketChannel ssChannel = (ServerSocketChannel) key.channel();
            SocketChannel sc = ssChannel.accept();
            sc.configureBlocking(false);
            sc.register(key.selector(), SelectionKey.OP_READ, ByteBuffer.allocateDirect(BUF_SIZE));
        }

        public void handleRead(SelectionKey key) throws Exception {
            SocketChannel sc = (SocketChannel) key.channel();
            ByteBuffer buf = (ByteBuffer) key.attachment();
            long bytesRead = sc.read(buf);
            while (bytesRead > 0) {
                buf.flip();
                while (buf.hasRemaining()) {
                    System.out.print((char) buf.get());
                }
                System.out.println();
                buf.clear();
                bytesRead = sc.read(buf);
            }
            if (bytesRead == -1) {
                sc.close();
            }
        }

        public void handleWrite(SelectionKey key) throws Exception {
            ByteBuffer buf = (ByteBuffer) key.attachment();
            buf.flip();
            SocketChannel sc = (SocketChannel) key.channel();
            while (buf.hasRemaining()) {
                sc.write(buf);
            }
            buf.compact();
        }

        public void selector() {
            Selector selector = null;
            ServerSocketChannel ssc = null;
            try {
                selector = Selector.open();
                // 打开ServerSocketChannel
                ssc = ServerSocketChannel.open();
                ssc.socket().bind(new InetSocketAddress(PORT));
                ssc.configureBlocking(false);
                ssc.register(selector, SelectionKey.OP_ACCEPT);
                while (true) {
                    if (selector.select(TIMEOUT) == 0) {
                        System.out.println("==");
                        continue;
                    }
                    Iterator<SelectionKey> iter = selector.selectedKeys().iterator();
                    while (iter.hasNext()) {
                        SelectionKey key = iter.next();
                        if (key.isAcceptable()) {
                            handleAccept(key);
                        }
                        if (key.isReadable()) {
                            handleRead(key);
                        }
                        if (key.isWritable() && key.isValid()) {
                            handleWrite(key);
                        }
                        if (key.isConnectable()) {
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
                    if (ssc != null) {
                        ssc.close();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
    
}
