package cn.tomato.ai.trigger.http;

import cn.tomato.ai.api.IMcpGatewayService;
import cn.tomato.ai.cases.mcp.IMcpSessionService;
import cn.tomato.ai.domain.session.service.impl.SessionManagementService;
import cn.tomato.ai.types.enums.ResponseCode;
import cn.tomato.ai.types.exception.AppException;
import io.netty.util.internal.StringUtil;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

/**
 * @author Wxh
 * @date 2026年05月28日 17:42
 */
@Slf4j
@RestController
@CrossOrigin(origins = "*", allowedHeaders = "*", methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE})
@RequestMapping("/")
public class McpGatewayController implements IMcpGatewayService {

    @Resource
    private IMcpSessionService mcpSessionService;

    public McpGatewayController() {
        System.out.println("McpGatewayController");
    }

    @GetMapping(value = "{gatewayId}/mcp/sse", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    @Override
    public Flux<ServerSentEvent<String>> establishSSEConnection(@PathVariable("gatewayId") String gatewayId) throws Exception {

        try{

            log.info("建立 mcp 连接{}", gatewayId);

            if(StringUtils.isBlank(gatewayId)){
                log.info("gatewayId is null");

                throw new AppException(ResponseCode.ILLEGAL_PARAMETER.getCode(),ResponseCode.ILLEGAL_PARAMETER.getInfo());
            }

            return mcpSessionService.createMcpSession(gatewayId);
        }catch (Exception e){
            log.info("建立mcp连接失败{}",gatewayId);
            throw e;
        }

    }
}
