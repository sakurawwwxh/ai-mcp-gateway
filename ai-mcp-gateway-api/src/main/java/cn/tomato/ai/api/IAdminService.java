package cn.tomato.ai.api;

import cn.tomato.ai.api.dto.GatewayAuthDTO;
import cn.tomato.ai.api.dto.GatewayAuthQueryDTO;
import cn.tomato.ai.api.dto.GatewayConfigDTO;
import cn.tomato.ai.api.dto.GatewayConfigQueryDTO;
import cn.tomato.ai.api.dto.GatewayConfigRequestDTO;
import cn.tomato.ai.api.dto.GatewayProtocolDTO;
import cn.tomato.ai.api.dto.GatewayProtocolQueryDTO;
import cn.tomato.ai.api.dto.GatewayToolConfigDTO;
import cn.tomato.ai.api.dto.GatewayToolQueryDTO;
import cn.tomato.ai.api.dto.GatewayConfigResponseDTO;
import cn.tomato.ai.api.response.Response;
import cn.tomato.ai.api.response.ResponsePage;

import java.util.List;

/**
 * 管理端服务接口
 * 17 端点对外契约
 *
 * @author Wxh
 * @date 2026-06-11
 */
public interface IAdminService {

    /**
     * 保存网关基础配置
     */
    Response<Boolean> saveGatewayConfig(GatewayConfigRequestDTO.GatewayConfig request);

    /**
     * 保存网关工具配置
     */
    Response<Boolean> saveGatewayToolConfig(GatewayConfigRequestDTO.GatewayToolConfig request);

    /**
     * 保存网关协议配置
     */
    Response<Boolean> saveGatewayProtocol(GatewayConfigRequestDTO.GatewayProtocol request);

    /**
     * 保存网关鉴权配置
     */
    Response<Boolean> saveGatewayAuth(GatewayConfigRequestDTO.GatewayAuth request);

    /**
     * 查询网关配置列表
     */
    Response<List<GatewayConfigDTO>> queryGatewayConfigList();

    /**
     * 分页查询网关配置
     */
    ResponsePage<List<GatewayConfigDTO>> queryGatewayConfigPage(GatewayConfigQueryDTO q);

    /**
     * 查询所有工具列表
     */
    Response<List<GatewayToolConfigDTO>> queryGatewayToolList();

    /**
     * 分页查询工具
     */
    ResponsePage<List<GatewayToolConfigDTO>> queryGatewayToolPage(GatewayToolQueryDTO q);

    /**
     * 按网关 ID 查询工具列表
     */
    Response<List<GatewayToolConfigDTO>> queryGatewayToolListByGatewayId(String gatewayId);

    /**
     * 删除工具
     */
    Response<Boolean> deleteGatewayToolConfig(String gatewayId, Long toolId);

    /**
     * 查询所有鉴权
     */
    Response<List<GatewayAuthDTO>> queryGatewayAuthList();

    /**
     * 分页查询鉴权
     */
    ResponsePage<List<GatewayAuthDTO>> queryGatewayAuthPage(GatewayAuthQueryDTO q);

    /**
     * 按网关 ID 删除鉴权
     */
    Response<Boolean> deleteGatewayAuth(String gatewayId);

    /**
     * 查询所有协议
     */
    Response<List<GatewayProtocolDTO>> queryGatewayProtocolList();

    /**
     * 分页查询协议
     */
    ResponsePage<List<GatewayProtocolDTO>> queryGatewayProtocolPage(GatewayProtocolQueryDTO q);

    /**
     * 按网关 ID 查询协议列表
     */
    Response<List<GatewayProtocolDTO>> queryGatewayProtocolListByGatewayId(String gatewayId);

    /**
     * 按协议 ID 删除协议
     */
    Response<Boolean> deleteGatewayProtocol(Long protocolId);

    /**
     * 解析 Swagger 协议（不落库，仅预览）
     */
    Response<List<GatewayProtocolDTO>> analysisProtocol(GatewayConfigRequestDTO.GatewayProtocolImport requestDTO);

    /**
     * 导入 Swagger 协议（解析 + 落库）
     */
    Response<GatewayConfigResponseDTO> importGatewayProtocol(GatewayConfigRequestDTO.GatewayProtocolImport requestDTO);

}
