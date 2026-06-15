package cn.tomato.ai.cases.admin;

import cn.tomato.ai.domain.admin.model.entity.GatewayConfigEntity;

import java.util.List;

/**
 * 管理端-网关列表查询服务接口（编排层）
 * 返回领域 Entity，由 trigger 层负责翻译为 DTO
 *
 * @author Wxh
 * @date 2026-06-12
 */
public interface IAdminManageService {

    /** 查询网关配置列表 */
    List<GatewayConfigEntity> queryGatewayConfigList();

}
