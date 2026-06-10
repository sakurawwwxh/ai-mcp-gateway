package cn.tomato.ai.domain.protocol.service.analysis.strategy.impl;

import cn.tomato.ai.domain.protocol.model.entity.AnalysisCommandEntity;
import cn.tomato.ai.domain.protocol.model.valobj.http.HTTPProtocolVO;
import cn.tomato.ai.domain.protocol.service.analysis.strategy.AbstractProtocolAnalysisStrategy;
import cn.tomato.ai.domain.protocol.service.analysis.strategy.IProtocolAnalysisStrategy;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author Wxh
 * @date 2026年06月09日 17:11
 */
@Slf4j
@Component("requestBodyAnalysis")
public class RequestBodyProtocolAnalysisStrategy extends AbstractProtocolAnalysisStrategy {


    @Override
    public void doAnalysis(JSONObject operation, JSONObject definitions, List<HTTPProtocolVO.ProtocolMapping> mappings) {
        JSONObject requestBody = operation.getJSONObject("requestBody");
        if (requestBody == null) return;

        JSONObject content = requestBody.getJSONObject("content");
        JSONObject appJson = content.getJSONObject("application/json");
        if (appJson == null) return;

        JSONObject schema = appJson.getJSONObject("schema");
        String ref = schema.getString("$ref");

        if (ref != null) {
            String refName = ref.substring(ref.lastIndexOf('/') + 1);
            JSONObject reqSchema = definitions.getJSONObject(refName);
            String rootName = toLowerCamel(refName);

            HTTPProtocolVO.ProtocolMapping rootMapping = HTTPProtocolVO.ProtocolMapping.builder()
                    .mappingType("request")
                    .parentPath(null)
                    .fieldName(rootName)
                    .mcpPath(rootName)
                    .mcpType(convertType(reqSchema.getString("type")))
                    .mcpDesc(reqSchema.getString("description"))
                    .isRequired(1)
                    .sortOrder(1)
                    .build();

            mappings.add(rootMapping);

            parseProperties(rootName, reqSchema.getJSONObject("properties"), reqSchema.getJSONArray("required"), definitions, mappings);
        }
    }
}
