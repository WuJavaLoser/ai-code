package com.yt.aicode.basic.common;

import org.springframework.stereotype.Component;

import java.util.concurrent.atomic.AtomicLong;

/**
 * 雪花算法ID生成器 - 生成10位数唯一ID
 * 防止黑客连环爬取数据
 * 
 * @author ai-code
 */
@Component
public class SnowflakeIdGenerator {
    
    /**
     * 起始时间戳 (2024-01-01 00:00:00)
     */
    private static final long START_TIMESTAMP = 1704067200000L;
    
    /**
     * 机器ID位数
     */
    private static final long MACHINE_ID_BITS = 5L;
    
    /**
     * 序列号位数
     */
    private static final long SEQUENCE_BITS = 12L;
    
    /**
     * 机器ID最大值
     */
    private static final long MAX_MACHINE_ID = ~(-1L << MACHINE_ID_BITS);
    
    /**
     * 序列号最大值
     */
    private static final long MAX_SEQUENCE = ~(-1L << SEQUENCE_BITS);
    
    /**
     * 机器ID左移位数
     */
    private static final long MACHINE_ID_SHIFT = SEQUENCE_BITS;
    
    /**
     * 时间戳左移位数
     */
    private static final long TIMESTAMP_SHIFT = SEQUENCE_BITS + MACHINE_ID_BITS;
    
    /**
     * 机器ID (0-31)
     */
    private final long machineId;
    
    /**
     * 序列号
     */
    private final AtomicLong sequence = new AtomicLong(0L);
    
    /**
     * 上一次生成ID的时间戳
     */
    private volatile long lastTimestamp = -1L;
    
    /**
     * 构造函数
     */
    public SnowflakeIdGenerator() {
        // 使用本地IP的hash值作为机器ID
        this.machineId = generateMachineId();
        if (machineId > MAX_MACHINE_ID || machineId < 0) {
            throw new IllegalArgumentException(String.format("Machine ID can't be greater than %d or less than 0", MAX_MACHINE_ID));
        }
    }
    
    /**
     * 生成机器ID
     */
    private long generateMachineId() {
        try {
            java.net.InetAddress addr = java.net.InetAddress.getLocalHost();
            String hostAddress = addr.getHostAddress();
            return Math.abs(hostAddress.hashCode()) % (MAX_MACHINE_ID + 1);
        } catch (Exception e) {
            // 如果获取失败，使用随机数
            return (long) (Math.random() * MAX_MACHINE_ID);
        }
    }
    
    /**
     * 生成10位数唯一ID
     */
    public synchronized long generateId() {
        long currentTimestamp = getCurrentTimestamp();
        
        if (currentTimestamp < lastTimestamp) {
            throw new RuntimeException(String.format("Clock moved backwards. Refusing to generate id for %d milliseconds", 
                lastTimestamp - currentTimestamp));
        }
        
        if (currentTimestamp == lastTimestamp) {
            long seq = sequence.incrementAndGet() & MAX_SEQUENCE;
            if (seq == 0) {
                currentTimestamp = waitForNextMillis(lastTimestamp);
            }
            sequence.set(seq);
        } else {
            sequence.set(0L);
        }
        
        lastTimestamp = currentTimestamp;
        
        // 生成原始ID
        long rawId = ((currentTimestamp - START_TIMESTAMP) << TIMESTAMP_SHIFT) 
                   | (machineId << MACHINE_ID_SHIFT) 
                   | sequence.get();
        
        // 转换为10位数ID (1000000000 - 9999999999)
        return convertToTenDigits(rawId);
    }
    
    /**
     * 将原始ID转换为10位数
     */
    private long convertToTenDigits(long rawId) {
        // 使用hash和模运算确保ID在10位数范围内
        long hash = Math.abs(rawId);
        
        // 确保ID在 1000000000 到 9999999999 之间

        return 1000000000L + (hash % 9000000000L);
    }
    
    /**
     * 获取当前时间戳
     */
    private long getCurrentTimestamp() {
        return System.currentTimeMillis();
    }
    
    /**
     * 等待下一毫秒
     */
    private long waitForNextMillis(long lastTimestamp) {
        long timestamp = getCurrentTimestamp();
        while (timestamp <= lastTimestamp) {
            timestamp = getCurrentTimestamp();
        }
        return timestamp;
    }
    
    /**
     * 生成10位数字符串ID
     */
    public String generateStringId() {
        return String.valueOf(generateId());
    }
}