package com.josephmfaulkner.dualDBDemo.testFixtures;

import java.net.URI;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.test.context.TestPropertySource;
import org.testcontainers.postgresql.PostgreSQLContainer;

import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.dynamodb.services.local.main.ServerRunner;
import software.amazon.dynamodb.services.local.server.DynamoDBProxyServer;

@TestPropertySource(properties = {
    "spring.jpa.hibernate.ddl-auto=create-drop"
})
public abstract class _BaseDualDbTest {

    @ServiceConnection
    protected static final PostgreSQLContainer postgres = 
        new PostgreSQLContainer("postgres:18-alpine");

    private static DynamoDBProxyServer dynamoDbProxyServer;
    protected static DynamoDbClient dynamoDbClient;
    protected static DynamoDbEnhancedClient enhancedClient;

    @BeforeAll
    public static void setup() throws Exception {
        String port = "8085";
        final String[] localArgs = {"-inMemory", "-port", port};
        dynamoDbProxyServer = ServerRunner.createServerFromCommandLineArgs(localArgs);
        dynamoDbProxyServer.start();

        dynamoDbClient = DynamoDbClient.builder()
                .endpointOverride(URI.create("http://localhost:" + port))
                .region(Region.US_EAST_1)
                .credentialsProvider(StaticCredentialsProvider.create(AwsBasicCredentials.create("dummy", "dummy")))
                .build();

        enhancedClient = DynamoDbEnhancedClient.builder().dynamoDbClient(dynamoDbClient).build();
    }

    @AfterAll
    public static void teardown() throws Exception {
        if (dynamoDbProxyServer != null) dynamoDbProxyServer.stop();
        if (dynamoDbClient != null) dynamoDbClient.close();
    }
}