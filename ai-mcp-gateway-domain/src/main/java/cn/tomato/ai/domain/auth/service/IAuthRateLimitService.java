package cn.tomato.ai.domain.auth.service;

import cn.tomato.ai.domain.auth.model.entity.LicenseCommandEntity;
import cn.tomato.ai.domain.auth.model.entity.RateLimitCommandEntity;

public interface IAuthRateLimitService {
    /***
     * true - 限流
     * false - 未限流
     * @param commandEntity
     * @return
     */
    boolean rateLimit(RateLimitCommandEntity commandEntity);
}
