package cn.tomato.ai.cases.admin;

import cn.tomato.ai.api.dto.GatewayLLMRequestDTO;

/**
 * 管理端-LLM 对接测试服务接口（编排层）
 *
 * <p>编排流程：拼装本地网关 SSE 端点 → 构建/复用 ChatModel 缓存 → 调用 LLM 推理。
 *
 * @author Wxh
 * @date 2026-06-17
 */
public interface IAdminLLMService {

    /**
     * 通过 LLM 对接测试指定网关的工具链路。
     *
     * @param requestDTO 测试请求（网关 ID、消息、鉴权 Key、超时、是否重建缓存）
     * @return LLM 最终回复的文本内容
     */
    String testCallGateway(GatewayLLMRequestDTO requestDTO);

}
