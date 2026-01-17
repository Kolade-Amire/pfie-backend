package com.kay.pfie;

import javax.sql.DataSource;
import org.flywaydb.core.Flyway;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;

public final class PfieMigrate {
    private static final Logger log = LoggerFactory.getLogger(PfieMigrate.class);

    private PfieMigrate() {}

    public static void main(String[] args) {
        ConfigurableApplicationContext ctx =
                new SpringApplicationBuilder(PfieBackendApplication.class)
                        .web(WebApplicationType.NONE)
                        .run(args);

        int exitCode = 0;
        try {
            DataSource dataSource = ctx.getBean(DataSource.class);
            logTargetDatabase(dataSource);
            Flyway flyway =
                    Flyway.configure()
                            .dataSource(dataSource)
                            .load();
            flyway.migrate();
            log.info("Flyway migration completed");
        } catch (Exception e) {
            exitCode = 1;
            log.error("Flyway migration failed", e);
        } finally {
            final int finalExitCode = exitCode;
            int code = SpringApplication.exit(ctx, () -> finalExitCode);
            System.exit(code);
        }
    }

    private static void logTargetDatabase(DataSource dataSource) {
        try (var connection = dataSource.getConnection()) {
            var meta = connection.getMetaData();
            log.info("Running migrations on {}", meta.getURL());
        } catch (Exception e) {
            log.warn("Unable to determine target database URL");
        }
    }
}
