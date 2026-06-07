package cn.tomato.ai.domain.auth.service;

import cn.tomato.ai.domain.auth.model.entity.LicenseCommandEntity;

public interface IAuthLicenseService {

    boolean checkLicense(LicenseCommandEntity commandEntity);
}
