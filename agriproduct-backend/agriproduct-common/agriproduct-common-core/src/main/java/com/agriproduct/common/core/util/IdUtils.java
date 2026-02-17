package com.agriproduct.common.core.util;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * ID生成工具类（雪花算法）
 */
public class IdUtils {

    /**
     * 开始时间戳 (2024-01-01)
     */
    private static final long START_TIMESTAMP = 1704067200000L;

    /**
     * 数据中心ID所占位数
     */
    private static final long DATACENTER_ID_BITS = 5L;

    /**
     * 机器ID所占位数
     */
    private static final long WORKER_ID_BITS = 5L;

    /**
     * 序列号所占位数
     */
    private static final long SEQUENCE_BITS = 12L;

    /**
     * 数据中心ID最大值
     */
    private static final long MAX_DATACENTER_ID = ~(-1L << DATACENTER_ID_BITS);

    /**
     * 机器ID最大值
     */
    private static final long MAX_WORKER_ID = ~(-1L << WORKER_ID_BITS);

    /**
     * 序列号最大值
     */
    private static final long MAX_SEQUENCE = ~(-1L << SEQUENCE_BITS);

    /**
     * 机器ID左移位数
     */
    private static final long WORKER_ID_SHIFT = SEQUENCE_BITS;

    /**
     * 数据中心ID左移位数
     */
    private static final long DATACENTER_ID_SHIFT = SEQUENCE_BITS + WORKER_ID_BITS;

    /**
     * 时间戳左移位数
     */
    private static final long TIMESTAMP_SHIFT = SEQUENCE_BITS + WORKER_ID_BITS + DATACENTER_ID_BITS;

    /**
     * 数据中心ID
     */
    private static long datacenterId = 0L;

    /**
     * 机器ID
     */
    private static long workerId = 0L;

    /**
     * 序列号
     */
    private static long sequence = 0L;

    /**
     * 上一次时间戳
     */
    private static long lastTimestamp = -1L;

    static {
        // 根据IP地址生成数据中心ID和机器ID
        try {
            InetAddress ip = InetAddress.getLocalHost();
            String hostAddress = ip.getHostAddress();
            String[] segments = hostAddress.split("\\.");
            if (segments.length == 4) {
                datacenterId = Long.parseLong(segments[2]) % 32;
                workerId = Long.parseLong(segments[3]) % 32;
            }
        } catch (UnknownHostException e) {
            // 使用默认值
            datacenterId = 0L;
            workerId = 0L;
        }
    }

    /**
     * 获取下一个ID（线程安全）
     */
    public static synchronized long nextId() {
        long timestamp = getCurrentTimestamp();

        // 时钟回拨检查
        if (timestamp < lastTimestamp) {
            throw new RuntimeException(String.format("时钟回拨 %d 毫秒", lastTimestamp - timestamp));
        }

        // 同一毫秒内
        if (timestamp == lastTimestamp) {
            sequence = (sequence + 1) & MAX_SEQUENCE;
            // 序列号溢出
            if (sequence == 0) {
                timestamp = waitNextMillis(lastTimestamp);
            }
        } else {
            // 不同毫秒，序列号重置
            sequence = 0L;
        }

        lastTimestamp = timestamp;

        // 生成ID
        return ((timestamp - START_TIMESTAMP) << TIMESTAMP_SHIFT)
                | (datacenterId << DATACENTER_ID_SHIFT)
                | (workerId << WORKER_ID_SHIFT)
                | sequence;
    }

    /**
     * 获取下一个ID（字符串）
     */
    public static String nextIdStr() {
        return String.valueOf(nextId());
    }

    /**
     * 获取当前时间戳
     */
    private static long getCurrentTimestamp() {
        return System.currentTimeMillis();
    }

    /**
     * 等待下一毫秒
     */
    private static long waitNextMillis(long lastTimestamp) {
        long timestamp = getCurrentTimestamp();
        while (timestamp <= lastTimestamp) {
            timestamp = getCurrentTimestamp();
        }
        return timestamp;
    }

    /**
     * 设置数据中心ID
     */
    public static void setDatacenterId(long id) {
        if (id < 0 || id > MAX_DATACENTER_ID) {
            throw new IllegalArgumentException(String.format("数据中心ID范围: 0-%d", MAX_DATACENTER_ID));
        }
        datacenterId = id;
    }

    /**
     * 设置机器ID
     */
    public static void setWorkerId(long id) {
        if (id < 0 || id > MAX_WORKER_ID) {
            throw new IllegalArgumentException(String.format("机器ID范围: 0-%d", MAX_WORKER_ID));
        }
        workerId = id;
    }

    /**
     * 生成订单号
     */
    public static String generateOrderNo() {
        return String.format("%d%06d", System.currentTimeMillis() / 1000, (int) (Math.random() * 1000000));
    }
}
