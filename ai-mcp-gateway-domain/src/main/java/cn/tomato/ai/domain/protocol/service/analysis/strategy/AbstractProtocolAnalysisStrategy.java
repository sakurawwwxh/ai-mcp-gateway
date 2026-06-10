package cn.tomato.ai.domain.protocol.service.analysis.strategy;

import cn.tomato.ai.domain.protocol.model.valobj.http.HTTPProtocolVO;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.util.List;

/**
 * @author Wxh
 * @date 2026年06月09日 17:11
 */
public abstract class AbstractProtocolAnalysisStrategy implements IProtocolAnalysisStrategy {


    protected void parseProperties(String parentMcpPath, JSONObject properties, JSONArray requiredList, JSONObject definitions, List<HTTPProtocolVO.ProtocolMapping> mappings) {
        if (properties == null) return;
        int sortOrder = 1;
        for (String propName : properties.keySet()) {
            JSONObject prop = properties.getJSONObject(propName);
            String currentMcpPath = parentMcpPath + "." + propName;
            JSONObject effectiveSchema = prop;
            String type = prop.getString("type");
            String description = prop.getString("description");
            if (prop.containsKey("$ref")) {
                String ref = prop.getString("$ref");
                String refName = ref.substring(ref.lastIndexOf("/") + 1);
                effectiveSchema = definitions.getJSONObject(refName);
                if (type == null) type = effectiveSchema.getString("type");
                if (description == null) description = effectiveSchema.getString("description");
            }
            HTTPProtocolVO.ProtocolMapping m = HTTPProtocolVO.ProtocolMapping.builder()
                    .mappingType("request").parentPath(parentMcpPath).fieldName(propName).mcpPath(currentMcpPath)
                    .mcpType(convertType(type)).mcpDesc(description)
                    .isRequired(requiredList != null && requiredList.contains(propName) ? 1 : 0).sortOrder(sortOrder++).build();
            mappings.add(m);
            if (effectiveSchema.containsKey("properties")) {
                parseProperties(currentMcpPath, effectiveSchema.getJSONObject("properties"), effectiveSchema.getJSONArray("required"), definitions, mappings);
            }
        }
    }

    protected String convertType(String type) {
        if (type == null) return "string";
        return switch (type.toLowerCase()) {
            case "string", "char", "date", "datetime" -> "string";
            case "integer", "int", "long", "double", "float", "number" -> "number";
            case "boolean", "bool" -> "boolean";
            case "array", "list" -> "array";
            default -> "object";
        };
    }


    protected String toLowerCamel(String name) {
        if (name == null || name.isEmpty()) return name;
        char[] cs = name.toCharArray();
        cs[0] = Character.toLowerCase(cs[0]);
        return new String(cs);
    }
}
