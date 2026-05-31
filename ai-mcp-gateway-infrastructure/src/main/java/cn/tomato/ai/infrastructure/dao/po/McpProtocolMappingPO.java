package cn.tomato.ai.infrastructure.dao.po;

import java.util.Date;

/**
 * 协议映射持久化对象
 * 对应表：mcp_protocol_mapping
 */
public class McpProtocolMappingPO {
    /**
     * 主键ID
     */
    private Long id;

    /**
     * 所属网关ID
     */
    private String gatewayId;

    /**
     * 所属工具ID
     */
    private Long toolId;

    /**
     * 映射类型：request-请求参数映射，response-响应数据映射
     */
    private String mappingType;

    /**
     * 父级路径（如：xxxRequest01，用于构建嵌套结构，根节点为NULL）
     */
    private String parentPath;

    /**
     * 字段名称（如：city、company、name）
     */
    private String fieldName;

    /**
     * MCP完整路径（如：xxxRequest01.city、xxxRequest01.company.name）
     */
    private String mcpPath;

    /**
     * MCP数据类型：string/number/boolean/object/array
     */
    private String mcpType;

    /**
     * MCP字段描述
     */
    private String mcpDesc;

    /**
     * 是否必填：0-否，1-是（用于生成required数组）
     */
    private Integer isRequired;

    /**
     * HTTP路径（JSON路径，如：company.name 或 data.result，object类型可为空）
     */
    private String httpPath;

    /**
     * HTTP位置：body/query/path/header（仅对request类型有效）
     */
    private String httpLocation;

    /**
     * 排序顺序（同级字段排序）
     */
    private Integer sortOrder;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getGatewayId() {
        return gatewayId;
    }

    public void setGatewayId(String gatewayId) {
        this.gatewayId = gatewayId;
    }

    public Long getToolId() {
        return toolId;
    }

    public void setToolId(Long toolId) {
        this.toolId = toolId;
    }

    public String getMappingType() {
        return mappingType;
    }

    public void setMappingType(String mappingType) {
        this.mappingType = mappingType;
    }

    public String getParentPath() {
        return parentPath;
    }

    public void setParentPath(String parentPath) {
        this.parentPath = parentPath;
    }

    public String getFieldName() {
        return fieldName;
    }

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    public String getMcpPath() {
        return mcpPath;
    }

    public void setMcpPath(String mcpPath) {
        this.mcpPath = mcpPath;
    }

    public String getMcpType() {
        return mcpType;
    }

    public void setMcpType(String mcpType) {
        this.mcpType = mcpType;
    }

    public String getMcpDesc() {
        return mcpDesc;
    }

    public void setMcpDesc(String mcpDesc) {
        this.mcpDesc = mcpDesc;
    }

    public Integer getIsRequired() {
        return isRequired;
    }

    public void setIsRequired(Integer isRequired) {
        this.isRequired = isRequired;
    }

    public String getHttpPath() {
        return httpPath;
    }

    public void setHttpPath(String httpPath) {
        this.httpPath = httpPath;
    }

    public String getHttpLocation() {
        return httpLocation;
    }

    public void setHttpLocation(String httpLocation) {
        this.httpLocation = httpLocation;
    }

    public Integer getSortOrder() {
        return sortOrder;
    }

    public void setSortOrder(Integer sortOrder) {
        this.sortOrder = sortOrder;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }
}
