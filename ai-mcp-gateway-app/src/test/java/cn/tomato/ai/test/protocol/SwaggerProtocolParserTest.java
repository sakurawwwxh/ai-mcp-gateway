package cn.tomato.ai.test.protocol;

import cn.tomato.ai.domain.protocol.model.valobj.http.HTTPProtocolVO;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.junit.Assert;
import org.junit.Test;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class SwaggerProtocolParserTest {

    @Test
    public void parseSwaggerAndBuildHTTPProtocolVO() throws Exception {
        String json;
        try (InputStream is = getClass().getClassLoader().getResourceAsStream("swagger/api-docs-test03.json")) {
            Assert.assertNotNull(is);
            json = new String(is.readAllBytes(), StandardCharsets.UTF_8);
        }
        List<String> endpoints = Arrays.asList("/api/v1/mcp/get_company_employee");
        List<HTTPProtocolVO> result = parse(json, endpoints);

        Assert.assertNotNull(result);
        Assert.assertEquals(1, result.size());

        HTTPProtocolVO vo = result.get(0);
        Assert.assertNotNull(vo.getHttpUrl());
        Assert.assertTrue(vo.getHttpUrl().endsWith("/api/v1/mcp/get_company_employee"));
        Assert.assertEquals("post", vo.getHttpMethod());
        Assert.assertNotNull(vo.getHttpHeaders());
        Assert.assertTrue(vo.getHttpHeaders().contains("application/json"));
        Assert.assertEquals(Integer.valueOf(30000), vo.getTimeout());
        Assert.assertNotNull(vo.getMappings());
        Assert.assertFalse(vo.getMappings().isEmpty());

        long rootCount = vo.getMappings().stream()
                .filter(m -> m.getParentPath() == null)
                .count();
        Assert.assertTrue(rootCount >= 1);
        boolean allRequest = vo.getMappings().stream()
                .allMatch(m -> "request".equals(m.getMappingType()));
        Assert.assertTrue(allRequest);

        System.out.println("result:" + JSON.toJSONString(result));
    }

    private List<HTTPProtocolVO> parse(String json, List<String> endpoints) {
        JSONObject root = JSON.parseObject(json);
        String baseUrl = root.getJSONArray("servers").getJSONObject(0).getString("url");
        JSONObject paths = root.getJSONObject("paths");
        JSONObject schemas = root.getJSONObject("components").getJSONObject("schemas");
        List<HTTPProtocolVO> list = new ArrayList<>();
        for (String endpoint : endpoints) {
            JSONObject pathItem = paths.getJSONObject(endpoint);
            if (pathItem == null) continue;
            String method = detectMethod(pathItem);
            JSONObject operation = pathItem.getJSONObject(method);
            HTTPProtocolVO vo = new HTTPProtocolVO();
            vo.setHttpUrl(baseUrl + endpoint);
            vo.setHttpMethod(method);
            vo.setHttpHeaders(JSON.toJSONString(new HashMap<String, String>() {{
                put("Content-Type", "application/json");
            }}));
            vo.setTimeout(30000);
            List<HTTPProtocolVO.ProtocolMapping> mappings = new ArrayList<>();
            JSONObject requestBody = operation.getJSONObject("requestBody");
            if (requestBody != null) {
                JSONObject content = requestBody.getJSONObject("content");
                JSONObject appJson = content.getJSONObject("application/json");
                if (appJson != null) {
                    JSONObject schema = appJson.getJSONObject("schema");
                    String ref = schema.getString("$ref");
                    if (ref != null) {
                        String refName = ref.substring(ref.lastIndexOf("/") + 1);
                        JSONObject reqSchema = schemas.getJSONObject(refName);
                        String rootName = toLowerCamel(refName);
                        HTTPProtocolVO.ProtocolMapping rm = HTTPProtocolVO.ProtocolMapping.builder()
                                .mappingType("request").parentPath(null).fieldName(rootName).mcpPath(rootName)
                                .mcpType(convertType(reqSchema.getString("type")))
                                .mcpDesc(reqSchema.getString("description")).isRequired(1).sortOrder(1).build();
                        mappings.add(rm);
                        parseProperties(rootName, reqSchema.getJSONObject("properties"), reqSchema.getJSONArray("required"), schemas, mappings);
                    }
                }
            }
            JSONArray parameters = operation.getJSONArray("parameters");
            if (parameters != null) {
                for (int i = 0; i < parameters.size(); i++) {
                    JSONObject param = parameters.getJSONObject(i);
                    String in = param.getString("in");
                    if (!"query".equals(in) && !"path".equals(in)) continue;
                    String name = param.getString("name");
                    boolean required = param.getBooleanValue("required");
                    String description = param.getString("description");
                    JSONObject schema = param.getJSONObject("schema");
                    String type = schema.getString("type");
                    String ref = schema.getString("$ref");
                    if (ref != null) {
                        String refName = ref.substring(ref.lastIndexOf("/") + 1);
                        JSONObject reqSchema = schemas.getJSONObject(refName);
                        if (type == null) type = reqSchema.getString("type");
                        if (description == null) description = reqSchema.getString("description");
                        HTTPProtocolVO.ProtocolMapping rm = HTTPProtocolVO.ProtocolMapping.builder()
                                .mappingType("request").parentPath(null).fieldName(name).mcpPath(name)
                                .mcpType(convertType(type)).mcpDesc(description)
                                .isRequired(required ? 1 : 0).sortOrder(mappings.size() + 1).build();
                        mappings.add(rm);
                        parseProperties(name, reqSchema.getJSONObject("properties"), reqSchema.getJSONArray("required"), schemas, mappings);
                    } else {
                        HTTPProtocolVO.ProtocolMapping m = HTTPProtocolVO.ProtocolMapping.builder()
                                .mappingType("request").parentPath(null).fieldName(name).mcpPath(name)
                                .mcpType(convertType(type)).mcpDesc(description)
                                .isRequired(required ? 1 : 0).sortOrder(mappings.size() + 1).build();
                        mappings.add(m);
                    }
                }
            }
            vo.setMappings(mappings);
            list.add(vo);
        }
        return list;
    }

    private void parseProperties(String parentMcpPath, JSONObject properties, JSONArray requiredList, JSONObject definitions, List<HTTPProtocolVO.ProtocolMapping> mappings) {
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

    private String convertType(String type) {
        if (type == null) return "string";
        return switch (type.toLowerCase()) {
            case "string", "char", "date", "datetime" -> "string";
            case "integer", "int", "long", "double", "float", "number" -> "number";
            case "boolean", "bool" -> "boolean";
            case "array", "list" -> "array";
            default -> "object";
        };
    }

    private String detectMethod(JSONObject pathItem) {
        if (pathItem.containsKey("post")) return "post";
        if (pathItem.containsKey("get")) return "get";
        if (pathItem.containsKey("put")) return "put";
        if (pathItem.containsKey("delete")) return "delete";
        return "post";
    }

    private String toLowerCamel(String name) {
        if (name == null || name.isEmpty()) return name;
        char[] cs = name.toCharArray();
        cs[0] = Character.toLowerCase(cs[0]);
        return new String(cs);
    }
}
