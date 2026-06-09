package cn.tomato.ai.cases.mcp.session;

import cn.bugstack.wrench.design.framework.tree.StrategyHandler;
import cn.tomato.ai.cases.mcp.IMcpSessionService;
import cn.tomato.ai.cases.mcp.session.factory.DefaultMcpSessionFactory;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

/**
 * @author Wxh
 * @date 2026年05月28日 21:22
 */
@Slf4j
@Service
public class McpSessionService  implements IMcpSessionService {

    @Resource
    private DefaultMcpSessionFactory defaultMcpSessionFactory;

    @Override
    public Flux<ServerSentEvent<String>> createMcpSession(String gatewayId,String apiKey) throws Exception {
        StrategyHandler<String, DefaultMcpSessionFactory.DynamicContext, Flux<ServerSentEvent<String>>> strategyHandler = defaultMcpSessionFactory.strategyHandler();

        DefaultMcpSessionFactory.DynamicContext dynamicContext = new DefaultMcpSessionFactory.DynamicContext();
        dynamicContext.setApiKey(apiKey);

        return strategyHandler.apply(gatewayId,dynamicContext);
    }
}
