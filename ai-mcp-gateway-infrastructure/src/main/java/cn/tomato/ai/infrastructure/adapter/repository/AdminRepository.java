package cn.tomato.ai.infrastructure.adapter.repository;

import cn.tomato.ai.domain.admin.adapter.repository.IAdminRepository;
import cn.tomato.ai.domain.admin.model.entity.GatewayAuthConfigEntity;
import cn.tomato.ai.domain.admin.model.entity.GatewayAuthPageEntity;
import cn.tomato.ai.domain.admin.model.entity.GatewayAuthQueryEntity;
import cn.tomato.ai.domain.admin.model.entity.GatewayConfigEntity;
import cn.tomato.ai.domain.admin.model.entity.GatewayConfigPageEntity;
import cn.tomato.ai.domain.admin.model.entity.GatewayConfigQueryEntity;
import cn.tomato.ai.domain.admin.model.entity.GatewayProtocolConfigEntity;
import cn.tomato.ai.domain.admin.model.entity.GatewayProtocolPageEntity;
import cn.tomato.ai.domain.admin.model.entity.GatewayProtocolQueryEntity;
import cn.tomato.ai.domain.admin.model.entity.GatewayToolConfigEntity;
import cn.tomato.ai.domain.admin.model.entity.GatewayToolPageEntity;
import cn.tomato.ai.domain.admin.model.entity.GatewayToolQueryEntity;
import cn.tomato.ai.infrastructure.dao.IMcpGatewayAuthDao;
import cn.tomato.ai.infrastructure.dao.IMcpGatewayDao;
import cn.tomato.ai.infrastructure.dao.IMcpGatewayToolDao;
import cn.tomato.ai.infrastructure.dao.IMcpProtocolHttpDao;
import cn.tomato.ai.infrastructure.dao.IMcpProtocolMappingDao;
import cn.tomato.ai.infrastructure.dao.po.McpGatewayAuthPO;
import cn.tomato.ai.infrastructure.dao.po.McpGatewayPO;
import cn.tomato.ai.infrastructure.dao.po.McpGatewayToolPO;
import cn.tomato.ai.infrastructure.dao.po.McpProtocolHttpPO;
import cn.tomato.ai.infrastructure.dao.po.McpProtocolMappingPO;
import cn.tomato.ai.types.enums.GatewayEnum;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 管理端仓储实现
 * 注入 5 个 DAO,mcpGatewayDao.queryAll() + stream 拼装
 *
 * @author Wxh
 * @date 2026-06-11
 */
@Slf4j
@Repository
public class AdminRepository implements IAdminRepository {

    @Resource
    private IMcpGatewayDao mcpGatewayDao;

    @Resource
    private IMcpGatewayAuthDao mcpGatewayAuthDao;

    @Resource
    private IMcpGatewayToolDao mcpGatewayToolDao;

    @Resource
    private IMcpProtocolHttpDao mcpProtocolHttpDao;

    @Resource
    private IMcpProtocolMappingDao mcpProtocolMappingDao;

    @Override
    public List<GatewayConfigEntity> queryGatewayConfigList() {
        List<McpGatewayPO> poList = mcpGatewayDao.queryAll();
        return poList.stream()
                .map(this::convert)
                .collect(Collectors.toList());
    }

    @Override
    public GatewayConfigPageEntity queryGatewayConfigPage(GatewayConfigQueryEntity q) {
        McpGatewayPO po = new McpGatewayPO();
        po.setGatewayId(q.getGatewayId());
        po.setGatewayName(q.getGatewayName());
        po.setPage(q.getPage());
        po.setRows(q.getRows());
        Long total = mcpGatewayDao.queryGatewayListCount(po);
        List<McpGatewayPO> poList = total == null || total == 0
                ? java.util.Collections.emptyList()
                : mcpGatewayDao.queryGatewayList(po);
        List<GatewayConfigEntity> dataList = poList.stream()
                .map(this::convert)
                .collect(Collectors.toList());
        return GatewayConfigPageEntity.builder()
                .dataList(dataList)
                .total(total == null ? 0L : total)
                .build();
    }

    @Override
    public List<GatewayToolConfigEntity> queryGatewayToolList() {
        return mcpGatewayToolDao.queryAll().stream()
                .map(this::convertTool)
                .collect(Collectors.toList());
    }

