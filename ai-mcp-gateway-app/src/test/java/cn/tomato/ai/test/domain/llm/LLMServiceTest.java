package cn.tomato.ai.test.domain.llm;

import cn.tomato.ai.domain.llm.model.entity.BuildChatModelCommandEntity;
import cn.tomato.ai.domain.llm.model.valobj.McpConfigVO;
import cn.tomato.ai.domain.llm.service.ILLMService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * LLM 对接领域服务测试
 *
 * <p>前置条件（任一不满足则本测试失败，属正常现象）：
 * <ol>
 *     <li>已执行 docs/dev-ops/mysql/sql/3-20-ai_mcp_gateway_llm_verify.sql 导入 gateway_005 数据</li>
 *     <li>网关服务已启动（端口 8777，context-path /api-gateway），且 gateway_005 可建立 SSE 连接</li>
 *     <li>下游业务服务已启动（localhost:8701），工具回调可达</li>
 *     <li>application-dev.yml 的 spring.ai.openai.* 配置指向可达的 LLM 网关</li>
 * </ol>
 *
 * @author Wxh
 * @date 2026-06-17
 */
@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest
public class LLMServiceTest {

    @Resource
    private ILLMService llmService;

    @Test
    public void test_buildAndCallChatModel() {
        // 1. 拼装 MCP 连接配置（指向本地网关 gateway_005）
        McpConfigVO mcpConfigVO = McpConfigVO.builder()
                .baseUri("http://127.0.0.1:8777")
                .sseEndpoint("/api-gateway/gateway_005/mcp/sse?api_key=gw-GPJBQHFeBWVMSGASFii5xtsmlHF5SjURFwh7C7yGRP3UtX")
                .authApiKey("gw-GPJBQHFeBWVMSGASFii5xtsmlHF5SjURFwh7C7yGRP3UtX")
                .timeout(60000L)
                .build();

        // 2. 构建 ChatModel（建立 SSE 连接 + 注入工具回调）
        BuildChatModelCommandEntity commandEntity = BuildChatModelCommandEntity.builder()
                .gatewayId("gateway_005")
                .mcpConfigVO(mcpConfigVO)
                .build();
        llmService.buildChatModel(commandEntity);

        // 3. 取缓存 ChatModel 并调用 LLM 推理
        ChatModel chatModel = llmService.getChatModel("gateway_005");
        assert chatModel != null : "ChatModel 构建后不应为 null";

        String content = chatModel.call("查询北京字节跳动的员工信息");
        log.info("LLM 回复:{}", content);
    }

}
