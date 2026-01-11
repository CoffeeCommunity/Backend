package coffee.community.backend.global.i18n;

import org.jspecify.annotations.NonNull;
import org.springframework.context.support.AbstractMessageSource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.yaml.snakeyaml.Yaml;

import java.io.InputStream;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class YamlMessageSource extends AbstractMessageSource {

    private final Map<String, Map<String, String>> messages;

    public YamlMessageSource(String basename) {
        this.messages = loadYamlMessages(basename);
    }

    @Override
    protected MessageFormat resolveCode(@NonNull String code, Locale locale) {
        String language = locale.getLanguage(); // ko, en

        Map<String, String> localeMessages = messages.get(language);

        if (localeMessages == null) {
            localeMessages = messages.get("ko"); // fallback
        }

        if (localeMessages == null) {
            return null;
        }

        String message = localeMessages.get(code);
        return message != null ? new MessageFormat(message, locale) : null;
    }

    @SuppressWarnings("unchecked")
    private Map<String, Map<String, String>> loadYamlMessages(String basename) {
        Map<String, Map<String, String>> result = new HashMap<>();

        try {
            PathMatchingResourcePatternResolver resolver =
                    new PathMatchingResourcePatternResolver();

            Resource[] resources =
                    resolver.getResources("classpath*:" + basename + "*.yml");

            Yaml yaml = new Yaml();

            for (Resource resource : resources) {
                String filename = resource.getFilename();
                String language = extractLanguage(filename);

                try (InputStream is = resource.getInputStream()) {
                    Map<String, Object> yamlData =
                            (Map<String, Object>) yaml.load(is);

                    Map<String, String> flatMap = flatten("", yamlData);
                    result.put(language, flatMap);
                }
            }
        } catch (Exception e) {
            throw new IllegalStateException("Failed to load YAML messages", e);
        }

        return result;
    }

    private Map<String, String> flatten(
            String prefix,
            Map<String, Object> source
    ) {
        Map<String, String> result = new HashMap<>();

        for (Map.Entry<String, Object> entry : source.entrySet()) {
            String key = prefix.isEmpty()
                    ? entry.getKey()
                    : prefix + "." + entry.getKey();

            Object value = entry.getValue();

            if (value instanceof Map) {
                @SuppressWarnings("unchecked")
                Map<String, Object> nestedMap = (Map<String, Object>) value;

                result.putAll(flatten(key, nestedMap));
            } else if (value != null) {
                result.put(key, value.toString());
            }
        }

        return result;
    }

    private String extractLanguage(String filename) {
        if (filename == null) return "ko";
        int idx = filename.indexOf('_');
        int dot = filename.lastIndexOf('.');
        if (idx == -1 || dot == -1) return "ko";
        return filename.substring(idx + 1, dot); // ko, en
    }
}