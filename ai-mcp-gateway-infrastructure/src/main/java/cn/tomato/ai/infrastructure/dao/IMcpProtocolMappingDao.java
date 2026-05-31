package cn.tomato.ai.infrastructure.dao;

import cn.tomato.ai.infrastructure.dao.po.McpProtocolMappingPO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 协议映射DAO接口
 * 对应表：mcp_protocol_mapping
 */
@Mapper
public interface IMcpProtocolMappingDao {

    /**
     * 插入协议映射配置
     * @param mcpProtocolMappingPO 协议映射对象
     * @return 影响行数
     */
    int insert(McpProtocolMappingPO mcpProtocolMappingPO);

    /**
     * 根据ID更新协议映射配置
     * @param mcpProtocolMappingPO 协议映射对象（ID不能为空）
     * @return 影响行数
     */
    int updateById(McpProtocolMappingPO mcpProtocolMappingPO);

    /**
     * 根据ID删除协议映射配置
     * @param id 主键ID
     * @return 影响行数
     */
    int deleteById(@Param("id") Long id);

    /**
     * 根据ID查询协议映射配置
     * @param id 主键ID
     * @return 协议映射对象
     */
    McpProtocolMappingPO selectById(@Param("id") Long id);

    /**
     * 根据工具ID查询协议映射列表
     * @param toolId 工具ID
     * @return 协议映射列表（按sort_order排序）
     */
    List<McpProtocolMappingPO> selectByToolId(@Param("toolId") Long toolId);

    /**
     * 根据工具ID和映射类型查询协议映射列表
     * @param toolId 工具ID
     * @param mappingType 映射类型：request-请求参数映射，response-响应数据映射
     * @return 协议映射列表（按sort_order排序）
     */
    List<McpProtocolMappingPO> selectByToolIdAndMappingType(@Param("toolId") Long toolId, @Param("mappingType") String mappingType);

    /**
     * 根据网关ID查询协议映射列表
     * @param gatewayId 网关ID
     * @return 协议映射列表（按tool_id和sort_order排序）
     */
    List<McpProtocolMappingPO> selectByGatewayId(@Param("gatewayId") String gatewayId);

    /**
     * 查询所有协议映射配置
     * @return 协议映射列表（按tool_id和sort_order排序）
     */
    List<McpProtocolMappingPO> selectAll();

    /**
     * 根据工具ID删除所有协议映射配置
     * @param toolId 工具ID
     * @return 影响行数
     */
    int deleteByToolId(@Param("toolId") Long toolId);
}
