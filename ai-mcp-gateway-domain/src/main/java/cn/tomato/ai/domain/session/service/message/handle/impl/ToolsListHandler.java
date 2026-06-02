package cn.tomato.ai.domain.session.service.message.handle.impl;

import cn.tomato.ai.domain.session.adapter.repository.ISessionRepository;
import cn.tomato.ai.domain.session.model.valobj.McpSchemaVO;
import cn.tomato.ai.domain.session.model.valobj.gateway.McpGatewayConfigVO;
import cn.tomato.ai.domain.session.model.valobj.gateway.McpGatewayToolConfigVO;
import cn.tomato.ai.domain.session.service.message.handle.IRequestHandler;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author Wxh
 * @date 2026年05月29日 17:14
 */
@Slf4j
@Service("toolsListHandler")
public class ToolsListHandler implements IRequestHandler {


    @Resource
    private ISessionRepository repository;


    @Override
    public McpSchemaVO.JSONRPCResponse handle(String gatewayId, McpSchemaVO.JSONRPCRequest message) {

        //1.查询网关配置
        McpGatewayConfigVO mcpGatewayConfigVO = repository.queryMcpGatewayConfigByGatewayId(gatewayId);

        //2. 查询网关工具列表
        List<McpGatewayToolConfigVO> mcpGatewayToolConfigVOS = repository.queryMcpGatewayToolConfigListByGatewayId(gatewayId);


        List<McpSchemaVO.Tool> tools = buildTools(mcpGatewayConfigVO, mcpGatewayToolConfigVOS);

        return new McpSchemaVO.JSONRPCResponse("2.0", message.id(),Map.of("tools",tools), null);
    }

    private List<McpSchemaVO.Tool> buildTools(McpGatewayConfigVO gatewayConfig, List<McpGatewayToolConfigVO> toolConfigs) {

        Map<Long, List<McpGatewayToolConfigVO>> toolsMap = toolConfigs.stream().collect(Collectors.groupingBy(McpGatewayToolConfigVO::getToolId));

        List<McpSchemaVO.Tool> tools = new ArrayList<>();

        for (Map.Entry<Long, List<McpGatewayToolConfigVO>> entry : toolsMap.entrySet()) {

            Long toolId = entry.getKey();
            List<McpGatewayToolConfigVO> configs = entry.getValue();

            //排序
            configs.sort(((o1, o2) -> {
                int s1 = o1.getSortOrder() != null ? o1.getSortOrder() : 0;
                int s2 = o2.getSortOrder() != null ? o2.getSortOrder() : 0;
                return Integer.compare(s1, s2);
            }));


            // 父子元素 Map parentPath -> List<Children>
            Map<String, List<McpGatewayToolConfigVO>> childrenMap = new HashMap<>();

            List<McpGatewayToolConfigVO> roots = new ArrayList<>();

            for (McpGatewayToolConfigVO config : configs) {
                if (config.getParentPath() == null) {
                    roots.add(config);
                } else {
                    childrenMap.computeIfAbsent(config.getParentPath(), k -> new ArrayList<>()).add(config);
                }
            }

            // 排序
            roots.sort(((o1, o2) -> {
                int s1 = o1.getSortOrder() != null ? o1.getSortOrder() : 0;
                int s2 = o2.getSortOrder() != null ? o2.getSortOrder() : 0;
                return Integer.compare(s1, s2);
            }));


            // 构建输入结构
            Map<String, Object> properties = new HashMap<>();
            List<String> required = new ArrayList<>();

            for (McpGatewayToolConfigVO root : roots) {
                properties.put(root.getFieldName(), buildProperty(root, childrenMap));
                if (Integer.valueOf(1).equals(root.getIsRequired())) {
                    required.add(root.getFieldName());
                }
            }

            // 获取类型
            String type = roots.size() == 1 ? roots.get(0).getMcpType() : "object";

            // 构造函数
            McpSchemaVO.JsonSchema inputSchema = new McpSchemaVO.JsonSchema(
                    type,
                    properties,
                    required.isEmpty() ? null : required,
                    false,
                    null,
                    null
            );

            // 工具描述
            String name = "unknown-tool-" + toolId;
            String desc = "";
            if (gatewayConfig != null && Objects.equals(gatewayConfig.getToolId(), toolId)) {
                name = gatewayConfig.getToolName();
                desc = gatewayConfig.getToolDesc();
            }

            tools.add(new McpSchemaVO.Tool(name, desc, inputSchema));
        }
        return tools;
    }


    private Map<String, Object> buildProperty(McpGatewayToolConfigVO current, Map<String, List<McpGatewayToolConfigVO>> childrenMap) {
        Map<String, Object> property = new HashMap<>();
        property.put("type", current.getMcpType());
        if (current.getMcpDesc() != null) {
            property.put("description", current.getMcpDesc());
        }

        // 校验孩子元素
        List<McpGatewayToolConfigVO> children = childrenMap.get(current.getMcpPath());
        if (children != null && !children.isEmpty()) {
            Map<String, Object> props = new HashMap<>();
            List<String> reqs = new ArrayList<>();

            // 排序
            children.sort((o1, o2) -> {
                int s1 = o1.getSortOrder() != null ? o1.getSortOrder() : 0;
                int s2 = o2.getSortOrder() != null ? o2.getSortOrder() : 0;
                return Integer.compare(s1, s2);
            });

            for (McpGatewayToolConfigVO child : children) {
                // 注意，buildProperty 嵌套递归，一层层的寻找，是否还有孩子元素（children）
                props.put(child.getFieldName(), buildProperty(child, childrenMap));
                if (Integer.valueOf(1).equals(child.getIsRequired())) {
                    reqs.add(child.getFieldName());
                }
            }

            property.put("properties", props);

            if (!reqs.isEmpty()) {
                property.put("required", reqs);
            }

        }
        return property;
    }
}
