package cn.tomato.ai.test.infrastructure.dao;

import cn.tomato.ai.infrastructure.dao.IMcpProtocolMappingDao;
import cn.tomato.ai.infrastructure.dao.po.McpProtocolMappingPO;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

/**
 * 协议映射DAO单元测试
 */
@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest
public class IMcpProtocolMappingDaoTest {

    @Resource
    private IMcpProtocolMappingDao mcpProtocolMappingDao;

    /**
     * 测试插入协议映射配置
     */
    @Test
    public void test_insert() {
        McpProtocolMappingPO mcpProtocolMappingPO = new McpProtocolMappingPO();
        mcpProtocolMappingPO.setGatewayId("gateway_001");
        mcpProtocolMappingPO.setToolId(1L);
        mcpProtocolMappingPO.setMappingType("request");
        mcpProtocolMappingPO.setFieldName("testField");
        mcpProtocolMappingPO.setMcpPath("testRequest.testField");
        mcpProtocolMappingPO.setMcpType("string");
        mcpProtocolMappingPO.setMcpDesc("测试字段");
        mcpProtocolMappingPO.setIsRequired(1);
        mcpProtocolMappingPO.setHttpPath("testField");
        mcpProtocolMappingPO.setHttpLocation("body");
        mcpProtocolMappingPO.setSortOrder(1);

        int rows = mcpProtocolMappingDao.insert(mcpProtocolMappingPO);
        log.info("插入协议映射配置，影响行数：{}，主键ID：{}", rows, mcpProtocolMappingPO.getId());
    }

    /**
     * 测试根据ID更新协议映射配置
     */
    @Test
    public void test_updateById() {
        McpProtocolMappingPO mcpProtocolMappingPO = new McpProtocolMappingPO();
        mcpProtocolMappingPO.setId(1L);
        mcpProtocolMappingPO.setMcpDesc("更新后的字段描述");

        int rows = mcpProtocolMappingDao.updateById(mcpProtocolMappingPO);
        log.info("更新协议映射配置，影响行数：{}", rows);
    }

    /**
     * 测试根据ID删除协议映射配置
     */
    @Test
    public void test_deleteById() {
        Long id = 8L;
        int rows = mcpProtocolMappingDao.deleteById(id);
        log.info("删除协议映射配置，影响行数：{}", rows);
    }

    /**
     * 测试根据ID查询协议映射配置
     */
    @Test
    public void test_selectById() {
        Long id = 1L;
        McpProtocolMappingPO mcpProtocolMappingPO = mcpProtocolMappingDao.selectById(id);
        log.info("根据ID查询协议映射配置：{}", mcpProtocolMappingPO);
    }

    /**
     * 测试根据工具ID查询协议映射列表
     */
    @Test
    public void test_selectByToolId() {
        Long toolId = 1L;
        List<McpProtocolMappingPO> list = mcpProtocolMappingDao.selectByToolId(toolId);
        log.info("根据工具ID查询协议映射列表，数量：{}", list.size());
        list.forEach(item -> log.info("协议映射配置：{}", item));
    }

    /**
     * 测试根据工具ID和映射类型查询协议映射列表
     */
    @Test
    public void test_selectByToolIdAndMappingType() {
        Long toolId = 1L;
        String mappingType = "request";
        List<McpProtocolMappingPO> list = mcpProtocolMappingDao.selectByToolIdAndMappingType(toolId, mappingType);
        log.info("根据工具ID和映射类型查询协议映射列表，数量：{}", list.size());
        list.forEach(item -> log.info("协议映射配置：{}", item));
    }

    /**
     * 测试根据网关ID查询协议映射列表
     */
    @Test
    public void test_selectByGatewayId() {
        String gatewayId = "gateway_001";
        List<McpProtocolMappingPO> list = mcpProtocolMappingDao.selectByGatewayId(gatewayId);
        log.info("根据网关ID查询协议映射列表，数量：{}", list.size());
        list.forEach(item -> log.info("协议映射配置：{}", item));
    }

    /**
     * 测试查询所有协议映射配置
     */
    @Test
    public void test_selectAll() {
        List<McpProtocolMappingPO> list = mcpProtocolMappingDao.selectAll();
        log.info("查询所有协议映射配置，数量：{}", list.size());
        list.forEach(item -> log.info("协议映射配置：{}", item));
    }

    /**
     * 测试根据工具ID删除所有协议映射配置
     */
    @Test
    public void test_deleteByToolId() {
        Long toolId = 2L;
        int rows = mcpProtocolMappingDao.deleteByToolId(toolId);
        log.info("根据工具ID删除协议映射配置，影响行数：{}", rows);
    }
}
