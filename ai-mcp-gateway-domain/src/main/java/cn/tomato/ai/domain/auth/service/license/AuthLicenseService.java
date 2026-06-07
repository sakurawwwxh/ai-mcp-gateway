package cn.tomato.ai.domain.auth.service.license;

import cn.tomato.ai.domain.auth.adapter.repository.IAuthRepository;
import cn.tomato.ai.domain.auth.model.entity.LicenseCommandEntity;
import cn.tomato.ai.domain.auth.model.valobj.McpGatewayAuthVO;
import cn.tomato.ai.domain.auth.model.valobj.enums.AuthStatusEnum;
import cn.tomato.ai.domain.auth.service.IAuthLicenseService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * License 校验服务
 *
 * @author Wxh
 * @date 2026-06-06 15:53
 */
@Slf4j
@Service
public class AuthLicenseService implements IAuthLicenseService {

    @Resource
    private IAuthRepository repository;

    @Override
    public boolean checkLicense(LicenseCommandEntity commandEntity) {
        // 查询网关是否开启强校验，未开启则直接放行
        AuthStatusEnum.GatewayConfig gatewayAuthStatus = repository.queryGatewayAuthStatus(commandEntity.getGatewayId());
        if (AuthStatusEnum.GatewayConfig.NOT_VERIFIED.equals(gatewayAuthStatus)) {
            return true;
        }

        // 查询网关认证配置
        McpGatewayAuthVO mcpGatewayAuthVO = repository.queryEffectiveGatewayAuthInfo(commandEntity);
        if (null == mcpGatewayAuthVO) {
            return false;
        }

        // 显式禁用的授权直接拒绝
        if (AuthStatusEnum.AuthConfig.DISABLE.equals(mcpGatewayAuthVO.getStatus())) {
            return false;
        }

        // 未设置过期时间时视为永久有效
        Date expireTime = mcpGatewayAuthVO.getExpireTime();
        if (null == expireTime) {
            return true;
        }

        boolean isBefore = new Date().before(expireTime);
        if (!isBefore) {
            log.warn("apiKey 授权已过期 gatewayId:{} apiKey:{}", commandEntity.getGatewayId(), commandEntity.getApiKey());
        }

        return isBefore;
    }
}
