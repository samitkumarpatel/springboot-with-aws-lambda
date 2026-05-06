package net.samitkumar.springboot_with_aws_lambda;

import io.awspring.cloud.dynamodb.DynamoDbTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;
import org.springframework.security.web.util.matcher.MediaTypeRequestMatcher;
import org.springframework.session.MapSession;
import org.springframework.session.SessionRepository;
import org.springframework.session.config.annotation.web.http.EnableSpringHttpSession;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.enhanced.dynamodb.Key;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.time.Duration;
import java.time.Instant;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableWebSecurity
@EnableSpringHttpSession
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http
                .authorizeHttpRequests(auth -> auth.anyRequest().authenticated())
                .formLogin(Customizer.withDefaults())
                .logout(Customizer.withDefaults())
                .build();
    }
}

@Component
class DynamodbSessionStore implements SessionRepository<MapSession> {
    private static final Logger log = LoggerFactory.getLogger(DynamodbSessionStore.class);

    private final DynamoDbTemplate dynamoDbTemplate;

    DynamodbSessionStore(DynamoDbTemplate dynamoDbTemplate) {
        this.dynamoDbTemplate = dynamoDbTemplate;
    }

    @Override
    public MapSession createSession() {
        MapSession session = new MapSession();
        session.setMaxInactiveInterval(Duration.ofMinutes(30));
        return session;
    }

    @Override
    public void save(MapSession session) {
        try {
            dynamoDbTemplate.save(toEntity(session));
        } catch (Exception e) {
            log.error("Failed to save session {}: {}", session.getId(), e.getMessage());
        }
    }

    @Override
    public MapSession findById(String id) {
        try {
            SessionEntity entity = dynamoDbTemplate.load(
                    Key.builder().partitionValue(id).build(), SessionEntity.class);
            if (entity == null) return null;
            MapSession session = fromEntity(entity);
            return session.isExpired() ? null : session;
        } catch (Exception e) {
            log.error("Failed to load session {}: {}", id, e.getMessage());
            return null;
        }
    }

    @Override
    public void deleteById(String id) {
        try {
            dynamoDbTemplate.delete(Key.builder().partitionValue(id).build(), SessionEntity.class);
        } catch (Exception e) {
            log.error("Failed to delete session {}: {}", id, e.getMessage());
        }
    }

    private SessionEntity toEntity(MapSession session) {
        SessionEntity entity = new SessionEntity();
        entity.setSessionId(session.getId());
        entity.setCreationTime(session.getCreationTime().toEpochMilli());
        entity.setLastAccessedTime(session.getLastAccessedTime().toEpochMilli());
        entity.setMaxInactiveInterval(session.getMaxInactiveInterval().getSeconds());
        entity.setTtl(Instant.now().plus(session.getMaxInactiveInterval()).getEpochSecond());

        Map<String, String> attrs = new HashMap<>();
        session.getAttributeNames().forEach(name -> {
            try (ByteArrayOutputStream bos = new ByteArrayOutputStream();
                 ObjectOutputStream oos = new ObjectOutputStream(bos)) {
                oos.writeObject(session.getAttribute(name));
                attrs.put(name, Base64.getEncoder().encodeToString(bos.toByteArray()));
            } catch (Exception e) {
                throw new RuntimeException("Failed to serialize session attribute: " + name, e);
            }
        });
        entity.setAttributes(attrs);
        return entity;
    }

    private MapSession fromEntity(SessionEntity entity) {
        MapSession session = new MapSession(entity.getSessionId());
        session.setCreationTime(Instant.ofEpochMilli(entity.getCreationTime()));
        session.setLastAccessedTime(Instant.ofEpochMilli(entity.getLastAccessedTime()));
        session.setMaxInactiveInterval(Duration.ofSeconds(entity.getMaxInactiveInterval()));

        if (entity.getAttributes() != null) {
            entity.getAttributes().forEach((name, value) -> {
                try (ObjectInputStream ois = new ObjectInputStream(
                        new ByteArrayInputStream(Base64.getDecoder().decode(value)))) {
                    session.setAttribute(name, ois.readObject());
                } catch (Exception e) {
                    throw new RuntimeException("Failed to deserialize session attribute: " + name, e);
                }
            });
        }
        return session;
    }
}
