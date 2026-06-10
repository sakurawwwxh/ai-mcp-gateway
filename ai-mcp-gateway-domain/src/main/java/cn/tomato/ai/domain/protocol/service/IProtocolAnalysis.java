package cn.tomato.ai.domain.protocol.service;

import cn.tomato.ai.domain.protocol.model.entity.AnalysisCommandEntity;
import cn.tomato.ai.domain.protocol.model.valobj.http.HTTPProtocolVO;

import java.util.List;

public interface IProtocolAnalysis {

    List<HTTPProtocolVO> doAnalysis(AnalysisCommandEntity commandEntity);
}
