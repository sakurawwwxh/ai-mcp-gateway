package cn.tomato.ai.test.domain.gateway;

import cn.tomato.ai.domain.gateway.model.entity.GatewayToolConfigCommandEntity;
import cn.tomato.ai.domain.gateway.model.valobj.GatewayToolConfigVO;
import cn.tomato.ai.domain.gateway.service.IGatewayToolConfigService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * 网关工具配置服务测试
 *
 * @author Wxh
 * @date 2026-06-11
 */
@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class GatewayToolConfigServiceTest {

    private static final String GATEWAY_ID = "gateway_test_" + System.currentTimeMillis();
    private static final String TOOL_NAME = "JavaSDKMCPClient_getCompanyEmployee_" + System.currentTimeMillis();

    @Resource
    private IGatewayToolConfigService gatewayToolConfigService;

    @Test
    public void test_saveGatewayToolConfig() {
        GatewayToolConfigCommandEntity commandEntity = new GatewayToolConfigCommandEntity();
        GatewayToolConfigVO gatewayToolConfigVO = GatewayToolConfigVO.builder()
                .gatewayId(GATEWAY_ID)
                .toolId(Long.valueOf(RandomStringUtils.randomNumeric(8)))
                .toolName(TOOL_NAME)
                .toolType("function")
                .toolDescription("获取公司雇员信息")
                .toolVersion("1.0.0")
                .protocolId(83666188L)
                .protocolType("http")
                .build();
        commandEntity.setGatewayToolConfigVO(gatewayToolConfigVO);

        gatewayToolConfigService.saveGatewayToolConfig(commandEntity);
        log.info("保存网关工具配置成功 gatewayId: {} toolId: {}", gatewayToolConfigVO.getGatewayId(), gatewayToolConfigVO.getToolId());
    }

    @Test
    public void test_updateGatewayToolProtocol() {
        GatewayToolConfigCommandEntity commandEntity = GatewayToolConfigCommandEntity.buildUpdateGatewayProtocol(
                GATEWAY_ID,
                2752L,
                83666188L,
                "http");

        gatewayToolConfigService.updateGatewayToolProtocol(commandEntity);
        log.info("更新网关工具协议成功 gatewayId: {} protocolId: {}", commandEntity.getGatewayToolConfigVO().getGatewayId(), commandEntity.getGatewayToolConfigVO().getProtocolId());
    }

}
