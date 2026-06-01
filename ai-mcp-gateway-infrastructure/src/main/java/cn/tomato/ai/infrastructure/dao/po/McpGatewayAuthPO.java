package cn.tomato.ai.infrastructure.dao.po;

import lombok.Data;

import java.util.Date;

/**
 * 网关认证持久化对象
 * 对应表：mcp_gateway_auth
 */
@Data
public class McpGatewayAuthPO {
    /**
     * 主键ID
     */
    private Long id;

    /**
     * 网关ID
     */
    private String gatewayId;

    /**
     * API密钥
     */
    private String apiKey;

    /**
     * 速率限制（次/小时）
     */
    private Integer rateLimit;

    /**
     * 过期时间
     */
    private Date expireTime;

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
