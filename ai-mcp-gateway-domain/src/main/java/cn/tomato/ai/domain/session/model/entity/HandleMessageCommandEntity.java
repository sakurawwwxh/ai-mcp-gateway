package cn.tomato.ai.domain.session.model.entity;

import cn.tomato.ai.domain.session.model.valobj.McpSchemaVO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.IOException;

/**
 * 消息处理命令实体
 * 封装消息处理所需的参数
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class HandleMessageCommandEntity {

    private String gatewayId;
    private String sessionId;
    private McpSchemaVO.JSONRPCMessage jsonrpcMessage;

    /**
     * 通过原始消息体构造
     * 自动反序列化 JSON-RPC 消息
     */
    public HandleMessageCommandEntity(String gatewayId, String sessionId, String messageBody) throws IOException {
        this.gatewayId = gatewayId;
        this.sessionId = sessionId;
        this.jsonrpcMessage = McpSchemaVO.deserializeJsonRpcMessage(messageBody);
    }
}
