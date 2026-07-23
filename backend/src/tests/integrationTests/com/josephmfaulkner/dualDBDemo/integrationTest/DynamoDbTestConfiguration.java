package com.josephmfaulkner.dualDBDemo.integrationTest;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;

import com.josephmfaulkner.dualDBDemo.posts.persistence.PostRepository;
import com.josephmfaulkner.dualDBDemo.posts.persistence.dual.PostRepositoryDualWrite;
import com.josephmfaulkner.dualDBDemo.posts.persistence.dynamodb.PostRepositoryDynamoDb;

import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;

import java.net.URI;

@TestConfiguration
public class DynamoDbTestConfiguration {

    /* 
    @Bean
    @Primary
    public PostRepository postRepository(
        PostRepositoryDynamoDb dynamoRepo
    ) {
        return dynamoRepo;
    };

    @Bean
    @Primary
    public PostRepository postRepository(
        PostRepositoryDualWrite dualWriteRepo
    ) {
        return dualWriteRepo;
    };
    */


    @Bean
    @Primary
    public DynamoDbClient dynamoDbClient() {
        return DynamoDbClient.builder()
                .endpointOverride(URI.create("http://localhost:8085"))
                .region(Region.US_EAST_1)
                .credentialsProvider(StaticCredentialsProvider.create(AwsBasicCredentials.create("dummy", "dummy")))
                .build();
    }

    @Bean
    @Primary
    public DynamoDbEnhancedClient dynamoDbEnhancedClient(DynamoDbClient dynamoDbClient) {
        return DynamoDbEnhancedClient.builder().dynamoDbClient(dynamoDbClient).build();
    }


}