package com.example.petshopbackend.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class ConfigChecker implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(ConfigChecker.class);

    // 我们尝试注入和AI配置中完全相同的两个属性
    @Value("${spring.ai.openai.base-url}")
    private String aiBaseUrl;

    @Value("${spring.ai.openai.api-key}")
    private String aiApiKey;

    @Override
    public void run(String... args) throws Exception {
        // 应用启动后，这段代码会自动执行
        log.info("================= Spring AI 配置检查 ==================");
        log.info("读取到的 Base URL 是: {}", aiBaseUrl);
        log.info("读取到的 API Key 是: {}...", (aiApiKey != null && aiApiKey.length() > 5) ? aiApiKey.substring(0, 5) : "null");
        log.info("=====================================================");
    }
}
