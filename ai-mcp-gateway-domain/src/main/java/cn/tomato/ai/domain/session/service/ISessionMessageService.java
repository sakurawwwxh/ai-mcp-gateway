package cn.tomato.ai.domain.session.service;

import cn.tomato.ai.domain.session.model.entity.HandleMessageCommandEntity;
import cn.tomato.ai.domain.session.model.valobj.McpSchemaVO;

/**
 * 会话消息服务接口
 *
 * @author Wxh
 */
public interface ISessionMessageService {

    /**
     * 处理MCP消息
     *
     * @param gatewayId 网关唯一标识
     * @param message   MCP消息对象
     * @return JSONRPC响应
     */
    McpSchemaVO.JSONRPCResponse processHandlerMessage(String gatewayId, McpSchemaVO.JSONRPCMessage message);

    McpSchemaVO.JSONRPCResponse processHandlerMessage(HandleMessageCommandEntity commandEntity);
}
