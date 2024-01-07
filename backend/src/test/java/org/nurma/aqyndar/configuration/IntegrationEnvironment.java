package org.nurma.aqyndar.configuration;

import org.nurma.aqyndar.util.GenerateKeys;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;

import java.net.URI;
import java.net.URISyntaxException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

abstract public class IntegrationEnvironment {
    private static final String IMAGE_NAME = "postgres:15";
    public static final PostgreSQLContainer<?> POSTGRE_SQL_CONTAINER;

    static {
        POSTGRE_SQL_CONTAINER = new PostgreSQLContainer<>(IMAGE_NAME);
        POSTGRE_SQL_CONTAINER.start();
    }

    static Connection openConnection() {
        try {
            return DriverManager.getConnection(
                    POSTGRE_SQL_CONTAINER.getJdbcUrl(),
                    POSTGRE_SQL_CONTAINER.getUsername(),
                    POSTGRE_SQL_CONTAINER.getPassword()
            );
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @DynamicPropertySource
    static void registerPgProperties(DynamicPropertyRegistry registry) {
        String jdbcUrl = POSTGRE_SQL_CONTAINER.getJdbcUrl();
        String username = POSTGRE_SQL_CONTAINER.getUsername();
        String password = POSTGRE_SQL_CONTAINER.getPassword();

        try {
            URI uri = new URI(jdbcUrl.replace("jdbc:postgresql://", "https://"));
            String host = uri.getHost();
            int port = uri.getPort();
            String dbName = uri.getPath().replaceFirst("/", "");

            registry.add("DATABASE_HOST", () -> host);
            registry.add("DATABASE_PORT", () -> port);
            registry.add("DATABASE_NAME", () -> dbName);
            registry.add("DATABASE_USERNAME", () -> username);
            registry.add("DATABASE_PASSWORD", () -> password);

        } catch (URISyntaxException e) {
            throw new RuntimeException("Failed to parse JDBC URL", e);
        }

        registry.add("jwt.secret.access", GenerateKeys::generateKey);
        registry.add("jwt.secret.refresh", GenerateKeys::generateKey);

        registry.add("cors.allowedOrigins",
                () -> "http://localhost:5173,http://localhost:4173,https://aqyndar.com");


        registry.add("voice-service.base-url", () -> "http://localhost:8000/");
    }
}
