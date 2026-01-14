package coffee.community.backend.global.config;

import coffee.community.backend.global.i18n.YamlMessageSource;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

@Configuration
public class MessageConfig {

    @Bean
    @Primary
    public MessageSource messageSource() {
        // basename → classpath:messages/messages_ko.yml
        return new YamlMessageSource("messages/messages_");
    }

    /**
     * &#064;Valid  검증 메시지에도 MessageSource 적용
     */
    @Bean
    public LocalValidatorFactoryBean validator(MessageSource messageSource) {
        LocalValidatorFactoryBean factory = new LocalValidatorFactoryBean();
        factory.setValidationMessageSource(messageSource);
        return factory;
    }
}