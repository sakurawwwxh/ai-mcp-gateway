package cn.tomato.ai.test.domain.auth;

import cn.tomato.ai.domain.auth.model.entity.LicenseCommandEntity;
import cn.tomato.ai.domain.auth.model.entity.RegisterCommandEntity;
import cn.tomato.ai.domain.auth.service.IAuthLicenseService;
import cn.tomato.ai.domain.auth.service.IAuthRegisterService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Date;

import static org.junit.Assert.assertTrue;

/**
 * Auth license service test.
 */
@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest
public class AuthLicenseServiceTest {

    @Resource
    private IAuthLicenseService authLicenseService;

    @Resource
    private IAuthRegisterService authRegisterService;

    @Test
    public void test_checkLicense() {
        // 1. Register a new apiKey, or use an existing record in the database.
//        RegisterCommandEntity registerCommandEntity = new RegisterCommandEntity();
//        registerCommandEntity.setGatewayId("gateway_001");
//        registerCommandEntity.setRateLimit(10);
//        registerCommandEntity.setExpireTime(new Date(System.currentTimeMillis() + 1000L * 60 * 60));
//
//        String apiKey = authRegisterService.register(registerCommandEntity);
//        log.info("register result apiKey: {}", apiKey);

//         2. Check license.
        LicenseCommandEntity commandEntity = new LicenseCommandEntity();
        commandEntity.setGatewayId("gateway_001");
//        commandEntity.setApiKey(apiKey);
        commandEntity.setApiKey("gw-dZKWHKFVZYF2j4fr3ElDh51pkX6zHAAri7hAwqyWvWWB2PTA");

        boolean success = authLicenseService.checkLicense(commandEntity);
        log.info("license check result success: {}", success);

        assertTrue(success);
    }
}
