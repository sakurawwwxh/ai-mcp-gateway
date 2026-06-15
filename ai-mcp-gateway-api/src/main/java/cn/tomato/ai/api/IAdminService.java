package cn.tomato.ai.api;

import cn.tomato.ai.api.dto.GatewayConfigDTO;
import cn.tomato.ai.api.dto.GatewayConfigRequestDTO;
import cn.tomato.ai.api.response.Response;

import java.util.List;

/**
 * 管理端服务接口
 * 5 端点对外契约
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

}
