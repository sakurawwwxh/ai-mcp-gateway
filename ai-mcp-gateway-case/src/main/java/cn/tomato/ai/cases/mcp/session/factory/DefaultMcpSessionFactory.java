package cn.tomato.ai.cases.mcp.session.factory;

import cn.bugstack.wrench.design.framework.tree.StrategyHandler;
import cn.tomato.ai.cases.mcp.session.node.RootNode;
import cn.tomato.ai.domain.session.model.valobj.SessionConfigVO;
import jakarta.annotation.Resource;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

/**
 * @author Wxh
 * @date 2026年05月28日 21:39
 */
@Service
public class DefaultMcpSessionFactory {

    @Resource
    private RootNode rootNode;

    public StrategyHandler<String, DefaultMcpSessionFactory.DynamicContext, Flux<ServerSentEvent<String>>> 
strategyHandler (){
        return rootNode;
    }
    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class DynamicContext{
        private SessionConfigVO sessionConfigVO;
    }
}
