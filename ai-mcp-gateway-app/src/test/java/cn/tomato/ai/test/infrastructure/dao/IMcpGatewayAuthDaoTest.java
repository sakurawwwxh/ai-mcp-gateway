package cn.tomato.ai.test.infrastructure.dao;

import cn.tomato.ai.infrastructure.dao.IMcpGatewayAuthDao;
import cn.tomato.ai.infrastructure.dao.po.McpGatewayAuthPO;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Date;
import java.util.List;

@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest
public class IMcpGatewayAuthDaoTest {

    @Resource
    private IMcpGatewayAuthDao mcpGatewayAuthDao;

    @Test
    public void test_insert() {
        McpGatewayAuthPO mcpGatewayAuthPO = new McpGatewayAuthPO();
        mcpGatewayAuthPO.setGatewayId("gateway_002");
        mcpGatewayAuthPO.setApiKey("TEST_API_KEY_123456");
        mcpGatewayAuthPO.setRateLimit(500);
        mcpGatewayAuthPO.setExpireTime(new Date());
        mcpGatewayAuthPO.setStatus(1);

        int rows = mcpGatewayAuthDao.insert(mcpGatewayAuthPO);
        log.info("插入网关认证配置，影响行数：{}，主键ID：{}", rows, mcpGatewayAuthPO.getId());
    }

    @Test
    public void test_updateById() {
        McpGatewayAuthPO mcpGatewayAuthPO = new McpGatewayAuthPO();
        mcpGatewayAuthPO.setId(1L);
        mcpGatewayAuthPO.setRateLimit(2000);
        mcpGatewayAuthPO.setApiKey("UPDATED_API_KEY_789");

        int rows = mcpGatewayAuthDao.updateById(mcpGatewayAuthPO);
        log.info("更新网关认证配置，影响行数：{}", rows);
    }

    @Test
    public void test_deleteById() {
        Long id = 2L;
        int rows = mcpGatewayAuthDao.deleteById(id);
        log.info("删除网关认证配置，影响行数：{}", rows);
    }

    @Test
    public void test_queryById() {
        Long id = 1L;
        McpGatewayAuthPO mcpGatewayAuthPO = mcpGatewayAuthDao.queryById(id);
        log.info("根据ID查询网关认证配置：{}", mcpGatewayAuthPO);
    }

    @Test
    public void test_queryAll() {
        List<McpGatewayAuthPO> list = mcpGatewayAuthDao.queryAll();
        log.info("查询所有网关认证配置，数量：{}", list.size());
        list.forEach(item -> log.info("网关认证配置：{}", item));
    }
}
