package com.example.task.DTO;

public class ResponseDTO<T> {
    private int code;
    private String message;
    private T data;
    private String error;

    public ResponseDTO(int code, String message, T data, String error) {
        this.code = code;
        this.message = message;
        this.data = data;
        this.error = error;
    }

    public static <T> ResponseDTO<T> success(String message, T data) {
        return new ResponseDTO<>(200, message, data, null);
    }

    public static <T> ResponseDTO<T> error(int code, String errorMessage) {
        return new ResponseDTO<>(code, null, null, errorMessage);
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public T getData() {
        return data;
    }

    public String getError() {
        return error;
    }
}
