package cn.tomato.ai.infrastructure.dao;

import cn.tomato.ai.infrastructure.dao.po.McpProtocolHttpPO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * HTTP协议配置数据访问接口
 */
@Mapper
public interface IMcpProtocolHttpDao {

    int insert(McpProtocolHttpPO po);

    int deleteById(Long id);

    int updateById(McpProtocolHttpPO po);

    McpProtocolHttpPO queryById(Long id);

    List<McpProtocolHttpPO> queryAll();

    /**
     * 根据协议ID查询HTTP协议配置
     *
     * @param protocolId 协议ID
     * @return HTTP协议配置
     */
    McpProtocolHttpPO queryMcpProtocolHttpByProtocolId(Long protocolId);

    /** 分页统计（protocolId 精确匹配） */
    Long queryProtocolListCount(McpProtocolHttpPO query);

    /** 分页查询协议 */
    List<McpProtocolHttpPO> queryProtocolList(McpProtocolHttpPO query);

    /** 根据协议 ID 集合查询 */
    List<McpProtocolHttpPO> queryByProtocolIds(java.util.List<Long> protocolIds);

    /** 根据协议 ID 删除协议 */
    int deleteByProtocolId(Long protocolId);

}
