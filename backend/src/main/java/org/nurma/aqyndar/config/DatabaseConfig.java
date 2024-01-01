package org.nurma.aqyndar.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

@Configuration
@Log4j2
public class DatabaseConfig {

    @Value("${JDBC_DATABASE_URL:}")
    private String herokuDbUrl;

    @Value("${JDBC_DATABASE_USERNAME:}")
    private String herokuUsername;

    @Value("${JDBC_DATABASE_PASSWORD:}")
    private String herokuPassword;

    @Value("${DATABASE_HOST:}")
    private String host;

    @Value("${DATABASE_PORT:}")
    private String port;

    @Value("${DATABASE_NAME:}")
    private String dbName;

    @Value("${DATABASE_USERNAME:}")
    private String username;

    @Value("${DATABASE_PASSWORD:}")
    private String password;

    @Bean
    public DataSource dataSource() {
        HikariConfig config = new HikariConfig();


        if (!herokuDbUrl.isEmpty()) {
            log.info("Using Heroku database");
            config.setJdbcUrl(herokuDbUrl);
            config.setUsername(herokuUsername);
            config.setPassword(herokuPassword);
        } else {
            log.info("Using local database");
            String dbUrl = "jdbc:postgresql://" + host + ':' + port + '/' + dbName;
            config.setJdbcUrl(dbUrl);
            config.setUsername(username);
            config.setPassword(password);
        }

        final int maxPoolSize = 10;
        final int minIdle = 2;
        final int idleTimeout = 600000;
        final int maxLifetime = 1800000;

        config.setDriverClassName("org.postgresql.Driver");
        config.setMaximumPoolSize(maxPoolSize);
        config.setMinimumIdle(minIdle);
        config.setIdleTimeout(idleTimeout);
        config.setMaxLifetime(maxLifetime);


        return new HikariDataSource(config);
    }
}

