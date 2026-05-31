package cn.tomato.ai.domain.session.service;

import cn.tomato.ai.domain.session.model.valobj.McpSchemaVO;

public interface ISessionMessageService {

    McpSchemaVO.JSONRPCResponse processHandleMessage(McpSchemaVO.JSONRPCMessage message);
}
