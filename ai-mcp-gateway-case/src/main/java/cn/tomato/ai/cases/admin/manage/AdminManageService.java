package cn.tomato.ai.cases.admin.manage;

import cn.tomato.ai.cases.admin.IAdminManageService;
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
import cn.tomato.ai.domain.admin.service.IAdminService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 管理端-网关列表查询服务实现
 * 纯委派：分页查询直接走 IAdminRepository，其他走 IAdminService
 *
 * @author Wxh
 * @date 2026-06-12
 */
@Slf4j
@Service
public class AdminManageService implements IAdminManageService {

    @Resource
    private IAdminService adminService;

    @Resource
    private IAdminRepository adminRepository;

    @Override
    public List<GatewayConfigEntity> queryGatewayConfigList() {
        log.info("queryGatewayConfigList");
        return adminService.queryGatewayConfigList();
    }

    @Override
    public GatewayConfigPageEntity queryGatewayConfigPage(GatewayConfigQueryEntity q) {
        log.info("queryGatewayConfigPage page:{} rows:{} gatewayId:{} gatewayName:{}",
                q.getPage(), q.getRows(), q.getGatewayId(), q.getGatewayName());
        return adminRepository.queryGatewayConfigPage(q);
    }

    @Override
    public List<GatewayToolConfigEntity> queryGatewayToolList() {
        log.info("queryGatewayToolList");
        return adminRepository.queryGatewayToolList();
    }

    @Override
    public GatewayToolPageEntity queryGatewayToolPage(GatewayToolQueryEntity q) {
        log.info("queryGatewayToolPage page:{} rows:{} gatewayId:{} toolName:{}",
                q.getPage(), q.getRows(), q.getGatewayId(), q.getToolName());
        return adminRepository.queryGatewayToolPage(q);
    }

    @Override
    public List<GatewayToolConfigEntity> queryGatewayToolListByGatewayId(String gatewayId) {
        log.info("queryGatewayToolListByGatewayId gatewayId:{}", gatewayId);
        return adminRepository.queryGatewayToolListByGatewayId(gatewayId);
    }

    @Override
    public boolean deleteGatewayTool(String gatewayId, Long toolId) {
        log.info("deleteGatewayTool gatewayId:{} toolId:{}", gatewayId, toolId);
        return adminRepository.deleteGatewayTool(gatewayId, toolId);
    }

    @Override
    public List<GatewayAuthConfigEntity> queryGatewayAuthList() {
        log.info("queryGatewayAuthList");
        return adminRepository.queryGatewayAuthList();
    }

    @Override
    public GatewayAuthPageEntity queryGatewayAuthPage(GatewayAuthQueryEntity q) {
        log.info("queryGatewayAuthPage page:{} rows:{} gatewayId:{}",
                q.getPage(), q.getRows(), q.getGatewayId());
        return adminRepository.queryGatewayAuthPage(q);
    }

    @Override
    public boolean deleteGatewayAuth(String gatewayId) {
        log.info("deleteGatewayAuth gatewayId:{}", gatewayId);
        return adminRepository.deleteGatewayAuth(gatewayId);
    }

    @Override
    public List<GatewayProtocolConfigEntity> queryGatewayProtocolList() {
        log.info("queryGatewayProtocolList");
        return adminRepository.queryGatewayProtocolList();
    }

    @Override
    public GatewayProtocolPageEntity queryGatewayProtocolPage(GatewayProtocolQueryEntity q) {
        log.info("queryGatewayProtocolPage page:{} rows:{} protocolId:{} gatewayId:{}",
                q.getPage(), q.getRows(), q.getProtocolId(), q.getGatewayId());
        // 若指定 gatewayId，先经工具表拿到 protocolId 集合，再分页（在 DB 层无法精确分页，故退化为 list 后内存分页）
        if (q.getGatewayId() != null && !q.getGatewayId().isEmpty()) {
            List<GatewayToolConfigEntity> tools = adminRepository.queryGatewayToolListByGatewayId(q.getGatewayId());
            List<Long> protocolIds = tools.stream()
                    .map(GatewayToolConfigEntity::getProtocolId)
                    .filter(java.util.Objects::nonNull)
                    .distinct()
                    .collect(Collectors.toList());
            if (protocolIds.isEmpty()) {
                return GatewayProtocolPageEntity.builder().dataList(Collections.emptyList()).total(0L).build();
            }
            List<GatewayProtocolConfigEntity> all = adminRepository.queryGatewayProtocolListByProtocolIds(protocolIds);
            int page = q.getPage() == null ? 1 : q.getPage();
            int rows = q.getRows() == null ? 10 : q.getRows();
            int from = Math.min((page - 1) * rows, all.size());
            int to = Math.min(from + rows, all.size());
            return GatewayProtocolPageEntity.builder()
                    .dataList(all.subList(from, to))
                    .total((long) all.size())
                    .build();
        }
        return adminRepository.queryGatewayProtocolPage(q);
    }

    @Override
    public List<GatewayProtocolConfigEntity> queryGatewayProtocolListByProtocolIds(List<Long> protocolIds) {
        log.info("queryGatewayProtocolListByProtocolIds count:{}", protocolIds == null ? 0 : protocolIds.size());
        return adminRepository.queryGatewayProtocolListByProtocolIds(protocolIds);
    }

    @Override
    public boolean deleteGatewayProtocol(Long protocolId) {
        log.info("deleteGatewayProtocol protocolId:{}", protocolId);
        return adminRepository.deleteGatewayProtocol(protocolId);
    }

}