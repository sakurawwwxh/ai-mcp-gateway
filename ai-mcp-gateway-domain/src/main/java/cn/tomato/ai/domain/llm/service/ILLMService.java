package cn.tomato.ai.domain.llm.service;

import cn.tomato.ai.domain.llm.model.entity.BuildChatModelCommandEntity;
import org.springframework.ai.chat.model.ChatModel;

/**
 * LLM 对接领域服务
 *
 * <p>负责按网关 ID 构建 / 缓存 ChatModel，并将 MCP 协议工具回调注入其中，
 * 使 LLM 可在推理过程中自主调用网关暴露的工具。
 *
 * @author Wxh
 * @date 2026-06-17
 */
public interface ILLMService {

    /**
     * 构建并缓存指定网关的 ChatModel（覆盖已有缓存）。
     *
     * @param commandEntity 构建命令（含网关 ID 与 MCP 连接配置）
     */
    void buildChatModel(BuildChatModelCommandEntity commandEntity);

    /**
     * 获取指定网关已缓存的 ChatModel。
     *
     * @param gatewayId 网关 ID
     * @return ChatModel 实例；未构建时返回 null
     */
    ChatModel getChatModel(String gatewayId);

}
