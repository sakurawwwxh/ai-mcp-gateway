package cn.tomato.ai.domain.session.model.valobj;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * MCP (Model Context Protocol) 协议数据模型
 * 参考 MCP 官方 SDK: io.modelcontextprotocol.sdk.mcp.McpSchema
 *
 * 实现 JSON-RPC 2.0 消息格式，用于 AI 客户端与服务器之间的通信
 *
 * @author Wxh
 * @date 2026年05月29日 15:42
 */
@Slf4j
public final class McpSchemaVO {

    /** 最新协议版本号 */
    public static final String LATEST_PROTOCOL_VERSION = "2024-11-05";

    /** JSON-RPC 协议版本 */
    public static final String JSONRPC_VERSION = "2.0";

    /** HashMap 类型引用，用于 JSON 反序列化 */
    private static final TypeReference<HashMap<String, Object>> MAP_TYPE_REF = new TypeReference<>() {
    };

    /** Jackson ObjectMapper 实例 */
    private static final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * 反序列化 JSON-RPC 消息
     * 根据消息内容判断类型：
     * - 有 method + id → JSONRPCRequest（请求）
     * - 有 method 无 id → JSONRPCNotification（通知）
     * - 有 result 或 error → JSONRPCResponse（响应）
     *
     * @param jsonText JSON 字符串
     * @return JSONRPCMessage 实例
     * @throws IOException 解析异常
     */
    public static JSONRPCMessage deserializeJsonRpcMessage(String jsonText)
            throws IOException {

        log.debug("Received JSON message: {}", jsonText);

        var map = objectMapper.readValue(jsonText, MAP_TYPE_REF);

        if (map.containsKey("method") && map.containsKey("id")) {
            return objectMapper.convertValue(map, JSONRPCRequest.class);
        } else if (map.containsKey("method") && !map.containsKey("id")) {
            return objectMapper.convertValue(map, JSONRPCNotification.class);
        } else if (map.containsKey("result") || map.containsKey("error")) {
            return objectMapper.convertValue(map, JSONRPCResponse.class);
        }

        throw new IllegalArgumentException("Cannot deserialize JSONRPCMessage: " + jsonText);
    }

    /**
     * 从 Object 类型转换为指定类型
     *
     * @param data    源数据对象
     * @param typeRef 目标类型引用
     * @return 转换后的对象
     */
    public static  <T> T unmarshalFrom(Object data, TypeReference<T> typeRef) {
        return objectMapper.convertValue(data, typeRef);
    }

    /**
     * JSON-RPC 2.0 消息基础接口
     * 使用 sealed interface 限制实现类
     */
    public sealed interface JSONRPCMessage permits JSONRPCRequest, JSONRPCNotification, JSONRPCResponse {

        /** 获取协议版本号 */
        String jsonrpc();

    }

    /**
     * JSON-RPC 请求对象
     * 客户端发送的请求，包含 method 和 id
     *
     * @param jsonrpc 协议版本 "2.0"
     * @param method  请求方法：initialize、tools/list、tools/call、resources/list
     * @param id      请求唯一标识，用于匹配响应
     * @param params  请求参数
     */
    @JsonInclude(JsonInclude.Include.NON_ABSENT)
    @JsonIgnoreProperties(ignoreUnknown = true)
    public record JSONRPCRequest(@JsonProperty("jsonrpc") String jsonrpc,
                                 @JsonProperty("method") String method,
                                 @JsonProperty("id") Object id,
                                 @JsonProperty("params") Object params
    ) implements JSONRPCMessage {
    }

    /**
     * JSON-RPC 通知对象
     * 客户端发送的通知，有 method 但无 id，不需要响应
     *
     * @param jsonrpc 协议版本 "2.0"
     * @param method  通知方法
     * @param params  通知参数
     */
    @JsonInclude(JsonInclude.Include.NON_ABSENT)
    @JsonIgnoreProperties(ignoreUnknown = true)
    public record JSONRPCNotification(
            @JsonProperty("jsonrpc") String jsonrpc,
            @JsonProperty("method") String method,
            @JsonProperty("params") Object params) implements JSONRPCMessage {
    }

