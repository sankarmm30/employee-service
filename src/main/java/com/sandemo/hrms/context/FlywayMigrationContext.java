package com.sandemo.hrms.context;

import org.flywaydb.core.Flyway;
import org.flywaydb.core.api.configuration.ClassicConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

/**
 * @author Sankar M <sankar.mm30@gmail.com>
 *
 * Bean configuration for flyway database migration
 */
@Configuration
public class FlywayMigrationContext {

    private static final Logger LOG = LoggerFactory.getLogger(FlywayMigrationContext.class);

    @Autowired
    private DataSource dataSource;

    /**
     * Initialize Flyway class and execute migration
     *
     * @return
     */
    @Bean(name = "flyway", initMethod = "migrate")
    public Flyway flyway() {

        LOG.info("Starting Flyway migration...");

        ClassicConfiguration configuration = new ClassicConfiguration();
        configuration.setDataSource(dataSource);

        Flyway flyway = new Flyway(configuration);

        // Migrates a database schema to the current version.
        // It scans the classpath for available migrations and applies pending migrations.

        flyway.migrate();

        LOG.info("Flyway migration stands complete");

        return flyway;
    }
}