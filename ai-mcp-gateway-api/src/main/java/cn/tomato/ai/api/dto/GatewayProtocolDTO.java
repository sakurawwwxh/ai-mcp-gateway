package cn.tomato.ai.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 网关协议配置 DTO
 * 内嵌 HTTP 主体 + 映射规则列表
 *
 * @author Wxh
 * @date 2026-06-15
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GatewayProtocolDTO {

    /** 协议 ID */
    private Long protocolId;

    /** HTTP 接口地址 */
    private String httpUrl;

    /** HTTP 方法 */
    private String httpMethod;

    /** HTTP 请求头（JSON 字符串） */
    private String httpHeaders;

    /** 超时（毫秒） */
    private Integer timeout;

    /** 重试次数 0-10 */
    private Integer retryTimes;

    /** 启用状态 0-禁用 / 1-启用 */
    private Integer status;

    /** 协议映射列表 */
    private List<ProtocolMappingDTO> mappings;

    /**
     * 协议映射 DTO
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ProtocolMappingDTO {
        /** 映射类型 */
        private String mappingType;
        /** 父级路径 */
        private String parentPath;
        /** 外部字段名 */
        private String fieldName;
        /** MCP 路径 */
        private String mcpPath;
        /** MCP 类型 */
        private String mcpType;
        /** MCP 描述 */
        private String mcpDesc;
        /** 是否必填 0/1 */
        private Integer isRequired;
        /** 排序顺序 */
        private Integer sortOrder;
    }

}