    /**
     * JSON-RPC 响应对象
     * 服务端返回的响应，包含 result 或 error
     *
     * @param jsonrpc 协议版本 "2.0"
     * @param id      对应请求的 id
     * @param result  成功时的响应结果
     * @param error   失败时的错误信息
     */
    @JsonInclude(JsonInclude.Include.NON_ABSENT)
    @JsonIgnoreProperties(ignoreUnknown = true)
    public record JSONRPCResponse(
            @JsonProperty("jsonrpc") String jsonrpc,
            @JsonProperty("id") Object id,
            @JsonProperty("result") Object result,
            @JsonProperty("error") JSONRPCError error
    ) implements JSONRPCMessage {
        /**
         * JSON-RPC 错误对象
         *
         * @param code    错误码
         * @param message 错误描述
         * @param data    附加数据
         */
        @JsonInclude(JsonInclude.Include.NON_ABSENT)
        @JsonIgnoreProperties(ignoreUnknown = true)
        public record JSONRPCError(
                @JsonProperty("code") int code,
                @JsonProperty("message") String message,
                @JsonProperty("data") Object data) {
        }
    }

    /**
     * MCP 请求基础接口
     */
    public sealed interface Request
            permits CallToolRequest, InitializeRequest {

    }

    // ==================== 初始化相关 ====================

    /**
     * 初始化请求
     * 客户端发起连接时发送，包含协议版本和客户端信息
     *
     * @param protocolVersion 客户端支持的协议版本
     * @param capabilities    客户端能力
     * @param clientInfo      客户端信息（名称、版本）
     */
    @JsonInclude(JsonInclude.Include.NON_ABSENT)
    @JsonIgnoreProperties(ignoreUnknown = true)
    public record InitializeRequest( // @formatter:off
                                     @JsonProperty("protocolVersion") String protocolVersion,
                                     @JsonProperty("capabilities") ClientCapabilities capabilities,
                                     @JsonProperty("clientInfo") Implementation clientInfo) implements Request {
    } // @formatter:on

    /**
     * 初始化响应
     * 服务端返回的能力和信息
     *
     * @param protocolVersion 服务端支持的协议版本
     * @param capabilities    服务端能力
     * @param serverInfo      服务端信息（名称、版本）
     * @param instructions    服务端说明
     */
    @JsonInclude(JsonInclude.Include.NON_ABSENT)
    @JsonIgnoreProperties(ignoreUnknown = true)
    public record InitializeResult( // @formatter:off
                                    @JsonProperty("protocolVersion") String protocolVersion,
                                    @JsonProperty("capabilities") ServerCapabilities capabilities,
                                    @JsonProperty("serverInfo") Implementation serverInfo,
                                    @JsonProperty("instructions") String instructions) {
    }

    /**
     * 客户端能力
     * 描述客户端支持的功能
     *
     * @param experimental 实验性功能
     * @param roots        根目录能力
     * @param sampling     采样能力
     */
    @JsonInclude(JsonInclude.Include.NON_ABSENT)
    @JsonIgnoreProperties(ignoreUnknown = true)
    public record ClientCapabilities( // @formatter:off
                                      @JsonProperty("experimental") Map<String, Object> experimental,
                                      @JsonProperty("roots") RootCapabilities roots,
                                      @JsonProperty("sampling") Sampling sampling) {

        /**
         * 根目录能力
         * 定义服务器可操作的文件系统边界
         *
         * @param listChanged 客户端是否会发送根目录变更通知
         */
        @JsonInclude(JsonInclude.Include.NON_ABSENT)
        @JsonIgnoreProperties(ignoreUnknown = true)
        public record RootCapabilities(
                @JsonProperty("listChanged") Boolean listChanged) {
        }

        /**
         * 采样能力
         * 允许服务器请求客户端进行 LLM 采样
         */
        @JsonInclude(JsonInclude.Include.NON_ABSENT)
        public record Sampling() {
        }

        public static Builder builder() {
            return new Builder();
        }

        public static class Builder {
            private Map<String, Object> experimental;
            private RootCapabilities roots;
            private Sampling sampling;

            public Builder experimental(Map<String, Object> experimental) {
                this.experimental = experimental;
                return this;
            }

            public Builder roots(Boolean listChanged) {
                this.roots = new RootCapabilities(listChanged);
                return this;
            }

            public Builder sampling() {
                this.sampling = new Sampling();
                return this;
            }

            public ClientCapabilities build() {
                return new ClientCapabilities(experimental, roots, sampling);
            }
        }
    }// @formatter:on

