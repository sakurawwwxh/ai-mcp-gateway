package cn.tomato.ai.infrastructure.dao;

import cn.tomato.ai.infrastructure.dao.po.McpGatewayPO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface IMcpGatewayDao {

    int insert(McpGatewayPO po);

    int deleteById(Long id);

    int updateById(McpGatewayPO po);

    int updateAuthStatusByGatewayId(McpGatewayPO po);

    McpGatewayPO queryById(Long id);

    List<McpGatewayPO> queryAll();

    McpGatewayPO queryMcpGatewayByGatewayId(String gatewayId);

    /** 分页统计（gatewayId / gatewayName 模糊匹配） */
    Long queryGatewayListCount(McpGatewayPO query);

    /** 分页查询（gatewayId / gatewayName 模糊匹配） */
    List<McpGatewayPO> queryGatewayList(McpGatewayPO query);
}
