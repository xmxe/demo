package com.xmxe.study_demo.nio;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.RandomAccessFile;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.Scanner;

/**
 * NIO中的关键Buffer实现有:ByteBuffer,CharBuffer,DoubleBuffer,FloatBuffer,IntBuffer,LongBuffer,ShortBuffer
 * 分别对应基本数据类型:byte,char,double,float,int,long,short。
 * NIO中还有MappedByteBuffer,HeapByteBuffer,DirectByteBuffer等缓冲区,本质上是一块可以写入数据，然后可以从中读取数据的内存。这块内存被包装成NIO
 * 
 * Buffer对象，并提供了一组方法，用来方便的访问该块内存。Buffer顾名思义:缓冲区，实际上是一个容器，一个连续数组。
 * Channel提供从文件、网络读取数据的渠道，但是读写的数据都必须经过Buffer.
 * 向Buffer中写数据:从Channel到Buffer:fileChannel.read(buf);通过Buffer的put()方法buf.put()
 * 从Buffer中读取数据:从Buffer到Channel(channel.write(buf))使用get()方法从Buffer中读取数据buf.get()
 */
public class NIO_Buffer {

    /**
     * JAVA处理大文件，一般用BufferedReader,BufferedInputStream这类带缓冲的IO类，不过如果文件超大的话，更快的方式是采用MappedByteBuffer。MappedByteBuffer是NIO引入的文件内存映射方案，读写性能极高。NIO最主要的就是实现了对异步操作的支持。
     * 其中一种通过把一个套接字通道(SocketChannel)注册到一个选择器(Selector)中,不时调用后者的选择(select)方法就能返回满足的选择键(SelectionKey),键中包含了SOCKET事件信息。这就是select模型
     * 
     * FileChannel提供了map方法来把文件影射为内存映像文件:MappedByteBuffer map(int mode,long position,long size);可以把文件的从position开始的size大小的区域映射为内存映像文件，mode指出了可访问该内存映像文件的方式:
     * READ_ONLY,（只读）:试图修改得到的缓冲区将导致抛出ReadOnlyBufferException.(MapMode.READ_ONLY);
     * READ_WRITE（读/写）:对得到的缓冲区的更改最终将传播到文件；该更改对映射到同一文件的其他程序不一定是可见的。(MapMode.READ_WRITE)
     * PRIVATE（专用）: 对得到的缓冲区的更改不会传播到文件，并且该更改对映射到同一文件的其他程序也不是可见的；相反，会创建缓冲区已修改部分的专用副本。(MapMode.PRIVATE)
     * 
     * MappedByteBuffer是ByteBuffer的子类，其扩充了三个方法:
     * force():缓冲区是READ_WRITE模式下，此方法对缓冲区内容的修改强行写入文件；
     * load():将缓冲区的内容载入内存，并返回该缓冲区的引用；
     * isLoaded():如果缓冲区的内容在物理内存中，则返回真，否则返回假
     */
    public void mappedByteBuffer() {
        File file = new File("D://data.txt");
        long len = file.length();
        byte[] ds = new byte[(int) len];

        try (RandomAccessFile randomAccessFile = new RandomAccessFile(file, "r");) {

            MappedByteBuffer mappedByteBuffer = randomAccessFile.getChannel().map(FileChannel.MapMode.READ_ONLY, 0,
                    len);
            for (int offset = 0; offset < len; offset++) {
                byte b = mappedByteBuffer.get();
                ds[offset] = b;
            }

            Scanner scan = new Scanner(new ByteArrayInputStream(ds)).useDelimiter(" ");
            while (scan.hasNext()) {
                System.out.print(scan.next() + " ");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        /**
         * map过程
         * FileChannel提供了map方法把文件映射到虚拟内存，通常情况可以映射整个文件，如果文件比较大，可以进行分段映射。
         * 
         * FileChannel中的几个变量:
         * MapMode
         * mode:内存映像文件访问的方式，共三种:MapMode.READ_ONLY:只读，试图修改得到的缓冲区将导致抛出异常。MapMode.READ_WRITE:读/写，对得到的缓冲区的更改最终将写入文件；但该更改对映射到同一文件的其他程序不一定是可见的。MapMode.PRIVATE:私用，可读可写,但是修改的内容不会写入文件，只是buffer自身的改变，这种能力称之为”copy on write”。
         * position:文件映射时的起始位置。
         * allocationGranularity:Memory allocation size for mapping buffers，通过native函数initIDs初始化。
         */

    }

}

/**
 * capacity(缓冲区数组的总长度,即可以容纳的最大数据量；在缓冲区创建时被设定并且不能改变)
 * capacity:作为一个内存块，Buffer有一个固定的大小值，也叫“capacity”.你只能往里写capacity个byte、long，char等类型。一旦Buffer满了，需要将其清空（通过读数据或者清除数据）才能继续写数据往里写数据。
 * 
 * position(下一个要操作的数据元素的位置,下一个要被读或写的元素的索引，每次读写缓冲区数据时都会改变改值，为下次读写作准备)
 * position:当你写数据到Buffer中时，position表示当前的位置。初始的position值为0.当一个byte、long等数据写到Buffer后，position会向前移动到下一个可插入数据的Buffer单元。position最大可为capacity – 1.当读取数据时，也是从某个特定位置读。当将Buffer从写模式切换到读模式，position会被重置为0.当从Buffer的position处读取数据时，position向前移动到下一个可读的位置。
 * 
 * limit(缓冲区数组中不可操作的下一个元素的位置:limit<=capacity表示缓冲区的当前终点，不能对缓冲区超过极限的位置进行读写操作。且极限是可以修改的)
 * limit:在写模式下，Buffer的limit表示你最多能往Buffer里写多少数据。写模式下，limit等于Buffer的capacity。当切换Buffer到读模式时，limit表示你最多能读到多少数据。因此，当切换Buffer到读模式时，limit会被设置成写模式下的position值。换句话说，你能读到之前写入的所有数据（limit被设置成已写数据的数量，这个值在写模式下就是position）
 *
 * mark(标记，调用mark()来设置mark=position，再调用reset()可以让position恢复到标记的位置) mark <= position <= limit <= capacity
 * position和limit的含义取决于Buffer处在读模式还是写模式。不管Buffer处在什么模式，capacity的含义总是一样的。
 */
/**
 * buffer常用方法
 * allocate(int capacity):从堆空间中分配一个容量大小为capacity的byte数组作为缓冲区的byte数据存储器
 * allocateDirect(int capacity):是不使用JVM堆栈而是通过操作系统来创建内存块用作缓冲区，它与当前操作系统能够更好的耦合，因此能进一步提高I/O操作速度。但是分配直接缓冲区的系统开销很大，因此只有在缓冲区较大并长期存在，或者需要经常重用时，才使用这种缓冲区
 * wrap(byte[] array):这个缓冲区的数据会存放在byte数组中，bytes数组或buff缓冲区任何一方中数据的改动都会影响另一方。其实ByteBuffer底层本来就有一个bytes数组负责来保存buffer缓冲区中的数据，通过allocate方法系统会帮你构造一个byte数组
 * wrap(byte[] array, int offset, int length):在上一个方法的基础上可以指定偏移量和长度，这个offset也就是包装后byteBuffer的position，而length呢就是limit-position的大小，从而我们可以得到limit的位置为length+position(offset)
 * limit(),limit(10)等:其中读取和设置这4个属性的方法的命名和jQuery中的val(),val(10)类似，一个负责get，一个负责set
 * reset():把position设置成mark的值，相当于之前做过一个标记，现在要退回到之前标记的地方
 * clear():position = 0;limit = capacity;mark = -1;有点初始化的味道，但是并不影响底层byte数组的内容
 * flip():limit = position;position = 0;mark = -1;翻转，也就是让flip之后的position到limit这块区域变成之前的0到position这块，翻转就是将一个处于存数据状态的缓冲区变为一个处于准备取数据的状态,一般在从Buffer读出数据前调用。
 * rewind():把position设为0，mark设为-1，不改变limit的值,一般在把数据重写入Buffer前调用,或者重新读取
 * remaining():return limit - position;返回limit和position之间相对位置差
 * hasRemaining():return position < limit返回是否还有未读内容
 * compact():把从position到limit中的内容移到0到limit-position的区域内，position和limit的取值也分别变成limit-position、capacity。如果先将positon设置到limit，再compact，那么相当于clear()
 * get() :相对读，从position位置读取一个byte，并将position+1，为下次读写作准备
 * get(int index):绝对读，读取byteBuffer底层的bytes中下标为index的byte，不改变position
 * get(byte[] dst, int offset, int length):从position位置开始相对读，读length个byte，并写入dst下标从offset到offset+length的区域
 * put(byte b):相对写，向position的位置写入一个byte，并将postion+1，为下次读写作准备
 * put(int index, byte b):绝对写，向byteBuffer底层的bytes中下标为index的位置插入byte b，不改变position
 * put(ByteBuffer src):用相对写，把src中可读的部分（也就是position到limit）写入此byteBuffer
 * put(byte[] src, int offset, int length):从src数组中的offset到offset+length区域读取数据并使用相对写写入此byteBuffer
 * ByteOrder order():检索此缓冲区的字节顺序。
 * ByteBuffer order(ByteOrder bo):修改缓冲区的字节顺序。
 * ByteBuffer putInt(int value):编写int值的相对put方法（可选操作）。以当前字节顺序将包含给定int值的四个字节写入当前位置的缓冲区，然后将位置递增四。
 * byte[] array():返回支持此缓冲区的字节数组（可选操作）。对此缓冲区内容的修改将导致返回的数组的内容被修改，反之亦然。在调用此方法之前调用hasArray方法，以确保此缓冲区具有可访问的后台阵列。
 * 
 * 通过调用Buffer.mark()方法，可以标记Buffer中的一个特定position。之后可以通过调用Buffer.reset()方法恢复到这个position
 * 
 * 可以使用equals()和compareTo()方法比较两个Buffer。
 * equals()
 * 当满足下列条件时，表示两个Buffer相等:有相同的类型（byte、char、int等）。Buffer中剩余的byte、char等的个数相等。Buffer中所有剩余的byte、char等都相同。
 * 如你所见，equals只是比较Buffer的一部分，不是每一个在它里面的元素都比较。实际上，它只比较Buffer中的剩余元素。
 * compareTo()方法
 * compareTo()方法比较两个Buffer的剩余元素(byte、char等)，如果满足下列条件，则认为一个Buffer“小于”另一个Buffer:第一个不相等的元素小于另一个Buffer中对应的元素。所有元素都相等，但第一个Buffer比另一个先耗尽(第一个Buffer的元素个数比另一个少)。
 * 
 */