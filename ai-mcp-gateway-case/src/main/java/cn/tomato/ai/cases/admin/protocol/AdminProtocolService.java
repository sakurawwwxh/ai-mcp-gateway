package cn.tomato.ai.cases.admin.protocol;

import cn.tomato.ai.cases.admin.IAdminProtocolService;
import cn.tomato.ai.domain.protocol.model.entity.StorageCommandEntity;
import cn.tomato.ai.domain.protocol.service.IProtocolStorage;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * 管理端-协议服务实现
 * 纯委派：StorageCommandEntity → IProtocolStorage
 *
 * @author Wxh
 * @date 2026-06-12
 */
@Slf4j
@Service
public class AdminProtocolService implements IAdminProtocolService {

    @Resource
    private IProtocolStorage protocolStorage;

    @Override
    public void saveGatewayProtocol(StorageCommandEntity commandEntity) {
        log.info("saveGatewayProtocol");
        protocolStorage.doStorage(commandEntity);
    }

}
