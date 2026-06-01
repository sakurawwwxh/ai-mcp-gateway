package cn.tomato.ai.infrastructure.adapter.repository;

import cn.tomato.ai.domain.session.adapter.repository.ISessionRepository;
import cn.tomato.ai.domain.session.model.valobj.gateway.McpGatewayConfigVO;
import cn.tomato.ai.infrastructure.dao.IMcpGatewayDao;
import cn.tomato.ai.infrastructure.dao.IMcpProtocolRegistryDao;
import cn.tomato.ai.infrastructure.dao.po.McpGatewayPO;
import cn.tomato.ai.infrastructure.dao.po.McpProtocolMappingPO;
import cn.tomato.ai.infrastructure.dao.po.McpProtocolRegistryPO;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

/**
 * @author Wxh
 * @date 2026年06月01日 11:23
 */

@Slf4j
@Repository
public class SessionRepository implements ISessionRepository {

    @Resource
    private IMcpGatewayDao mcpGatewayDao;

    @Resource
    private IMcpProtocolRegistryDao mcpProtocolRegistryDao;

    @Override
    public McpGatewayConfigVO queryMcpGatewayConfigByGatewayId(String gatewayId) {

        McpGatewayPO mcpGatewayPO =  mcpGatewayDao.queryMcpGatewayByGatewayId(gatewayId);
        if (null == mcpGatewayPO) return null;

       McpProtocolRegistryPO mcpProtocolRegistryPO =  mcpProtocolRegistryDao.queryMcpProtocolRegistryByGatewayId(gatewayId);
        if (null == mcpProtocolRegistryPO) return null;

        return McpGatewayConfigVO.builder()
                .gatewayId(mcpGatewayPO.getGatewayId())
                .gatewayName(mcpGatewayPO.getGatewayName())
                .toolId(mcpProtocolRegistryPO.getToolId())
                .toolName(mcpProtocolRegistryPO.getToolName())
                .toolDesc(mcpProtocolRegistryPO.getToolDescription())
                .toolVersion(mcpProtocolRegistryPO.getToolVersion())
                .build();
    }
}
