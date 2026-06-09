package cn.tomato.ai.api;

import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.ServerSentEvent;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * MCP 网关服务接口
 *
 * @author Wxh
 */
public interface IMcpGatewayService {

    /**
     * 建立 SSE 连接
     *
     * @param gatewayId 网关ID
     * @return 流式响应
     * @throws Exception 异常
     */
    Flux<ServerSentEvent<String>> handleSseConnection(String gatewayId,String apiKey) throws Exception;

    /**
     * 处理 SSE 消息
     *
     * @param gatewayId   网关ID
     * @param sessionId   会话ID
     * @param messageBody 请求消息
     * @return 响应结果
     */
    Mono<ResponseEntity<Void>> handleMessage(String gatewayId, String apiKey, String sessionId, String messageBody);
}
