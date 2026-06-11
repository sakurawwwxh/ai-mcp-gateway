package cn.tomato.ai.domain.gateway.model.valobj;

import lombok.*;

/**
 * 网关工具配置值对象
 *
 * @author Wxh
 * @date 2026-06-11
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GatewayToolConfigVO {

    /**
     * 所属网关ID
     */
    private String gatewayId;
    /**
     * 工具ID
     */
    private Long toolId;
    /**
     * MCP工具名称（如：JavaSDKMCPClient_getCompanyEmployee）
     */
    private String toolName;
    /**
     * 工具类型：function/resource
     */
    private String toolType;
    /**
     * 工具描述
     */
    private String toolDescription;
    /**
     * 工具版本
     */
    private String toolVersion;
    /**
     * 协议ID
     */
    private Long protocolId;
    /**
     * 协议类型；http、dubbo、rabbitmq
     */
    private String protocolType;

}
