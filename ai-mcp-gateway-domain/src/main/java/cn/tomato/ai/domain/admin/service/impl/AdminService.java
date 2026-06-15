package cn.tomato.ai.domain.admin.service.impl;

import cn.tomato.ai.domain.admin.adapter.repository.IAdminRepository;
import cn.tomato.ai.domain.admin.model.entity.GatewayConfigEntity;
import cn.tomato.ai.domain.admin.service.IAdminService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 管理端-网关查询服务实现
 *
 * @author Wxh
 * @date 2026-06-11
 */
@Slf4j
@Service
public class AdminService implements IAdminService {

    @Resource
    private IAdminRepository repository;

    @Override
    public List<GatewayConfigEntity> queryGatewayConfigList() {
        log.info("domain admin queryGatewayConfigList");
        return repository.queryGatewayConfigList();
    }

}
