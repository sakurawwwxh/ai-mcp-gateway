package cn.tomato.ai.api.dto;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

/**
 * 管理端请求外壳 DTO
 * 包含 4 个静态内部类,对应 4 个子业务
 *
 * @author Wxh
 * @date 2026-06-11
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GatewayConfigRequestDTO {

    private GatewayConfig gatewayConfig;
    private GatewayToolConfig gatewayToolConfig;
    private GatewayProtocol gatewayProtocol;
    private GatewayAuth gatewayAuth;
    /** Swagger 协议导入请求 */
    private GatewayProtocolImport gatewayProtocolImport;

    /**
     * 1) /admin/save_gateway_config 入参
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class GatewayConfig {
        private String gatewayId;
        private String name;
        private String desc;
        private String version;
        /** 鉴权状态:0-不校验,1-强校验 */
        private Integer auth;
        /** 网关状态:0-禁用,1-启用 */
        private Integer status;
    }

    /**
     * 2) /admin/save_gateway_tool_config 入参
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class GatewayToolConfig {
        private String gatewayId;
        private Long toolId;
        private String toolName;
        private String toolType;
        private String toolDescription;
        private String toolVersion;
        private Long protocolId;
        private String protocolType;
    }

    /**
     * 3) /admin/save_gateway_protocol 入参
     * 内嵌 HTTP 协议主体 + 映射规则列表
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class GatewayProtocol {
        private String gatewayId;
        private HTTPProtocol http;
        private List<ProtocolMapping> mapping;
    }

    /**
     * 3.1) HTTP 协议主体
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class HTTPProtocol {
        private String httpUrl;
        private String httpHeaders;
        private String httpMethod;
        private Integer timeout;
        /** 重试次数 0-10 */
        private Integer retryTimes;
        /** 启用状态 0-禁用 / 1-启用 */
        private Integer status;
    }

    /**
     * 3.2) 协议映射规则
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ProtocolMapping {
        private String mappingType;
        private String parentPath;
        private String fieldName;
        private String mcpPath;
        private String mcpType;
        private String mcpDesc;
        private Integer isRequired;
        private Integer sortOrder;
    }

    /**
     * 4) /admin/save_gateway_auth 入参
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class GatewayAuth {
        private String gatewayId;
        private String apiKey;
        private Integer rateLimit;
        /**
         * 过期时间；前端传 ISO-8601 (yyyy-MM-dd'T'HH:mm:ss) 或 yyyy-MM-dd
         */
        @JSONField(format = "yyyy-MM-dd'T'HH:mm:ss")
        private Date expireTime;
    }

    /**
     * 5) /admin/analysis_protocol + /admin/import_gateway_protocol 入参
     * 粘贴 Swagger / OpenAPI JSON，导入时勾选 endpoints
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class GatewayProtocolImport {
        /** 归属网关 ID（导入时注入到每条 VO） */
        private String gatewayId;
        /** Swagger / OpenAPI JSON 原文 */
        private String openApiJson;
        /** 选中的接口路径列表（解析后用户勾选，analysis 时可为 null） */
        private List<String> endpoints;
    }

}
