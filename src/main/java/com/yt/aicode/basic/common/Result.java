package com.yt.aicode.basic.common;

import com.yt.aicode.exception.ErrorCode;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 通用返回类 (内置静态工厂方法)
 *
 * @param <T> 数据类型
 * @author wys17
 */
@Data
public class Result<T> implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    // 1. 字段：定义数据结构
    private int code;
    private T data;
    private String message;

    // 2. 构造函数：用于创建实例的基础 (通常设为 private，强制使用静态方法)
    // 这里我们保持 public，使其更灵活
    public Result(int code, T data, String message) {
        this.code = code;
        this.data = data;
        this.message = message;
    }

    public Result(int code, T data) {
        this(code, data, "");
    }

    public Result(ErrorCode errorCode) {
        this(errorCode.getCode(), null, errorCode.getMessage());
    }

    // 3. 静态工厂方法：提供便捷的创建入口 (从 ResultUtils 移入)

    /**
     * 成功
     *
     * @param data 数据
     * @param <T>  数据类型
     * @return 响应
     */
    public static <T> Result<T> success(T data) {
        return new Result<>(0, data, "ok");
    }

    /**
     * 失败
     *
     * @param errorCode 错误码
     * @return 响应
     */
    public static Result<?> error(ErrorCode errorCode) {
        return new Result<>(errorCode);
    }

    /**
     * 失败
     *
     * @param code    错误码
     * @param message 错误信息
     * @return 响应
     */
    public static Result<?> error(int code, String message) {
        return new Result<>(code, null, message);
    }

    /**
     * 失败
     *
     * @param errorCode 错误码
     * @param message   自定义覆盖信息
     * @return 响应
     */
    public static Result<?> error(ErrorCode errorCode, String message) {
        return new Result<>(errorCode.getCode(), null, message);
    }
}