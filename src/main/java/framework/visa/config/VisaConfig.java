package framework.visa.config;

import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class VisaConfig {
    private static final String CONFIG_LOCATION = "classpath:config/config.json";
    private static final String DUREE_VISA_KEY = "duree-visa";
    private static final Pattern DUREE_VISA_PATTERN = Pattern.compile("\\\"duree-visa\\\"\\s*:\\s*(\\d+)");

    private final int dureeVisaMois;

    public VisaConfig(ResourceLoader resourceLoader) {
        this.dureeVisaMois = loadDureeVisaMois(resourceLoader);
    }

    public int getDureeVisaMois() {
        return dureeVisaMois;
    }

    private int loadDureeVisaMois(ResourceLoader resourceLoader) {
        Resource resource = resourceLoader.getResource(CONFIG_LOCATION);

        try (InputStream inputStream = resource.getInputStream()) {
            String content = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
            Matcher matcher = DUREE_VISA_PATTERN.matcher(content);

            if (!matcher.find()) {
                throw new IllegalStateException("Configuration invalide: cle '" + DUREE_VISA_KEY + "' absente ou non numerique.");
            }

            int valeur = Integer.parseInt(matcher.group(1));
            if (valeur <= 0) {
                throw new IllegalStateException("Configuration invalide: '" + DUREE_VISA_KEY + "' doit etre > 0.");
            }

            return valeur;
        } catch (IOException e) {
            throw new IllegalStateException("Impossible de lire " + CONFIG_LOCATION + ".", e);
        }
    }
}
