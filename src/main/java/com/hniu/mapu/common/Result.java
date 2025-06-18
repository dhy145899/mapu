package com.hniu.mapu.common;

import lombok.Data;

/**
 * 统一响应结果类
 *
 * @param <T> 响应数据类型
 * @author jiujiu
 */
@Data
public class Result<T> {
    /**
     * 响应状态码
     */
    private Integer code;

    /**
     * 响应消息
     */
    private String message;

    /**
     * 响应数据
     */
    private T data;

    /**
     * 私有构造方法
     */
    private Result() {
    }

    /**
     * 成功响应
     *
     * @return Result对象
     */
    public static <T> Result<T> success() {
        Result<T> result = new Result<>();
        result.code = 200;
        result.message = "操作成功";
        return result;
    }

    /**
     * 成功响应带数据
     *
     * @param data 响应数据
     * @return Result对象
     */
    public static <T> Result<T> success(T data) {
        Result<T> result = new Result<>();
        result.code = 200;
        result.message = "操作成功";
        result.data = data;
        return result;
    }

    /**
     * 成功响应带数据和消息
     *
     * @param data 响应数据
     * @param message 响应消息
     * @return Result对象
     */
    public static <T> Result<T> success(T data, String message) {
        Result<T> result = new Result<>();
        result.code = 200;
        result.message = message;
        result.data = data;
        return result;
    }

    /**
     * 失败响应
     *
     * @param message 错误消息
     * @return Result对象
     */
    public static <T> Result<T> error(String message) {
        Result<T> result = new Result<>();
        result.code = 500;
        result.message = message;
        return result;
    }

    /**
     * 失败响应带状态码
     *
     * @param code    状态码
     * @param message 错误消息
     * @return Result对象
     */
    public static <T> Result<T> error(Integer code, String message) {
        Result<T> result = new Result<>();
        result.code = code;
        result.message = message;
        return result;
    }

    public static <T> Result<T> error() {
        Result<T> result = new Result<>();
        result.code = 500;
        result.message = "操作失败";
        return result;
    }

    /*public static <T> Result<T> success(String message) {
        Result<T> result = new Result<>();
        result.code = 200;
        result.message = message;
        result.data = null;
        return result;
    }

    public static <T> Result<T> toAjax(Integer row, String message) {
        return row > 0 ? Result.success(message + "成功") : Result.error(message + "失败");
    }

    public static <T> Result<T> toAjax(Integer row) {
        return row > 0 ? Result.success() : Result.error();
    }*/
}