package cn.tomato.ai.domain.gateway.service;

import cn.tomato.ai.domain.gateway.model.entity.GatewayConfigCommandEntity;

/**
 * 网关配置接口
 *
 * @author Wxh
 * @date 2026-06-11
 */
public interface IGatewayConfigService {

    void saveGatewayConfig(GatewayConfigCommandEntity commandEntity);

    void updateGatewayAuthStatus(GatewayConfigCommandEntity commandEntity);

}
