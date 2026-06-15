package cn.tomato.ai.test.domain.gateway;

import cn.tomato.ai.domain.gateway.model.entity.GatewayConfigCommandEntity;
import cn.tomato.ai.domain.gateway.model.valobj.GatewayConfigVO;
import cn.tomato.ai.domain.gateway.service.IGatewayConfigService;
import cn.tomato.ai.types.enums.GatewayEnum;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * 网关配置服务测试
 *
 * @author Wxh
 * @date 2026-06-11
 */
@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class GatewayConfigServiceTest {

    private static final String GATEWAY_ID = "gateway_test_" + System.currentTimeMillis();

    @Resource
    private IGatewayConfigService gatewayConfigService;

    @Test
    public void test_saveGatewayConfig() {
        GatewayConfigCommandEntity commandEntity = new GatewayConfigCommandEntity();
        GatewayConfigVO gatewayConfigVO = GatewayConfigVO.builder()
                .gatewayId(GATEWAY_ID)
                .gatewayName("员工信息查询网关")
                .gatewayDesc("用于查询公司员工信息的MCP网关")
                .version("1.0.0")
                .auth(GatewayEnum.GatewayAuthStatusEnum.STRONG_VERIFIED)
                .status(GatewayEnum.GatewayStatus.ENABLE)
                .build();
        commandEntity.setGatewayConfigVO(gatewayConfigVO);

        gatewayConfigService.saveGatewayConfig(commandEntity);
        log.info("保存网关配置成功 gatewayId: {}", gatewayConfigVO.getGatewayId());
    }

    @Test
    public void test_updateGatewayAuthStatus() {
        GatewayConfigCommandEntity commandEntity = GatewayConfigCommandEntity.buildUpdateGatewayAuthStatusVO(GATEWAY_ID, GatewayEnum.GatewayAuthStatusEnum.NOT_VERIFIED);
        gatewayConfigService.updateGatewayAuthStatus(commandEntity);
        log.info("更新网关鉴权状态成功 gatewayId: {}", commandEntity.getGatewayConfigVO().getGatewayId());
    }

}
