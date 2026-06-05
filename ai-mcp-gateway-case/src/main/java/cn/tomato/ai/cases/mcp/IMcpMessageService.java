package cn.tomato.ai.cases.mcp;

import cn.tomato.ai.domain.session.model.entity.HandleMessageCommandEntity;
import org.springframework.http.ResponseEntity;

/**
 * MCP 消息处理服务接口
 */
public interface IMcpMessageService {

    /**
     * 处理 MCP 消息
     *
     * @param commandEntity 消息处理命令实体
     * @return 处理结果
     * @throws Exception 处理异常
     */
    ResponseEntity<Void> handleMessage(HandleMessageCommandEntity commandEntity) throws Exception;
}
