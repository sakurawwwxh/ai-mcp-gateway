package cn.tomato.ai.cases.admin.auth;

import cn.tomato.ai.cases.admin.IAdminAuthService;
import cn.tomato.ai.domain.auth.model.entity.RegisterCommandEntity;
import cn.tomato.ai.domain.auth.service.IAuthRegisterService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * 管理端-鉴权服务实现
 * 纯委派：RegisterCommandEntity → IAuthRegisterService
 *
 * @author Wxh
 * @date 2026-06-12
 */
@Slf4j
@Service
public class AdminAuthService implements IAdminAuthService {

    @Resource
    private IAuthRegisterService authRegisterService;

    @Override
    public String saveGatewayAuth(RegisterCommandEntity commandEntity) {
        log.info("saveGatewayAuth gatewayId={}", commandEntity.getGatewayId());
        return authRegisterService.register(commandEntity);
    }

}
