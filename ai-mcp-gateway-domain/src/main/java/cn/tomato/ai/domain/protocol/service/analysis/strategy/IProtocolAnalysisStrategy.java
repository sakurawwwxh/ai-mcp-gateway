package cn.tomato.ai.domain.protocol.service.analysis.strategy;

import cn.tomato.ai.domain.protocol.model.entity.AnalysisCommandEntity;
import cn.tomato.ai.domain.protocol.model.valobj.http.HTTPProtocolVO;
import com.alibaba.fastjson.JSONObject;

import java.util.List;

public interface IProtocolAnalysisStrategy {

    void doAnalysis(JSONObject operation, JSONObject definitions, List<HTTPProtocolVO.ProtocolMapping> mappings);
}
