package cn.tomato.ai.test.infrastructure.dao;

import cn.tomato.ai.infrastructure.dao.IMcpProtocolRegistryDao;
import cn.tomato.ai.infrastructure.dao.po.McpProtocolRegistryPO;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

/**
 * 协议注册DAO单元测试
 */
@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest
public class IMcpProtocolRegistryDaoTest {

    @Resource
    private IMcpProtocolRegistryDao mcpProtocolRegistryDao;

    /**
     * 测试插入协议注册配置
     */
    @Test
    public void test_insert() {
        McpProtocolRegistryPO mcpProtocolRegistryPO = new McpProtocolRegistryPO();
        mcpProtocolRegistryPO.setGatewayId("gateway_001");
        mcpProtocolRegistryPO.setToolId(2L);
        mcpProtocolRegistryPO.setToolName("JavaSDKMCPClient_testTool");
        mcpProtocolRegistryPO.setToolType("function");
        mcpProtocolRegistryPO.setToolDescription("测试工具");
        mcpProtocolRegistryPO.setHttpUrl("http://localhost:8701/api/v1/mcp/test");
        mcpProtocolRegistryPO.setHttpMethod("POST");
        mcpProtocolRegistryPO.setHttpHeaders("{\"Content-Type\": \"application/json\"}");
        mcpProtocolRegistryPO.setTimeout(30000);
        mcpProtocolRegistryPO.setRetryTimes(0);
        mcpProtocolRegistryPO.setStatus(1);

        int rows = mcpProtocolRegistryDao.insert(mcpProtocolRegistryPO);
        log.info("插入协议注册配置，影响行数：{}，主键ID：{}", rows, mcpProtocolRegistryPO.getId());
    }

    /**
     * 测试根据ID更新协议注册配置
     */
    @Test
    public void test_updateById() {
        McpProtocolRegistryPO mcpProtocolRegistryPO = new McpProtocolRegistryPO();
        mcpProtocolRegistryPO.setId(1L);
        mcpProtocolRegistryPO.setToolDescription("更新后的工具描述");
        mcpProtocolRegistryPO.setTimeout(60000);

        int rows = mcpProtocolRegistryDao.updateById(mcpProtocolRegistryPO);
        log.info("更新协议注册配置，影响行数：{}", rows);
    }

    /**
     * 测试根据ID删除协议注册配置
     */
    @Test
    public void test_deleteById() {
        Long id = 2L;
        int rows = mcpProtocolRegistryDao.deleteById(id);
        log.info("删除协议注册配置，影响行数：{}", rows);
    }

    /**
     * 测试根据ID查询协议注册配置
     */
    @Test
    public void test_selectById() {
        Long id = 1L;
        McpProtocolRegistryPO mcpProtocolRegistryPO = mcpProtocolRegistryDao.selectById(id);
        log.info("根据ID查询协议注册配置：{}", mcpProtocolRegistryPO);
    }

    /**
     * 测试根据网关ID和工具名称查询协议注册配置
     */
    @Test
    public void test_selectByGatewayIdAndToolName() {
        String gatewayId = "gateway_001";
        String toolName = "JavaSDKMCPClient_getCompanyEmployee";
        McpProtocolRegistryPO mcpProtocolRegistryPO = mcpProtocolRegistryDao.selectByGatewayIdAndToolName(gatewayId, toolName);
        log.info("根据网关ID和工具名称查询协议注册配置：{}", mcpProtocolRegistryPO);
    }

    /**
     * 测试根据网关ID查询协议注册列表
     */
    @Test
    public void test_selectByGatewayId() {
        String gatewayId = "gateway_001";
        List<McpProtocolRegistryPO> list = mcpProtocolRegistryDao.selectByGatewayId(gatewayId);
        log.info("根据网关ID查询协议注册列表，数量：{}", list.size());
        list.forEach(item -> log.info("协议注册配置：{}", item));
    }

    /**
     * 测试查询所有协议注册配置
     */
    @Test
    public void test_selectAll() {
        List<McpProtocolRegistryPO> list = mcpProtocolRegistryDao.selectAll();
        log.info("查询所有协议注册配置，数量：{}", list.size());
        list.forEach(item -> log.info("协议注册配置：{}", item));
    }

    /**
     * 测试根据状态查询协议注册配置
     */
    @Test
    public void test_selectByStatus() {
        Integer status = 1;
        List<McpProtocolRegistryPO> list = mcpProtocolRegistryDao.selectByStatus(status);
        log.info("根据状态查询协议注册配置，数量：{}", list.size());
        list.forEach(item -> log.info("协议注册配置：{}", item));
    }
}
