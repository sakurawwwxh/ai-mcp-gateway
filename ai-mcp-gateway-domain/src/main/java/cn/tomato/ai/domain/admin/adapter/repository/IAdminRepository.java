package cn.tomato.ai.domain.admin.adapter.repository;

import cn.tomato.ai.domain.admin.model.entity.GatewayConfigEntity;

import java.util.List;

/**
 * 管理端仓储接口
 *
 * @author Wxh
 * @date 2026-06-11
 */
public interface IAdminRepository {

    /**
     * 查询网关配置列表
     */
    List<GatewayConfigEntity> queryGatewayConfigList();

}
