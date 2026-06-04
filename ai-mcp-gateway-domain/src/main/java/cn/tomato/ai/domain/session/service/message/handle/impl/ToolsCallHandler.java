package cn.tomato.ai.domain.session.service.message.handle.impl;

import cn.tomato.ai.domain.session.adapter.port.ISessionPort;
import cn.tomato.ai.domain.session.adapter.repository.ISessionRepository;
import cn.tomato.ai.domain.session.model.valobj.McpSchemaVO;
import cn.tomato.ai.domain.session.model.valobj.gateway.McpGatewayProtocolConfigVO;
import cn.tomato.ai.domain.session.service.message.handle.IRequestHandler;
import cn.tomato.ai.types.enums.McpErrorCodes;
import com.fasterxml.jackson.core.type.TypeReference;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * @author Wxh
 * @date 2026年05月29日 17:13
 */
@Slf4j
@Service("toolsCallHandler")
public class ToolsCallHandler implements IRequestHandler {

    @Resource
    private ISessionRepository repository;

    @Resource
    private ISessionPort port;

    @Override
    public McpSchemaVO.JSONRPCResponse handle(String gatewayId, McpSchemaVO.JSONRPCRequest message) {

        try {
            McpGatewayProtocolConfigVO  mcpGatewayProtocolConfigVO=repository.queryMcpGatewayProtocolConfig(gatewayId);

            McpSchemaVO.CallToolRequest callToolRequest = McpSchemaVO.unmarshalFrom(message.params(), new TypeReference<>() {
            });

            Map<String, Object> argumentsObj = callToolRequest.arguments();
            //TODO
            String name = callToolRequest.name();

            Object result = port.toolCall(mcpGatewayProtocolConfigVO.getHttpConfig(), argumentsObj);

            return new McpSchemaVO.JSONRPCResponse(McpSchemaVO.JSONRPC_VERSION, message.id(), Map.of(
                    "content", new Object[]{
                            Map.of(
                                    "type", "text",
                                    "text", result
                            ),

                    },
                    "isError", "false"
            ), null);

        }catch (Exception e){
            return new McpSchemaVO.JSONRPCResponse(McpSchemaVO.JSONRPC_VERSION,
                    message.id(),
                    null,
                    new McpSchemaVO.JSONRPCResponse.JSONRPCError(McpErrorCodes.INVALID_PARAMS, e.getMessage(), null));

        }

//        Object id = message.id();
//        Object params = message.params();
//
//        if (!(params instanceof Map)) {
//
//            new McpSchemaVO.JSONRPCResponse.JSONRPCError(McpErrorCodes.INVALID_PARAMS, "Invalid arguments format", null);
//
//            return new McpSchemaVO.JSONRPCResponse("2.0",
//                    message.id(),
//                    null,
//                    new McpSchemaVO.JSONRPCResponse.JSONRPCError(McpErrorCodes.INVALID_PARAMS, "无效参数 - 无效的方法参数", null));
//        }
//
//        Map<String, Object> paramsMap = (Map<String, Object>) params;
//        String toolName = (String) paramsMap.get("name");
//        Object argumentsObj = paramsMap.get("arguments");
//
//        Map<String, Object> arguments = (Map<String, Object>) argumentsObj;
//
//        if ("toUpperCase".equals(toolName)) {
//            String word = arguments.get("word").toString();
//
//            return new McpSchemaVO.JSONRPCResponse("2.0", message.id(), Map.of(
//                    "content", new Object[]{
//                            Map.of(
//                                    "type", "text",
//                                    "text", word.toUpperCase()
//                            )
//                    }
//            ), null);
//        }
//
//        return new McpSchemaVO.JSONRPCResponse("2.0",
//                message.id(),
//                null,
//                new McpSchemaVO.JSONRPCResponse.JSONRPCError(McpErrorCodes.METHOD_NOT_FOUND, "方法未找到 - 方法不存在或不可用", null));
    }
}
