package cn.tomato.ai.test.infrastructure.dao;

import cn.tomato.ai.infrastructure.dao.IMcpProtocolHttpDao;
import cn.tomato.ai.infrastructure.dao.po.McpProtocolHttpPO;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest
public class IMcpProtocolRegistryDaoTest {

    @Resource
    private IMcpProtocolHttpDao mcpProtocolHttpDao;

    @Test
    public void test_insert() {
        McpProtocolHttpPO po = new McpProtocolHttpPO();
        po.setProtocolId(1L);
        po.setHttpUrl("http://localhost:8701/api/v1/mcp/test");
        po.setHttpMethod("POST");
        po.setHttpHeaders("{\"Content-Type\": \"application/json\"}");
        po.setTimeout(30000);
        po.setRetryTimes(0);
        po.setStatus(1);

        int rows = mcpProtocolHttpDao.insert(po);
        log.info("插入HTTP协议配置，影响行数：{}，主键ID：{}", rows, po.getId());
    }

    @Test
    public void test_updateById() {
        McpProtocolHttpPO po = new McpProtocolHttpPO();
        po.setId(1L);
        po.setTimeout(60000);

        int rows = mcpProtocolHttpDao.updateById(po);
        log.info("更新HTTP协议配置，影响行数：{}", rows);
    }

    @Test
    public void test_deleteById() {
        Long id = 2L;
        int rows = mcpProtocolHttpDao.deleteById(id);
        log.info("删除HTTP协议配置，影响行数：{}", rows);
    }

    @Test
    public void test_queryById() {
        Long id = 1L;
        McpProtocolHttpPO po = mcpProtocolHttpDao.queryById(id);
        log.info("根据ID查询HTTP协议配置：{}", po);
    }

    @Test
    public void test_queryAll() {
        List<McpProtocolHttpPO> list = mcpProtocolHttpDao.queryAll();
        log.info("查询所有HTTP协议配置，数量：{}", list.size());
        list.forEach(item -> log.info("HTTP协议配置：{}", item));
    }

    @Test
    public void test_queryMcpProtocolHttpByProtocolId() {
        Long protocolId = 1L;
        McpProtocolHttpPO po = mcpProtocolHttpDao.queryMcpProtocolHttpByProtocolId(protocolId);
        log.info("根据协议ID查询HTTP协议配置：{}", po);
    }
}
