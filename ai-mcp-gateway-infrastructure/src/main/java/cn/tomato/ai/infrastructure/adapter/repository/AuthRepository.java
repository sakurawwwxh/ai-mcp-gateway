package cn.tomato.ai.infrastructure.adapter.repository;

import cn.tomato.ai.domain.auth.adapter.repository.IAuthRepository;
import cn.tomato.ai.domain.auth.model.entity.LicenseCommandEntity;
import cn.tomato.ai.domain.auth.model.valobj.McpGatewayAuthVO;
import cn.tomato.ai.domain.auth.model.valobj.enums.AuthStatusEnum;
import cn.tomato.ai.infrastructure.dao.IMcpGatewayAuthDao;
import cn.tomato.ai.infrastructure.dao.IMcpGatewayDao;
import cn.tomato.ai.infrastructure.dao.po.McpGatewayAuthPO;
import cn.tomato.ai.infrastructure.dao.po.McpGatewayPO;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

/**
 * 鉴权仓储实现
 *
 * @author Wxh
 * @date 2026-06-06 16:50
 */
@Slf4j
@Repository
public class AuthRepository implements IAuthRepository {

    @Resource
    private IMcpGatewayAuthDao mcpGatewayAuthDao;

    @Resource
    private IMcpGatewayDao mcpGatewayDao;

    @Override
    public int queryEffectiveGatewayAuthCount(String gatewayId) {
        return mcpGatewayAuthDao.queryEffectiveGatewayAuthCount(gatewayId);
    }

    @Override
    public McpGatewayAuthVO queryEffectiveGatewayAuthInfo(LicenseCommandEntity commandEntity) {
        McpGatewayAuthPO mcpGatewayAuthPO = mcpGatewayAuthDao.queryEffectiveGatewayAuth(
                commandEntity.getGatewayId(),
                commandEntity.getApiKey()
        );
        if (null == mcpGatewayAuthPO) {
            return null;
        }

        return McpGatewayAuthVO.builder()
                .gatewayId(mcpGatewayAuthPO.getGatewayId())
                .apiKey(mcpGatewayAuthPO.getApiKey())
                .rateLimit(mcpGatewayAuthPO.getRateLimit())
                .expireTime(mcpGatewayAuthPO.getExpireTime())
                .status(AuthStatusEnum.AuthConfig.get(mcpGatewayAuthPO.getStatus()))
                .build();
    }

    @Override
    public AuthStatusEnum.GatewayConfig queryGatewayAuthStatus(String gatewayId) {
        McpGatewayPO mcpGatewayPO = mcpGatewayDao.queryMcpGatewayByGatewayId(gatewayId);
        if (null == mcpGatewayPO) {
            return null;
        }
        return AuthStatusEnum.GatewayConfig.get(mcpGatewayPO.getAuth());
    }

    @Override
    public void insert(McpGatewayAuthVO mcpGatewayAuthVO) {
        McpGatewayAuthPO mcpGatewayAuthPO = new McpGatewayAuthPO();
        mcpGatewayAuthPO.setGatewayId(mcpGatewayAuthVO.getGatewayId());
        mcpGatewayAuthPO.setApiKey(mcpGatewayAuthVO.getApiKey());
        mcpGatewayAuthPO.setRateLimit(mcpGatewayAuthVO.getRateLimit());
        mcpGatewayAuthPO.setExpireTime(mcpGatewayAuthVO.getExpireTime());
        mcpGatewayAuthPO.setStatus(mcpGatewayAuthVO.getStatus().getCode());

        mcpGatewayAuthDao.insert(mcpGatewayAuthPO);
    }
}
