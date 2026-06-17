package cn.tomato.ai.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * LLM 对接测试 MCP 网关响应 DTO
 *
 * @author Wxh
 * @date 2026-06-17
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GatewayLLMResponseDTO {

    /** LLM 最终回复的文本内容（可能包含工具调用结果） */
    private String content;

}
