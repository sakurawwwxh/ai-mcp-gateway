package cn.tomato.ai.infrastructure.dao;

import cn.tomato.ai.infrastructure.dao.po.McpGatewayPO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 网关配置DAO接口
 * 对应表：mcp_gateway
 */
@Mapper
public interface IMcpGatewayDao {

    /**
     * 插入网关配置
     * @param mcpGatewayPO 网关配置对象
     * @return 影响行数
     */
    int insert(McpGatewayPO mcpGatewayPO);

    /**
     * 根据ID更新网关配置
     * @param mcpGatewayPO 网关配置对象（ID不能为空）
     * @return 影响行数
     */
    int updateById(McpGatewayPO mcpGatewayPO);

    /**
     * 根据ID删除网关配置
     * @param id 主键ID
     * @return 影响行数
     */
    int deleteById(@Param("id") Long id);

    /**
     * 根据ID查询网关配置
     * @param id 主键ID
     * @return 网关配置对象
     */
    McpGatewayPO selectById(@Param("id") Long id);

    /**
     * 根据网关唯一标识查询网关配置
     * @param gatewayId 网关唯一标识
     * @return 网关配置对象
     */
    McpGatewayPO selectByGatewayId(@Param("gatewayId") String gatewayId);

    /**
     * 查询所有网关配置
     * @return 网关配置列表
     */
    List<McpGatewayPO> selectAll();

    /**
     * 根据状态查询网关配置
     * @param status 状态：0-禁用，1-启用
     * @return 网关配置列表
     */
    List<McpGatewayPO> selectByStatus(@Param("status") Integer status);
}
