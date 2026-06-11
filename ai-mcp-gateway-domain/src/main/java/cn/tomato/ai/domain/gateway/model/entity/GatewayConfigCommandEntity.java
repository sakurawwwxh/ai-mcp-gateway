package cn.tomato.ai.domain.gateway.model.entity;

import cn.tomato.ai.domain.gateway.model.valobj.GatewayConfigVO;
import cn.tomato.ai.types.enums.GatewayEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 网关配置实体
 *
 * @author Wxh
 * @date 2026-06-11
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GatewayConfigCommandEntity {

    private GatewayConfigVO gatewayConfigVO;

    public static GatewayConfigCommandEntity buildUpdateGatewayAuthStatusVO(String gatewayId, GatewayEnum.GatewayAuthStatusEnum auth) {
        return GatewayConfigCommandEntity.builder()
                .gatewayConfigVO(GatewayConfigVO.builder()
                        .gatewayId(gatewayId)
                        .auth(auth)
                        .build())
                .build();
    }

}
