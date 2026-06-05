package cn.tomato.ai.trigger.http;

import cn.tomato.ai.api.IMcpGatewayService;
import cn.tomato.ai.cases.mcp.IMcpMessageService;
import cn.tomato.ai.cases.mcp.IMcpSessionService;
import cn.tomato.ai.domain.session.model.entity.HandleMessageCommandEntity;
import cn.tomato.ai.types.enums.ResponseCode;
import cn.tomato.ai.types.exception.AppException;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * MCP 网关控制器
 * 处理 SSE 连接和消息请求
 */
@Slf4j
@RestController
@CrossOrigin(origins = "*", allowedHeaders = "*", methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE, RequestMethod.OPTIONS})
@RequestMapping("/")
public class McpGatewayController implements IMcpGatewayService {

    @Resource
    private IMcpSessionService mcpSessionService;

    @Resource
    private IMcpMessageService mcpMessageService;

    /**
     * 建立 SSE 长连接
     * 客户端通过此接口订阅服务端推送的消息流
     *
     * @param gatewayId 网关唯一标识
     * @return SSE 事件流
     */
    @GetMapping(value = "{gatewayId}/mcp/sse", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    @Override
    public Flux<ServerSentEvent<String>> handleSseConnection(@PathVariable("gatewayId") String gatewayId) throws Exception {
        try {
            log.info("建立 mcp SSE连接{}", gatewayId);

            if (StringUtils.isBlank(gatewayId)) {
                log.info("gatewayId is null");
                throw new AppException(ResponseCode.ILLEGAL_PARAMETER.getCode(), ResponseCode.ILLEGAL_PARAMETER.getInfo());
            }

            return mcpSessionService.createMcpSession(gatewayId);
        } catch (Exception e) {
            log.info("建立mcp连接失败{}", gatewayId);
            throw e;
        }
    }

    /**
     * 处理客户端发送的 MCP 消息
     * 接收 JSON-RPC 格式的请求，解析后分发到对应的 Handler 处理
     *
     * @param gatewayId   网关唯一标识
     * @param sessionId   会话 ID，由 SSE 连接时分配
     * @param messageBody JSON-RPC 格式的消息体
     * @return 处理结果
     */
    @PostMapping(value = "{gatewayId}/mcp/sse", consumes = MediaType.APPLICATION_JSON_VALUE)
    @Override
    public Mono<ResponseEntity<Void>> handleMessage(@PathVariable("gatewayId") String gatewayId,
                                                      @RequestParam("sessionId") String sessionId,
                                                      @RequestBody String messageBody) {
        try {
            HandleMessageCommandEntity commandEntity = new HandleMessageCommandEntity(gatewayId, sessionId, messageBody);
            mcpMessageService.handleMessage(commandEntity);
        } catch (Exception e) {
            log.error("处理 MCP 消息失败，gatewayId:{} sessionId:{}", gatewayId, sessionId, e);
            return Mono.just(ResponseEntity.internalServerError().build());
        }
        return Mono.just(ResponseEntity.accepted().build());
    }
}
