package cn.tomato.ai.infrastructure.adapter.port;

import cn.tomato.ai.domain.session.adapter.port.ISessionPort;
import cn.tomato.ai.domain.session.model.valobj.gateway.McpToolProtocolConfigVO;
import cn.tomato.ai.infrastructure.gateway.GenericHttpGateway;
import cn.tomato.ai.types.enums.ResponseCode;
import cn.tomato.ai.types.exception.AppException;
import com.alibaba.fastjson.JSON;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.Resource;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import org.springframework.stereotype.Component;
import retrofit2.Call;

import java.io.IOException;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import lombok.extern.slf4j.Slf4j;

/**
 * 会话端口适配器 - 实现领域层定义的 ISessionPort 接口
 * 负责调用外部 HTTP 服务，将 MCP 工具调用转发到实际的后端 API
 */
@Slf4j
@Component
public class SessionPort implements ISessionPort {

    @Resource
    private GenericHttpGateway gateway;

    /** JSON 序列化工具，用于解析请求头等 JSON 字符串 */
    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * 执行工具调用 - 根据 HTTP 配置发起请求
     *
     * @param httpConfig HTTP 配置信息（URL、请求方法、请求头等）
     * @param params     请求参数，格式为 Map，包含请求体或查询参数
     * @return 响应结果字符串
     * @throws IOException 请求执行异常
     */
    @Override
    public Object toolCall(McpToolProtocolConfigVO.HTTPConfig httpConfig, Object params) throws IOException {
        // 1. 构建请求头
        String httpHeadersJson = httpConfig.getHttpHeaders();

        Map<String, Object> headers = objectMapper.readValue(httpHeadersJson, Map.class);

        // 2. 判断请求方法
        String httpMethod = httpConfig.getHttpMethod().toLowerCase();

        // 3. 参数校验
        if (!(params instanceof Map<?, ?> arguments)) {
            throw new AppException(ResponseCode.ILLEGAL_PARAMETER.getCode(), ResponseCode.ILLEGAL_PARAMETER.getInfo());
        }

        // 调试日志
        log.info("HTTP调用参数 - URL: {}, Method: {}, Arguments: {}", httpConfig.getHttpUrl(), httpMethod, JSON.toJSONString(arguments));

        // 修复 IPv6 问题：将 localhost 替换为 127.0.0.1
        String url = httpConfig.getHttpUrl().replace("localhost", "127.0.0.1");

        switch (httpMethod) {
            // POST 请求
            case "post": {
                // 取第一个参数值作为请求体
                Object requestBodyObj = arguments.values().toArray()[0];
                String requestBodyJson = JSON.toJSONString(requestBodyObj);
                log.info("POST请求体: {}", requestBodyJson);

                RequestBody requestBody = RequestBody.create(requestBodyJson,
                        MediaType.parse("application/json"));

                Call<ResponseBody> call = gateway.post(url, headers, requestBody);
                retrofit2.Response<ResponseBody> response = call.execute();

                log.info("HTTP响应状态码: {}", response.code());
                ResponseBody responseBody = response.body();

                if (responseBody == null) {
                    log.error("HTTP响应体为空, 错误体: {}", response.errorBody() != null ? response.errorBody().string() : "null");
                    throw new AppException(ResponseCode.ILLEGAL_PARAMETER.getCode(), "HTTP请求返回为空");
                }

                return responseBody.string();
            }
            // GET 请求
            case "get": {
                Map<String, Object> objMapRequest = new java.util.HashMap<>((Map<String, Object>) arguments.values().toArray()[0]);

                // 替换路径参数
                Matcher matcher = Pattern.compile("\\{([^}]+)\\}").matcher(url);
                while (matcher.find()) {
                    String name = matcher.group(1);
                    if (objMapRequest.containsKey(name)) {
                        url = url.replace("{" + name + "}", String.valueOf(objMapRequest.get(name)));
                        objMapRequest.remove(name);
                    }
                }

                Call<ResponseBody> call = gateway.get(url, headers, objMapRequest);

                ResponseBody responseBody = call.execute().body();

                assert responseBody != null;

                return responseBody.string();
            }
        }

        throw new AppException(ResponseCode.METHOD_NOT_FOUND.getCode(), ResponseCode.METHOD_NOT_FOUND.getInfo());
    }
}
