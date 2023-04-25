package com.xmxe.study_demo.util.httpclient.okhttp;

import java.util.Objects;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

public class JacksonObjectUtil{
    private static final Logger LOGGER = LoggerFactory.getLogger(JacksonObjectUtil.class);

    private static ObjectMapper objectMapper = new ObjectMapper();

    static {
        // 忽略未知的字段
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        // 读取不认识的枚举时,当null值处理
        objectMapper.configure(DeserializationFeature.READ_UNKNOWN_ENUM_VALUES_AS_NULL, true);
        // 全部输出
        objectMapper.configure(JsonParser.Feature.AUTO_CLOSE_SOURCE, true);
    }


    /**
     * 将对象序列化成字节流
     * @param value
     * @return
     */
    public static byte[] objToByte(Object value){
        try {
            if(Objects.nonNull(value)){
                return objectMapper.writeValueAsBytes(value);
            }
        } catch (Exception e){
            LOGGER.warn("将对象序列化成字节失败",e);
        }
        return null;
    }

    /**
     * 将对象序列化成json字符串
     * @param value
     * @return
     */
    public static String objToJson(Object value){
        try {
            if(Objects.nonNull(value)){
                return objectMapper.writeValueAsString(value);
            }
        } catch (Exception e){
            LOGGER.warn("将对象序列化成json字符串失败",e);
        }
        return "";
    }

    /**
     * 将json字符串转对象,支持指定类
     * @param value
     * @return
     */
    public static <T> T jsonToObj(String value, Class<T> classType){
        try {
            if(StringUtils.isNotBlank(value)){
                return objectMapper.readValue(value, classType);
            }
        } catch (Exception e){
            LOGGER.warn("将json字符串转对象失败,字符串:" + value, e);
        }
        return null;
    }


    /**
     * 将json字符串转对象,支持范型类
     * @param value
     * @return
     */
    public static <T> T jsonToObj(String value, TypeReference<T> referenceType){
        try {
            if(StringUtils.isNotBlank(value)){
                return objectMapper.readValue(value, referenceType);
            }
        } catch (Exception e){
            LOGGER.warn("将json字符串转对象失败,字符串:" + value, e);
        }
        return null;
    }

    /**
     * 将字节流转对象,支持范型类
     * @param value
     * @return
     */
    public static <T> T byteToObj(byte[] value, TypeReference<T> referenceType){
        try {
            if(Objects.nonNull(value)){
                return objectMapper.readValue(value, referenceType);
            }
        } catch (Exception e){
            LOGGER.warn("将字节流转对象失败", e);
        }
        return null;
    }
}