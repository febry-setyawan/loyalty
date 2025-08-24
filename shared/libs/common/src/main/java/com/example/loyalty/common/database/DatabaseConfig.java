package com.example.loyalty.common.database;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import javax.sql.DataSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

/**
 * Database connection configuration with connection pooling Provides optimized database connections
 * for all services
 */
@Configuration
public class DatabaseConfig {

  @Value("${spring.datasource.url}")
  private String jdbcUrl;

  @Value("${spring.datasource.username}")
  private String username;

  @Value("${spring.datasource.password}")
  private String password;

  @Value("${spring.datasource.driver-class-name:org.postgresql.Driver}")
  private String driverClassName;

  @Value("${app.database.pool.minimum-idle:5}")
  private int minimumIdle;

  @Value("${app.database.pool.maximum-pool-size:20}")
  private int maximumPoolSize;

  @Value("${app.database.pool.connection-timeout:30000}")
  private long connectionTimeout;

  @Value("${app.database.pool.idle-timeout:600000}")
  private long idleTimeout;

  @Value("${app.database.pool.max-lifetime:1800000}")
  private long maxLifetime;

  @Value("${app.database.pool.leak-detection-threshold:60000}")
  private long leakDetectionThreshold;

  @Bean
  @Primary
  public DataSource dataSource() {
    HikariConfig config = new HikariConfig();

    // Basic connection settings
    config.setJdbcUrl(jdbcUrl);
    config.setUsername(username);
    config.setPassword(password);
    config.setDriverClassName(driverClassName);

    // Connection pool settings
    config.setMinimumIdle(minimumIdle);
    config.setMaximumPoolSize(maximumPoolSize);
    config.setConnectionTimeout(connectionTimeout);
    config.setIdleTimeout(idleTimeout);
    config.setMaxLifetime(maxLifetime);
    config.setLeakDetectionThreshold(leakDetectionThreshold);

    // Connection pool name for monitoring
    config.setPoolName("LoyaltyConnectionPool");

    // Performance optimizations
    config.addDataSourceProperty("cachePrepStmts", "true");
    config.addDataSourceProperty("prepStmtCacheSize", "250");
    config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
    config.addDataSourceProperty("useServerPrepStmts", "true");
    config.addDataSourceProperty("useLocalSessionState", "true");
    config.addDataSourceProperty("rewriteBatchedStatements", "true");
    config.addDataSourceProperty("cacheResultSetMetadata", "true");
    config.addDataSourceProperty("cacheServerConfiguration", "true");
    config.addDataSourceProperty("elideSetAutoCommits", "true");
    config.addDataSourceProperty("maintainTimeStats", "false");

    // Health check query
    config.setConnectionTestQuery("SELECT 1");

    return new HikariDataSource(config);
  }
}
