package com.xmxe.study_demo.serializable;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

// 什么是序列化 怎么序列化 为什么序列化 反序列化会遇到什么问题，如何解决(https://mp.weixin.qq.com/s/iIqQeQNeDKimTT5nvye-ow)
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
