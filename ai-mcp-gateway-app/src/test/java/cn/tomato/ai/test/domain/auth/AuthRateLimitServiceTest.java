package cn.tomato.ai.test.domain.auth;

import cn.tomato.ai.domain.auth.model.entity.RateLimitCommandEntity;
import cn.tomato.ai.domain.auth.service.IAuthRateLimitService;
import cn.tomato.ai.domain.auth.service.IAuthRegisterService;
import com.google.common.util.concurrent.RateLimiter;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.assertFalse;

@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest
public class AuthRateLimitServiceTest {

    @Resource
    private IAuthRateLimitService authRateLimitService;

    @Resource
    private IAuthRegisterService authRegisterService;
    @Test
    public void test_rateLimit_success() throws InterruptedException {
        // 1. 注册 (36000次/小时 => 10次/秒)
        /*RegisterCommandEntity registerCommandEntity = new RegisterCommandEntity();
        registerCommandEntity.setGatewayId("gateway_001");
        registerCommandEntity.setRateLimit(36000);
        registerCommandEntity.setExpireTime(new Date(System.currentTimeMillis() + 1000L * 60 * 60));
        String apiKey = authRegisterService.register(registerCommandEntity);
        log.info("注册结果 apiKey: {}", apiKey);*/

        RateLimitCommandEntity commandEntity = new RateLimitCommandEntity();
        commandEntity.setGatewayId("gateway_001");
        commandEntity.setApiKey("gw-dZKWHKFVZYF2j4fr3ElDh51pkX6zHAAri7hAwqyWvWWB2PTA");

        boolean rateLimit = authRateLimitService.rateLimit(commandEntity);
        log.info("限流结果(第一次) rateLimit: {}", rateLimit);
        assertFalse(rateLimit);

        Thread.sleep(150);

        rateLimit = authRateLimitService.rateLimit(commandEntity);
        log.info("限流结果(第二次) rateLimit: {}", rateLimit);
        assertFalse(rateLimit);
    }

    @Test
    public void testGuavaRateLimiter() throws InterruptedException {
        RateLimiter rateLimiter = RateLimiter.create(1.0);

        log.info("限流: {}", rateLimiter.tryAcquire());
        log.info("限流: {}", rateLimiter.tryAcquire());

        Thread.sleep(1000);

        log.info("限流: {}", rateLimiter.tryAcquire());
        log.info("限流: {}", rateLimiter.tryAcquire());
        log.info("限流: {}", rateLimiter.tryAcquire());
    }
}
