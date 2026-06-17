package cn.tomato.ai.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * 网关鉴权配置 DTO
 *
 * @author Wxh
 * @date 2026-06-15
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GatewayAuthDTO {

    /** 网关 ID */
    private String gatewayId;

    /** API 密钥 */
    private String apiKey;

    /** 速率限制（次/小时） */
    private Integer rateLimit;

    /** 过期时间 */
    private Date expireTime;

}