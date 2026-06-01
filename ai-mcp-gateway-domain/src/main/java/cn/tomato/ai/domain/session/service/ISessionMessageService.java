package cn.tomato.ai.domain.session.service;

import cn.tomato.ai.domain.session.model.valobj.McpSchemaVO;

public interface ISessionMessageService {

    /**
     * 处理MCP消息
     * @param gatewayId 网关唯一标识
     * @param message MCP消息对象
     * @return JSONRPC响应
     */
    McpSchemaVO.JSONRPCResponse processHandleMessage(String gatewayId, McpSchemaVO.JSONRPCMessage message);
}
