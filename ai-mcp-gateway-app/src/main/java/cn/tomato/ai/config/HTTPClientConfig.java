package cn.tomato.ai.config;

import cn.tomato.ai.infrastructure.gateway.GenericHttpGateway;
import okhttp3.ConnectionPool;
import okhttp3.OkHttpClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import java.util.concurrent.TimeUnit;

/**
 * @author Wxh
 * @date 2026年06月03日 16:11
 */
@Configuration
public class HTTPClientConfig {

    @Bean
    public OkHttpClient okHttpClient() {
          return new OkHttpClient.Builder()
                  .connectionPool(new ConnectionPool(10,5,TimeUnit.MINUTES))
                  .retryOnConnectionFailure(true)
                  .connectTimeout(100,TimeUnit.SECONDS)
                  .readTimeout(300,TimeUnit.SECONDS)
                  .writeTimeout(300,TimeUnit.SECONDS)
                  .build();
    }

    @Bean
    public GenericHttpGateway genericHttpGateway(OkHttpClient okHttpClient) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://127.0.0.1/")
                .addConverterFactory(GsonConverterFactory.create())
                .client(okHttpClient)
                .build();

        return retrofit.create(GenericHttpGateway.class);
    }
}
