package cn.tomato.ai.cases.mcp.message;

import cn.tomato.ai.cases.mcp.message.factory.DefaultMcpMessageFactory;
import cn.tomato.ai.domain.session.model.entity.HandleMessageCommandEntity;
import cn.tomato.ai.domain.session.service.ISessionManagementService;
import cn.tomato.ai.domain.session.service.ISessionMessageService;
import cn.bugstack.wrench.design.framework.tree.AbstractMultiThreadStrategyRouter;
import org.springframework.http.ResponseEntity;

import jakarta.annotation.Resource;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

/**
 * 消息处理策略树抽象基类
 * 所有消息处理节点继承此类
 */
public abstract class AbstractMcpMessageServiceSupport extends AbstractMultiThreadStrategyRouter<HandleMessageCommandEntity, DefaultMcpMessageFactory.DynamicContext, ResponseEntity<Void>> {

    @Resource
    protected ISessionMessageService serviceMessageService;

    @Resource
    protected ISessionManagementService sessionManagementService;

    @Override
    protected void multiThread(HandleMessageCommandEntity requestParameter, DefaultMcpMessageFactory.DynamicContext dynamicContext) throws ExecutionException, InterruptedException, TimeoutException {
        // 暂不使用多线程
    }
}
