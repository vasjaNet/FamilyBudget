package org.s3m.commonlib.config;

import lombok.Data;

/**
 * Common API response wrapper used across all services
 */
@Data
public class ResponseWrapper<T> {

    private String status;
    private String message;
    private T data;
    private long timestamp;

    public ResponseWrapper() {
        this.timestamp = System.currentTimeMillis();
    }

    public ResponseWrapper(String status, String message, T data) {
        this();
        this.status = status;
        this.message = message;
        this.data = data;
    }

    public static <T> ResponseWrapper<T> success(T data) {
        return new ResponseWrapper<>("success", "Operation successful", data);
    }

    public static <T> ResponseWrapper<T> success(String message, T data) {
        return new ResponseWrapper<>("success", message, data);
    }

    public static <T> ResponseWrapper<T> error(String message) {
        return new ResponseWrapper<>("error", message, null);
    }

    public static <T> ResponseWrapper<T> error(String message, T data) {
        return new ResponseWrapper<>("error", message, data);
    }
}

