package cn.tomato.ai.domain.admin.model.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 网关工具分页结果
 *
 * @author Wxh
 * @date 2026-06-15
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GatewayToolPageEntity {

    /** 当前页数据 */
    private List<GatewayToolConfigEntity> dataList;

    /** 总记录数 */
    private Long total;

}