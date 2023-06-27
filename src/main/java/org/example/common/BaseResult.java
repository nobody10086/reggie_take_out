package org.example.common;

import lombok.Data;

import java.util.HashMap;
import java.util.Map;

/**
 * 通用返回结果类,服务端相应数据最终都会封装在这里
 * param<T>
 * */
@Data
public class BaseResult<T> {

    private Integer code; //1：成功

    private String msg;

    private T data;

    private Map map = new HashMap();

    public static <T> BaseResult<T> success(T object) {
        BaseResult<T> baseResult = new BaseResult<T>();
        baseResult.data = object;
        baseResult.code = 1;
        return baseResult;
    }

    public static <T> BaseResult<T> error(String msg) {
        BaseResult<T> baseResult = new BaseResult<T>();
        baseResult.code = 200;
        baseResult.msg = msg;
        return baseResult;
    }

    public BaseResult<T> add(String key, Object value) {
        this.map.put(key,value);
        return this;
    }
}
