package cn.tomato.ai.domain.admin.adapter.repository;

import cn.tomato.ai.domain.admin.model.entity.GatewayAuthConfigEntity;
import cn.tomato.ai.domain.admin.model.entity.GatewayAuthPageEntity;
import cn.tomato.ai.domain.admin.model.entity.GatewayAuthQueryEntity;
import cn.tomato.ai.domain.admin.model.entity.GatewayConfigEntity;
import cn.tomato.ai.domain.admin.model.entity.GatewayConfigPageEntity;
import cn.tomato.ai.domain.admin.model.entity.GatewayConfigQueryEntity;
import cn.tomato.ai.domain.admin.model.entity.GatewayProtocolConfigEntity;
import cn.tomato.ai.domain.admin.model.entity.GatewayProtocolPageEntity;
import cn.tomato.ai.domain.admin.model.entity.GatewayProtocolQueryEntity;
import cn.tomato.ai.domain.admin.model.entity.GatewayToolConfigEntity;
import cn.tomato.ai.domain.admin.model.entity.GatewayToolPageEntity;
import cn.tomato.ai.domain.admin.model.entity.GatewayToolQueryEntity;

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

    /**
     * 分页查询网关配置
     */
    GatewayConfigPageEntity queryGatewayConfigPage(GatewayConfigQueryEntity q);

    /**
     * 查询所有网关工具
     */
    List<GatewayToolConfigEntity> queryGatewayToolList();

    /**
     * 分页查询网关工具
     */
    GatewayToolPageEntity queryGatewayToolPage(GatewayToolQueryEntity q);

    /**
     * 根据网关 ID 查询工具列表
     */
    List<GatewayToolConfigEntity> queryGatewayToolListByGatewayId(String gatewayId);

    /**
     * 根据网关 ID + 工具 ID 删除工具
     */
    boolean deleteGatewayTool(String gatewayId, Long toolId);

    /**
     * 查询所有鉴权配置
     */
    List<GatewayAuthConfigEntity> queryGatewayAuthList();

    /**
     * 分页查询鉴权配置
     */
    GatewayAuthPageEntity queryGatewayAuthPage(GatewayAuthQueryEntity q);

    /**
     * 按网关 ID 删除鉴权
     */
    boolean deleteGatewayAuth(String gatewayId);

    /**
     * 查询所有协议
     */
    List<GatewayProtocolConfigEntity> queryGatewayProtocolList();

    /**
     * 分页查询协议
     */
    GatewayProtocolPageEntity queryGatewayProtocolPage(GatewayProtocolQueryEntity q);

    /**
     * 根据协议 ID 集合查询协议
     */
    List<GatewayProtocolConfigEntity> queryGatewayProtocolListByProtocolIds(List<Long> protocolIds);

    /**
     * 按协议 ID 删除协议
     */
    boolean deleteGatewayProtocol(Long protocolId);

}
