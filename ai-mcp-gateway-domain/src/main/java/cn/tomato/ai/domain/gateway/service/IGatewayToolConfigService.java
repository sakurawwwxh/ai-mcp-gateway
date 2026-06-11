package cn.tomato.ai.domain.gateway.service;

import cn.tomato.ai.domain.gateway.model.entity.GatewayToolConfigCommandEntity;

/**
 * 网关工具配置服务接口
 *
 * @author Wxh
 * @date 2026-06-11
 */
public interface IGatewayToolConfigService {

    void saveGatewayToolConfig(GatewayToolConfigCommandEntity commandEntity);

    void updateGatewayToolProtocol(GatewayToolConfigCommandEntity commandEntity);

}
