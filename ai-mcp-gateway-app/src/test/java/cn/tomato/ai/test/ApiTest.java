package cn.tomato.ai.test;

import io.modelcontextprotocol.client.McpClient;
import io.modelcontextprotocol.client.McpSyncClient;
import io.modelcontextprotocol.client.transport.HttpClientSseClientTransport;
import io.modelcontextprotocol.spec.McpSchema;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.mcp.SyncMcpToolCallbackProvider;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.Duration;

@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest
public class ApiTest {

    @Resource
    private ChatClient.Builder chatClientBuilder;

    @Test
    public void test_mcp() {
        ChatClient chatClient = chatClientBuilder.defaultOptions(
                OpenAiChatOptions.builder().model("mimo-v2.5")
                        .toolCallbacks(new SyncMcpToolCallbackProvider(sseMcpClient02()).getToolCallbacks())
                        .build()
        ).build();

        log.info("测试结果:{}", chatClient.prompt("把tomato转换为大写").call().content());
    }


    public McpSyncClient sseMcpClient02(){
        HttpClientSseClientTransport sseClientTransport = HttpClientSseClientTransport
                .builder("http://127.0.0.1:8777")
                .sseEndpoint("/api-gateway/test10001/mcp/sse")
                .build();

        McpSyncClient mcpSyncClient = McpClient.sync(sseClientTransport).requestTimeout(Duration.ofMillis(360000)).build();

        McpSchema.InitializeResult init = mcpSyncClient.initialize();
        log.info("Tool Mcp01 initialized {}",init);

        return mcpSyncClient;
    }

    /**
     * http://appbuilder.baidu.com/v2/ai_search/mcp/sse?api_key=bce-v3/ALTAK-9VesUGkpTVaSP9z3G2DMH/ebba0afc23dfecba61761109a696e5edb4cbe71e
     * 百度搜索MCP服务(url)；https://sai.baidu.com/zh/detail/e014c6ffd555697deabf00d058baf388
     * 百度搜索MCP服务(key - 可自行申请)；https://console.bce.baidu.com/iam/?_=1753597622044#/iam/apikey/list
     */
    public McpSyncClient sseMcpClient01(){
        HttpClientSseClientTransport sseClientTransport = HttpClientSseClientTransport
                .builder("http://appbuilder.baidu.com")
                .sseEndpoint("/v2/ai_search/mcp/sse?api_key=bce-v3/ALTAK-9VesUGkpTVaSP9z3G2DMH/ebba0afc23dfecba61761109a696e5edb4cbe71e")
                .build();

        McpSyncClient mcpSyncClient = McpClient.sync(sseClientTransport).requestTimeout(Duration.ofMillis(36000)).build();

        McpSchema.InitializeResult init = mcpSyncClient.initialize();
        log.info("Tool Mcp01 initialized {}",init);

        return mcpSyncClient;
    }

}
