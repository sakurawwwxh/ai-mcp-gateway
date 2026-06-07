package cn.tomato.ai.domain.auth.service;

import cn.tomato.ai.domain.auth.model.entity.RegisterCommandEntity;

public interface IAuthRegisterService {

    String register(RegisterCommandEntity commandEntity);
}
