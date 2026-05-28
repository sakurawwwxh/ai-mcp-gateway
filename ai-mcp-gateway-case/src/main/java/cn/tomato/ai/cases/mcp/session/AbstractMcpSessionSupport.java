package cn.tomato.ai.cases.mcp.session;

import cn.bugstack.wrench.design.framework.tree.AbstractMultiThreadStrategyRouter;
import cn.tomato.ai.cases.mcp.session.factory.DefaultMcpSessionFactory;
import cn.tomato.ai.domain.session.service.ISessionManagementService;
import jakarta.annotation.Resource;
import org.springframework.http.codec.ServerSentEvent;
import reactor.core.publisher.Flux;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

/**
 * @author Wxh
 * @date 2026年05月28日 21:25
 */
public abstract class AbstractMcpSessionSupport  extends AbstractMultiThreadStrategyRouter<String, DefaultMcpSessionFactory.DynamicContext, Flux<ServerSentEvent<String>>>  {

    @Resource
    protected ISessionManagementService sessionManagementService;

    @Override
    protected void multiThread(String requestParameter, DefaultMcpSessionFactory.DynamicContext dynamicContext) throws ExecutionException, InterruptedException, TimeoutException {

    }
}
