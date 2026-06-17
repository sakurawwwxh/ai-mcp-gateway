package cn.tomato.ai.infrastructure.dao.po;

import cn.tomato.ai.infrastructure.dao.po.base.BasePagePO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * 网关配置持久化对象
 * 对应表：mcp_gateway
 *
 * @author Wxh
 * @date 2026-06-15
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class McpGatewayPO extends BasePagePO {
    /**
     * 主键ID
     */
    private Long id;

    /**
     * 网关唯一标识
     */
    private String gatewayId;

    /**
     * 网关名称
     */
    private String gatewayName;

    /**
     * 网关描述
     */
    private String gatewayDesc;

    /**
     * 网关版本
     */
    private String version;

    /**
     * 状态：0-禁用，1-启用
     */
    private Integer status;

    /**
     * 状态：0-不校验，1-强校验
     */
    private Integer auth;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;
}