    @Override
    public GatewayToolPageEntity queryGatewayToolPage(GatewayToolQueryEntity q) {
        McpGatewayToolPO po = new McpGatewayToolPO();
        po.setGatewayId(q.getGatewayId());
        po.setToolName(q.getToolName());
        if (q.getToolId() != null && !q.getToolId().isEmpty()) {
            try { po.setToolId(Long.parseLong(q.getToolId())); } catch (NumberFormatException ignore) {}
        }
        po.setPage(q.getPage());
        po.setRows(q.getRows());
        Long total = mcpGatewayToolDao.queryToolListCount(po);
        List<McpGatewayToolPO> poList = total == null || total == 0
                ? java.util.Collections.emptyList()
                : mcpGatewayToolDao.queryToolList(po);
        List<GatewayToolConfigEntity> dataList = poList.stream()
                .map(this::convertTool)
                .collect(Collectors.toList());
        return GatewayToolPageEntity.builder()
                .dataList(dataList)
                .total(total == null ? 0L : total)
                .build();
    }

    @Override
    public List<GatewayToolConfigEntity> queryGatewayToolListByGatewayId(String gatewayId) {
        return mcpGatewayToolDao.queryByGatewayId(gatewayId).stream()
                .map(this::convertTool)
                .collect(Collectors.toList());
    }

    @Override
    public boolean deleteGatewayTool(String gatewayId, Long toolId) {
        return mcpGatewayToolDao.deleteByGatewayIdAndToolId(gatewayId, toolId) > 0;
    }

    @Override
    public List<GatewayAuthConfigEntity> queryGatewayAuthList() {
        return mcpGatewayAuthDao.queryAll().stream()
                .map(this::convertAuth)
                .collect(Collectors.toList());
    }

    @Override
    public GatewayAuthPageEntity queryGatewayAuthPage(GatewayAuthQueryEntity q) {
        McpGatewayAuthPO po = new McpGatewayAuthPO();
        po.setGatewayId(q.getGatewayId());
        po.setPage(q.getPage());
        po.setRows(q.getRows());
        Long total = mcpGatewayAuthDao.queryAuthListCount(po);
        List<McpGatewayAuthPO> poList = total == null || total == 0
                ? java.util.Collections.emptyList()
                : mcpGatewayAuthDao.queryAuthList(po);
        List<GatewayAuthConfigEntity> dataList = poList.stream()
                .map(this::convertAuth)
                .collect(Collectors.toList());
        return GatewayAuthPageEntity.builder()
                .dataList(dataList)
                .total(total == null ? 0L : total)
                .build();
    }

    @Override
    public boolean deleteGatewayAuth(String gatewayId) {
        return mcpGatewayAuthDao.deleteByGatewayId(gatewayId) > 0;
    }

    private GatewayAuthConfigEntity convertAuth(McpGatewayAuthPO po) {
        return GatewayAuthConfigEntity.builder()
                .gatewayId(po.getGatewayId())
                .apiKey(po.getApiKey())
                .rateLimit(po.getRateLimit())
                .expireTime(po.getExpireTime())
                .build();
    }

    @Override
    public List<GatewayProtocolConfigEntity> queryGatewayProtocolList() {
        List<McpProtocolHttpPO> httpList = mcpProtocolHttpDao.queryAll();
        return assembleProtocols(httpList);
    }

    @Override
    public GatewayProtocolPageEntity queryGatewayProtocolPage(GatewayProtocolQueryEntity q) {
        McpProtocolHttpPO po = new McpProtocolHttpPO();
        po.setProtocolId(q.getProtocolId());
        po.setHttpUrl(q.getHttpUrl());
        po.setPage(q.getPage());
        po.setRows(q.getRows());
        Long total = mcpProtocolHttpDao.queryProtocolListCount(po);
        List<McpProtocolHttpPO> httpList = total == null || total == 0
                ? Collections.emptyList()
                : mcpProtocolHttpDao.queryProtocolList(po);
        List<GatewayProtocolConfigEntity> dataList = assembleProtocols(httpList);
        return GatewayProtocolPageEntity.builder()
                .dataList(dataList)
                .total(total == null ? 0L : total)
                .build();
    }

