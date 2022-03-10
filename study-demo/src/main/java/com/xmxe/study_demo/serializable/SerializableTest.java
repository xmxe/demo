package com.xmxe.study_demo.serializable;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

public class SerializableTest {
    public static void main(String[] args) throws Exception {
        serializeFlyPig();
        FlyPig flyPig = deserializeFlyPig();
        System.out.println(flyPig.toString());
    }
    /**
     * 序列化
     */
    private static void serializeFlyPig() throws Exception {
        FlyPig flyPig = new FlyPig();
        flyPig.setColor("black");
        flyPig.setName("riemann");
        flyPig.setName("audi");
        // ObjectOutputStream 对象输出流，将 flyPig 对象存储到E盘的 flyPig.txt 文件中，完成对 flyPig 对象的序列化操作
        ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(new File("C:\\Users\\wangx\\Desktop\\flypig.txt")));
        oos.writeObject(flyPig);
        System.out.println("FlyPig 对象序列化成功！");
        oos.close();
    }

    /**
     * 反序列化
     */
    private static FlyPig deserializeFlyPig() throws Exception {
        ObjectInputStream ois = new ObjectInputStream(new FileInputStream(new File("C:\\Users\\wangx\\Desktop\\flypig.txt")));
        FlyPig pig = (FlyPig) ois.readObject();
        System.out.println("FlyPig 对象反序列化成功！");
        return pig;
    }

    /**
     * map写文件
     */
    public void writeFileByMap(Map<String,Object> map){
        File file = new File("");
        try{
            StringBuffer stringBuffer = new StringBuffer();
            FileWriter fileWriter = new FileWriter(file,true);
            Set<Entry<String,Object>> set = map.entrySet();
            Iterator<Entry<String,Object>> it = set.iterator();
            while(it.hasNext()){
                Map.Entry<String,Object> en = it.next();
                if(en.getKey().equals("anObject")){
                    stringBuffer.append(en.getKey()+":"+en.getValue()).append(System.getProperty("line.separator"));

                }
            }
            fileWriter.write(stringBuffer.toString());
            fileWriter.close();

        }catch(Exception e){
            e.printStackTrace();
        }
    }

}

class FlyPig implements Serializable {

    private static final long serialVersionUID = 1L;
    
    private static String AGE = "269";
    private String name;
    private String color;
    transient private String car;

    private String addTip;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getCar() {
        return car;
    }

    public void setCar(String car) {
        this.car = car;
    }

    public String getAddTip() {
        return addTip;
    }

    public void setAddTip(String addTip) {
        this.addTip = addTip;
    }

    @Override
    public String toString() {
        return "FlyPig{" + "name='" + name + '\'' + ", color='" + color + '\'' + ", car='" + car + '\'' + ", AGE='"
                + AGE + '\'' + '}';
    }

}


/**
 * Java Serializable：明明就一个空的接口嘛(https://mp.weixin.qq.com/s/7ojBbuJ4For2VvgcpqIuVw)
 * 什么是序列化 怎么序列化 为什么序列化 反序列化会遇到什么问题，如何解决(https://mp.weixin.qq.com/s/iIqQeQNeDKimTT5nvye-ow)
 * Java序列化和反序列化为什么要实现Serializable接口(https://mp.weixin.qq.com/s/RLzpPOlKv5omoqRv-5ckPQ)
 * 关于序列化/反序列化，我梭哈(https://mp.weixin.qq.com/s/uTrNn_C-wnKPQieUcQ9z5g)
 */