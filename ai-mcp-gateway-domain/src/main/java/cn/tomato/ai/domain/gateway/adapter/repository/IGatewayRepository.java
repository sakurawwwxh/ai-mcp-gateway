package cn.tomato.ai.domain.gateway.adapter.repository;

import cn.tomato.ai.domain.gateway.model.entity.GatewayConfigCommandEntity;
import cn.tomato.ai.domain.gateway.model.entity.GatewayToolConfigCommandEntity;
import cn.tomato.ai.domain.gateway.model.valobj.GatewayConfigVO;

/**
 * 网关仓储服务接口
 *
 * @author Wxh
 * @date 2026-06-11
 */
public interface IGatewayRepository {

    void saveGatewayConfig(GatewayConfigCommandEntity commandEntity);

    void updateGatewayAuthStatus(GatewayConfigCommandEntity commandEntity);

    void saveGatewayToolConfig(GatewayToolConfigCommandEntity commandEntity);

    void updateGatewayToolProtocol(GatewayToolConfigCommandEntity commandEntity);

    /**
     * 按网关 ID 查询网关配置。
     *
     * @param gatewayId 网关 ID
     * @return 网关配置值对象；网关不存在时返回 null
     */
    GatewayConfigVO queryGatewayConfig(String gatewayId);

}
