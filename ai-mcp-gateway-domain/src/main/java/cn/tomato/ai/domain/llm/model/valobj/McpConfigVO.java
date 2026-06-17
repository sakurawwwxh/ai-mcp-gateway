package cn.tomato.ai.domain.llm.model.valobj;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * MCP 客户端连接配置值对象
 *
 * <p>由 case 层根据请求参数与本地服务配置（端口 / 上下文路径）拼装而成，
 * 传递给 domain 层用于建立 SSE 长连接并装配工具回调。
 *
 * @author Wxh
 * @date 2026-06-17
 */
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class McpConfigVO {

    /** 网关服务基础地址，例如 http://127.0.0.1:8777 */
    private String baseUri;

    /** MCP SSE 端点相对路径，例如 /api-gateway/{gatewayId}/mcp/sse?api_key=xxx */
    private String sseEndpoint;

    /** 网关鉴权 API Key */
    private String authApiKey;

    /** MCP 客户端请求超时时间（毫秒） */
    private Long timeout;

}
