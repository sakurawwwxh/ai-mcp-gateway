package cn.tomato.ai.domain.admin.model.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 网关鉴权分页查询条件
 *
 * @author Wxh
 * @date 2026-06-15
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GatewayAuthQueryEntity {

    /** 网关 ID（精确匹配） */
    private String gatewayId;

    /** 当前页码 */
    private Integer page;

    /** 每页条数 */
    private Integer rows;

}