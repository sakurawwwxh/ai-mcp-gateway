package cn.tomato.ai.domain.protocol.adapter.repository;

import cn.tomato.ai.domain.protocol.model.valobj.http.HTTPProtocolVO;

import java.util.List;

/**
 * 协议仓储服务接口
 *
 * @author xiaofuge bugstack.cn @小傅哥
 * 2026/6/10 08:40
 */
public interface IProtocolRepository {

    List<Long> saveHttpProtocolAndMapping(List<HTTPProtocolVO> httpProtocolVOS);

}
