package cn.tomato.ai.domain.session.service.message;

import cn.tomato.ai.domain.session.model.valobj.McpSchemaVO;
import cn.tomato.ai.domain.session.model.valobj.enums.SessionMessageHandlerMethodEnum;
import cn.tomato.ai.domain.session.service.ISessionMessageService;
import cn.tomato.ai.domain.session.service.message.handle.IRequestHandler;
import cn.tomato.ai.types.exception.AppException;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Map;

import static cn.tomato.ai.types.enums.ResponseCode.METHOD_NOT_FOUND;

/**
 * @author Wxh
 * @date 2026年05月29日 17:06
 */
@Slf4j
@Service
public class SessionMessageService implements ISessionMessageService {

    @Resource
    private Map<String, IRequestHandler> requestHandlerMap;

    @Override
    public McpSchemaVO.JSONRPCResponse processHandleMessage(McpSchemaVO.JSONRPCRequest request) {

        String method = request.method();

        SessionMessageHandlerMethodEnum sessionMessageHandlerMethodEnum = SessionMessageHandlerMethodEnum.getByMethod(method);
        if (null == sessionMessageHandlerMethodEnum) {
            throw new AppException(METHOD_NOT_FOUND.getCode(), METHOD_NOT_FOUND.getInfo());
        }
        String handlerName = sessionMessageHandlerMethodEnum.getHandlerName();
        IRequestHandler requestHandler = requestHandlerMap.get(handlerName);

        if (null == requestHandler) {
            throw new AppException(METHOD_NOT_FOUND.getCode(), METHOD_NOT_FOUND.getInfo());
        }

        // 使用枚举策略模式处理请求
        return requestHandler.handle(request);
    }
}
