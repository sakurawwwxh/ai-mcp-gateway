package cn.tomato.ai.domain.session.service.management;

import cn.tomato.ai.domain.session.model.valobj.SessionConfigVO;
import cn.tomato.ai.domain.session.service.ISessionManagementService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Sinks;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * @author Wxh
 * @date 2026年05月27日 20:30
 */
@Slf4j
@Service
public class SessionManagementService implements ISessionManagementService {

    /**
     * 会话超时时间（分钟）- 也可以把配置抽取到yml里
     */
    private static final long SESSION_TIMEOUT_MINUTES = 30;

    /**
     * 定时清理过期会话的调度器
     */
    private final ScheduledExecutorService cleanupScheduler = Executors.newSingleThreadScheduledExecutor();

    /**
     * 活跃会话存储，key为sessionId，线程安全
     */
    private final Map<String,SessionConfigVO>  activeSessions = new ConcurrentHashMap<>();

    /**
     * 初始化会话管理服务，启动定时清理任务
     */
    public SessionManagementService() {
        cleanupScheduler.scheduleAtFixedRate(this::cleanExpiredSessions,5, 5, TimeUnit.MINUTES);
        log.info("会话管理服务已启动，会话超时时间: {} 分钟", SESSION_TIMEOUT_MINUTES);
    }

    /**
     * 创建新会话，生成sessionId和SSE sink，发送端点信息
     */
    @Override
    public SessionConfigVO createSession(String gatewayId,String apiKey) {
        log.info("创建会话 gatewayId:{}", gatewayId);
        String sessionId = generateSessionId();
        Sinks.Many<ServerSentEvent<String>> sink = Sinks.many().multicast().onBackpressureBuffer();

        String messageEndpoint  = "/api-gateway/" + gatewayId + "/mcp/sse?sessionId=" + sessionId;

        if (StringUtils.isNotBlank(apiKey)){
            messageEndpoint += "&api_key=" + apiKey;
        }

        sink.tryEmitNext(ServerSentEvent.<String>builder()
                .event("endpoint")
                .data(messageEndpoint)
                .build());

        SessionConfigVO sessionConfig = new SessionConfigVO(sessionId, sink);
        activeSessions.put(sessionId, sessionConfig);

        log.info("创建会话: {}, gatewayId: {}", sessionId, gatewayId);
        return sessionConfig;
    }

    /**
     * 移除指定会话，标记为非活跃并关闭SSE sink
     */
    @Override
    public void removeSession(String sessionId) {
        SessionConfigVO sessionConfigVO = activeSessions.remove(sessionId);

        if (sessionConfigVO != null) return;

        sessionConfigVO.markInactive();

        try {
            sessionConfigVO.getSink().tryEmitComplete();
        }catch (Exception e){
            log.warn("关闭会话失败",e);
        }
    }

    /**
     * 获取指定会话，更新最后访问时间
     */
    @Override
    public SessionConfigVO getSession(String sessionId) {

        if (sessionId == null || sessionId.isEmpty()) return null;

        SessionConfigVO sessionConfigVO = activeSessions.get(sessionId);

        if (null != sessionConfigVO && sessionConfigVO.isActive()) {
            sessionConfigVO.updateLastAccessTime();
            return sessionConfigVO;
        }

        return null;
    }

    /**
     * 清理所有过期会话，由定时任务调度执行
     */
    @Override
    public void cleanExpiredSessions() {
        int cleanedCount = 0;

        for (Map.Entry<String, SessionConfigVO> entry : activeSessions.entrySet()) {
            SessionConfigVO sessionConfigVO = entry.getValue();

            if (!sessionConfigVO.isActive() || sessionConfigVO.isExpired(SESSION_TIMEOUT_MINUTES)) {
                removeSession(sessionConfigVO.getSessionId());
                cleanedCount++;
            }
        }

        if (cleanedCount > 0) {
            log.info("清理了{}", cleanedCount);
        }
    }

    /**
     * 关闭会话管理服务，清理所有会话并停止调度器
     */
    @Override
    public void shutdown() {
        for (String sessionId : activeSessions.keySet()) {
            removeSession(sessionId);
        }

        cleanupScheduler.shutdown();

        try{

            if (!cleanupScheduler.awaitTermination(5, TimeUnit.SECONDS)){
                cleanupScheduler.shutdown();
            }

        }catch (InterruptedException e){
            cleanupScheduler.shutdown();
            Thread.currentThread().interrupt();
        }

        log.info("session management service shutdown");
    }

    /**
     * 生成32位十六进制会话ID
     */
    private String generateSessionId() {
        byte[] bytes = new byte[16];
        new java.security.SecureRandom().nextBytes(bytes);
        return java.util.HexFormat.of().formatHex(bytes);
    }
}
