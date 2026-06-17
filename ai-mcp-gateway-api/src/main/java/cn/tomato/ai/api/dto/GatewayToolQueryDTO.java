package cn.tomato.ai.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 网关工具分页查询入参 DTO
 *
 * @author Wxh
 * @date 2026-06-15
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GatewayToolQueryDTO {

    /** 所属网关 ID（精确匹配） */
    private String gatewayId;

    /** 工具名称（模糊匹配） */
    private String toolName;

    /** Tool ID（精确匹配,字符串以兼容 8 位数字与未来扩展） */
    private String toolId;

    /** 当前页码（从 1 开始） */
    private Integer page;

    /** 每页条数 */
    private Integer rows;

}