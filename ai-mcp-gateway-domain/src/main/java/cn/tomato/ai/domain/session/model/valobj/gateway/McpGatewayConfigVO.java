package cn.tomato.ai.domain.session.model.valobj.gateway;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 网关协议配置值对象
 * 包含网关及其工具的聚合信息，用于初始化响应和工具列表返回
 */
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class McpGatewayConfigVO {

    /**
     * 网关ID
     */
    private String gatewayId;

    /**
     * 网关名称
     */
    private String gatewayName;

    /**
     * 工具ID
     */
    private Long toolId;

    /**
     * 工具名称
     */
    private String toolName;

    /**
     * 工具描述
     */
    private String toolDesc;

    /**
     * 工具版本
     */
    private String toolVersion;

}
