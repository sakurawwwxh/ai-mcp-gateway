package cn.tomato.ai.domain.llm.service.impl;

import cn.tomato.ai.domain.llm.model.entity.BuildChatModelCommandEntity;
import cn.tomato.ai.domain.llm.model.valobj.McpConfigVO;
import cn.tomato.ai.domain.llm.service.ILLMService;
import io.modelcontextprotocol.client.McpClient;
import io.modelcontextprotocol.client.McpSyncClient;
import io.modelcontextprotocol.client.transport.HttpClientSseClientTransport;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.mcp.SyncMcpToolCallbackProvider;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.ai.openai.api.OpenAiApi;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * LLM 对接领域服务实现
 *
 * <p>按网关 ID 构建 / 缓存 ChatModel：通过 SSE 长连接接入对应网关的 MCP 服务，
 * 拉取工具回调注入 OpenAiChatOptions，使 LLM 在推理时可自主调用网关工具。
 *
 * <p>ChatModel 缓存按 gatewayId 隔离；配置变更后由调用方传 reload=true 触发重建。
 *
 * @author Wxh
 * @date 2026-06-17
 */
@Slf4j
@Service
public class LLMService implements ILLMService {

    @Resource
    private OpenAiApi openAiApi;

    /** 模型名称，来自 spring.ai.openai.chat.options.model（如 mimo-v2.5） */
    @Value("${spring.ai.openai.chat.options.model}")
    private String model;

    /** 按 gatewayId 缓存的 ChatModel（线程安全） */
    private final Map<String, ChatModel> chatModelMap = new ConcurrentHashMap<>();

    @Override
    public void buildChatModel(BuildChatModelCommandEntity commandEntity) {
        String gatewayId = commandEntity.getGatewayId();
        McpConfigVO mcpConfigVO = commandEntity.getMcpConfigVO();
        log.info("构建ChatModel开始 gatewayId:{} baseUri:{} sseEndpoint:{}",
                gatewayId, mcpConfigVO.getBaseUri(), mcpConfigVO.getSseEndpoint());

        // 1. 建立 MCP SSE 长连接 transport
        HttpClientSseClientTransport transport = HttpClientSseClientTransport
                .builder(mcpConfigVO.getBaseUri())
                .sseEndpoint(mcpConfigVO.getSseEndpoint())
                .build();

        // 2. 创建同步 MCP 客户端并完成初始化握手
        McpSyncClient mcpSyncClient = McpClient.sync(transport)
                .requestTimeout(Duration.ofMillis(mcpConfigVO.getTimeout()))
                .build();
        mcpSyncClient.initialize();

        // 3. 从 MCP 客户端拉取工具回调（LLM 据此感知可调用工具）
        ToolCallback[] toolCallbacks = new SyncMcpToolCallbackProvider(mcpSyncClient).getToolCallbacks();

        // 4. 组装 ChatModel：工具回调注入 OpenAiChatOptions，复用全局 OpenAiApi
        OpenAiChatOptions options = OpenAiChatOptions.builder()
                .model(model)
                .toolCallbacks(toolCallbacks)
                .build();
        ChatModel chatModel = OpenAiChatModel.builder()
                .openAiApi(openAiApi)
                .defaultOptions(options)
                .build();

        chatModelMap.put(gatewayId, chatModel);
        log.info("构建ChatModel完成 gatewayId:{} toolCount:{}", gatewayId, toolCallbacks.length);
    }

    @Override
    public ChatModel getChatModel(String gatewayId) {
        return chatModelMap.get(gatewayId);
    }

}
