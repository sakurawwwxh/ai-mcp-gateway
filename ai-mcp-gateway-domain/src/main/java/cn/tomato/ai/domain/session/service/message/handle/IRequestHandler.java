package cn.tomato.ai.domain.session.service.message.handle;

import cn.tomato.ai.domain.session.model.valobj.McpSchemaVO;

public interface IRequestHandler {

    McpSchemaVO.JSONRPCResponse handle(McpSchemaVO.JSONRPCRequest message);

}
