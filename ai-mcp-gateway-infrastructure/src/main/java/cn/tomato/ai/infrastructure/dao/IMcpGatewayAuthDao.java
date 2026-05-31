package cn.tomato.ai.infrastructure.dao;

import cn.tomato.ai.infrastructure.dao.po.McpGatewayAuthPO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 网关认证DAO接口
 * 对应表：mcp_gateway_auth
 */
@Mapper
public interface IMcpGatewayAuthDao {

    /**
     * 插入网关认证配置
     * @param mcpGatewayAuthPO 网关认证对象
     * @return 影响行数
     */
    int insert(McpGatewayAuthPO mcpGatewayAuthPO);

    /**
     * 根据ID更新网关认证配置
     * @param mcpGatewayAuthPO 网关认证对象（ID不能为空）
     * @return 影响行数
     */
    int updateById(McpGatewayAuthPO mcpGatewayAuthPO);

    /**
     * 根据ID删除网关认证配置
     * @param id 主键ID
     * @return 影响行数
     */
    int deleteById(@Param("id") Long id);

    /**
     * 根据ID查询网关认证配置
     * @param id 主键ID
     * @return 网关认证对象
     */
    McpGatewayAuthPO selectById(@Param("id") Long id);

    /**
     * 根据网关ID查询认证配置
     * @param gatewayId 网关ID
     * @return 网关认证对象
     */
    McpGatewayAuthPO selectByGatewayId(@Param("gatewayId") String gatewayId);

    /**
     * 根据API密钥查询认证配置
     * @param apiKey API密钥
     * @return 网关认证对象
     */
    McpGatewayAuthPO selectByApiKey(@Param("apiKey") String apiKey);

    /**
     * 查询所有网关认证配置
     * @return 网关认证列表
     */
    List<McpGatewayAuthPO> selectAll();

    /**
     * 根据状态查询网关认证配置
     * @param status 状态：0-禁用，1-启用
     * @return 网关认证列表
     */
    List<McpGatewayAuthPO> selectByStatus(@Param("status") Integer status);
}
