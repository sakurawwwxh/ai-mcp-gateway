package cn.tomato.ai.test.infrastructure.dao;

import cn.tomato.ai.infrastructure.dao.IMcpGatewayDao;
import cn.tomato.ai.infrastructure.dao.po.McpGatewayPO;
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
public class IMcpGatewayDaoTest {

    @Resource
    private IMcpGatewayDao mcpGatewayDao;

    @Test
    public void test_insert() {
        McpGatewayPO mcpGatewayPO = new McpGatewayPO();
        mcpGatewayPO.setGatewayId("gateway_002");
        mcpGatewayPO.setGatewayName("测试网关");
        mcpGatewayPO.setGatewayDesc("用于测试的MCP网关");
        mcpGatewayPO.setStatus(1);

        int rows = mcpGatewayDao.insert(mcpGatewayPO);
        log.info("插入网关配置，影响行数：{}，主键ID：{}", rows, mcpGatewayPO.getId());
    }

    @Test
    public void test_updateById() {
        McpGatewayPO mcpGatewayPO = new McpGatewayPO();
        mcpGatewayPO.setId(1L);
        mcpGatewayPO.setGatewayName("更新后的网关名称");
        mcpGatewayPO.setGatewayDesc("更新后的网关描述");

        int rows = mcpGatewayDao.updateById(mcpGatewayPO);
        log.info("更新网关配置，影响行数：{}", rows);
    }

    @Test
    public void test_deleteById() {
        Long id = 2L;
        int rows = mcpGatewayDao.deleteById(id);
        log.info("删除网关配置，影响行数：{}", rows);
    }

    @Test
    public void test_queryById() {
        Long id = 1L;
        McpGatewayPO mcpGatewayPO = mcpGatewayDao.queryById(id);
        log.info("根据ID查询网关配置：{}", mcpGatewayPO);
    }

    @Test
    public void test_queryAll() {
        List<McpGatewayPO> list = mcpGatewayDao.queryAll();
        log.info("查询所有网关配置，数量：{}", list.size());
        list.forEach(item -> log.info("网关配置：{}", item));
    }

    @Test
    public void test_queryMcpGatewayByGatewayId() {
        String gatewayId = "gateway_001";
        McpGatewayPO mcpGatewayPO = mcpGatewayDao.queryMcpGatewayByGatewayId(gatewayId);
        log.info("根据网关ID查询网关配置：{}", mcpGatewayPO);
    }
}
