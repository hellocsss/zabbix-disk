package com.zabbix.disk.result;

import lombok.Data;

import java.io.Serializable;

@Data
public class ResponseEntity<T> implements Serializable {

    private static final long serialVersionUID = -1946423580509554322L;

    /**状态码**/
    private int code;

    /**信息*/
    private String message;

    /**结果数据**/
    private T result;

    /**
     * 成功码.
     */
    public static final int SUCCESS_CODE = 200;

    /**
     * 成功信息.
     */
    public static final String SUCCESS_MESSAGE = "操作成功";

    /**
     * 错误码.
     */
    public static final int ERROR_CODE = 500;

    /**
     * 错误信息.
     */
    public static final String ERROR_MESSAGE = "内部异常";

    ResponseEntity() {
        this(SUCCESS_CODE,SUCCESS_MESSAGE);
    }

    ResponseEntity(int code, String message){
        this(code, message, null);
    }

    ResponseEntity(int code, String message, T result){
        super();
        this.code(code).message(message).result(result);
    }

    private ResponseEntity<T> code(int code){
        this.code = code;
        return this;
    }

    private ResponseEntity<T> message(String message){
        this.message = message;
        return this;
    }

    private ResponseEntity<T> result(T result){
        this.result = result;
        return this;
    }

}
