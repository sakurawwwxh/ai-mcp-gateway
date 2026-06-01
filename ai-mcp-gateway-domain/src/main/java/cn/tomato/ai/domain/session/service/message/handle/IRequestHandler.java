package cn.tomato.ai.domain.session.service.message.handle;

import cn.tomato.ai.domain.session.model.valobj.McpSchemaVO;

public interface IRequestHandler {

    /**
     * 处理请求
     * @param gatewayId 网关唯一标识
     * @param message JSONRPC请求对象
     * @return JSONRPC响应
     */
    McpSchemaVO.JSONRPCResponse handle(String gatewayId, McpSchemaVO.JSONRPCRequest message);

}
