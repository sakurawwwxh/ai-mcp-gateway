package cn.tomato.ai.domain.protocol.model.entity;

import cn.tomato.ai.domain.protocol.model.valobj.enums.AnalysisTypeEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author Wxh
 * @date 2026年06月09日 16:51
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AnalysisCommandEntity {

    /**
     * 解析类型枚举
     */
    private AnalysisTypeEnum type;

    /**
     * swagger 导出的 api json 数据
     */
    private String openApiJson;

    /**
     * 解析的接口端点
     */
    private List<String> endpoints;

}
