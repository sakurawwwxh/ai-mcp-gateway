package cn.tomato.ai.infrastructure.adapter.repository;

import cn.tomato.ai.domain.gateway.adapter.repository.IGatewayRepository;
import cn.tomato.ai.domain.gateway.model.entity.GatewayConfigCommandEntity;
import cn.tomato.ai.domain.gateway.model.entity.GatewayToolConfigCommandEntity;
import cn.tomato.ai.domain.gateway.model.valobj.GatewayConfigVO;
import cn.tomato.ai.domain.gateway.model.valobj.GatewayToolConfigVO;
import cn.tomato.ai.infrastructure.dao.IMcpGatewayDao;
import cn.tomato.ai.infrastructure.dao.IMcpGatewayToolDao;
import cn.tomato.ai.infrastructure.dao.po.McpGatewayPO;
import cn.tomato.ai.infrastructure.dao.po.McpGatewayToolPO;
import cn.tomato.ai.types.enums.GatewayEnum;
import cn.tomato.ai.types.enums.ResponseCode;
import cn.tomato.ai.types.exception.AppException;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

/**
 * 网关仓储服务实现
 *
 * @author Wxh
 * @date 2026-06-11
 */
@Slf4j
@Repository
public class GatewayRepository implements IGatewayRepository {

    @Resource
    private IMcpGatewayDao mcpGatewayDao;

    @Resource
    private IMcpGatewayToolDao mcpGatewayToolDao;

    @Override
    public void saveGatewayConfig(GatewayConfigCommandEntity commandEntity) {
        GatewayConfigVO gatewayConfigVO = commandEntity.getGatewayConfigVO();

        McpGatewayPO mcpGatewayPO = new McpGatewayPO();
        mcpGatewayPO.setGatewayId(gatewayConfigVO.getGatewayId());
        mcpGatewayPO.setGatewayName(gatewayConfigVO.getGatewayName());
        mcpGatewayPO.setGatewayDesc(gatewayConfigVO.getGatewayDesc());
        mcpGatewayPO.setVersion(gatewayConfigVO.getVersion());
        mcpGatewayPO.setAuth(null != gatewayConfigVO.getAuth() ? gatewayConfigVO.getAuth().getCode() : GatewayEnum.GatewayAuthStatusEnum.STRONG_VERIFIED.getCode());
        mcpGatewayPO.setStatus(null != gatewayConfigVO.getStatus() ? gatewayConfigVO.getStatus().getCode() : GatewayEnum.GatewayStatus.ENABLE.getCode());
        mcpGatewayDao.insert(mcpGatewayPO);
    }

    @Override
    public void updateGatewayAuthStatus(GatewayConfigCommandEntity commandEntity) {
        GatewayConfigVO gatewayConfigVO = commandEntity.getGatewayConfigVO();
        if (null == gatewayConfigVO.getAuth()) {
            return;
        }

        McpGatewayPO mcpGatewayPO = new McpGatewayPO();
        mcpGatewayPO.setGatewayId(gatewayConfigVO.getGatewayId());
        mcpGatewayPO.setAuth(null != gatewayConfigVO.getAuth() ? gatewayConfigVO.getAuth().getCode() : null);
        int count = mcpGatewayDao.updateAuthStatusByGatewayId(mcpGatewayPO);
        if (1 != count) {
            throw new AppException(ResponseCode.DB_UPDATE_FAIL.getCode(), ResponseCode.DB_UPDATE_FAIL.getInfo());
        }
    }

    @Override
    public void saveGatewayToolConfig(GatewayToolConfigCommandEntity commandEntity) {
        GatewayToolConfigVO gatewayToolConfigVO = commandEntity.getGatewayToolConfigVO();

        McpGatewayToolPO mcpGatewayToolPO = new McpGatewayToolPO();
        mcpGatewayToolPO.setGatewayId(gatewayToolConfigVO.getGatewayId());
        mcpGatewayToolPO.setToolId(gatewayToolConfigVO.getToolId());
        mcpGatewayToolPO.setToolName(gatewayToolConfigVO.getToolName());
        mcpGatewayToolPO.setToolType(gatewayToolConfigVO.getToolType());
        mcpGatewayToolPO.setToolDescription(gatewayToolConfigVO.getToolDescription());
        mcpGatewayToolPO.setToolVersion(gatewayToolConfigVO.getToolVersion());
        mcpGatewayToolPO.setProtocolId(gatewayToolConfigVO.getProtocolId());
        mcpGatewayToolPO.setProtocolType(gatewayToolConfigVO.getProtocolType());
        mcpGatewayToolDao.insert(mcpGatewayToolPO);
    }

    @Override
    public void updateGatewayToolProtocol(GatewayToolConfigCommandEntity commandEntity) {
        GatewayToolConfigVO gatewayToolConfigVO = commandEntity.getGatewayToolConfigVO();

        McpGatewayToolPO mcpGatewayToolPO = new McpGatewayToolPO();
        mcpGatewayToolPO.setGatewayId(gatewayToolConfigVO.getGatewayId());
        mcpGatewayToolPO.setProtocolId(gatewayToolConfigVO.getProtocolId());
        mcpGatewayToolPO.setProtocolType(gatewayToolConfigVO.getProtocolType());

        int count = mcpGatewayToolDao.updateProtocolByGatewayId(mcpGatewayToolPO);
        if (1 != count) {
            throw new AppException(ResponseCode.DB_UPDATE_FAIL.getCode(), ResponseCode.DB_UPDATE_FAIL.getInfo());
        }
    }

}
