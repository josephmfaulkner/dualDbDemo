package com.josephmfaulkner.dualDBDemo.testFixtures;

import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.test.context.TestPropertySource;
import org.testcontainers.postgresql.PostgreSQLContainer;


@TestPropertySource(properties = {
    //"spring.flyway.locations=filesystem:../database-migrations/postgres-migrations,filesystem:database-migrations/postgres-migrations",
    //"spring.flyway.fail-on-missing-locations=true",
    //"spring.flyway.validate-on-migrate=true",
    //"spring.flyway.clean-disabled=false"
    "spring.jpa.hibernate.ddl-auto=create-drop"
})
public abstract class _BaseLocalPostgresDbTest {
    @ServiceConnection
    protected static final PostgreSQLContainer postgres = 
        new PostgreSQLContainer("postgres:18-alpine");
}
