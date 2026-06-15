package cn.tomato.ai.domain.admin.model.entity;

import cn.tomato.ai.types.enums.GatewayEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 管理端-网关配置聚合实体
 * 由 AdminRepository 装配,供 case 层翻译为 DTO
 *
 * @author Wxh
 * @date 2026-06-11
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GatewayConfigEntity {

    private String gatewayId;
    private String name;
    private String desc;
    private String version;
    private GatewayEnum.GatewayAuthStatusEnum auth;
    private GatewayEnum.GatewayStatus status;

}
