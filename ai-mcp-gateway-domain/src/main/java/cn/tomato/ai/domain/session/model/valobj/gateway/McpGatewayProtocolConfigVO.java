package cn.tomato.ai.domain.session.model.valobj.gateway;

import lombok.*;

/**
 * @author Wxh
 * @date 2026年06月03日 17:15
 */
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class McpGatewayProtocolConfigVO {

    private HTTPConfig httpConfig;

    @Data
    public static class HTTPConfig{
        private String httpUrl;
        private String httpHeaders;
        private String httpMethod;
        private Integer timeout;
    }
}
