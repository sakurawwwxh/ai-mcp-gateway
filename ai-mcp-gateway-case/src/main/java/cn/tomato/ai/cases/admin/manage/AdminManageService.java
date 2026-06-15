package cn.tomato.ai.cases.admin.manage;

import cn.tomato.ai.cases.admin.IAdminManageService;
import cn.tomato.ai.domain.admin.model.entity.GatewayConfigEntity;
import cn.tomato.ai.domain.admin.service.IAdminService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 管理端-网关列表查询服务实现
 * 纯委派：IAdminService → Entity，trigger 层负责 Entity→DTO
 *
 * @author Wxh
 * @date 2026-06-12
 */
@Slf4j
@Service
public class AdminManageService implements IAdminManageService {

    @Resource
    private IAdminService adminService;

    @Override
    public List<GatewayConfigEntity> queryGatewayConfigList() {
        log.info("queryGatewayConfigList");
        return adminService.queryGatewayConfigList();
    }

}
