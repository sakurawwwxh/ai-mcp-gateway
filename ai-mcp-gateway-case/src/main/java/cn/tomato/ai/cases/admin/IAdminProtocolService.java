package cn.tomato.ai.cases.admin;

import cn.tomato.ai.domain.protocol.model.entity.AnalysisCommandEntity;
import cn.tomato.ai.domain.protocol.model.entity.StorageCommandEntity;
import cn.tomato.ai.domain.protocol.model.valobj.http.HTTPProtocolVO;

import java.util.List;

/**
 * 管理端-协议服务接口（编排层）
 * 接收已翻译的 StorageCommandEntity，纯委派给 domain service
 *
 * @author Wxh
 * @date 2026-06-12
 */
public interface IAdminProtocolService {

    /** 保存协议配置 */
    void saveGatewayProtocol(StorageCommandEntity commandEntity);

    /** 解析 Swagger，仅返回 VO 列表，不落库 */
    List<HTTPProtocolVO> analysisProtocol(AnalysisCommandEntity commandEntity);

}
