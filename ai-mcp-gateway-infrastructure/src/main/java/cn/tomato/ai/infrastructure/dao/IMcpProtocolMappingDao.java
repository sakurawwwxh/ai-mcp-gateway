package cn.tomato.ai.infrastructure.dao;

import cn.tomato.ai.infrastructure.dao.po.McpProtocolMappingPO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 协议映射数据访问接口
 */
@Mapper
public interface IMcpProtocolMappingDao {

    int insert(McpProtocolMappingPO po);

    int deleteById(Long id);

    int updateById(McpProtocolMappingPO po);

    McpProtocolMappingPO queryById(Long id);

    List<McpProtocolMappingPO> queryAll();

    /**
     * 根据协议ID查询映射配置列表
     *
     * @param protocolId 协议ID
     * @return 映射配置列表
     */
    List<McpProtocolMappingPO> queryMcpGatewayToolConfigListByProtocolId(Long protocolId);

    /** 根据协议 ID 集合批量查询映射 */
    List<McpProtocolMappingPO> queryByProtocolIds(java.util.List<Long> protocolIds);

    /** 根据协议 ID 删除映射 */
    int deleteByProtocolId(Long protocolId);
}
