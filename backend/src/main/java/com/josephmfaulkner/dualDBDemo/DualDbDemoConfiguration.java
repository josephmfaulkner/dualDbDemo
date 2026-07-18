package com.josephmfaulkner.dualDBDemo;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;

import com.josephmfaulkner.dualDBDemo.posts.persistence.PostRepository;
import com.josephmfaulkner.dualDBDemo.posts.persistence.dynamodb.PostRepositoryDynamoDb;
import com.josephmfaulkner.dualDBDemo.posts.persistence.postgres.PostRepositoryPostgres;

import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;


import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;

@Configuration
@Profile("!test")
public class DualDbDemoConfiguration {

    @Bean
    public DynamoDbEnhancedClient dynamoDbEnhancedClient() {
        return DynamoDbEnhancedClient.builder()
            .dynamoDbClient(
                DynamoDbClient.builder()
                    .endpointOverride(java.net.URI.create("http://localhost:8000"))
                    .region(Region.of("us-west-2"))
                    .credentialsProvider(
                        StaticCredentialsProvider.create(
                            AwsBasicCredentials.create(
                                "local", 
                                "local"
                            )
                        )
                    )
                    .build()
            )
        .build();
    }

    @Bean
    @Primary // Makes this the default choice whenever a class requests a generic PostRepository
    public PostRepository postRepository(
            PostRepositoryPostgres postgresRepo,
            PostRepositoryDynamoDb dynamoRepo,
            @Value("${spring.application.storage.engine:postgres}") String repoTech
    ) {
        return switch (repoTech.toLowerCase()) {
            case "postgres"   -> postgresRepo;
            case "dynamodb"   -> dynamoRepo;
            default -> throw new IllegalArgumentException("Unsupported repository implementation: " + repoTech);
        };
    }

}