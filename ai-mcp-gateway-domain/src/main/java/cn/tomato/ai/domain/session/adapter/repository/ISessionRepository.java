package cn.tomato.ai.domain.session.adapter.repository;

import cn.tomato.ai.domain.session.model.valobj.gateway.McpGatewayConfigVO;
import cn.tomato.ai.domain.session.model.valobj.gateway.McpToolConfigVO;
import cn.tomato.ai.domain.session.model.valobj.gateway.McpToolProtocolConfigVO;

import java.util.List;

/**
 * 会话仓储接口
 */
public interface ISessionRepository {

    /**
     * 根据网关ID查询网关配置
     */
    McpGatewayConfigVO queryMcpGatewayConfigByGatewayId(String gatewayId);

    /**
     * 根据网关ID查询工具配置列表（含协议映射）
     */
    List<McpToolConfigVO> queryMcpGatewayToolConfigListByGatewayId(String gatewayId);

    /**
     * 根据网关ID和工具名称查询协议配置
     */
    McpToolProtocolConfigVO queryMcpGatewayProtocolConfig(String gatewayId, String toolName);

}
