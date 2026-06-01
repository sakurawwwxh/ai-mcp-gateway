package cn.tomato.ai.domain.session.service.message.handle.impl;

import cn.tomato.ai.domain.session.model.valobj.McpSchemaVO;
import cn.tomato.ai.domain.session.service.message.handle.IRequestHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * @author Wxh
 * @date 2026年05月29日 17:14
 */
@Slf4j
@Service("resourcesListHandler")
public class ResourcesListHandler implements IRequestHandler {
    @Override
    public McpSchemaVO.JSONRPCResponse handle(String gatewayId, McpSchemaVO.JSONRPCRequest message) {
        return new McpSchemaVO.JSONRPCResponse("2.0", message.id(), Map.of(
                "resources", Map.of(
                        "resources", new Object[]{}
                )
        ), null);
    }
}
