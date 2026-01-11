package coffee.community.backend.global.config;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import net.nurigo.sdk.NurigoApp;
import net.nurigo.sdk.message.service.DefaultMessageService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Configuration
@RequiredArgsConstructor
@ConfigurationProperties(prefix = "coolsms")
@Data
public class SmsConfig {
    private String apiKey;
    private String apiSecret;
    private String fromNumber;

    @Bean
    @ConditionalOnMissingBean
    public DefaultMessageService messageService() {
        DefaultMessageService service = NurigoApp.INSTANCE.initialize(apiKey, apiSecret, "https://api.coolsms.co.kr");
        log.info("CoolSMS 초기화 완료: apiKey={}", apiKey.substring(0, 8) + "...");
        return service;
    }
}
