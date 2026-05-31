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
@Service("toolsListHandler")
public class ToolsListHandler implements IRequestHandler {
    @Override
    public McpSchemaVO.JSONRPCResponse handle(McpSchemaVO.JSONRPCRequest message) {

        return new McpSchemaVO.JSONRPCResponse("2.0", message.id(), Map.of(
                "tools", new Object[]{
                        Map.of(
                                "name", "toUpperCase",
                                "description", "小写转大写",
                                "inputSchema", Map.of(
                                        "type", "object",
                                        "properties", Map.of(
                                                "word", Map.of(
                                                        "type", "string",
                                                        "description", "单词，字符串"
                                                )
                                        ),
                                        "required", new String[]{"word"}
                                )
                        )
                }
        ), null);
    }
}
