package cn.tomato.ai.infrastructure.adapter.repository;

import cn.tomato.ai.domain.session.adapter.repository.ISessionRepository;
import cn.tomato.ai.domain.session.model.valobj.gateway.McpGatewayConfigVO;
import cn.tomato.ai.domain.session.model.valobj.gateway.McpToolProtocolConfigVO;
import cn.tomato.ai.domain.session.model.valobj.gateway.McpToolConfigVO;
import cn.tomato.ai.infrastructure.dao.IMcpGatewayDao;
import cn.tomato.ai.infrastructure.dao.IMcpGatewayToolDao;
import cn.tomato.ai.infrastructure.dao.IMcpProtocolMappingDao;
import cn.tomato.ai.infrastructure.dao.IMcpProtocolHttpDao;
import cn.tomato.ai.infrastructure.dao.po.McpGatewayPO;
import cn.tomato.ai.infrastructure.dao.po.McpGatewayToolPO;
import cn.tomato.ai.infrastructure.dao.po.McpProtocolMappingPO;
import cn.tomato.ai.infrastructure.dao.po.McpProtocolHttpPO;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

/**
 * 会话仓储实现
 * 负责从数据库查询网关、工具、协议映射等配置信息，并转换为领域层VO对象
 */
@Slf4j
@Repository
public class SessionRepository implements ISessionRepository {

    @Resource
    private IMcpGatewayDao mcpGatewayDao;

    @Resource
    private IMcpGatewayToolDao mcpGatewayToolDao;

    @Resource
    private IMcpProtocolHttpDao mcpProtocolHttpDao;

    @Resource
    private IMcpProtocolMappingDao mcpProtocolMappingDao;

    /**
     * 根据网关ID查询网关配置
     * 只查mcp_gateway表，返回网关级别信息
     */
    @Override
    public McpGatewayConfigVO queryMcpGatewayConfigByGatewayId(String gatewayId) {
        McpGatewayPO mcpGatewayPO = mcpGatewayDao.queryMcpGatewayByGatewayId(gatewayId);
        if (null == mcpGatewayPO) return null;

        return McpGatewayConfigVO.builder()
                .gatewayId(mcpGatewayPO.getGatewayId())
                .gatewayName(mcpGatewayPO.getGatewayName())
                .gatewayDesc(mcpGatewayPO.getGatewayDesc())
                .version(mcpGatewayPO.getVersion())
                .build();
    }

    /**
     * 根据网关ID查询工具配置列表（含协议映射）
     * 先查mcp_gateway_tool获取工具列表，再对每个工具查mcp_protocol_mapping获取字段映射
     */
    @Override
    public List<McpToolConfigVO> queryMcpGatewayToolConfigListByGatewayId(String gatewayId) {

        List<McpToolConfigVO> mcpToolConfigVOS = new ArrayList<>();

        // 1. 查询工具列表
        List<McpGatewayToolPO> mcpGatewayToolPOList = mcpGatewayToolDao.queryEffectiveTools(gatewayId);

        // 2. 组装参数信息
        for (McpGatewayToolPO tool : mcpGatewayToolPOList) {

            // 根据协议ID查询映射配置
            List<McpProtocolMappingPO> mappingPOList = mcpProtocolMappingDao.queryMcpGatewayToolConfigListByProtocolId(tool.getProtocolId());

            List<McpToolProtocolConfigVO.ProtocolMapping> requestProtocolMappings = new ArrayList<>();

            // 构建协议映射列表
            for (McpProtocolMappingPO mcpProtocolMappingPO : mappingPOList) {
                McpToolProtocolConfigVO.ProtocolMapping protocolMapping = McpToolProtocolConfigVO.ProtocolMapping.builder()
                        .mappingType(mcpProtocolMappingPO.getMappingType())
                        .parentPath(mcpProtocolMappingPO.getParentPath())
                        .fieldName(mcpProtocolMappingPO.getFieldName())
                        .mcpPath(mcpProtocolMappingPO.getMcpPath())
                        .mcpType(mcpProtocolMappingPO.getMcpType())
                        .mcpDesc(mcpProtocolMappingPO.getMcpDesc())
                        .isRequired(mcpProtocolMappingPO.getIsRequired())
                        .sortOrder(mcpProtocolMappingPO.getSortOrder())
                        .build();
                requestProtocolMappings.add(protocolMapping);
            }

            // 组装工具配置VO（包含工具信息和协议映射）
            McpToolConfigVO toolConfigVO = McpToolConfigVO.builder()
                    .gatewayId(tool.getGatewayId())
                    .toolId(tool.getToolId())
                    .toolName(tool.getToolName())
                    .toolDescription(tool.getToolDescription())
                    .toolVersion(tool.getToolVersion())
                    .mcpToolProtocolConfigVO(McpToolProtocolConfigVO.builder()
                            .requestProtocolMappings(requestProtocolMappings)
                            .build())
                    .build();

            mcpToolConfigVOS.add(toolConfigVO);
        }

        return mcpToolConfigVOS;
    }

    /**
     * 根据网关ID和工具名称查询协议配置
     * 先通过mcp_gateway_tool查出protocolId，再用protocolId查mcp_protocol_http获取HTTP配置
     */
    @Override
    public McpToolProtocolConfigVO queryMcpGatewayProtocolConfig(String gatewayId, String toolName) {

        // 1. 获取协议ID - 根据网关ID + 工具名称
        McpGatewayToolPO mcpGatewayToolPOReq = new McpGatewayToolPO();
        mcpGatewayToolPOReq.setGatewayId(gatewayId);
        mcpGatewayToolPOReq.setToolName(toolName);
        Long protocolId = mcpGatewayToolDao.queryToolProtocolIdByToolName(mcpGatewayToolPOReq);

        // 2. 查询HTTP协议配置
        McpProtocolHttpPO mcpProtocolHttpPO = mcpProtocolHttpDao.queryMcpProtocolHttpByProtocolId(protocolId);
        if (null == mcpProtocolHttpPO) return null;

        McpToolProtocolConfigVO.HTTPConfig httpConfig = new McpToolProtocolConfigVO.HTTPConfig();
        httpConfig.setHttpUrl(mcpProtocolHttpPO.getHttpUrl());
        httpConfig.setHttpHeaders(mcpProtocolHttpPO.getHttpHeaders());
        httpConfig.setHttpMethod(mcpProtocolHttpPO.getHttpMethod());
        httpConfig.setTimeout(mcpProtocolHttpPO.getTimeout());

        return McpToolProtocolConfigVO.builder().httpConfig(httpConfig).build();
    }

}
