package com.xmxe.study_demo.nio;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.nio.channels.FileChannel;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.concurrent.TimeUnit;

/**
 * NIO主要有三大核心部分：Channel(通道)，Buffer(缓冲区), Selector(选择区) IO是面向流的，NIO是面向缓冲区的
 * 传统IO基于字节流和字符流进行操作，而NIO基于Channel和Buffer进行操作，数据总是从通道读取到缓冲区中，或者从缓冲区写入到通道中
 * Selector(选择区)用于监听多个通道的事件（比如：连接打开，数据到达）因此，单个线程可以监听多个数据通道。
 * 
 * Java NIO？看这一篇就够了！(https://blog.csdn.net/forezp/article/details/88414741) Java
 * NIO系列教程(https://ifeve.com/overview/)
 */

/**
 * NIO中的Channel的主要实现有： FileChannel(从文件中读写数据) DatagramChannel(通过UDP读写网络中的数据)
 * SocketChannel(通过TCP读写网络中的数据)
 * ServerSocketChannel(可以监听新进来的TCP连接，像Web服务器那样。对每一个新进来的连接都会创建一个SocketChannel)
 */
public class NIO_Channel {
    /**
     * NIO读取文件 RandomAccessFile进行操作，也可以通过FileInputStream.getChannel()获取channel进行操作
     * 
     */
    public void newIORead() {
        RandomAccessFile aFile = null;
        try {
            aFile = new RandomAccessFile("src/nio.txt", "rw");
            FileChannel fileChannel = aFile.getChannel();
            // buffer分配空间 根据Buffer实现类表明分配时的单位 下面代表分配1024个字节 CharBuffer就代表1024个字符
            ByteBuffer buf = ByteBuffer.allocate(1024);
            // 通道必须结合Buffer使用，不能直接向通道中读/写数据，
            // read()表示读channel数据写入到buffer，write()表示读取buffer数据写入到channel。
            int bytesRead = fileChannel.read(buf);
            while (bytesRead != -1) {
                // 在读模式下，可以读取之前写入到buffer的所有数据,调用flip()方法,position设回0，并将limit设成之前的position的值
                // 数据就是从position到limit的数据
                // capacity(缓冲区数组的总长度),position(下一个要操作的数据元素的位置),limit(缓冲区数组中不可操作的下一个元素的位置：limit<=capacity),mark(用于记录当前position的前一个位置或者默认是-1)
                buf.flip();
                // hasRemaining()用于判断当前位置(position)和限制(limit)之间是否有任何元素
                while (buf.hasRemaining()) {
                    System.out.print((char) buf.get());
                }
                // clear()方法：position将被设回0，limit设置成capacity，换句话说，Buffer被清空了，
                // 其实Buffer中的数据并未被清除，只是这些标记告诉我们可以从哪里开始往Buffer里写数据。
                // 如果Buffer中有一些未读的数据，调用clear()方法，数据将“被遗忘”，意味着不再有任何标记会告诉你哪些数据被读过，哪些还没有。
                // 如果Buffer中仍有未读的数据，且后续还需要这些数据，但是此时想要先写些数据，那么使用compact()方法。
                // compact()方法将所有未读的数据拷贝到Buffer起始处。然后将position设到最后一个未读元素正后面。limit属性依然像clear()方法一样，设置成capacity。
                // 现在Buffer准备好写数据了，但是不会覆盖未读的数据。
                buf.compact();
                bytesRead = fileChannel.read(buf);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (aFile != null) {
                    aFile.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * NIO写数据 通过FileChannel写入数据
     */
    public void FileChannelOnWrite() {
        try {
            RandomAccessFile accessFile = new RandomAccessFile("D://file1.txt", "rw");
            FileChannel fc = accessFile.getChannel();
            byte[] bytes = new String("write to file1.txt").getBytes();
            // 获得ByteBuffer的实例 类似allocate(int capacity)方法
            ByteBuffer byteBuffer = ByteBuffer.wrap(bytes);
            // 读取缓冲区数据写入到通道。
            fc.write(byteBuffer);
            // 清空缓存区 使得缓存区可以继续写入数据
            byteBuffer.clear();
            // 缓存区写入内容
            byteBuffer.put(new String(",a good boy").getBytes());
            // 写模式转化读模式
            byteBuffer.flip();
            // 读取缓冲区数据写入到通道。
            fc.write(byteBuffer);
            fc.close();
            accessFile.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * FileChannel的transferFrom()方法可以将数据从源通道传输到FileChannel中
     * （译者注：这个方法在JDK文档中的解释为将字节从给定的可读取字节通道传输到此通道的文件中）。
     */
    public void testTransferFrom() {
        try {
            RandomAccessFile fromFile = new RandomAccessFile("D://file1.txt", "rw");
            FileChannel fromChannel = fromFile.getChannel();
            RandomAccessFile toFile = new RandomAccessFile("D://file2.txt", "rw");
            FileChannel toChannel = toFile.getChannel();

            long position = 0;
            long count = fromChannel.size();
            toChannel.transferFrom(fromChannel, position, count);

            fromFile.close();
            toFile.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * transferTo()方法将数据从FileChannel传输到其他的channel中
     */
    public static void testTransferTo() {
        try {
            RandomAccessFile fromFile = new RandomAccessFile("D://file1.txt", "rw");
            FileChannel fromChannel = fromFile.getChannel();
            RandomAccessFile toFile = new RandomAccessFile("D://file3.txt", "rw");
            FileChannel toChannel = toFile.getChannel();

            long position = 0;
            long count = fromChannel.size();
            fromChannel.transferTo(position, count, toChannel);
            fromFile.close();
            toFile.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //-----------------------nio socket-------------------------------------

    /**
     * NIO新建socket client
     */
    public void newIOSocketClient() {
        ByteBuffer buffer = ByteBuffer.allocate(1024);
        SocketChannel socketChannel = null;
        try {
            // 通过 ServerSocketChannel.open()方法来创建一个新的ServerSocketChannel对象，
            // 该对象关联了一个未绑定ServerSocket的通道.通过调用该对象上的socket()方法可以获取与之关联的ServerSocket。
            socketChannel = SocketChannel.open();
            socketChannel.configureBlocking(false);
            socketChannel.connect(new InetSocketAddress("10.10.195.115", 8080));
            // 为了确定连接是否建立，可以调用finishConnect()的方法。
            if (socketChannel.finishConnect()) {
                int i = 0;
                while (true) {
                    TimeUnit.SECONDS.sleep(1);
                    String info = "I'm " + i++ + "-th information from client";
                    buffer.clear();
                    buffer.put(info.getBytes());
                    buffer.flip();
                    while (buffer.hasRemaining()) {
                        System.out.println(buffer);
                        socketChannel.write(buffer);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (socketChannel != null) {
                    socketChannel.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * NIO socket server
     * 
     * @throws Exception
     */
    public void NIOSocketServer() throws Exception {
        ServerSocketChannel socketChannel = ServerSocketChannel.open();
        // 非阻塞模式
        socketChannel.configureBlocking(false);
        // socketChannel.socket().bind(new InetSocketAddress(9999));jdk 1.7之前
        socketChannel.bind(new InetSocketAddress(9999));
        ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
        while (true) {
            // 通过 ServerSocketChannel.accept() 方法监听新进来的连接。
            // 在阻塞模式下当 accept()方法返回的时候,它返回一个包含新进来的连接的SocketChannel，否则accept()方法会一直阻塞到有新连接到达。
            // 在非阻塞模式下，在没有新连接的情况下，accept()会立即返回null，该模式下通常不会仅仅只监听一个连接,因此需在while循环中调用
            // accept()方法.
            SocketChannel channel = socketChannel.accept();
            if (channel != null) {
                InetSocketAddress remoteAddress = (InetSocketAddress) channel.getRemoteAddress();
                System.out.println(remoteAddress.getAddress());
                System.out.println(remoteAddress.getPort());
                channel.read(byteBuffer);
                byteBuffer.flip();
                while (byteBuffer.hasRemaining()) {
                    System.out.print((char) byteBuffer.get());
                }
            }
        }
    }

    /**
     * UDP发送方
     * 
     * @throws Exception
     */
    public void DatagramChannelsend() throws Exception {
        // 通过DatagramChannel的open()方法来创建。需要注意DatagramChannel的open（）方法只是打开获得通道，
        // 但此时尚未连接。尽管DatagramChannel无需建立连接（远端连接），但仍然可以通过isConnect（）检测当前的channel是否声明了远端连接地址。
        DatagramChannel channel = DatagramChannel.open();
        ByteBuffer byteBuffer = ByteBuffer.wrap(new String("i 'm client").getBytes());
        // 通过send（）方法将ByteBuffer中的内容发送到指定的SocketAddress对象所描述的地址。在阻塞模式下，调用线程会被阻塞至有数据包被加入传输队列。
        // 非阻塞模式下，如果发送内容为空则返回0，否则返回发送的字节数。发送数据报是一个全有或全无(all-or-nothing)的行为。
        // 如果传输队列没有足够空间来承载整个数据报，那么什么内容都不会被发送。
        // 请注意send()方法返回的非零值并不表示数据报到达了目的地，仅代表数据报被成功加到本地网络层的传输队列。
        // 此外，传输过程中的协议可能将数据报分解成碎片，被分解的数据报在目的地会被重新组合起来，接收者将看不到碎片。
        // 但是，如果有一个碎片不能按时到达，那么整个数据报将被丢弃。分解有助于发送大数据报，但也会会造成较高的丢包率。
        int bytesSent = channel.send(byteBuffer, new InetSocketAddress("127.0.0.1", 9999));

    }

    /**
     * UDP接收方
     */
    private void receiveData() throws IOException {
        DatagramChannel channel = DatagramChannel.open();
        channel.socket().bind(new InetSocketAddress(9999));
        ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
        byteBuffer.clear();
        // 通过receive（）方法接受DatagramChannel中数据。从该方法将传入的数据报的数据将被复制到ByteBuffer中，
        // 同时返回一个SocketAddress对象以指出数据来源。在阻塞模式下，receive（）将会阻塞至有数据包到来，
        // 非阻塞模式下，如果没有可接受的包则返回null。如果包内的数据大小超过缓冲区容量时，多出的数据会被悄悄抛弃
        SocketAddress address = channel.receive(byteBuffer);// receive data
        byteBuffer.flip();
        while (byteBuffer.hasRemaining()) {
            System.out.print((char) byteBuffer.get());
        }
    }

}
/**
 * Channel常用方法 
 * int read(ByteBuffer dst) 从Channel到中读取数据到ByteBuffer 
 * long read(ByteBuffer[] dsts) 将Channel到中的数据“分散”到ByteBuffer[] 
 * int write(ByteBuffer src) 将ByteBuffer到中的数据写入到Channel 
 * long write(ByteBuffer[] srcs) 将ByteBuffer[]到中的数据“聚集”到Channel 
 * long position() 返回此通道的文件位置 
 * FileChannel position(long p) 设置此通道的文件位置 
 * long size() 返回此通道的文件的当前大小 
 * FileChannel truncate(long s) 将此通道的文件截取为给定大小 
 * void force(boolean metaData) 强制将所有对此通道的文件更新写入到存储设备中
 * 
 */