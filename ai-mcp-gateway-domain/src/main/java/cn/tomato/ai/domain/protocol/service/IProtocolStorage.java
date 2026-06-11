package cn.tomato.ai.domain.protocol.service;

import cn.tomato.ai.domain.protocol.model.entity.StorageCommandEntity;

import java.util.List;

/**
 * 协议存储接口
 *
 * @author xiaofuge bugstack.cn @小傅哥
 * 2026/6/10 08:45
 */
public interface IProtocolStorage {

    List<Long> doStorage(StorageCommandEntity commandEntity);

}
