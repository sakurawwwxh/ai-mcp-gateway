package cn.tomato.ai.api;

import org.springframework.http.codec.ServerSentEvent;
import reactor.core.publisher.Flux;

public interface IMcpGatewayService {


    /**
     * <p>
     *    建立 SSE 连接
     * @param gatewayId - 网关id
     * @return 流式响应
     * @throws Exception
     * </p>
     */
    Flux<ServerSentEvent<String>> establishSSEConnection(String gatewayId) throws Exception;
}
