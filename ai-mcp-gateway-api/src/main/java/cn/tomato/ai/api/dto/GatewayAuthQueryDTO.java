package cn.tomato.ai.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 网关鉴权分页查询入参 DTO
 *
 * @author Wxh
 * @date 2026-06-15
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GatewayAuthQueryDTO {

    /** 网关 ID（精确匹配） */
    private String gatewayId;

    /** 当前页码（从 1 开始） */
    private Integer page;

    /** 每页条数 */
    private Integer rows;

}