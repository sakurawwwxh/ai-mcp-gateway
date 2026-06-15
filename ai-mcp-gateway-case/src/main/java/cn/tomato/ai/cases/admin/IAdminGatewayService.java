package cn.tomato.ai.cases.admin;

import cn.tomato.ai.domain.gateway.model.entity.GatewayConfigCommandEntity;
import cn.tomato.ai.domain.gateway.model.entity.GatewayToolConfigCommandEntity;

/**
 * 管理端-网关配置服务接口（编排层）
 * 接收已翻译的 CommandEntity，纯委派给 domain service
 *
 * @author Wxh
 * @date 2026-06-12
 */
public interface IAdminGatewayService {

    /** 保存网关基础配置 */
    void saveGatewayConfig(GatewayConfigCommandEntity commandEntity);

    /** 保存网关工具配置 */
    void saveGatewayToolConfig(GatewayToolConfigCommandEntity commandEntity);

}