    @Override
    public List<GatewayProtocolConfigEntity> queryGatewayProtocolListByProtocolIds(List<Long> protocolIds) {
        if (protocolIds == null || protocolIds.isEmpty()) return Collections.emptyList();
        List<McpProtocolHttpPO> httpList = mcpProtocolHttpDao.queryByProtocolIds(protocolIds);
        return assembleProtocols(httpList);
    }

    @Override
    public boolean deleteGatewayProtocol(Long protocolId) {
        // 先删映射、再删 HTTP 协议
        mcpProtocolMappingDao.deleteByProtocolId(protocolId);
        return mcpProtocolHttpDao.deleteByProtocolId(protocolId) > 0;
    }

    /**
     * 装配协议列表：批量取 mapping，按 protocolId 分组
     */
    private List<GatewayProtocolConfigEntity> assembleProtocols(List<McpProtocolHttpPO> httpList) {
        if (httpList == null || httpList.isEmpty()) return Collections.emptyList();
        List<Long> protocolIds = httpList.stream().map(McpProtocolHttpPO::getProtocolId).collect(Collectors.toList());
        List<McpProtocolMappingPO> allMappings = mcpProtocolMappingDao.queryByProtocolIds(protocolIds);
        Map<Long, List<McpProtocolMappingPO>> mappingByProtocol = allMappings.stream()
                .collect(Collectors.groupingBy(McpProtocolMappingPO::getProtocolId));
        return httpList.stream().map(http -> convertProtocol(http, mappingByProtocol)).collect(Collectors.toList());
    }

    private GatewayProtocolConfigEntity convertProtocol(McpProtocolHttpPO http,
                                                        Map<Long, List<McpProtocolMappingPO>> mappingByProtocol) {
        List<GatewayProtocolConfigEntity.ProtocolMappingEntity> mappings = mappingByProtocol
                .getOrDefault(http.getProtocolId(), Collections.emptyList())
                .stream()
                .map(this::convertMapping)
                .collect(Collectors.toList());
        return GatewayProtocolConfigEntity.builder()
                .protocolId(http.getProtocolId())
                .httpUrl(http.getHttpUrl())
                .httpMethod(http.getHttpMethod())
                .httpHeaders(http.getHttpHeaders())
                .timeout(http.getTimeout())
                .retryTimes(http.getRetryTimes())
                .status(http.getStatus())
                .mappings(mappings)
                .build();
    }

    private GatewayProtocolConfigEntity.ProtocolMappingEntity convertMapping(McpProtocolMappingPO po) {
        return GatewayProtocolConfigEntity.ProtocolMappingEntity.builder()
                .mappingType(po.getMappingType())
                .parentPath(po.getParentPath())
                .fieldName(po.getFieldName())
                .mcpPath(po.getMcpPath())
                .mcpType(po.getMcpType())
                .mcpDesc(po.getMcpDesc())
                .isRequired(po.getIsRequired())
                .sortOrder(po.getSortOrder())
                .build();
    }

    private GatewayToolConfigEntity convertTool(McpGatewayToolPO po) {
        return GatewayToolConfigEntity.builder()
                .gatewayId(po.getGatewayId())
                .toolId(po.getToolId())
                .toolName(po.getToolName())
                .toolType(po.getToolType())
                .toolDescription(po.getToolDescription())
                .toolVersion(po.getToolVersion())
                .protocolId(po.getProtocolId())
                .protocolType(po.getProtocolType())
                .build();
    }

    private GatewayConfigEntity convert(McpGatewayPO po) {
        return GatewayConfigEntity.builder()
                .gatewayId(po.getGatewayId())
                .name(po.getGatewayName())
                .desc(po.getGatewayDesc())
                .version(po.getVersion())
                .auth(translateAuth(po.getAuth()))
                .status(translateStatus(po.getStatus()))
                .build();
    }

    private GatewayEnum.GatewayAuthStatusEnum translateAuth(Integer code) {
        if (code == null) return null;
        for (GatewayEnum.GatewayAuthStatusEnum val : GatewayEnum.GatewayAuthStatusEnum.values()) {
            if (val.getCode().equals(code)) return val;
        }
        return null;
    }

    private GatewayEnum.GatewayStatus translateStatus(Integer code) {
        if (code == null) return null;
        for (GatewayEnum.GatewayStatus val : GatewayEnum.GatewayStatus.values()) {
            if (val.getCode().equals(code)) return val;
        }
        return null;
    }

}
