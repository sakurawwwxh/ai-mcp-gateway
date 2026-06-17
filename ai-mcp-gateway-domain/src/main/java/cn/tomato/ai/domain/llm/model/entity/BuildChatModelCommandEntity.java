package cn.tomato.ai.domain.llm.model.entity;

import cn.tomato.ai.domain.llm.model.valobj.McpConfigVO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 构建 / 重建 ChatModel 的命令实体
 *
 * @author Wxh
 * @date 2026-06-17
 */
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BuildChatModelCommandEntity {

    /** 网关 ID，作为 ChatModel 缓存键 */
    private String gatewayId;

    /** MCP 客户端连接配置 */
    private McpConfigVO mcpConfigVO;

}
