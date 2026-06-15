package cn.tomato.ai.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 写入操作返回 DTO
 *
 * @author Wxh
 * @date 2026-06-11
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GatewayConfigResponseDTO {

    /** 是否成功 */
    private Boolean success;

    /**
     * 服务端生成的 API Key（仅 /admin/save_gateway_auth 端点返回）
     * 前端需展示给用户，用于 SSE 连接 ?api_key=xxx 鉴权
     */
    private String apiKey;

}