    /**
     * 服务端能力
     * 描述服务端支持的功能
     *
     * @param completions  补全能力
     * @param experimental 实验性功能
     * @param logging      日志能力
     * @param prompts      提示词能力
     * @param resources    资源能力
     * @param tools        工具能力
     */
    @JsonInclude(JsonInclude.Include.NON_ABSENT)
    @JsonIgnoreProperties(ignoreUnknown = true)
    public record ServerCapabilities( // @formatter:off
                                      @JsonProperty("completions") CompletionCapabilities completions,
                                      @JsonProperty("experimental") Map<String, Object> experimental,
                                      @JsonProperty("logging") LoggingCapabilities logging,
                                      @JsonProperty("prompts") PromptCapabilities prompts,
                                      @JsonProperty("resources") ResourceCapabilities resources,
                                      @JsonProperty("tools") ToolCapabilities tools) {

        /** 补全能力 */
        @JsonInclude(JsonInclude.Include.NON_ABSENT)
        public record CompletionCapabilities() {
        }

        /** 日志能力 */
        @JsonInclude(JsonInclude.Include.NON_ABSENT)
        public record LoggingCapabilities() {
        }

        /**
         * 提示词能力
         * @param listChanged 是否支持提示词列表变更通知
         */
        @JsonInclude(JsonInclude.Include.NON_ABSENT)
        public record PromptCapabilities(
                @JsonProperty("listChanged") Boolean listChanged) {
        }

        /**
         * 资源能力
         * @param subscribe   是否支持资源订阅
         * @param listChanged 是否支持资源列表变更通知
         */
        @JsonInclude(JsonInclude.Include.NON_ABSENT)
        public record ResourceCapabilities(
                @JsonProperty("subscribe") Boolean subscribe,
                @JsonProperty("listChanged") Boolean listChanged) {
        }

        /**
         * 工具能力
         * @param listChanged 是否支持工具列表变更通知
         */
        @JsonInclude(JsonInclude.Include.NON_ABSENT)
        public record ToolCapabilities(
                @JsonProperty("listChanged") Boolean listChanged) {
        }

        public static Builder builder() {
            return new Builder();
        }

        public static class Builder {

            private CompletionCapabilities completions;
            private Map<String, Object> experimental;
            private LoggingCapabilities logging = new LoggingCapabilities();
            private PromptCapabilities prompts;
            private ResourceCapabilities resources;
            private ToolCapabilities tools;

            public Builder completions() {
                this.completions = new CompletionCapabilities();
                return this;
            }

            public Builder experimental(Map<String, Object> experimental) {
                this.experimental = experimental;
                return this;
            }

            public Builder logging() {
                this.logging = new LoggingCapabilities();
                return this;
            }

            public Builder prompts(Boolean listChanged) {
                this.prompts = new PromptCapabilities(listChanged);
                return this;
            }

            public Builder resources(Boolean subscribe, Boolean listChanged) {
                this.resources = new ResourceCapabilities(subscribe, listChanged);
                return this;
            }

            public Builder tools(Boolean listChanged) {
                this.tools = new ToolCapabilities(listChanged);
                return this;
            }

            public ServerCapabilities build() {
                return new ServerCapabilities(completions, experimental, logging, prompts, resources, tools);
            }
        }
    } // @formatter:on

