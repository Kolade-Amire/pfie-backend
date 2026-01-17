package com.kay.pfie.health;

import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component("readinessChecks")
public class ReadinessHealthIndicator implements HealthIndicator {
    private final ObjectProvider<JdbcTemplate> jdbcTemplateProvider;

    public ReadinessHealthIndicator(ObjectProvider<JdbcTemplate> jdbcTemplateProvider) {
        this.jdbcTemplateProvider = jdbcTemplateProvider;
    }

    @Override
    public Health health() {
        JdbcTemplate jdbcTemplate = jdbcTemplateProvider.getIfAvailable();
        if (jdbcTemplate == null) {
            return Health.down().withDetail("reason", "jdbcTemplate_missing").build();
        }
        try {
            jdbcTemplate.queryForObject("SELECT 1", Integer.class);
            Boolean hasSchemaHistory =
                    jdbcTemplate.queryForObject(
                            "SELECT EXISTS (" +
                                    "SELECT 1 FROM information_schema.tables " +
                                    "WHERE table_name = 'flyway_schema_history'" +
                                    ")",
                            Boolean.class
                    );
            if (hasSchemaHistory == null || !hasSchemaHistory) {
                return Health.down().withDetail("reason", "flyway_schema_history_missing").build();
            }
            Integer applied =
                    jdbcTemplate.queryForObject(
                            "SELECT COUNT(1) FROM flyway_schema_history WHERE success = true",
                            Integer.class
                    );
            if (applied == null || applied == 0) {
                return Health.down().withDetail("reason", "no_successful_migrations").build();
            }
            return Health.up().withDetail("migrationsApplied", applied).build();
        } catch (Exception ex) {
            return Health.down(ex).build();
        }
    }
}
