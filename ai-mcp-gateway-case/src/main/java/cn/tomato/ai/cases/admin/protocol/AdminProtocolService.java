package cn.tomato.ai.cases.admin.protocol;

import cn.tomato.ai.cases.admin.IAdminProtocolService;
import cn.tomato.ai.domain.protocol.model.entity.AnalysisCommandEntity;
import cn.tomato.ai.domain.protocol.model.entity.StorageCommandEntity;
import cn.tomato.ai.domain.protocol.model.valobj.http.HTTPProtocolVO;
import cn.tomato.ai.domain.protocol.service.IProtocolAnalysis;
import cn.tomato.ai.domain.protocol.service.IProtocolStorage;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 管理端-协议服务实现
 * 纯委派：StorageCommandEntity → IProtocolStorage；AnalysisCommandEntity → IProtocolAnalysis
 *
 * @author Wxh
 * @date 2026-06-12
 */
@Slf4j
@Service
public class AdminProtocolService implements IAdminProtocolService {

    @Resource
    private IProtocolStorage protocolStorage;

    @Resource
    private IProtocolAnalysis protocolAnalysis;

    @Override
    public void saveGatewayProtocol(StorageCommandEntity commandEntity) {
        log.info("saveGatewayProtocol count:{}",
                commandEntity.getHttpProtocolVOS() == null ? 0 : commandEntity.getHttpProtocolVOS().size());
        protocolStorage.doStorage(commandEntity);
    }

    @Override
    public List<HTTPProtocolVO> analysisProtocol(AnalysisCommandEntity commandEntity) {
        log.info("analysisProtocol openApiJson length:{}",
                commandEntity.getOpenApiJson() == null ? 0 : commandEntity.getOpenApiJson().length());
        return protocolAnalysis.doAnalysis(commandEntity);
    }

}
