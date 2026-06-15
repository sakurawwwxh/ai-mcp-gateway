package cn.tomato.ai.infrastructure.adapter.repository;

import cn.tomato.ai.domain.admin.adapter.repository.IAdminRepository;
import cn.tomato.ai.domain.admin.model.entity.GatewayConfigEntity;
import cn.tomato.ai.infrastructure.dao.IMcpGatewayAuthDao;
import cn.tomato.ai.infrastructure.dao.IMcpGatewayDao;
import cn.tomato.ai.infrastructure.dao.IMcpGatewayToolDao;
import cn.tomato.ai.infrastructure.dao.IMcpProtocolHttpDao;
import cn.tomato.ai.infrastructure.dao.IMcpProtocolMappingDao;
import cn.tomato.ai.infrastructure.dao.po.McpGatewayPO;
import cn.tomato.ai.types.enums.GatewayEnum;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 管理端仓储实现
 * 注入 5 个 DAO,mcpGatewayDao.queryAll() + stream 拼装
 *
 * @author Wxh
 * @date 2026-06-11
 */
@Slf4j
@Repository
public class AdminRepository implements IAdminRepository {

    @Resource
    private IMcpGatewayDao mcpGatewayDao;

    @Resource
    private IMcpGatewayAuthDao mcpGatewayAuthDao;

    @Resource
    private IMcpGatewayToolDao mcpGatewayToolDao;

    @Resource
    private IMcpProtocolHttpDao mcpProtocolHttpDao;

    @Resource
    private IMcpProtocolMappingDao mcpProtocolMappingDao;

    @Override
    public List<GatewayConfigEntity> queryGatewayConfigList() {
        List<McpGatewayPO> poList = mcpGatewayDao.queryAll();
        return poList.stream()
                .map(this::convert)
                .collect(Collectors.toList());
    }

    private GatewayConfigEntity convert(McpGatewayPO po) {
        return GatewayConfigEntity.builder()
                .gatewayId(po.getGatewayId())
                .name(po.getGatewayName())
                .desc(po.getGatewayDesc())
                .version(po.getVersion())
                .auth(translateAuth(po.getAuth()))
                .status(translateStatus(po.getStatus()))
                .build();
    }

    private GatewayEnum.GatewayAuthStatusEnum translateAuth(Integer code) {
        if (code == null) return null;
        for (GatewayEnum.GatewayAuthStatusEnum val : GatewayEnum.GatewayAuthStatusEnum.values()) {
            if (val.getCode().equals(code)) return val;
        }
        return null;
    }

    private GatewayEnum.GatewayStatus translateStatus(Integer code) {
        if (code == null) return null;
        for (GatewayEnum.GatewayStatus val : GatewayEnum.GatewayStatus.values()) {
            if (val.getCode().equals(code)) return val;
        }
        return null;
    }

}
