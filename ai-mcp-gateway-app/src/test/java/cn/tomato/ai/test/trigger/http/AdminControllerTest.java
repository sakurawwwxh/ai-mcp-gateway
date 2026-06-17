package cn.tomato.ai.test.trigger.http;

import cn.tomato.ai.api.dto.GatewayLLMRequestDTO;
import cn.tomato.ai.api.response.Response;
import cn.tomato.ai.trigger.http.AdminController;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * 管理端 LLM 对接测试接口测试
 *
 * <p>前置条件（任一不满足则本测试失败，属正常现象）：
 * <ol>
 *     <li>已执行 docs/dev-ops/mysql/sql/3-20-ai_mcp_gateway_llm_verify.sql 导入 gateway_005 数据</li>
 *     <li>网关服务（本进程）已启动，gateway_005 可建立 SSE 连接</li>
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
public class AdminControllerTest {

    @Resource
    private AdminController adminController;

    @Test
    public void test_testCallGateway() {
        GatewayLLMRequestDTO request = GatewayLLMRequestDTO.builder()
                .gatewayId("gateway_005")
                .message("查询北京字节跳动的员工信息")
                .authApiKey("f7c3b9e2a1d84f6e9b0c3a5d7e8f1a2b")
                .timeout(60000L)
                .reload(true)
                .build();

        Response<cn.tomato.ai.api.dto.GatewayLLMResponseDTO> response = adminController.testCallGateway(request);
        log.info("响应 code:{} info:{} content:{}",
                response.getCode(),
                response.getInfo(),
                null != response.getData() ? response.getData().getContent() : null);

        assert "0000".equals(response.getCode()) : "主链路应返回 SUCCESS(0000)";
        assert response.getData() != null && response.getData().getContent() != null : "回复内容不应为空";
    }

}
