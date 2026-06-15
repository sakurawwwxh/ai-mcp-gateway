package cn.tomato.ai.cases.admin;

import cn.tomato.ai.domain.auth.model.entity.RegisterCommandEntity;

/**
 * 管理端-鉴权服务接口（编排层）
 * 接收已翻译的 RegisterCommandEntity，纯委派给 domain service
 *
 * @author Wxh
 * @date 2026-06-12
 */
public interface IAdminAuthService {

    /**
     * 保存鉴权配置
     *
     * @return 服务端生成的 API Key（前端需展示给用户，用于 SSE 鉴权）
     */
    String saveGatewayAuth(RegisterCommandEntity commandEntity);

}
