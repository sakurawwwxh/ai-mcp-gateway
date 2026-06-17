package cn.tomato.ai.cases.admin.llm;

import cn.tomato.ai.api.dto.GatewayLLMRequestDTO;
import cn.tomato.ai.cases.admin.IAdminLLMService;
import cn.tomato.ai.domain.llm.model.entity.BuildChatModelCommandEntity;
import cn.tomato.ai.domain.llm.model.valobj.McpConfigVO;
import cn.tomato.ai.domain.llm.service.ILLMService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * 管理端-LLM 对接测试服务实现
 *
 * <p>编排流程：
 * <ol>
 *     <li>按本地服务端口/上下文路径拼装 MCP SSE 端点（含 api_key 鉴权参数）</li>
 *     <li>首次或 reload=true 时调用 domain 构建 ChatModel（建立 SSE 连接 + 拉取工具回调）</li>
 *     <li>复用已缓存 ChatModel 调用 LLM 推理，返回文本</li>
 * </ol>
 *
 * @author Wxh
 * @date 2026-06-17
 */
@Slf4j
@Service
public class AdminLLMService implements IAdminLLMService {

    @Resource
    private ILLMService llmService;

    /** 网关服务端口，来自 server.port */
    @Value("${server.port}")
    private String port;

    /** 网关服务上下文路径，来自 server.servlet.context-path */
    @Value("${server.servlet.context-path}")
    private String contextPath;

    /** 默认 MCP 客户端请求超时（毫秒），当请求未指定时使用 */
    private static final long DEFAULT_TIMEOUT = 60000L;

    @Override
    public String testCallGateway(GatewayLLMRequestDTO requestDTO) {
        String gatewayId = requestDTO.getGatewayId();
        String message = requestDTO.getMessage();
        String authApiKey = requestDTO.getAuthApiKey();
        Long timeout = null != requestDTO.getTimeout() ? requestDTO.getTimeout() : DEFAULT_TIMEOUT;
        boolean reload = null != requestDTO.getReload() && requestDTO.getReload();

        log.info("LLM测试调用 gatewayId:{} message:{} reload:{}", gatewayId, message, reload);

        // 1. 拼装本地网关 MCP SSE 端点（baseUri 指向本机服务，端点含 contextPath/gatewayId/api_key）
        String baseUri = "http://127.0.0.1:" + port;
        String sseEndpoint = contextPath + "/" + gatewayId + "/mcp/sse?api_key=" + authApiKey;
        McpConfigVO mcpConfigVO = McpConfigVO.builder()
                .baseUri(baseUri)
                .sseEndpoint(sseEndpoint)
                .authApiKey(authApiKey)
                .timeout(timeout)
                .build();

        // 2. 首次构建或强制重建 ChatModel（建立 SSE 连接 + 注入工具回调）
        if (reload || null == llmService.getChatModel(gatewayId)) {
            BuildChatModelCommandEntity commandEntity = BuildChatModelCommandEntity.builder()
                    .gatewayId(gatewayId)
                    .mcpConfigVO(mcpConfigVO)
                    .build();
            llmService.buildChatModel(commandEntity);
        }

        // 3. 取已缓存 ChatModel 调用 LLM 推理（工具回调已在 options 中注入，LLM 可自主调用网关工具）
        ChatModel chatModel = llmService.getChatModel(gatewayId);
        String content = chatModel.call(message);
        log.info("LLM测试调用完成 gatewayId:{}", gatewayId);
        return content;
    }

}
