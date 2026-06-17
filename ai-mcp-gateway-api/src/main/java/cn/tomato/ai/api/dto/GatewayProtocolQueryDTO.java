package cn.tomato.ai.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 网关协议分页查询入参 DTO
 *
 * @author Wxh
 * @date 2026-06-15
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GatewayProtocolQueryDTO {

    /** 协议 ID（精确匹配） */
    private Long protocolId;

    /** 网关 ID（经工具表关联查询使用，可选） */
    private String gatewayId;

    /** HTTP URL（模糊匹配,可选） */
    private String httpUrl;

    /** 当前页码（从 1 开始） */
    private Integer page;

    /** 每页条数 */
    private Integer rows;

}