package cn.tomato.ai.domain.session.service.message.handle.impl;

import cn.tomato.ai.domain.session.adapter.repository.ISessionRepository;
import cn.tomato.ai.domain.session.model.valobj.McpSchemaVO;
import cn.tomato.ai.domain.session.model.valobj.gateway.McpGatewayConfigVO;
import cn.tomato.ai.domain.session.service.message.handle.IRequestHandler;
import com.alibaba.fastjson.JSON;
import com.fasterxml.jackson.core.type.TypeReference;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashMap;

/**
 * MCP协议初始化处理器
 * 处理客户端的initialize请求，返回服务器能力和信息
 */
@Slf4j
@Service("initializeHandler")
public class InitializeHandler implements IRequestHandler {

    @Resource
    private ISessionRepository repository;

    /**
     * 处理initialize请求
     * 查询网关配置，组装InitializeResult返回给客户端
     */
    @Override
    public McpSchemaVO.JSONRPCResponse handle(String gatewayId, McpSchemaVO.JSONRPCRequest message) {

        log.info("消息处理服务-initialize gatewayId:{} request.params:{}", gatewayId, JSON.toJSONString(message.params()));

        // 1. 转换参数
        McpSchemaVO.InitializeRequest initializeRequest = McpSchemaVO.unmarshalFrom(message.params(), new TypeReference<>() {
        });

        // 2. 查询配置
        McpGatewayConfigVO mcpGatewayConfigVO = repository.queryMcpGatewayConfigByGatewayId(gatewayId);

        // 3. 组装信息（使用网关级别信息）
        McpSchemaVO.InitializeResult initializeResult = new McpSchemaVO.InitializeResult(initializeRequest.protocolVersion(),
                new McpSchemaVO.ServerCapabilities(new McpSchemaVO.ServerCapabilities.CompletionCapabilities(),
                        new HashMap<>(),
                        new McpSchemaVO.ServerCapabilities.LoggingCapabilities(),
                        new McpSchemaVO.ServerCapabilities.PromptCapabilities(true),
                        new McpSchemaVO.ServerCapabilities.ResourceCapabilities(false, true),
                        new McpSchemaVO.ServerCapabilities.ToolCapabilities(true)),
                new McpSchemaVO.Implementation(mcpGatewayConfigVO.getGatewayName(), mcpGatewayConfigVO.getVersion()),
                mcpGatewayConfigVO.getGatewayDesc()
        );

        // 4. 返回结果
        return new McpSchemaVO.JSONRPCResponse(McpSchemaVO.JSONRPC_VERSION, message.id(), initializeResult, null);
    }
}
