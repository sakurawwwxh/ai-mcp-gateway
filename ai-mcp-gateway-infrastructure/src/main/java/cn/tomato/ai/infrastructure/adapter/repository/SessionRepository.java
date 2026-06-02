package cn.tomato.ai.infrastructure.adapter.repository;

import cn.tomato.ai.domain.session.adapter.repository.ISessionRepository;
import cn.tomato.ai.domain.session.model.valobj.gateway.McpGatewayConfigVO;
import cn.tomato.ai.domain.session.model.valobj.gateway.McpGatewayToolConfigVO;
import cn.tomato.ai.infrastructure.dao.IMcpGatewayDao;
import cn.tomato.ai.infrastructure.dao.IMcpProtocolMappingDao;
import cn.tomato.ai.infrastructure.dao.IMcpProtocolRegistryDao;
import cn.tomato.ai.infrastructure.dao.po.McpGatewayPO;
import cn.tomato.ai.infrastructure.dao.po.McpProtocolMappingPO;
import cn.tomato.ai.infrastructure.dao.po.McpProtocolRegistryPO;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

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

    @Resource
    private IMcpProtocolMappingDao mcpProtocolMappingDao;

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

    @Override
    public List<McpGatewayToolConfigVO> queryMcpGatewayToolConfigListByGatewayId(String gatewayId) {

        McpProtocolMappingPO reqPO = new McpProtocolMappingPO();
        reqPO.setGatewayId(gatewayId);

        // 1. 查询协议工具映射配置
        List<McpProtocolMappingPO> poList = mcpProtocolMappingDao.queryMcpGatewayToolConfigList(reqPO);

        List<McpGatewayToolConfigVO> mcpGatewayToolConfigVOS = new ArrayList<>();
        for (McpProtocolMappingPO po : poList) {
            mcpGatewayToolConfigVOS.add(McpGatewayToolConfigVO.builder()
                    .gatewayId(po.getGatewayId())
                    .toolId(po.getToolId())
                    .mappingType(po.getMappingType())
                    .parentPath(po.getParentPath())
                    .fieldName(po.getFieldName())
                    .mcpPath(po.getMcpPath())
                    .mcpType(po.getMcpType())
                    .mcpDesc(po.getMcpDesc())
                    .isRequired(po.getIsRequired())
                    .sortOrder(po.getSortOrder())
                    .build());
        }

        return mcpGatewayToolConfigVOS;
    }
}
