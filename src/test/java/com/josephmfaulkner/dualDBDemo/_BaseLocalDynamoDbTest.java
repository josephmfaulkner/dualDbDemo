package com.josephmfaulkner.dualDBDemo;

import java.net.URI;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;

import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.dynamodb.services.local.main.ServerRunner;
import software.amazon.dynamodb.services.local.server.DynamoDBProxyServer;

public abstract class _BaseLocalDynamoDbTest {

    private static DynamoDBProxyServer dynamoDbProxyServer;
    private static DynamoDbClient dynamoDbClient;
    protected static DynamoDbEnhancedClient enhancedClient;

    public _BaseLocalDynamoDbTest() {
        
    }

    @BeforeAll
    public static void setup() throws Exception {
        String port = "8000";
        final String[] localArgs = {"-inMemory", "-port", port};
        System.out.println("Starting DynamoDB Local...");
        dynamoDbProxyServer = ServerRunner.createServerFromCommandLineArgs(localArgs);
        dynamoDbProxyServer.start();

        dynamoDbClient = DynamoDbClient.builder()
                .endpointOverride(URI.create("http://localhost:8000"))
                .region(Region.US_EAST_1)
                .credentialsProvider(StaticCredentialsProvider.create(
                        AwsBasicCredentials.create("dummy", "dummy")))
                .build();

        enhancedClient = DynamoDbEnhancedClient.builder()
                .dynamoDbClient(dynamoDbClient)
                .build();

    }

    @AfterAll
    public static void teardown() throws Exception {
        dynamoDbClient.close();
        dynamoDbProxyServer.stop();
    }

}
