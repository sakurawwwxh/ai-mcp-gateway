package cn.tomato.ai.domain.session.adapter.port;

import cn.tomato.ai.domain.session.model.valobj.gateway.McpToolProtocolConfigVO;

import java.io.IOException;

/**
 * 会话端口接口
 */
public interface ISessionPort {

    /**
     * 执行工具调用
     *
     * @param httpConfig HTTP配置
     * @param params     请求参数
     * @return 响应结果
     * @throws IOException 请求异常
     */
    Object toolCall(McpToolProtocolConfigVO.HTTPConfig httpConfig, Object params) throws IOException;
}
