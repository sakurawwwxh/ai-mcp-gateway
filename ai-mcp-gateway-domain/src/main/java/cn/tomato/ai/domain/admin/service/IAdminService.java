package cn.tomato.ai.domain.admin.service;

import cn.tomato.ai.domain.admin.model.entity.GatewayConfigEntity;

import java.util.List;

/**
 * 管理端-网关查询服务接口
 *
 * @author Wxh
 * @date 2026-06-11
 */
public interface IAdminService {

    /**
     * 查询网关配置列表
     */
    List<GatewayConfigEntity> queryGatewayConfigList();

}
