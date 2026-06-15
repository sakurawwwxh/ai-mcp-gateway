package cn.tomato.ai.config;

import cn.tomato.ai.api.response.Response;
import cn.tomato.ai.types.enums.ResponseCode;
import com.alibaba.fastjson.JSON;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.regex.Pattern;

/**
 * 管理端鉴权拦截器
 * 读取 X-Mock-Token header，校验为合法 mock token 后放行
 * mock token 规则: 任意 mock_ 开头且非空（与前端 auth store 一致）
 *
 * @author Wxh
 * @date 2026-06-12
 */
@Slf4j
@Component
public class AdminAuthInterceptor implements HandlerInterceptor {

    private static final String TOKEN_HEADER = "X-Mock-Token";
    private static final Pattern MOCK_TOKEN = Pattern.compile("^mock_[A-Za-z0-9]+$");

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 放行 CORS 预检
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            return true;
        }
        String token = request.getHeader(TOKEN_HEADER);
        if (token != null && MOCK_TOKEN.matcher(token).matches()) {
            return true;
        }
        log.warn("admin 鉴权失败 token={} path={}", token, request.getRequestURI());
        writeUnauthorized(response);
        return false;
    }

    private void writeUnauthorized(HttpServletResponse response) throws Exception {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json;charset=UTF-8");
        Response<Void> body = Response.<Void>builder()
                .code(ResponseCode.UNAUTHORIZED.getCode())
                .info(ResponseCode.UNAUTHORIZED.getInfo())
                .build();
        response.getWriter().write(JSON.toJSONString(body));
    }

}
