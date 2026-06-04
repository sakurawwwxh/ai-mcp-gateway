package cn.tomato.ai.domain.session.adapter.port;

import cn.tomato.ai.domain.session.model.valobj.gateway.McpGatewayProtocolConfigVO;

import java.io.IOException;

public interface ISessionPort {

    Object toolCall(McpGatewayProtocolConfigVO.HTTPConfig httpConfig,Object params) throws IOException;
}
