package com.xmxe.util;

/**
 * MySQL使用无符号整数位存储ip地址
 * 如果要存IP地址,用什么数据类型比较好？作者建议当存储IPv4地址时,应该使用32位的无符号整数（UNSIGNED INT）来存储IP地址,而不是使用字符串
 * 相对字符串存储,使用无符号整数来存储有如下的好处：节省空间,不管是数据存储空间,还是索引存储空间;便于使用范围查询（BETWEEN...AND）,且效率更高
 */
public class IpLongUtil {
    /**
     * 把字符串IP转换成long
     *
     * @param ipStr 字符串IP
     * @return IP对应的long值
     */
    public static long ip2Long(String ipStr) {
        String[] ip = ipStr.split("\\.");
        return (Long.valueOf(ip[0]) << 24) + (Long.valueOf(ip[1]) << 16)
                + (Long.valueOf(ip[2]) << 8) + Long.valueOf(ip[3]);
    }

    /**
     * 把IP的long值转换成字符串
     *
     * @param ipLong IP的long值
     * @return long值对应的字符串
     */
    public static String long2Ip(long ipLong) {
        StringBuilder ip = new StringBuilder();
        ip.append(ipLong >>> 24).append(".");
        ip.append((ipLong >>> 16) & 0xFF).append(".");
        ip.append((ipLong >>> 8) & 0xFF).append(".");
        ip.append(ipLong & 0xFF);
        return ip.toString();
    }

    public static void main(String[] args) {
        System.out.println(ip2Long("192.168.0.1"));
        System.out.println(long2Ip(3232235521L));
        System.out.println(ip2Long("10.0.0.1"));
    }

}