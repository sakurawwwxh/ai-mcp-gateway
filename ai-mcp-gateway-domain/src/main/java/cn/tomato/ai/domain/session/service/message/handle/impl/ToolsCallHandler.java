package cn.tomato.ai.domain.session.service.message.handle.impl;

import cn.tomato.ai.domain.session.adapter.port.ISessionPort;
import cn.tomato.ai.domain.session.adapter.repository.ISessionRepository;
import cn.tomato.ai.domain.session.model.valobj.McpSchemaVO;
import cn.tomato.ai.domain.session.model.valobj.gateway.McpToolProtocolConfigVO;
import cn.tomato.ai.domain.session.service.message.handle.IRequestHandler;
import cn.tomato.ai.types.enums.McpErrorCodes;
import cn.tomato.ai.types.enums.ResponseCode;
import cn.tomato.ai.types.exception.AppException;
import com.fasterxml.jackson.core.type.TypeReference;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * MCP工具调用处理器
 * 处理客户端的tools/call请求，执行实际的工具调用
 */
@Slf4j
@Service("toolsCallHandler")
public class ToolsCallHandler implements IRequestHandler {

    @Resource
    private ISessionRepository repository;

    @Resource
    private ISessionPort port;

    /**
     * 处理tools/call请求
     * 解析调用参数，查询协议配置，执行HTTP调用，返回结果
     */
    @Override
    public McpSchemaVO.JSONRPCResponse handle(String gatewayId, McpSchemaVO.JSONRPCRequest message) {
        try {
            // 1. 转换参数
            McpSchemaVO.CallToolRequest callToolRequest =
                    McpSchemaVO.unmarshalFrom(message.params(), new TypeReference<>() {
                    });

            Object argumentsObj = callToolRequest.arguments();
            String toolName = callToolRequest.name();

            // 2. 查询协议信息（需要传入toolName以支持多工具场景）
            McpToolProtocolConfigVO mcpToolProtocolConfigVO = repository.queryMcpGatewayProtocolConfig(gatewayId, toolName);
            if (null == mcpToolProtocolConfigVO) {
                throw new AppException(ResponseCode.METHOD_NOT_FOUND.getCode(), ResponseCode.METHOD_NOT_FOUND.getInfo());
            }

            // 3. 调用接口
            Object result = port.toolCall(mcpToolProtocolConfigVO.getHttpConfig(), argumentsObj);

            return new McpSchemaVO.JSONRPCResponse(McpSchemaVO.JSONRPC_VERSION, message.id(), Map.of(
                    "content", new Object[]{
                            Map.of(
                                    "type", "text",
                                    "text", result
                            ),

                    },
                    "isError", "false"
            ), null);

        } catch (Exception e) {
            return new McpSchemaVO.JSONRPCResponse(McpSchemaVO.JSONRPC_VERSION,
                    message.id(),
                    null,
                    new McpSchemaVO.JSONRPCResponse.JSONRPCError(McpErrorCodes.INVALID_PARAMS, e.getMessage(), null));

        }
    }

}
