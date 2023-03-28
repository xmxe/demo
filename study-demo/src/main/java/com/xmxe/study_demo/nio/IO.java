package com.xmxe.study_demo.nio;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;

public class IO {
    /**
     * 传统io操作读取文件
     */
    public void traditionIORead() {
        InputStream in = null;
        try {
            in = new BufferedInputStream(new FileInputStream("src/nomal_io.txt"));
            byte[] buf = new byte[1024];
            int bytesRead = in.read(buf);
            while (bytesRead != -1) {
                for (int i = 0; i < bytesRead; i++)
                    System.out.print((char) buf[i]);
                bytesRead = in.read(buf);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (in != null) {
                    in.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 使用BufferReader
     * 
     * @param path
     * @return
     */
    public String bufferReader(String path) {
        try {
            String pathname = path;
            File filename = new File(pathname);
            // 文件字节输入流对象
            FileInputStream fileInputStream = new FileInputStream(filename);
            // 字节转字符输入流对象 InputStreamReader：字节流到字符流的桥梁
            InputStreamReader reader = new InputStreamReader(fileInputStream, "GBK");
            // BufferedReader(字符缓冲输入流)，提供通用的缓冲方式文本读取
            BufferedReader br = new BufferedReader(reader);
            // 多线程StringBuffer 单线程StringBuilder
            StringBuffer txt = new StringBuffer();
            String line = "";
            while ((line = br.readLine()) != null) {
                txt.append(line + ";");
            }
            // 方法一：流的关闭：先打开的后关闭，后打开的先关闭
            // 方法二：可以只调用外层流的close方法关闭其装饰的内层流
            br.close();
            return txt.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    
    public static boolean writeFile(String path, String content) {
        // 相对路径，如果没有则要建立一个新的path文件
        File file = new File(path);
        try {
            // 创建新文件
            file.createNewFile();
            // 字符缓冲输出流：写东西到该文件
            BufferedWriter out = new BufferedWriter(new FileWriter(file));
            // 写东西：\r\n即为换行
            out.write(content);
            // 把缓存区内容压入文件
            out.flush();
            // 最后关闭流
            out.close();
            // 返回成功
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

   
    public void writeFile(File file) {
        OutputStream out = null;
        try {
            // 根据文件创建文件的输出流
            out = new FileOutputStream(file);
            String message = "我是好人。";
            // 把内容转换成字节数组
            byte[] data = message.getBytes();
            // 向文件写入内容
            out.write(data);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                // 关闭输出流
                if (out != null)
                    out.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 复制文件
     */
    public void copyFile() {
        // 构建源文件
        File file = new File("E:" + File.separator + "HelloWorld.txt");
        // 构建目标文件
        File fileCopy = new File("D:" + File.separator + "HelloWorld");
        InputStream in = null;
        OutputStream out = null;
        try {
            // 目标文件不存在就创建
            if (!(fileCopy.exists())) {
                fileCopy.createNewFile();
            }
            // 源文件创建输入流
            in = new FileInputStream(file);
            // 目标文件创建输出流
            out = new FileOutputStream(fileCopy, true);
            // 创建字节数组
            byte[] temp = new byte[1024];
            int length = 0;
            // 源文件读取一部分内容
            while ((length = in.read(temp)) != -1) {
                // 目标文件写入一部分内容
                out.write(temp, 0, length);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                // 关闭文件输入输出流
                if (in != null)
                    in.close();
                if (out != null)
                    out.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}
