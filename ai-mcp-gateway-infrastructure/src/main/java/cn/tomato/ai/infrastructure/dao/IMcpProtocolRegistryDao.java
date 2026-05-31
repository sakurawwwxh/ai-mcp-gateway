package cn.tomato.ai.infrastructure.dao;

import cn.tomato.ai.infrastructure.dao.po.McpProtocolRegistryPO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 协议注册DAO接口
 * 对应表：mcp_protocol_registry
 */
@Mapper
public interface IMcpProtocolRegistryDao {

    /**
     * 插入协议注册配置
     * @param mcpProtocolRegistryPO 协议注册对象
     * @return 影响行数
     */
    int insert(McpProtocolRegistryPO mcpProtocolRegistryPO);

    /**
     * 根据ID更新协议注册配置
     * @param mcpProtocolRegistryPO 协议注册对象（ID不能为空）
     * @return 影响行数
     */
    int updateById(McpProtocolRegistryPO mcpProtocolRegistryPO);

    /**
     * 根据ID删除协议注册配置
     * @param id 主键ID
     * @return 影响行数
     */
    int deleteById(@Param("id") Long id);

    /**
     * 根据ID查询协议注册配置
     * @param id 主键ID
     * @return 协议注册对象
     */
    McpProtocolRegistryPO selectById(@Param("id") Long id);

    /**
     * 根据网关ID和工具名称查询协议注册配置
     * @param gatewayId 网关ID
     * @param toolName MCP工具名称
     * @return 协议注册对象
     */
    McpProtocolRegistryPO selectByGatewayIdAndToolName(@Param("gatewayId") String gatewayId, @Param("toolName") String toolName);

    /**
     * 根据网关ID查询协议注册列表
     * @param gatewayId 网关ID
     * @return 协议注册列表
     */
    List<McpProtocolRegistryPO> selectByGatewayId(@Param("gatewayId") String gatewayId);

    /**
     * 查询所有协议注册配置
     * @return 协议注册列表
     */
    List<McpProtocolRegistryPO> selectAll();

    /**
     * 根据状态查询协议注册配置
     * @param status 状态：0-禁用，1-启用
     * @return 协议注册列表
     */
    List<McpProtocolRegistryPO> selectByStatus(@Param("status") Integer status);
}
