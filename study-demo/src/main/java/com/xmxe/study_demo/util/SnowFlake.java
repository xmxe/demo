package com.xmxe.study_demo.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * 雪花算法生成分布式ID
 */
public class SnowFlake {

    /**
     * 起始的时间戳
     */
    private final static long START_STMP = 1480166465631L;

    /**
     * 每一部分占用的位数
     */
    private final static long SEQUENCE_BIT = 12; // 序列号占用的位数
    private final static long MACHINE_BIT = 5; // 机器标识占用的位数
    private final static long DATACENTER_BIT = 5;// 数据中心占用的位数

    /**
     * 每一部分的最大值
     */
    private final static long MAX_DATACENTER_NUM = -1L ^ (-1L << DATACENTER_BIT);
    private final static long MAX_MACHINE_NUM = -1L ^ (-1L << MACHINE_BIT);
    private final static long MAX_SEQUENCE = -1L ^ (-1L << SEQUENCE_BIT);

    /**
     * 每一部分向左的位移
     */
    private final static long MACHINE_LEFT = SEQUENCE_BIT;
    private final static long DATACENTER_LEFT = SEQUENCE_BIT + MACHINE_BIT;
    private final static long TIMESTMP_LEFT = DATACENTER_LEFT + DATACENTER_BIT;

    private long datacenterId; // 数据中心
    private long machineId; // 机器标识
    private long sequence = 0L; // 序列号
    private long lastStmp = -1L;// 上一次时间戳

    public SnowFlake(long datacenterId, long machineId) {
        if (datacenterId > MAX_DATACENTER_NUM || datacenterId < 0) {
            throw new IllegalArgumentException("datacenterId can't be greater than MAX_DATACENTER_NUM or less than 0");
        }
        if (machineId > MAX_MACHINE_NUM || machineId < 0) {
            throw new IllegalArgumentException("machineId can't be greater than MAX_MACHINE_NUM or less than 0");
        }
        this.datacenterId = datacenterId;
        this.machineId = machineId;
    }

    /**
     * 产生下一个ID
     *
     * @return
     */
    public synchronized long nextId() {
        long currStmp = getNewstmp();
        if (currStmp < lastStmp) {
            throw new RuntimeException("Clock moved backwards.  Refusing to generate id");
        }

        if (currStmp == lastStmp) {
            // 相同毫秒内，序列号自增
            sequence = (sequence + 1) & MAX_SEQUENCE;
            // 同一毫秒的序列数已经达到最大
            if (sequence == 0L) {
                currStmp = getNextMill();
            }
        } else {
            // 不同毫秒内，序列号置为0
            sequence = 0L;
        }

        lastStmp = currStmp;

        return (currStmp - START_STMP) << TIMESTMP_LEFT // 时间戳部分
                | datacenterId << DATACENTER_LEFT // 数据中心部分
                | machineId << MACHINE_LEFT // 机器标识部分
                | sequence; // 序列号部分
    }

    private long getNextMill() {
        long mill = getNewstmp();
        while (mill <= lastStmp) {
            mill = getNewstmp();
        }
        return mill;
    }

    private long getNewstmp() {
        return System.currentTimeMillis();
    }

    public static void main(String[] args) {
        SnowFlake snowFlake = new SnowFlake(2, 3);

        for (int i = 0; i < (1 << 12); i++) {
            System.out.println(snowFlake.nextId());
        }

    }
}



/**
 * 饿汉式单例模式实现雪花算法
 **/
class SnowFlakeSingleE {

    private static SnowFlakeSingleE snowFlake = new SnowFlakeSingleE();

    private SnowFlakeSingleE() {}

    public static SnowFlakeSingleE getInstance() {
        return snowFlake;
    }

    // 序列号，同一毫秒内用此参数来控制并发
    private long sequence = 0L;
    // 上一次生成编号的时间串，格式：yyMMddHHmmssSSS
    private String lastTime = "";

    public synchronized String getNum() {
        String nowTime = getTime(); // 获取当前时间串，格式：yyMMddHHmmssSSS
        String machineId = "01"; // 机器编号，这里假装获取到的机器编号是2。实际项目中可从配置文件中读取
        // 本次和上次不是同一毫秒，直接生成编号返回
        if (!lastTime.equals(nowTime)) {
            sequence = 0L; // 重置序列号，方便下次使用
            lastTime = nowTime; // 更新时间串，方便下次使用
            return new StringBuilder(nowTime).append(machineId).append(sequence).toString();
        }
        // 本次和上次在同一个毫秒内，需要用序列号控制并发
        if (sequence < 99) { // 序列号没有达到最大值，直接生成编号返回
            sequence = sequence + 1;
            return new StringBuilder(nowTime).append(machineId).append(sequence).toString();
        }
        // 序列号达到最大值，需要等待下一毫秒的到来
        while (lastTime.equals(nowTime)) {
            nowTime = getTime();
        }
        sequence = 0L; // 重置序列号，方便下次使用
        lastTime = nowTime; // 更新时间串，方便下次使用
        return new StringBuilder(nowTime).append(machineId).append(sequence).toString();
    }

    private String getTime() {
        return LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyMMddHHmmssSSS"));
    }
}


/**
 * 懒汉式单例模式实现雪花算法
 **/
class SnowFlakeSingleL {

    private static SnowFlakeSingleL snowFlake = null;

    private SnowFlakeSingleL() {}

    public static SnowFlakeSingleL getInstance() {
        if (snowFlake == null) {
            synchronized (SnowFlakeSingleL.class) {
                if (snowFlake == null) {
                    snowFlake = new SnowFlakeSingleL();
                }
                return snowFlake;
            }
        }
        return snowFlake;
    }

    // 序列号，同一毫秒内用此参数来控制并发
    private long sequence = 0L;
    // 上一次生成编号的时间串，格式：yyMMddHHmmssSSS
    private String lastTime = "";

    public synchronized String getNum() {
        String nowTime = getTime(); // 获取当前时间串，格式：yyMMddHHmmssSSS
        String machineId = "01"; // 机器编号，这里假装获取到的机器编号是2。实际项目中可从配置文件中读取
        // 本次和上次不是同一毫秒，直接生成编号返回
        if (!lastTime.equals(nowTime)) {
            sequence = 0L; // 重置序列号，方便下次使用
            lastTime = nowTime; // 更新时间串，方便下次使用
            return new StringBuilder(nowTime).append(machineId).append(sequence).toString();
        }
        // 本次和上次在同一个毫秒内，需要用序列号控制并发
        if (sequence < 99) { // 序列号没有达到最大值，直接生成编号返回
            sequence = sequence + 1;
            return new StringBuilder(nowTime).append(machineId).append(sequence).toString();
        }
        // 序列号达到最大值，需要等待下一毫秒的到来
        while (lastTime.equals(nowTime)) {
            nowTime = getTime();
        }
        sequence = 0L; // 重置序列号，方便下次使用
        lastTime = nowTime; // 更新时间串，方便下次使用
        return new StringBuilder(nowTime).append(machineId).append(sequence).toString();
    }

    private String getTime() {
        return LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyMMddHHmmssSSS"));
    }
}