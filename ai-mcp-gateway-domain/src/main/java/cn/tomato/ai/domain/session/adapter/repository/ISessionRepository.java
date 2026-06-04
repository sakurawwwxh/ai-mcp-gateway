package cn.tomato.ai.domain.session.adapter.repository;

import cn.tomato.ai.domain.session.model.valobj.gateway.McpGatewayConfigVO;
import cn.tomato.ai.domain.session.model.valobj.gateway.McpGatewayProtocolConfigVO;
import cn.tomato.ai.domain.session.model.valobj.gateway.McpGatewayToolConfigVO;

import java.util.List;


/**
 *  会话仓储接口
 */
public interface ISessionRepository {
    McpGatewayConfigVO queryMcpGatewayConfigByGatewayId(String gatewayId);

    List<McpGatewayToolConfigVO> queryMcpGatewayToolConfigListByGatewayId(String gatewayId);

    McpGatewayProtocolConfigVO queryMcpGatewayProtocolConfig(String gatewayId);
}
