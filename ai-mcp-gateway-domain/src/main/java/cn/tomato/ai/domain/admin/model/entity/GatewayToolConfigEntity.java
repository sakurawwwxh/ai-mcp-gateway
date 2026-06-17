package cn.tomato.ai.domain.admin.model.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 网关工具配置实体
 *
 * @author Wxh
 * @date 2026-06-15
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GatewayToolConfigEntity {

    /** 所属网关 ID */
    private String gatewayId;

    /** 工具 ID */
    private Long toolId;

    /** 工具名称 */
    private String toolName;

    /** 工具类型 */
    private String toolType;

    /** 工具描述 */
    private String toolDescription;

    /** 工具版本 */
    private String toolVersion;

    /** 协议 ID */
    private Long protocolId;

    /** 协议类型 */
    private String protocolType;

}