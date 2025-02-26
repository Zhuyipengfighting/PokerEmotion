package per.test.p_emotion_springboot.common;

import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;
import per.test.p_emotion_springboot.common.ApiResponse;

@RestControllerAdvice
public class ResponseWrapperConfig implements ResponseBodyAdvice<Object> {

    // 对所有控制器方法生效
    @Override
    public boolean supports(MethodParameter returnType, Class converterType) {
        return true;
    }

    // 自动包装返回值
    @Override
    public Object beforeBodyWrite(Object body, MethodParameter returnType, MediaType selectedContentType,
                                  Class selectedConverterType, ServerHttpRequest request, ServerHttpResponse response) {
        // 如果已经是 ApiResponse 类型，直接返回
        if (body instanceof ApiResponse) {
            return body;
        }
        // 包装字符串需要特殊处理
        if (body instanceof String) {
            return JsonUtils.toJson(ApiResponse.success(body));
        }
        return ApiResponse.success(body);
    }
}
