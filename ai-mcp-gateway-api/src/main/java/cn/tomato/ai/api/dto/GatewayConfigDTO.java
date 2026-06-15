package cn.tomato.ai.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 网关配置查询返回 DTO
 *
 * @author Wxh
 * @date 2026-06-11
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GatewayConfigDTO {

    /** 网关唯一标识 */
    private String gatewayId;

    /** 网关名称 */
    private String name;

    /** 网关描述 */
    private String desc;

    /** 协议版本 */
    private String version;

    /** 鉴权类型 (enum name) */
    private String auth;

    /** 状态 (enum name) */
    private String status;

}
