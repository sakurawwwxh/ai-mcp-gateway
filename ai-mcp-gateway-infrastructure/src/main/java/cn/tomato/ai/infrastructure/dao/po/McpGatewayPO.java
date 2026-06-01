package cn.tomato.ai.infrastructure.dao.po;

import lombok.Data;

import java.util.Date;

/**
 * 网关配置持久化对象
 * 对应表：mcp_gateway
 */
@Data
public class McpGatewayPO {
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
     * 状态：0-禁用，1-启用
     */
    private Integer status;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;
}
