package cn.tomato.ai.cases.admin;

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
 * 管理端-网关列表查询服务接口（编排层）
 * 返回领域 Entity，由 trigger 层负责翻译为 DTO
 *
 * @author Wxh
 * @date 2026-06-12
 */
public interface IAdminManageService {

    /** 查询网关配置列表 */
    List<GatewayConfigEntity> queryGatewayConfigList();

    /** 分页查询网关配置 */
    GatewayConfigPageEntity queryGatewayConfigPage(GatewayConfigQueryEntity q);

    /** 查询所有工具 */
    List<GatewayToolConfigEntity> queryGatewayToolList();

    /** 分页查询工具 */
    GatewayToolPageEntity queryGatewayToolPage(GatewayToolQueryEntity q);

    /** 按网关 ID 查询工具 */
    List<GatewayToolConfigEntity> queryGatewayToolListByGatewayId(String gatewayId);

    /** 删除工具 */
    boolean deleteGatewayTool(String gatewayId, Long toolId);

    /** 查询所有鉴权 */
    List<GatewayAuthConfigEntity> queryGatewayAuthList();

    /** 分页查询鉴权 */
    GatewayAuthPageEntity queryGatewayAuthPage(GatewayAuthQueryEntity q);

    /** 按网关 ID 删除鉴权 */
    boolean deleteGatewayAuth(String gatewayId);

    /** 查询所有协议 */
    List<GatewayProtocolConfigEntity> queryGatewayProtocolList();

    /** 分页查询协议 */
    GatewayProtocolPageEntity queryGatewayProtocolPage(GatewayProtocolQueryEntity q);

    /** 按协议 ID 集合查询协议 */
    List<GatewayProtocolConfigEntity> queryGatewayProtocolListByProtocolIds(List<Long> protocolIds);

    /** 按协议 ID 删除协议 */
    boolean deleteGatewayProtocol(Long protocolId);

}
