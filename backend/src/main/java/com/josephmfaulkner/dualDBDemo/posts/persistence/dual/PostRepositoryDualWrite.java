package com.josephmfaulkner.dualDBDemo.posts.persistence.dual;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Primary;
import org.springframework.core.task.TaskExecutor;
import org.springframework.stereotype.Repository;

import com.josephmfaulkner.dualDBDemo.posts.dto.Post;
import com.josephmfaulkner.dualDBDemo.posts.persistence.PostRepository;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Repository
@Primary
public class PostRepositoryDualWrite implements PostRepository {

    private final PostRepository dynamoDbRepository;
    private final PostRepository postgresRepository;
    private final TaskExecutor taskExecutor;

    public PostRepositoryDualWrite(
            @Qualifier("postRepositoryDynamoDb") PostRepository dynamoDbRepository,
            @Qualifier("postRepositoryPostgres") PostRepository postgresRepository,
            TaskExecutor taskExecutor
    ) 
    {
        log.info("PostRepositoryDualWrite constructor");
        this.dynamoDbRepository = dynamoDbRepository;
        this.postgresRepository = postgresRepository;
        this.taskExecutor = taskExecutor;
    }

    @Override
    public List<Post> getAllPosts() {
        List<Post> dynamoDbPosts = this.dynamoDbRepository.getAllPosts();
        CompletableFuture.runAsync(() -> {
            try {
                for (Post dynamoDbPost : dynamoDbPosts) {
                    Post postgresPost = this.postgresRepository.getPostById(dynamoDbPost.id());
                    validateDrift(dynamoDbPost.id(), Optional.ofNullable(dynamoDbPost), Optional.ofNullable(postgresPost));
                }
            } catch (Exception e) {
                log.error("Failed to dual-read all posts to Postgres", e);
            }
                    
        }, taskExecutor);
        return dynamoDbPosts;   
    }

    @Override
    public Post getPostById(String id) {
        Post dynamoDbPost = this.dynamoDbRepository.getPostById(id);
        CompletableFuture.runAsync(() -> {
            try {
                Post postgresPost = this.postgresRepository.getPostById(id);
                validateDrift(id, Optional.ofNullable(dynamoDbPost), Optional.ofNullable(postgresPost));
            } catch (Exception e) {
                log.error("Failed to dual-read post {} to Postgres", dynamoDbPost.id(), e);
            }
        }, taskExecutor);
        return dynamoDbPost;
    }

    @Override
    public Post savePost(Post newPost) {
        Post savedPost = this.dynamoDbRepository.savePost(newPost);
        CompletableFuture.runAsync(() -> {
            try {
                postgresRepository.savePost(savedPost);
            } catch (Exception e) {
                log.error("Failed to dual-write post {} to Postgres", savedPost.id(), e);
            }
        }, taskExecutor);
        return savedPost;
    }

    @Override
    public Post updatePost(Post existingPost) {
        Post dynamoDbPost = this.dynamoDbRepository.updatePost(existingPost);
        CompletableFuture.runAsync(() -> {
            try {
                Post postgresPost = this.postgresRepository.updatePost(existingPost);
                validateDrift(existingPost.id(), Optional.ofNullable(dynamoDbPost), Optional.ofNullable(postgresPost));
            } catch (Exception e) {
                log.error("Failed to dual-update post {} to Postgres", dynamoDbPost.id(), e);
            }
        }, taskExecutor);
        return dynamoDbPost;
    }

    @Override
    public void deletePost(String id) {
        this.dynamoDbRepository.deletePost(id);
        CompletableFuture.runAsync(() -> {
            try {
                this.postgresRepository.deletePost(id);
            } catch (Exception e) {
                log.error("Failed to dual-delete post {} to Postgres", id, e);
            }
        }, taskExecutor);
    }

    private void validateDrift(String id, Optional<Post> primary, Optional<Post> secondary) {
        if (primary.isPresent() != secondary.isPresent()) {
            log.warn("[DRIFT DETECTED] Post {}: DynamoDB present={}, Postgres present={}", 
                     id, primary.isPresent(), secondary.isPresent());
            return;
        }
        
        if (primary.isPresent() && !primary.get().equals(secondary.get())) {
            // Alternatively, publish a metric (e.g., Micrometer Counter) here to trigger alerts
            log.warn("[DATA MISMATCH] Post {}: DynamoDB={}, Postgres={}", 
                     id, primary.get(), secondary.get());
        }
    }

}
