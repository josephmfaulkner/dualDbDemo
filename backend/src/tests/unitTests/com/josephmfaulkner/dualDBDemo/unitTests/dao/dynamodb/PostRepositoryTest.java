package com.josephmfaulkner.dualDBDemo.unitTests.dao.dynamodb;

import com.josephmfaulkner.dualDBDemo.testFixtures._BaseLocalDynamoDbTest;
import com.josephmfaulkner.dualDBDemo.exceptions.PostNotFoundException;
import com.josephmfaulkner.dualDBDemo.posts.dto.Comment;
import com.josephmfaulkner.dualDBDemo.posts.dto.Post;
import com.josephmfaulkner.dualDBDemo.posts.persistence.PostRepository;
import com.josephmfaulkner.dualDBDemo.posts.persistence.dynamodb.PostRepositoryDynamoDb;
import com.josephmfaulkner.dualDBDemo.posts.persistence.dynamodb.models.PostEntity;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.enhanced.dynamodb.model.CreateTableEnhancedRequest;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class PostRepositoryTest extends _BaseLocalDynamoDbTest {

    private PostRepository postRepository;
    private DynamoDbTable<PostEntity> postTable;

    @BeforeEach
    public void setupTest() {
        postRepository = new PostRepositoryDynamoDb(enhancedClient);

        postTable = enhancedClient.table("Posts", TableSchema.fromBean(PostEntity.class));
        postTable.createTable(CreateTableEnhancedRequest.builder().build());
    }

    @AfterEach
    public void teardownTest() {
        postTable.deleteTable();
    }

    @Test
    public void testSaveAndGetPostById() {
        Post post = Post.builder()
                .title("Test Title")
                .content("Test Content")
                .comments(Collections.singletonList(Comment.builder().id(1).content("A comment").build()))
                .build();
        Post savedPost = postRepository.savePost(post);

        assertNotNull(savedPost.id());

        Post retrievedPost = postRepository.getPostById(savedPost.id());
        assertNotNull(retrievedPost);
        assertEquals(savedPost.id(), retrievedPost.id());
        assertEquals(post.title(), retrievedPost.title());
        assertEquals(post.content(), retrievedPost.content());
    }

    @Test
    public void testGetAllPosts() {
        Post post1 = Post.builder().title("Title 1").content("Content 1").build();
        Post post2 = Post.builder().title("Title 2").content("Content 2").build();
        postRepository.savePost(post1);
        postRepository.savePost(post2);

        List<Post> posts = postRepository.getAllPosts();
        assertEquals(2, posts.size());
    }

    @Test
    public void testUpdatePost() {
        Post post = Post.builder().title("Original Title").content("Original Content").build();
        Post savedPost = postRepository.savePost(post);

        savedPost = Post.builder()
                .id(savedPost.id())
                .title("Updated Title")
                .content("Updated Content")
                .build();

        Post updatedPost = postRepository.updatePost(savedPost);

        assertNotNull(updatedPost);
        assertEquals("Updated Title", updatedPost.title());
        assertEquals("Updated Content", updatedPost.content());

        Post retrievedPost = postRepository.getPostById(savedPost.id());
        assertEquals("Updated Title", retrievedPost.title());
    }

    @Test
    public void testDeletePost() {
        Post post = Post.builder().title("To Be Deleted").content("Some content").build();
        Post savedPost = postRepository.savePost(post);

        // The post exists, so no exception should be thrown
        assertDoesNotThrow(() -> postRepository.deletePost(savedPost.id()));

        // Now that it's deleted, trying to get it should throw an exception
        assertThrows(PostNotFoundException.class, () -> postRepository.getPostById(savedPost.id()));
    }

    @Test
    public void testGetPostById_NotFound() {
        String nonExistentId = UUID.randomUUID().toString();
        assertThrows(PostNotFoundException.class, () -> postRepository.getPostById(nonExistentId));
    }

    @Test
    public void testUpdatePost_NotFound() {
        Post post = Post.builder()
                .id(UUID.randomUUID().toString())
                .title("Non-existent post")
                .content("This post does not exist in the DB")
                .build();
        assertThrows(PostNotFoundException.class, () -> postRepository.updatePost(post));
    }

    @Test
    public void testDeletePost_NotFound() {
        String nonExistentId = UUID.randomUUID().toString();
        assertThrows(PostNotFoundException.class, () -> postRepository.deletePost(nonExistentId));
    }
    
}
