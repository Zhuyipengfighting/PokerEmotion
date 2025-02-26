package per.test.p_emotion_springboot.common;

public class ApiResponse<T> {
    private int code;
    private String message;
    private T data;

    // 成功响应（无数据）
    public static <T> ApiResponse<T> success() {
        return new ApiResponse<>(200, "成功", null);
    }

    // 成功响应（带数据）
    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(200, "成功", data);
    }

    // 错误响应
    public static <T> ApiResponse<T> error(int code, String message) {
        return new ApiResponse<>(code, message, null);
    }

    // 构造函数（私有化）
    private ApiResponse(int code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    // Getter 方法
    public int getCode() { return code; }
    public String getMessage() { return message; }
    public T getData() { return data; }
}