package cn.tomato.ai.trigger.http;

import cn.tomato.ai.api.IMcpGatewayService;
import cn.tomato.ai.cases.mcp.IMcpMessageService;
import cn.tomato.ai.cases.mcp.IMcpSessionService;
import cn.tomato.ai.domain.session.model.valobj.McpSchemaVO;
import cn.tomato.ai.domain.session.model.valobj.SessionConfigVO;
import cn.tomato.ai.domain.session.service.ISessionManagementService;
import cn.tomato.ai.domain.session.service.ISessionMessageService;
import cn.tomato.ai.types.enums.ResponseCode;
import cn.tomato.ai.types.exception.AppException;
import com.alibaba.fastjson2.JSON;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Map;

/**
 * @author Wxh
 * @date 2026年05月28日 17:42
 */
@Slf4j
@RestController
@CrossOrigin(origins = "*", allowedHeaders = "*", methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE})
@RequestMapping("/")
public class McpGatewayController implements IMcpGatewayService {

    @Resource
    private IMcpSessionService mcpSessionService;

    @Resource
    private ISessionMessageService  sessionMessageService;

    @Resource
    private ISessionManagementService  sessionManagementService;

    @Resource
    private ObjectMapper  objectMapper;

    public McpGatewayController() {
        System.out.println("McpGatewayController");
    }

    /**
     * 建立 SSE 长连接
     * 客户端通过此接口订阅服务端推送的消息流
     *
     * @param gatewayId 网关唯一标识
     * @return SSE 事件流
     */
    @GetMapping(value = "{gatewayId}/mcp/sse", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    @Override
    public Flux<ServerSentEvent<String>> establishSSEConnection(@PathVariable("gatewayId") String gatewayId) throws Exception {

        try{

            log.info("建立 mcp SSE连接{}", gatewayId);

            if(StringUtils.isBlank(gatewayId)){
                log.info("gatewayId is null");

                throw new AppException(ResponseCode.ILLEGAL_PARAMETER.getCode(),ResponseCode.ILLEGAL_PARAMETER.getInfo());
            }

            return mcpSessionService.createMcpSession(gatewayId);
        }catch (Exception e){
            log.info("建立mcp连接失败{}",gatewayId);
            throw e;
        }

    }

    /***
     * {
     *     "jsonrpc": "2.0",
     *     "method": "initialize",
     *     "id": "8659fe4a-0",
     *     "params": {
     *         "protocolVersion": "2024-11-05",
     *         "capabilities": {},
     *         "clientInfo": {
     *             "name": "Java SDK MCP Client",
     *             "version": "1.0.0"
     *         }
     *     }
     * }
     */
    /**
     * 处理客户端发送的 MCP 消息
     * 接收 JSON-RPC 格式的请求，解析后分发到对应的 Handler 处理
     *
     * @param gatewayId   网关唯一标识
     * @param sessionId   会话 ID，由 SSE 连接时分配
     * @param messageBody JSON-RPC 格式的消息体
     * @return 处理结果
     */
    @PostMapping(value = "{gatewayId}/mcp/sse",consumes = MediaType.APPLICATION_JSON_VALUE)
    @Override
    public Mono<ResponseEntity<Object>> handleMessage(@PathVariable("gatewayId") String gatewayId,
                                                      @RequestParam String sessionId,
                                                      @RequestBody String messageBody) {

        try {

            log.info("处理 mcp SSE消息,gatewayId:{},sessionId:{},messageBody:{}", gatewayId,sessionId, messageBody);

            SessionConfigVO session = sessionManagementService.getSession(sessionId);


            if (null == session) {
                log.warn("会话不存在或已过期，gatewayId:{} sessionId:{}", gatewayId, sessionId);
                return Mono.just(ResponseEntity.notFound().build());
            }

            McpSchemaVO.JSONRPCMessage jsonrpcMessage = McpSchemaVO.deserializeJsonRpcMessage(messageBody);

            McpSchemaVO.JSONRPCResponse jsonrpcResponse = sessionMessageService.processHandleMessage(jsonrpcMessage);

            if (null != jsonrpcResponse) {

                String responseJson = objectMapper.writeValueAsString(jsonrpcResponse);

                session.getSink().tryEmitNext(ServerSentEvent.<String>builder()
                        .event("message")
                        .data(responseJson)
                        .build());
            }

            log.info("调用结果:{}", JSON.toJSONString(jsonrpcResponse));

            return Mono.just(ResponseEntity.accepted().build());

        } catch (Exception e) {
            log.error("处理 MCP SSE 消息失败，gatewayId:{} sessionId:{} messageBody:{}", gatewayId, sessionId, messageBody, e);
            return Mono.just(ResponseEntity.internalServerError().build());
        }

    }
}
