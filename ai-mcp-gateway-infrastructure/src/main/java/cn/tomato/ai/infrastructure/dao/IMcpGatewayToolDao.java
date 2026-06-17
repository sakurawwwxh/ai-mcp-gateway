package cn.tomato.ai.infrastructure.dao;

import cn.tomato.ai.infrastructure.dao.po.McpGatewayToolPO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 网关工具数据访问接口
 */
@Mapper
public interface IMcpGatewayToolDao {

    /**
     * 查询网关下的有效工具列表
     *
     * @param gatewayId 网关ID
     * @return 工具列表
     */
    List<McpGatewayToolPO> queryEffectiveTools(String gatewayId);

    /**
     * 根据网关ID和工具名称查询协议ID
     *
     * @param mcpGatewayToolPOReq 查询条件（包含gatewayId和toolName）
     * @return 协议ID
     */
    Long queryToolProtocolIdByToolName(McpGatewayToolPO mcpGatewayToolPOReq);

    /**
     * 插入网关工具配置
     *
     * @param po 网关工具配置持久化对象
     * @return 影响行数
     */
    int insert(McpGatewayToolPO po);

    /**
     * 根据网关ID更新协议ID与协议类型
     *
     * @param po 网关工具配置持久化对象（至少包含gatewayId、protocolId、protocolType）
     * @return 影响行数
     */
    int updateProtocolByGatewayId(McpGatewayToolPO po);

    /** 查询所有工具 */
    List<McpGatewayToolPO> queryAll();

    /** 分页统计（gatewayId 精确 + toolName 模糊） */
    Long queryToolListCount(McpGatewayToolPO query);

    /** 分页查询工具 */
    List<McpGatewayToolPO> queryToolList(McpGatewayToolPO query);

    /** 根据网关 ID 查询工具列表 */
    List<McpGatewayToolPO> queryByGatewayId(String gatewayId);

    /** 根据网关 ID + 工具 ID 删除 */
    int deleteByGatewayIdAndToolId(@org.apache.ibatis.annotations.Param("gatewayId") String gatewayId,
                                   @org.apache.ibatis.annotations.Param("toolId") Long toolId);

}
