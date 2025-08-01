package tech.qiantong.qknow.server.config;

import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;

/**
 * RestTemplate配置类
 * 用于配置HTTP客户端，支持与Text2Cypher API的通信
 */
@Configuration
public class RestTemplateConfig {
    
    @Bean("llmRestTemplate")
    public RestTemplate llmRestTemplate() {
        return new RestTemplateBuilder()
            .setConnectTimeout(Duration.ofSeconds(30))
            .setReadTimeout(Duration.ofSeconds(60))
            .build();
    }
    
    @Bean("text2cypherRestTemplate")
    public RestTemplate text2cypherRestTemplate() {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(30000); // 30秒连接超时
        factory.setReadTimeout(60000);    // 60秒读取超时
        
        return new RestTemplate(factory);
    }
} 