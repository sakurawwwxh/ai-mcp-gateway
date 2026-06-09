package cn.tomato.ai.cases.mcp.session.node;

import cn.bugstack.wrench.design.framework.tree.AbstractMultiThreadStrategyRouter;
import cn.bugstack.wrench.design.framework.tree.StrategyHandler;
import cn.tomato.ai.cases.mcp.session.AbstractMcpSessionSupport;
import cn.tomato.ai.cases.mcp.session.factory.DefaultMcpSessionFactory;
import cn.tomato.ai.domain.auth.model.entity.LicenseCommandEntity;
import cn.tomato.ai.domain.auth.service.IAuthLicenseService;
import cn.tomato.ai.types.enums.McpErrorCodes;
import cn.tomato.ai.types.exception.AppException;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

/**
 * @author Wxh
 * @date 2026年05月28日 21:41
 */
@Slf4j
@Service("mcpSessionVerifyNode")
public class VerifyNode extends AbstractMcpSessionSupport {

    @Resource(name = "mcpSessionSessionNode")
    private SessionNode sessionNode;

    @Resource
    private IAuthLicenseService  authLicenseService;

    @Override
    protected Flux<ServerSentEvent<String>> doApply(String requestParameter, DefaultMcpSessionFactory.DynamicContext dynamicContext) throws Exception {

        log.info("创建会话-VerifyNode:{}",requestParameter);


        boolean isCheckedSuccess = authLicenseService.checkLicense(new LicenseCommandEntity(requestParameter, dynamicContext.getApiKey()));

        if (!isCheckedSuccess) {
            throw new AppException(McpErrorCodes.INSUFFICIENT_PERMISSIONS,"fail to auth apikey");
        }

        return router(requestParameter, dynamicContext);
    }

    @Override
    public StrategyHandler<String, DefaultMcpSessionFactory.DynamicContext, Flux<ServerSentEvent<String>>> get(String requestParameter, DefaultMcpSessionFactory.DynamicContext dynamicContext) throws Exception {
        return sessionNode;
    }
}
