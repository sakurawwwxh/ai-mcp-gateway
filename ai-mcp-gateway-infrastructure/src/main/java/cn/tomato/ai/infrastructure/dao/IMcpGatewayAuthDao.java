package cn.tomato.ai.infrastructure.dao;

import cn.tomato.ai.infrastructure.dao.po.McpGatewayAuthPO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface IMcpGatewayAuthDao {

    int insert(McpGatewayAuthPO po);

    int deleteById(Long id);

    int updateById(McpGatewayAuthPO po);

    McpGatewayAuthPO queryById(Long id);

    McpGatewayAuthPO queryEffectiveGatewayAuth(@Param("gatewayId") String gatewayId, @Param("apiKey") String apiKey);

    int queryEffectiveGatewayAuthCount(String gatewayId);

    List<McpGatewayAuthPO> queryAll();
}
