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

}
