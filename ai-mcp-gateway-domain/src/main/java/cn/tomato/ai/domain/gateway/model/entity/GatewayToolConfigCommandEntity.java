package cn.tomato.ai.domain.gateway.model.entity;

import cn.tomato.ai.domain.gateway.model.valobj.GatewayToolConfigVO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 网关工具配置实体
 *
 * @author Wxh
 * @date 2026-06-11
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GatewayToolConfigCommandEntity {

    private GatewayToolConfigVO gatewayToolConfigVO;

    public static GatewayToolConfigCommandEntity buildUpdateGatewayProtocol(String gatewayId, Long toolId, Long protocolId, String protocolType) {
        return GatewayToolConfigCommandEntity.builder()
                .gatewayToolConfigVO(
                        GatewayToolConfigVO.builder()
                                .gatewayId(gatewayId)
                                .toolId(toolId)
                                .protocolId(protocolId)
                                .protocolType(protocolType)
                                .build())
                .build();
    }

}
