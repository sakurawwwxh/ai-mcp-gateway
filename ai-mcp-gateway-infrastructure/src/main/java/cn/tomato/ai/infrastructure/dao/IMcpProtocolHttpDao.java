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

}
