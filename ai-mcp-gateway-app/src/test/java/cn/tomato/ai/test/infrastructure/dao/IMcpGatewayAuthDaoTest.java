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

/**
 * 网关认证DAO单元测试
 */
@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest
public class IMcpGatewayAuthDaoTest {

    @Resource
    private IMcpGatewayAuthDao mcpGatewayAuthDao;

    /**
     * 测试插入网关认证配置
     */
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

    /**
     * 测试根据ID更新网关认证配置
     */
    @Test
    public void test_updateById() {
        McpGatewayAuthPO mcpGatewayAuthPO = new McpGatewayAuthPO();
        mcpGatewayAuthPO.setId(1L);
        mcpGatewayAuthPO.setRateLimit(2000);
        mcpGatewayAuthPO.setApiKey("UPDATED_API_KEY_789");

        int rows = mcpGatewayAuthDao.updateById(mcpGatewayAuthPO);
        log.info("更新网关认证配置，影响行数：{}", rows);
    }

    /**
     * 测试根据ID删除网关认证配置
     */
    @Test
    public void test_deleteById() {
        Long id = 2L;
        int rows = mcpGatewayAuthDao.deleteById(id);
        log.info("删除网关认证配置，影响行数：{}", rows);
    }

    /**
     * 测试根据ID查询网关认证配置
     */
    @Test
    public void test_selectById() {
        Long id = 1L;
        McpGatewayAuthPO mcpGatewayAuthPO = mcpGatewayAuthDao.selectById(id);
        log.info("根据ID查询网关认证配置：{}", mcpGatewayAuthPO);
    }

    /**
     * 测试根据网关ID查询认证配置
     */
    @Test
    public void test_selectByGatewayId() {
        String gatewayId = "gateway_001";
        McpGatewayAuthPO mcpGatewayAuthPO = mcpGatewayAuthDao.selectByGatewayId(gatewayId);
        log.info("根据网关ID查询认证配置：{}", mcpGatewayAuthPO);
    }

    /**
     * 测试根据API密钥查询认证配置
     */
    @Test
    public void test_selectByApiKey() {
        String apiKey = "RS590LKPOD8877DDLMFKS4";
        McpGatewayAuthPO mcpGatewayAuthPO = mcpGatewayAuthDao.selectByApiKey(apiKey);
        log.info("根据API密钥查询认证配置：{}", mcpGatewayAuthPO);
    }

    /**
     * 测试查询所有网关认证配置
     */
    @Test
    public void test_selectAll() {
        List<McpGatewayAuthPO> list = mcpGatewayAuthDao.selectAll();
        log.info("查询所有网关认证配置，数量：{}", list.size());
        list.forEach(item -> log.info("网关认证配置：{}", item));
    }

    /**
     * 测试根据状态查询网关认证配置
     */
    @Test
    public void test_selectByStatus() {
        Integer status = 1;
        List<McpGatewayAuthPO> list = mcpGatewayAuthDao.selectByStatus(status);
        log.info("根据状态查询网关认证配置，数量：{}", list.size());
        list.forEach(item -> log.info("网关认证配置：{}", item));
    }
}
