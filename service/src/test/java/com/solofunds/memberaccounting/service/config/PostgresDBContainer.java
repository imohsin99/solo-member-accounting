package com.solofunds.memberaccounting.service.config;

import org.testcontainers.containers.PostgreSQLContainer;

public class PostgresDBContainer extends PostgreSQLContainer<PostgresDBContainer> {
    private static final String IMAGE_VERSION = "postgres:15.3-alpine";
    private static final String DATABASE_NAME = "solofunds";
    private static final String USERNAME = "solofunds";
    private static final String PASSWORD = "Solofunds123";

    public static PostgresDBContainer container = new PostgresDBContainer()
            .withDatabaseName(DATABASE_NAME)
            .withUsername(USERNAME)
            .withPassword(PASSWORD);

    public PostgresDBContainer() {
        super(IMAGE_VERSION);
    }
}
