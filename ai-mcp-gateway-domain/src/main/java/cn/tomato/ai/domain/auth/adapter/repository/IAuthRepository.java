package cn.tomato.ai.domain.auth.adapter.repository;


import cn.tomato.ai.domain.auth.model.entity.LicenseCommandEntity;
import cn.tomato.ai.domain.auth.model.valobj.McpGatewayAuthVO;
import cn.tomato.ai.domain.auth.model.valobj.enums.AuthStatusEnum;

public interface IAuthRepository {

    int queryEffectiveGatewayAuthCount(String gatewayId);

    McpGatewayAuthVO queryEffectiveGatewayAuthInfo(LicenseCommandEntity commandEntity);

    AuthStatusEnum.GatewayConfig queryGatewayAuthStatus(String gatewayId);

    void insert(McpGatewayAuthVO mcpGatewayAuthVO);
}
