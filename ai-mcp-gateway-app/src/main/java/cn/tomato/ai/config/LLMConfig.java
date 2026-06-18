package cn.tomato.ai.config;

import org.springframework.ai.openai.api.OpenAiApi;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * LLM 对接配置
 *
 * <p>Spring AI 1.0.0 GA 的自动配置只注册 {@link org.springframework.ai.openai.OpenAiChatModel}，
 * 其内部使用的 {@link OpenAiApi} 为 private 构造、不暴露为 Bean。
 * 本配置按 spring.ai.openai.* 手动注册 OpenAiApi Bean，供 LLMService 据此构建
 * 绑定 MCP 工具回调的 ChatModel。
 *
 * @author Wxh
 * @date 2026-06-17
 */
@Configuration
public class LLMConfig {

    @Value("${spring.ai.openai.base-url}")
    private String baseUrl;

    @Value("${spring.ai.openai.api-key}")
    private String apiKey;

    @Bean
    public OpenAiApi openAiApi() {
        return OpenAiApi.builder()
                .baseUrl(baseUrl)
                .apiKey(apiKey)
                .build();
    }

}
