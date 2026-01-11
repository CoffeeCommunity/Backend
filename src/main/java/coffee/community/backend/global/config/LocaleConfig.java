package coffee.community.backend.global.config;

import coffee.community.backend.global.i18n.YamlMessageSource;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.i18n.AcceptHeaderLocaleResolver;

import java.util.Locale;

@Configuration
public class LocaleConfig {

    @Bean
    public MessageSource messageSource() {
        return new YamlMessageSource("i18n/messages");
    }

    @Bean
    public LocaleResolver localeResolver() {
        AcceptHeaderLocaleResolver resolver =
                new AcceptHeaderLocaleResolver();
        resolver.setDefaultLocale(Locale.KOREAN);
        return resolver;
    }
}