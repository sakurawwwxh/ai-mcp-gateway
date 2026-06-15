package cn.tomato.ai.cases.admin.gateway;

import cn.tomato.ai.cases.admin.IAdminGatewayService;
import cn.tomato.ai.domain.gateway.model.entity.GatewayConfigCommandEntity;
import cn.tomato.ai.domain.gateway.model.entity.GatewayToolConfigCommandEntity;
import cn.tomato.ai.domain.gateway.service.IGatewayConfigService;
import cn.tomato.ai.domain.gateway.service.IGatewayToolConfigService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * 管理端-网关配置服务实现
 * 纯委派：CommandEntity → IGatewayConfigService / IGatewayToolConfigService
 *
 * @author Wxh
 * @date 2026-06-12
 */
@Slf4j
@Service
public class AdminGatewayService implements IAdminGatewayService {

    @Resource
    private IGatewayConfigService gatewayConfigService;

    @Resource
    private IGatewayToolConfigService gatewayToolConfigService;

    @Override
    public void saveGatewayConfig(GatewayConfigCommandEntity commandEntity) {
        log.info("saveGatewayConfig gatewayId={}", commandEntity.getGatewayConfigVO().getGatewayId());
        gatewayConfigService.saveGatewayConfig(commandEntity);
    }

    @Override
    public void saveGatewayToolConfig(GatewayToolConfigCommandEntity commandEntity) {
        log.info("saveGatewayToolConfig gatewayId={} toolId={}",
                commandEntity.getGatewayToolConfigVO().getGatewayId(),
                commandEntity.getGatewayToolConfigVO().getToolId());
        gatewayToolConfigService.saveGatewayToolConfig(commandEntity);
    }

}
