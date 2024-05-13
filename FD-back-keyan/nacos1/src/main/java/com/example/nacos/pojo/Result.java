package com.example.nacos.pojo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Result<T> {
    private int code;
    private String msg;
    private T data;

    // 成功时使用的工厂方法
    public static <T> Result<T> success(T data) {
        return new Result<>(200, "success", data);
    }

    // 失败时使用的工厂方法
    public static <T> Result<T> error(String message) {
        return new Result<>(500, message, null);
    }

}
