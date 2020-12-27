package com.zabbix.disk.result;

import org.apache.commons.lang3.StringUtils;

public class ResponseResult {

    private ResponseResult() {
    }

    public static <E> ResponseEntity<E> resp(int code, String message, E o) {
        return new ResponseEntity<>(code, message, o);
    }

    public static <E> ResponseEntity<E> resp(int code, String message) {
        return resp(code, message, null);
    }

    public static <E> ResponseEntity<E> resp(int code) {
        return resp(code, null);
    }

    public static <E> ResponseEntity<E> resp(Exception e) {
        return new ResponseEntity<>(ResponseEntity.ERROR_CODE, e.getMessage());
    }

    /**
     * 请求错误
     * @param
     * @param <E>
     * @return
     */
    public static <E> ResponseEntity<E> error() {
        return resp(ResponseEntity.ERROR_CODE, ResponseEntity.ERROR_MESSAGE);
    }

    /**
     * 请求错误带信息
     * @param message
     * @param <E>
     * @return
     */
    public static <E> ResponseEntity<E> error(String message) {
        return resp(ResponseEntity.ERROR_CODE, StringUtils.isBlank(message) ? ResponseEntity.ERROR_MESSAGE : message);
    }

    /**
     * 请求成功
     * @param <E>
     * @return
     */
    public static <E> ResponseEntity<E> success() {
        return new ResponseEntity<>();
    }

    /**
     * 请求成功
     * @param o
     * @param <E>
     * @return
     */
    public static <E> ResponseEntity<E> success(E o) {
        return new ResponseEntity<>(ResponseEntity.SUCCESS_CODE, ResponseEntity.SUCCESS_MESSAGE, o);
    }
}