    // ==================== 通用模型 ====================

    /**
     * 实现信息
     * 用于标识客户端或服务端
     *
     * @param name    名称
     * @param version 版本号
     */
    @JsonInclude(JsonInclude.Include.NON_ABSENT)
    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Implementation(// @formatter:off
                                 @JsonProperty("name") String name,
                                 @JsonProperty("version") String version) {
    } // @formatter:on

    // ==================== 工具相关 ====================

    /**
     * 工具列表结果
     * tools/list 接口的响应
     *
     * @param tools      工具列表
     * @param nextCursor 下一页游标（用于分页）
     */
    @JsonInclude(JsonInclude.Include.NON_ABSENT)
    @JsonIgnoreProperties(ignoreUnknown = true)
    public record ListToolsResult( // @formatter:off
                                   @JsonProperty("tools") List<Tool> tools,
                                   @JsonProperty("nextCursor") String nextCursor) {
    }// @formatter:on

    /**
     * 工具定义
     * 描述一个可调用的 MCP 工具
     *
     * @param name        工具名称
     * @param description 工具描述
     * @param inputSchema 输入参数 JSON Schema
     */
    @JsonInclude(JsonInclude.Include.NON_ABSENT)
    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Tool( // @formatter:off
                        @JsonProperty("name") String name,
                        @JsonProperty("description") String description,
                        @JsonProperty("inputSchema") JsonSchema inputSchema) {

        /**
         * 从 JSON 字符串构造 Tool
         *
         * @param name        工具名称
         * @param description 工具描述
         * @param schema      JSON Schema 字符串
         */
        public Tool(String name, String description, String schema) {
            this(name, description, parseSchema(schema));
        }

    } // @formatter:on

    /**
     * JSON Schema 定义
     * 描述工具输入参数的结构
     *
     * @param type                 类型：object、string、number 等
     * @param properties           属性定义
     * @param required             必填属性列表
     * @param additionalProperties 是否允许额外属性
     * @param defs                 定义引用（$defs）
     * @param definitions          定义引用（definitions）
     */
    @JsonInclude(JsonInclude.Include.NON_ABSENT)
    @JsonIgnoreProperties(ignoreUnknown = true)
    public record JsonSchema( // @formatter:off
                              @JsonProperty("type") String type,
                              @JsonProperty("properties") Map<String, Object> properties,
                              @JsonProperty("required") List<String> required,
                              @JsonProperty("additionalProperties") Boolean additionalProperties,
                              @JsonProperty("$defs") Map<String, Object> defs,
                              @JsonProperty("definitions") Map<String, Object> definitions) {
    } // @formatter:on

    /**
     * 解析 JSON Schema 字符串
     *
     * @param schema JSON 字符串
     * @return JsonSchema 对象
     */
    private static JsonSchema parseSchema(String schema) {
        try {
            return objectMapper.readValue(schema, JsonSchema.class);
        }
        catch (IOException e) {
            throw new IllegalArgumentException("Invalid schema: " + schema, e);
        }
    }


    @JsonInclude(JsonInclude.Include.NON_ABSENT)
    @JsonIgnoreProperties(ignoreUnknown = true)
    public record CallToolRequest(// @formatter:off
                                  @JsonProperty("name") String name,
                                  @JsonProperty("arguments") Map<String, Object> arguments) implements Request {

        public CallToolRequest(String name, String jsonArguments) {
            this(name, parseJsonArguments(jsonArguments));
        }

        private static Map<String, Object> parseJsonArguments(String jsonArguments) {
            try {
                return objectMapper.readValue(jsonArguments, MAP_TYPE_REF);
            }
            catch (IOException e) {
                throw new IllegalArgumentException("Invalid arguments: " + jsonArguments, e);
            }
        }
    }// @formatter:off

}
