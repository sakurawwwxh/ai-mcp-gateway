package cn.tomato.ai.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * LLM 对接测试 MCP 网关请求 DTO
 *
 * <p>管理端通过 ChatModel + MCP 协议回调测试某个网关的工具链路是否可用。
 *
 * @author Wxh
 * @date 2026-06-17
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GatewayLLMRequestDTO {

    /** 网关 ID */
    private String gatewayId;

    /** 向 LLM 提出的自然语言消息（由 LLM 自主决定是否调用网关工具） */
    private String message;

    /** 网关鉴权 API Key，拼接到 SSE 端点 ?api_key=xxx */
    private String authApiKey;

    /** MCP 客户端请求超时时间（毫秒） */
    private Long timeout;

    /** 是否强制重建该网关的 ChatModel 缓存（配置变更后传 true） */
    private Boolean reload;

}
